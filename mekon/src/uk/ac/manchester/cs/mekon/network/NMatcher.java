/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.mekon.network;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;
import uk.ac.manchester.cs.mekon.store.disk.*;

/**
 * Abstract base-class for implementations of {@link IMatcher} that
 * operate on the intermediate network-based representation.
 * <p>
 * The instance-level frames that are passed to the implemented
 * methods, are converted into instantiations of the network-based
 * representation, and then passed to the corresponding abstract
 * methods.
 * <p>
 * The matching process can be customised by adding one or more
 * pre-processors to modify the networks that will be passed to the
 * abstract methods (see {@link #addPreProcessor}).
 *
 * @author Colin Puleston
 */
public abstract class NMatcher implements IMatcher {

	private IStore store;

	private IMatchInstanceRefExpander instanceRefExpander = null;

	private NetworkCreator networkCreator = new NetworkCreator();
	private MatcherOps matcherOps = new StandardMatcherOps();

	private abstract class MatcherOps {

		abstract MatcherOps update(boolean regexMatchEnabled);

		abstract IMatches match(NNode query);

		abstract boolean matches(NNode query, NNode instance);
	}

	private class StandardMatcherOps extends MatcherOps {

		MatcherOps update(boolean regexMatchEnabled) {

			return regexMatchEnabled ? new RegexEnhancedMatcherOps(store) : this;
		}

		IMatches match(NNode query) {

			return NMatcher.this.match(query);
		}

		boolean matches(NNode query, NNode instance) {

			return NMatcher.this.matches(query, instance);
		}
	}

	private class RegexEnhancedMatcherOps extends MatcherOps {

		private IStore store;

		private QueryStringRemover queryStringRemover = new QueryStringRemover();
		private QueryStringRetainer queryStringRetainer = new QueryStringRetainer();

		private abstract class QueryCopyPruner  {

			NNode copyAndPrune(NNode query) {

				NNode copy = query.copy();

				prune(copy);

				return copy;
			}

			boolean prune(NNode node) {

				node.removeFeatures(getPrunableDataFeatures(node));

				List<NLink> links = node.getLinks();

				return links.isEmpty() ? prunableNodes() : prune(links);
			}

			abstract boolean prunableNodes();

			abstract List<? extends NFeature<?>> getPrunableDataFeatures(NNode node);

			private boolean prune(List<NLink> links) {

				boolean retainedValues = false;

				for (NLink link : links) {

					for (NNode value : link.getValues()) {

						if (prune(value)) {

							link.removeValue(value);
						}
						else {

							retainedValues = true;
						}
					}
				}

				return !retainedValues;
			}
		}

		private class QueryStringRemover extends QueryCopyPruner {

			boolean prunableNodes() {

				return false;
			}

			List<? extends NFeature<?>> getPrunableDataFeatures(NNode node) {

				return node.getStrings();
			}
		}

		private class QueryStringRetainer extends QueryCopyPruner {

			boolean prune(NNode node) {

				return super.prune(node) && node.getStrings().isEmpty();
			}

			boolean prunableNodes() {

				return true;
			}

			List<? extends NFeature<?>> getPrunableDataFeatures(NNode node) {

				return node.getNumbers();
			}
		}

		private class SplitQuery  {

			private NNode coreQuery;
			private NNode strQuery;

			SplitQuery(NNode query) {

				coreQuery = queryStringRemover.copyAndPrune(query);
				strQuery = queryStringRetainer.copyAndPrune(query);
			}

			IMatches match() {

				IMatches coreMatches = NMatcher.this.match(coreQuery);

				if (coreMatches.anyMatches()) {

					if (coreMatches.ranked()) {

						return filterRankedMatches(coreMatches);
					}

					return filterUnrankedMatches(coreMatches);
				}

				return coreMatches;
			}

			boolean matches(NNode instance) {

				return NMatcher.this.matches(coreQuery, instance) & matchesStrings(instance);
			}

			private IMatches filterRankedMatches(IMatches coreMatches) {

				IRankedMatches filtered = new IRankedMatches();

				for (IMatchesRank rank : coreMatches.getRanks()) {

					List<CIdentity> rankFiltered = filterMatches(rank.getMatches());

					if (!rankFiltered.isEmpty()) {

						filtered.addRank(rankFiltered, rank.getRankingValue());
					}
				}

				return filtered;
			}

			private IMatches filterUnrankedMatches(IMatches coreMatches) {

				return new IUnrankedMatches(filterMatches(coreMatches.getAllMatches()));
			}

			private List<CIdentity> filterMatches(List<CIdentity> coreMatches) {

				List<CIdentity> filtered = new ArrayList<CIdentity>();

				for (CIdentity id : coreMatches) {

					if (matchesStrings(getInstanceNode(id))) {

						filtered.add(id);
					}
				}

				return filtered;
			}

