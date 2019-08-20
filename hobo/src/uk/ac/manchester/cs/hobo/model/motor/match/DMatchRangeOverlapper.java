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

import uk.ac.manchester.cs.hobo.model.*;

/**
 * Customiser that modifies the matching process so that specific
 * numeric-valued fields will be considered as matching whenever
 * the relevant numeric-ranges overlap (in contrast to the
 * range-subsumption that would normally be expected from a standard
 * matcher).
 *
 * @author Colin Puleston
 */
public abstract class DMatchRangeOverlapper
						<M extends DObject,
						Q extends M,
						R extends Number>
						extends DMatcherCustomiser<M, Q> {

	private class Filter extends DMatchFilter<M> {

		private CNumber queryMatchRange;

		protected boolean pass(M instance) {

			CNumber range = getMatchRangeOrNull(instance);

			return range != null && rangeOverlap(range);
		}

		Filter(Q query) {

			super(DMatchRangeOverlapper.this);

			queryMatchRange = getMatchRange(query);
		}

		private boolean rangeOverlap(CNumber range) {

			return range.intersectsWith(queryMatchRange);
		}
	}

	/**
	 * Constructor.
	 *
	 * @param model Relevant direct model
	 */
	protected DMatchRangeOverlapper(DModel model) {

		super(model);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean handlesQuery(Q query) {

		return getMatchRangeOrNull(query) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void preProcessQuery(Q query) {

		DCell<DNumberRange<R>> cell = getRangeMatchCellOrNull(query);

		if (cell != null) {

			cell.clear();
		}
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

		return new Filter(query).pass(instance);
	}

	/**
	 * Retrieves the required range-match cell from the specified
	 * top-level instance, if currently present.
	 *
	 * @param instance Top-level instance
	 * @return Required range-match cell, or null if not currently
	 * present
	 */
	protected abstract DCell<DNumberRange<R>> getRangeMatchCellOrNull(M instance);

	private CNumber getMatchRange(M instance) {

		return checkNotNull(getMatchRangeOrNull(instance));
	}

	private CNumber getMatchRangeOrNull(M instance) {

		DCell<DNumberRange<R>> cell = getRangeMatchCellOrNull(instance);

		return cell != null && cell.isSet() ? cell.get().asCNumber() : null;
	}

	private <V>V checkNotNull(V value) {

		if (value == null) {

			throw new Error("Unexpected null value!");
		}

		return value;
	}
}