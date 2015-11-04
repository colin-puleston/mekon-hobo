package uk.ac.manchester.cs.hobo.mechanism.match;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * @author Colin Puleston
 */
public abstract class DMatchAggregator
						<M extends DObject,
						Q extends M,
						T extends DObject>
						extends DMatcherCustomiser<M, Q> {

	private DCustomMatcher matcher;

	private class Targets {

		private Map<T, INumber> targetDurations = new HashMap<T, INumber>();

		Targets(M instance) {

			for (T target : getActiveTargets(instance)) {

				targetDurations.put(target, getDurationValue(target));
			}
		}

		Set<T> getTargets() {

			return targetDurations.keySet();
		}

		INumber getDuration(T target) {

			return targetDurations.get(target);
		}

		private INumber getDurationValue(T target) {

			return getDurationSlotValue(getDurationCell(target));
		}

		private INumber getDurationSlotValue(DCell<Integer> duration) {

			return (INumber)duration.getSlot().getValues().asList().get(0);
		}
	}

	private class InstanceTargets extends Targets {

		InstanceTargets(M instance) {

			super(instance);
		}

		boolean allAggregatesMatch(Targets queries) {

			for (T query : queries.getTargets()) {

				if (!aggregatesMatch(query, queries.getDuration(query))) {

					return false;
				}
			}

			return true;
		}

		private boolean aggregatesMatch(T query, INumber queryDuration) {

			INumber aggregateDuration = aggregateMatchingDurations(query);

			return queryDuration.getType().validValue(aggregateDuration);
		}

		private INumber aggregateMatchingDurations(T query) {

			int aggregate = 0;

			for (T instance : getTargets()) {

				if (nonDurationInfoMatches(query, instance)) {

					aggregate += getDuration(instance).asInteger();
				}
			}

			return new INumber(aggregate);
		}
	}

	private class Filter extends DMatchFilter<M> {

		private Targets queryTargets;

		protected boolean pass(M instance) {

			return new InstanceTargets(instance).allAggregatesMatch(queryTargets);
		}

		Filter(M query) {

			super(DMatchAggregator.this);

			queryTargets = new Targets(query);
		}
	}

	protected DMatchAggregator(DModel dModel, DCustomMatcher matcher) {

		super(dModel);

		this.matcher = matcher;
	}

	protected boolean handles(M instance) {

		return !getActiveTargets(instance).isEmpty();
	}

	protected void preProcess(M instance) {

		for (T target : getActiveTargets(instance)) {

			getDurationCell(target).clear();
		}
	}

	protected IMatches processMatches(M query, IMatches matches) {

		return new Filter(query).filter(matches);
	}

	protected boolean passesMatchesFilter(M query, M instance) {

		return new Filter(query).pass(instance);
	}

	protected abstract List<T> getAllTargets(M instance);

	protected abstract boolean containsNonDurationInfo(T target);

	protected abstract DCell<Integer> getDurationCellOrNull(T target);

	protected abstract boolean nonDurationInfoMatches(T query, T instance);

	private List<T> getActiveTargets(M instance) {

		List<T> activeTargets = new ArrayList<T>();

		for (T target : getAllTargets(instance)) {

			if (activeTarget(target)) {

				activeTargets.add(target);
			}
		}

		return activeTargets;
	}

	private boolean activeTarget(T target) {

		return containsNonDurationInfo(target) && hasDurationValue(target);
	}

	private boolean hasDurationValue(T target) {

		DCell<Integer> cell = getDurationCellOrNull(target);

		return cell != null && !cell.getSlot().getValues().isEmpty();
	}

	private DCell<Integer> getDurationCell(T target) {

		DCell<Integer> cell = getDurationCellOrNull(target);

		if (cell == null) {

			throw new Error("Should never happen!");
		}

		return cell;
	}
}