			private boolean matchesStrings(NNode instance) {

				return strQuery.subsumesStructure(instance, true);
			}

			private NNode getInstanceNode(CIdentity id) {

				return new NNetwork(store.get(id).getRootFrame()).getRootNode();
			}
		}

		RegexEnhancedMatcherOps(IStore store) {

			this.store = store;
		}

		MatcherOps update(boolean regexMatchEnabled) {

			return regexMatchEnabled ? this : new StandardMatcherOps();
		}

		IMatches match(NNode query) {

			return new SplitQuery(query).match();
		}

		boolean matches(NNode query, NNode instance) {

			return new SplitQuery(query).matches(instance);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialise(IStore store, IMatcherIndexes indexes) {

		this.store = store;

		instanceRefExpander = new IMatchInstanceRefExpander(store);
	}

	/**
	 * Sets whether regular-expression matching is to be enabled
	 * for string-valued slots in query-matching. If enabled via this
	 * particular implementation of the method (rather than any
	 * overriding implementations) then results will be achieved by
	 * stripping of any string-valued fields from provided queries,
	 * invoking the relevant query-matching method on the string-free
	 * queries, then filtering the results for string-matching,
	 * including regular-expression matching.
	 *
	 * @param enabled True if regular-expression matching to be
	 * enabled
	 */
	public void setRegexMatchEnabled(boolean enabled) {

		matcherOps = matcherOps.update(enabled);
	}

	/**
	 * Registers a pre-processor to perform certain required
	 * modifications to appropriate representations of instances that
	 * are about to be stored, or queries that are about to be matched.
	 *
	 * @param preProcessor Pre-processor for instances and queries
	 */
	public void addPreProcessor(NProcessor preProcessor) {

		networkCreator.addPreProcessor(preProcessor);
	}

	/**
	 * Converts the specified instance-level instance frame to the
	 * network-based representation, runs any registered pre-processors
	 * over the resulting network, then invokes {@link
	 * #add(NNode, CIdentity)} to perform the add operation.
	 *
	 * @param instance Instance to be added
	 * @param identity Unique identity for instance
	 */
	public void add(IFrame instance, CIdentity identity) {

		add(instanceToNetwork(instance), identity);
	}

	/**
	 * Converts the specified instance-level query frame to the
	 * network-based representation, runs any registered pre-processors
	 * over the resulting network, then invokes {@link #match(NNode)}
	 * to perform the matching operation.
	 *
	 * @param query Query to be matched
	 * @return Unique identities of all matching instances
	 */
	public IMatches match(IFrame query) {

		return matcherOps.match(queryToNetwork(query));
	}

	/**
	 * Converts the specified instance-level query and instance frames
	 * to the network-based representation, runs any registered
	 * pre-processors over the resulting networks, then invokes {@link
	 * #matches(NNode, NNode)} to perform the match-testing operation.
	 *
	 * @param query Query to be matched
	 * @param instance Instance to test for matching
	 * @return True if query matched by instance
	 */
	public boolean matches(IFrame query, IFrame instance) {

		return matcherOps.matches(queryToNetwork(query), instanceToNetwork(instance));
	}

	/**
	 * Adds the specified instance to the matcher.
	 *
	 * @param instance Instance to be added
	 * @param identity Unique identity for instance
	 */
	protected abstract void add(NNode instance, CIdentity identity);

	/**
	 * Finds all instances that match the specified query.
	 *
	 * @param query Query to be matched
	 * @return Unique identities of all matching instances
	 */
	protected abstract IMatches match(NNode query);

	/**
	 * Tests whether the specified query is matched by the specified
	 * instance.
	 *
	 * @param query Query to be matched
	 * @param instance Instance to test for matching
	 * @return True if query matched by instance
	 */
	protected abstract boolean matches(NNode query, NNode instance);

	/**
	 * Specifies whether the instantiations of the network-based
	 * representations that are passed to the abstract methods will
	 * include expansions for any instances referenced from within
	 * the network, with expansions derived via the
	 * {@link IMatchInstanceRefExpander} mechanism.
	 *
	 * @return True if referenced instances are to be expanded
	 */
	protected abstract boolean expandInstanceRefs();

	private NNode instanceToNetwork(IFrame instance) {

		if (expandInstanceRefs()) {

			getInstanceRefExpander().expandAll(instance);
		}

		return networkCreator.createNetwork(instance);
	}

	private NNode queryToNetwork(IFrame query) {

		return networkCreator.createNetwork(query);
	}

	private IMatchInstanceRefExpander getInstanceRefExpander() {

		if (instanceRefExpander == null) {

			throw new Error("Instance-ref-expander has not been set");
		}

		return instanceRefExpander;
	}
}
