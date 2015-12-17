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

package uk.ac.manchester.cs.hobo.mechanism.match;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * Implementation of the {@link IMatcher} interface that enables
 * customisation of the matching mechanisms provided by a
 * "core-matcher", based on a particular domain-specific Object
 * Model (OM). The core-matcher can be provided by any
 * implementation of {@link IMatcher}. Specific customisations
 * are provided in the form of appropriate extensions of the
 * {@link DMatcherCustomiser} class, via the {@link #initialise}
 * method.
 *
 * @author Colin Puleston
 */
public class DCustomMatcher implements IMatcher {

	private IMatcher coreMatcher;
	private Operations operations = new IndirectOperations();

	private abstract class Operations {

		abstract void initialise(DMatcherCustomisers customisers);

		abstract void add(IFrame instance, CIdentity identity);

		abstract void remove(CIdentity identity);

		abstract IMatches match(IFrame query);

		abstract boolean matches(IFrame query, IFrame instance);
	}

	private class IndirectOperations extends Operations {

		private Map<CIdentity, IFrame> instances = new HashMap<CIdentity, IFrame>();

		void initialise(DMatcherCustomisers customisers) {

			DirectOperations directOps = new DirectOperations(customisers);

			for (CIdentity id : instances.keySet()) {

				directOps.add(instances.get(id), id);
			}

			operations = directOps;
		}

		void add(IFrame instance, CIdentity identity) {

			instances.put(identity, instance);
		}

		void remove(CIdentity identity) {

			throw createNotInitialisedException();
		}

		IMatches match(IFrame query) {

			throw createNotInitialisedException();
		}

		boolean matches(IFrame query, IFrame instance) {

			throw createNotInitialisedException();
		}

		private KAccessException createNotInitialisedException() {

			return new KAccessException("DCustomMatcher has not been initialised");
		}
	}

	private class DirectOperations extends Operations {

		private DMatcherCustomisers customisers;

		DirectOperations(DMatcherCustomisers customisers) {

			this.customisers = customisers;
		}

		void initialise(DMatcherCustomisers customisers) {

			throw new KAccessException("DCustomMatcher has already been initialised");
		}

		void add(IFrame instance, CIdentity identity) {

			coreMatcher.add(preProcess(instance), identity);
		}

		void remove(CIdentity identity) {

			coreMatcher.remove(identity);
		}

		IMatches match(IFrame query) {

			return processMatches(query, coreMatcher.match(preProcess(query)));
		}

		boolean matches(IFrame query, IFrame instance) {

			if (coreMatcher.matches(preProcess(query), preProcess(instance))) {

				return passesMatchesFilter(query, instance);
			}

			return false;
		}

		private IFrame preProcess(IFrame instance) {

			return customisers.preProcess(instance);
		}

		private IMatches processMatches(IFrame query, IMatches matches) {

			return customisers.processMatches(query, matches);
		}

		private boolean passesMatchesFilter(IFrame query, IFrame instance) {

			return customisers.passesMatchesFilter(query, instance);
		}
	}

	/**
	 * Constructor.
	 *
	 * @param coreMatcher Provider of basic matching mechanisms
	 */
	public DCustomMatcher(IMatcher coreMatcher) {

		this.coreMatcher = coreMatcher;
	}

	/**
	 * Inititialiser that must be invoked prior to use. Addes the
	 * set of customisers that will provide the required
	 * customisations  to the basic matching mechanisms, as provided
	 * by the core-matcher.
	 * <p>
	 * NOTE: The reason that the customisers are not added via the
	 * constructor, is that they depend upon the {@link DModel}, which
	 * is not available during the main build process, during which
	 * all matchers must be created and added to the instance-store.
	 *
	 * @param customisers Customisers to provide customisations to
	 * basic matching mechanisms provided by core-matcher
	 */
	public void initialise(DMatcherCustomisers customisers) {

		operations.initialise(customisers);
	}

	/**
	 * Checks whether the core-matcher handles instance-level
	 * frames of the specified type by invoking the corresponding
	 * method on that matcher.
	 *
	 * @param type Relevant frame-type
	 * @return True if matcher handles specified type
	 */
	public boolean handlesType(CFrame type) {

		return coreMatcher.handlesType(type);
	}

	/**
	 * Polls the customisers to perform all required pre-processing
	 * on the specified instance, then invokes the corresponding
	 * method to add the resulting instance to the core-matcher.
	 *
	 * @param instance Instance to be added
	 * @param identity Unique identity for instance
	 */
	public void add(IFrame instance, CIdentity identity) {

		operations.add(instance, identity);
	}

	/**
	 * Invokes the corresponding method to remove the specified
	 * instance from the core-matcher.
	 *
	 * @param identity Unique identity of instance to be removed
	 */
	public void remove(CIdentity identity) {

		operations.remove(identity);
	}

	/**
	 * Polls the customisers to perform all required pre-processing
	 * on the specified query, invokes the corresponding method on
	 * the core-matcher to find all instances that match the resulting
	 * query, then polls the customisers to perform all required
	 * post-processing on the resulting set of matches.
	 *
	 * @param query Query to be matched
	 * @return Unique identities of all matching instances
	 */
	public IMatches match(IFrame query) {

		return operations.match(query);
	}

	/**
	 * Polls the customisers to perform all required pre-processing
	 * on the specified query and instance, then invokes the
	 * corresponding method on the core-matcher to test whether the
	 * resulting query is matched by the resulting instance.
	 *
	 * @param query Query to be matched
	 * @param instance Instance to test for matching
	 * @return True if query matched by instance
	 */
	public boolean matches(IFrame query, IFrame instance) {

		return operations.matches(query, instance);
	}

	/**
	 * Invokes the corresponding method on the the core-matcher.
	 */
	public void stop() {

		coreMatcher.stop();
	}
}
