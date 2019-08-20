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

import uk.ac.manchester.cs.hobo.*;
import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.model.motor.match.*;

/**
 * Customiser that modifies the matching process so that for
 * specific sections of the query, the normal subsumption
 * requirement is reversed, so that the relevant sections of the
 * query must be subsumed by the corresponding sections of the
 * stored instances, rather than vice-versa.
 *
 * @author Colin Puleston
 */
public abstract class DMatchSectionInverter
							<M extends DObject,
							Q extends M,
							I extends DObject>
							extends DMatcherCustomiser<M, Q> {

	private DCustomMatcher matcher;

	private class Filter extends DMatchFilter<M> {

		private I queryInversionSection;

		protected boolean pass(M instance) {

			I inversionSection = getInversionSectionOrNull(instance);

			if (inversionSection != null) {

				return inversionMatch(inversionSection);
			}

			return true;
		}

		Filter(Q query) {

			super(DMatchSectionInverter.this);

			queryInversionSection = getInversionSection(query);
		}

		private boolean inversionMatch(I inversionSection) {

			return matches(inversionSection, queryInversionSection);
		}
	}

	/**
	 * Constructor.
	 *
	 * @param model Relevant direct model
	 * @param matcher Relevant matcher
	 */
	public DMatchSectionInverter(DModel model, DCustomMatcher matcher) {

		super(model);

		this.matcher = matcher;
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean handlesQuery(Q query) {

		return getInversionSectionOrNull(query) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void preProcessQuery(Q query) {

		getInversionSectionCell(query).clear();
	}

	/**
	 * {@inheritDoc}
	 */
	protected IMatches processMatches(Q query, IMatches matches) {

		return new Filter(query).filter(matches);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean passesMatchesFilter(Q query, M instance) {

		throw new HAccessException("Method should never be invoked!");
	}

	/**
	 * Retrieves the cell containing the section for which the
	 * subsumption requirement is to be reversed, if currently present.
	 *
	 * @param instance Top-level instance
	 * @return Required inversion-match cell, or null if not currently
	 * present
	 */
	protected abstract DCell<I> getInversionSectionCellOrNull(M instance);

	private I getInversionSection(M instance) {

		return checkNotNull(getInversionSectionOrNull(instance));
	}

	private DCell<I> getInversionSectionCell(M instance) {

		return checkNotNull(getInversionSectionCellOrNull(instance));
	}

	private I getInversionSectionOrNull(M instance) {

		DCell<I> cell = getInversionSectionCellOrNull(instance);

		return cell != null && cell.isSet() ? cell.get() : null;
	}

	private boolean matches(DObject query, DObject instance) {

		return matcher.matches(query.getFrame(), instance.getFrame());
	}

	private <V>V checkNotNull(V value) {

		if (value == null) {

			throw new Error("Unexpected null value!");
		}

		return value;
	}
}