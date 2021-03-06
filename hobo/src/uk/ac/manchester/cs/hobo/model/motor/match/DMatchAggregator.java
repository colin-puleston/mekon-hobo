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

import uk.ac.manchester.cs.hobo.model.*;

/**
 * Customiser that modifies the matching process so that the
 * values of a specific numeric field contained within a specific
 * array-section of the stored instances will be combined into
 * single aggregated values, which will then be matched against
 * the value of the relevant numeric field in the query.
 *
 * @author Colin Puleston
 */
public abstract class DMatchAggregator
							<M extends DObject,
							Q extends M,
							T extends DObject,
							D extends DObject,
							N extends Number>
							extends DMatcherCustomiser<M, Q> {

	private DCustomMatcher matcher;

	private class Targets {

		private Map<T, INumber> aggregators = new HashMap<T, INumber>();

		Targets(M instance) {

			for (T target : getActiveTargets(instance)) {

				aggregators.put(target, getAggregator(target));
			}
		}

		Set<T> getTargets() {

			return aggregators.keySet();
		}

		INumber getAggregatorFor(T target) {

			return aggregators.get(target);
		}
	}

	private class InstanceTargets extends Targets {

		InstanceTargets(M instance) {

			super(instance);
		}

		boolean matches(Targets queries) {

			for (T query : queries.getTargets()) {

				if (!matches(query, queries.getAggregatorFor(query))) {

					return false;
				}
			}

			return true;
		}

		private boolean matches(T query, INumber queryAggregator) {

			INumber aggregate = aggregateOverDataSectionMatches(query);

			return queryAggregator.getType().validValue(aggregate);
		}

		private INumber aggregateOverDataSectionMatches(T query) {

			int aggregate = 0;

			for (T instance : getTargets()) {

				if (matchesDataSection(query, instance)) {

					aggregate += getAggregatorFor(instance).asInteger();
				}
			}

			return new INumber(aggregate);
		}
	}

	private class Filter extends DMatchFilter<M> {

		private Targets queryTargets;

		protected boolean pass(M instance) {

			return new InstanceTargets(instance).matches(queryTargets);
		}

		Filter(Q query) {

			super(DMatchAggregator.this);

			queryTargets = new Targets(query);
		}
	}

	/**
	 * Constructor.
	 *
	 * @param model Relevant direct model
	 * @param matcher Relevant matcher
	 */
	protected DMatchAggregator(DModel model, DCustomMatcher matcher) {

		super(model);

		this.matcher = matcher;
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean handlesQuery(Q query) {

		return !getActiveTargets(query).isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	protected void preProcessQuery(Q query) {

		for (T target : getActiveTargets(query)) {

			getAggregatorCell(target).clear();
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
	 * Provides the array over which the aggregation is to occur, if
	 * currently present.
	 *
	 * @param instance Top-level instance
	 * @return Required targets array, or null if not currently present
	 */
	protected abstract DArray<T> getTargetsArrayOrNull(M instance);

	/**
	 * Retrieves the numeric cell containing the value that will
	 * contribute towards the aggregation-value, if currently present.
	 *
	 * @param target Top-level target section
	 * @return Required aggrogator cell, or null if not currently
	 * present
	 */
	protected abstract DCell<DNumberRange<N>> getAggregatorCellOrNull(T target);

	/**
	 * Retrieves the sub-section of the target section for which a
	 * match must be present for the aggregator value to be included
	 * in the aggregate, if currently present.
	 *
	 * @param target Top-level target section
	 * @return Required data-section, or null if not currently present
	 */
	protected abstract D getDataSectionOrNull(T target);

	private List<T> getActiveTargets(M instance) {

		List<T> activeObjects = new ArrayList<T>();

		for (T target : getAllTargets(instance)) {

			if (hasAggregator(target)) {

				activeObjects.add(target);
			}
		}

		return activeObjects;
	}

	private List<T> getAllTargets(M instance) {

		DArray<T> array = getTargetsArrayOrNull(instance);

		return array != null ? array.getAll() : Collections.<T>emptyList();
	}

	private boolean matchesDataSection(T query, T instance) {

		D queryData = getDataSectionOrNull(query);

		if (queryData == null) {

			return true;
		}

		D instanceData = getDataSectionOrNull(instance);

		if (instanceData == null) {

			return false;
		}

		return matches(queryData, instanceData);
	}

	private boolean hasAggregator(T target) {

		DCell<DNumberRange<N>> cell = getAggregatorCellOrNull(target);

		return cell != null && cell.isSet();
	}

	private INumber getAggregator(T target) {

		return getAggregatorCell(target).get().asCNumber().asINumber();
	}

	private DCell<DNumberRange<N>> getAggregatorCell(T target) {

		return checkNotNull(getAggregatorCellOrNull(target));
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