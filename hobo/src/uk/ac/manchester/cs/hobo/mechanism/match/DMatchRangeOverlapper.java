package uk.ac.manchester.cs.hobo.mechanism.match;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * @author Colin Puleston
 */
public abstract class DMatchRangeOverlapper
						<M extends DObject,
						Q extends M>
						extends DMatcherCustomiser<M, Q> {

	private class Filter extends DMatchFilter<M> {

		private CNumber queryTargetRange;

		protected boolean pass(M instance) {

			CNumber targetRange = getTargetRangeOrNull(instance);

			return targetRange != null && targetMatch(targetRange);
		}

		Filter(M query) {

			super(DMatchRangeOverlapper.this);

			queryTargetRange = getQueryTargetRange(query);
		}

		private CNumber getQueryTargetRange(M query) {

			CNumber targetRange = getTargetRangeOrNull(query);

			if (targetRange == null) {

				throw new Error("Should never happen!");
			}

			return targetRange;
		}

		private boolean targetMatch(CNumber targetRange) {

			return targetRange.intersectsWith(queryTargetRange);
		}
	}

	protected DMatchRangeOverlapper(DModel dModel) {

		super(dModel);
	}

	protected boolean handles(M instance) {

		return getTargetRangeOrNull(instance) != null;
	}

	protected void preProcess(M instance) {

		DCell<Integer> target = getTargetCellOrNull(instance);

		if (target != null) {

			target.clear();
		}
	}

	protected IMatches processMatches(M query, IMatches matches) {

		return new Filter(query).filter(matches);
	}

	protected boolean passesMatchesFilter(M query, M instance) {

		return new Filter(query).pass(instance);
	}

	protected abstract DCell<Integer> getTargetCellOrNull(M instance);

	private CNumber getTargetRangeOrNull(M instance) {

		DCell<Integer> cell = getTargetCellOrNull(instance);

		if (cell != null) {

			List<IValue> values = cell.getSlot().getValues().asList();

			if (!values.isEmpty()) {

				return ((INumber)values.get(0)).getType();
			}
		}

		return null;
	}
}