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
	private QueryCustomiser queryCustomiser = null;

	private class CustomisedQueryNodeMatcher  {

		private CustomisedQuery query;

		CustomisedQueryNodeMatcher(CustomisedQuery query) {

			this.query = query;
		}

		IMatches match() {

			IMatches coreMatches = coreMatch();

			if (coreMatches.anyMatches()) {

				if (coreMatches.ranked()) {

					return filterRankedMatches(coreMatches);
				}

				return filterUnrankedMatches(coreMatches);
			}

			return coreMatches;
		}

		boolean matches(NNode instance) {

			return coreMatches(instance) && customMatches(instance);
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

				if (customMatches(getInstanceNode(id))) {

					filtered.add(id);
				}
			}

			return filtered;
		}

		private IMatches coreMatch() {

			return NMatcher.this.match(query.getCoreQuery());
		}

		private boolean coreMatches(NNode instance) {

			return NMatcher.this.matches(query.getCoreQuery(), instance);
		}

		private boolean customMatches(NNode instance) {

			return matchesDirect(query.getCustomQuery(), instance);
		}

		private NNode getInstanceNode(CIdentity id) {

			return new NNetwork(store.get(id).getRootFrame()).getRootNode();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialise(IMatcherConfig config) {

		store = config.getStore();

		instanceRefExpander = new IMatchInstanceRefExpander(store);
		queryCustomiser = new QueryCustomiser(config.getValueMatchCustomisers());
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

		NNode nQuery = queryToNetwork(query);

		CustomisedQuery cnQuery = queryCustomiser.checkCustomise(nQuery);

		if (cnQuery == null) {

			return match(nQuery);
		}

		return new CustomisedQueryNodeMatcher(cnQuery).match();
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

		NNode nQuery = queryToNetwork(query);
		NNode nInstance = instanceToNetwork(instance);

		CustomisedQuery cnQuery = queryCustomiser.checkCustomise(nQuery);

		if (cnQuery == null) {

			return matches(nQuery, nInstance);
		}

		return new CustomisedQueryNodeMatcher(cnQuery).matches(nInstance);
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

	void addValueMatchCustomiser(IValueMatchCustomiser customiser) {

		queryCustomiser.addValueMatchCustomiser(customiser);
	}

	boolean matchesDirect(NNode query, NNode instance) {

		return new QueryNodeDirectMatcher(queryCustomiser).matches(query, instance);
	}

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
