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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * Abstract base-class whose extensions provided specific
 * customisations of the matching process, based on a particular
 * domain-specific Object Model (OM).
 *
 * @author Colin Puleston
 */
public abstract class DMatcherCustomiser<M extends DObject> {

	private DModel model;
	private IStore iStore = null;

	private CFrame matchingType;

	/**
	 * Constructor.
	 *
	 * @param model Relevant direct model
	 */
	public DMatcherCustomiser(DModel model) {

		this.model = model;

		matchingType = model.getFrame(getMatchingClass());
	}

	/**
	 * Checks whether the customiser can handle the specified instance
	 * (which could be either an assertion or a query).
	 *
	 * @param instance Relevant instance
	 * @return True if customiser can handle specified instance
	 */
	protected abstract boolean handles(M instance);

	/**
	 * Performs any required pre-processing of the specified instance
	 * prior to storing (relevant only to assertions only) or being
	 * used in a matching process (potentially relevant to both queries
	 * and assertions).
	 *
	 * @param instance Relevant instance
	 */
	protected abstract void preProcess(M instance);

	/**
	 * Performs any required post-processing of the matches produced
	 * via invocation of the {@link IMatcher#match} method on the
	 * core-matcher.
	 *
	 * @param query Original version of query (prior to pre-processing)
	 * @param matches Raw match results
	 * @return Proccessed version of match results
	 */
	protected abstract IMatches processMatches(M query, IMatches matches);

	/**
	 * Subjects any query/instance pairs for which the {@link
	 * IMatcher#matches} method on the core-matcher produced a positive
	 * result to any additional required filtering.
	 *
	 * @param query Original version of query (prior to pre-processing)
	 * @param instance Original version of instance (prior to
	 * pre-processing)
	 * @return True if additional filtering is passed
	 */
	protected abstract boolean passesMatchesFilter(M query, M instance);

	/**
	 * Provides the base-class that represents the relevant type of both
	 * assertions and queries in the OM.
	 *
	 * @return Relevant class
	 */
	protected abstract Class<M> getMatchingClass();


	/**
	 * Retieves the specified instance fron the instance-store.
	 *
	 * @param identity Identity of required instance
	 * @return Retrieved instance
	 */
	protected M getStoredInstance(CIdentity identity) {

		return getMatchingObject(iStore.get(identity));
	}

	void initialisePostStoreBuild() {

		iStore = IStore.get(model.getCModel());
	}

	boolean handles(IFrame instance) {

		return hasMatchingType(instance) && handles(getMatchingObject(instance));
	}

	void preProcess(IFrame instance) {

		preProcess(getMatchingObject(instance));
	}

	IMatches processMatches(IFrame query, IMatches matches) {

		return processMatches(getMatchingObject(query), matches);
	}

	boolean passesMatchesFilter(IFrame query, IFrame instance) {

		return passesMatchesFilter(
					getMatchingObject(query),
					getMatchingObject(instance));
	}

	private boolean hasMatchingType(IFrame instance) {

		return matchingType.subsumes(instance.getType());
	}

	private M getMatchingObject(IFrame instance) {

		return model.getDObject(instance, getMatchingClass());
	}
}
