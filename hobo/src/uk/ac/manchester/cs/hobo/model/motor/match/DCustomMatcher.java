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

package uk.ac.manchester.cs.hobo.model.motor.match;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * Implementation of the {@link IMatcher} interface that enables
 * customisation of the matching mechanisms provided by a
 * "core-matcher", based on a particular domain-specific Object
 * Model (OM). The core-matcher can be provided by any
 * implementation of {@link IMatcher}.
 * <p>
 * Specific customisations to the basic matching mechanisms are
 * provided in the form of appropriate extensions of the {@link
 * DMatcherCustomiser} class, which form an ordered list, and
 * whose pre- and post-processing methods will be applied in the
 * relevant order before and after each matching operation.
 *
 * @author Colin Puleston
 */
public class DCustomMatcher implements IMatcher {

	private IMatcher coreMatcher;
	private Customisers customisers;

	/**
	 * Constructor.
	 *
	 * @param model Relevant direct model
	 * @param coreMatcher Provider of basic matching mechanisms
	 */
	public DCustomMatcher(DModel model, IMatcher coreMatcher) {

		customisers = new Customisers(model);

		this.coreMatcher = coreMatcher;
	}

	/**
	 * Adds a customiser to the end of the ordered list.
	 *
	 * @param customiser Customiser to add
	 */
	public void addCustomiser(DMatcherCustomiser<?> customiser) {

		customisers.add(customiser);
	}

	/**
	 * Invokes the corresponding method on the the core-matcher,
	 * and performs initialisations specific to this class that
	 * could not be performed until after the store has been created
	 * and registered.
	 *
	 * @param indexes Mappings between unique instance identities
 	 * and corresponding unique index values
	 */
	public void initialise(IMatcherIndexes indexes) {

		coreMatcher.initialise(indexes);
		customisers.initialisePostStoreBuild();
	}

	/**
	 * Invokes the corresponding method the core-matcher to determine
	 * whether a rebuild is required.
	 *
	 * @return true as rebuild required
	 */
	public boolean rebuildOnStartup() {

		return coreMatcher.rebuildOnStartup();
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
	 * method on the the core-matcher to add the resulting instance.
	 *
	 * @param instance Instance to be added
	 * @param identity Unique identity for instance
	 */
	public void add(IFrame instance, CIdentity identity) {

		coreMatcher.add(preProcess(instance), identity);
	}

	/**
	 * Invokes the corresponding method the core-matcher to remove
	 * the specified instance.
	 *
	 * @param identity Unique identity of instance to be removed
	 */
	public void remove(CIdentity identity) {

		coreMatcher.remove(identity);
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

		return processMatches(query, coreMatcher.match(preProcess(query)));
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

		if (coreMatcher.matches(preProcess(query), preProcess(instance))) {

			return passesMatchesFilter(query, instance);
		}

		return false;
	}

	/**
	 * Invokes the corresponding method on the the core-matcher.
	 */
	public void stop() {

		coreMatcher.stop();
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
