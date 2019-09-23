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

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.disk.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * Abstract base-class whose extensions provided specific
 * customisations of the matching process, based on a particular
 * domain-specific Object Model (OM).
 *
 * @author Colin Puleston
 */
public abstract class DMatcherCustomiser<M extends DObject, Q extends M> {

	private DModel model;
	private IStore iStore = null;

	private CFrame queryType;

	/**
	 * Constructor.
	 *
	 * @param model Relevant direct model
	 */
	public DMatcherCustomiser(DModel model) {

		this.model = model;

		queryType = model.getFrame(getQueryClass());
	}

	/**
	 * Checks whether the customiser can handle the specified query.
	 *
	 * @param query Relevant query
	 * @return True if customiser can handle specified query
	 */
	protected abstract boolean handlesQuery(Q query);

	/**
	 * Performs any required pre-processing of the specified query
	 * prior to use in a matching process.
	 *
	 * @param query Relevant query
	 */
	protected abstract void preProcessQuery(Q query);

	/**
	 * Performs any required post-processing of the matches produced
	 * via invocation of the {@link IMatcher#match} method on the
	 * core-matcher.
	 *
	 * @param query Original version of query (prior to pre-processing)
	 * @param matches Raw match results
	 * @return Proccessed version of match results
	 */
	protected abstract IMatches processMatches(Q query, IMatches matches);

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
	protected abstract boolean passesMatchesFilter(Q query, M instance);

	/**
	 * Provides the base-class that represents the relevant type of both
	 * assertions and queries in the OM.
	 *
	 * @return Relevant class
	 */
	protected abstract Class<M> getMatchingClass();

	/**
	 * Provides the base-class that represents the relevant type of queries
	 * in the OM, which is either identical to or an extension of the base
	 * matching-class (see {@link #getMatchingClass}.
	 *
	 * @return Relevant class
	 */
	protected abstract Class<Q> getQueryClass();

	/**
	 * Retieves the specified instance fron the instance-store.
	 *
	 * @param identity Identity of required instance
	 * @return Retrieved instance, or null if instance not of required type
	 */
	protected M getStoredInstanceOrNull(CIdentity identity) {

		return getMatchingObjectOrNull(iStore.get(identity).getRootFrame());
	}

	void initialisePostStoreBuild() {

		iStore = IDiskStoreManager.getStore(model.getCModel());
	}

	boolean handlesQuery(IFrame query) {

		if (hasQueryType(query)) {

			Q queryObj = getQueryObjectOrNull(query);

			return queryObj != null && handlesQuery(queryObj);
		}

		return false;
	}

	void preProcessQuery(IFrame query) {

		preProcessQuery(getQueryObject(query));
	}

	IMatches processMatches(IFrame query, IMatches matches) {

		return processMatches(getQueryObject(query), matches);
	}

	boolean passesMatchesFilter(IFrame query, IFrame instance) {

		M instObj = getMatchingObjectOrNull(instance);

		return instObj != null && passesMatchesFilter(getQueryObject(query), instObj);
	}

	private boolean hasQueryType(IFrame query) {

		return queryType.subsumes(query.getType());
	}

	private M getMatchingObjectOrNull(IFrame instance) {

		return getDObjectOrNull(instance, getMatchingClass());
	}

	private Q getQueryObjectOrNull(IFrame query) {

		return getDObjectOrNull(query, getQueryClass());
	}

	private Q getQueryObject(IFrame query) {

		return model.getDObject(query, getQueryClass());
	}

	private <D extends DObject>D getDObjectOrNull(IFrame query, Class<D> dClass) {

		DObject dObject = model.getDObject(query);

		if (dClass.isAssignableFrom(dObject.getClass())) {

			return dClass.cast(dObject);
		}

		return null;
	}
}
