package uk.ac.manchester.cs.hobo.mechanism.match;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

import uk.ac.manchester.cs.hobo.*;
import uk.ac.manchester.cs.hobo.model.*;

/**
 * @author Colin Puleston
 */
public abstract class DMatchRanker
						<M extends DObject,
						Q extends M,
						V extends DObject>
						extends DMatcherCustomiser<M, Q> {

	private DModel dModel;
	private DCustomMatcher matcher;

	private class Ranker {

		private RankedMatches rankedMatches;

		Ranker(IMatches matches, V rankingValue) {

			rankedMatches = new RankedMatches(matches);

			updateForOptionals(rankingValue);
		}

		IMatches getMatches() {

			return rankedMatches.toIMatches();
		}

		private void updateForOptionals(V rankingValue) {

			for (ISlot slot : rankingValue.getFrame().getSlots().asList()) {

				updateForOptionals(slot);
			}
		}

		private void updateForOptionals(ISlot slot) {

			CIdentity slotId = slot.getType().getIdentity();

			for (IValue value : slot.getValues().asList()) {

				updateForOptionals(slotId, value);
			}
		}

		private void updateForOptionals(CIdentity slotId, IValue value) {

			V rankingValueQuery = createOptionalRankingValueQuery(slotId, value);

			for (CIdentity matchId : rankedMatches.getMatches()) {

				checkOptionalMatch(rankingValueQuery, matchId);
			}
		}

		private void checkOptionalMatch(V rankingValueQuery, CIdentity matchId) {

			M instance = getStoredInstance(matchId);
			DCell<V> rankingCell = getMandatoryRankingCell(instance);

			if (rankingCell.isSet()) {

				V rankingValue = rankingCell.get();

				if (matches(rankingValueQuery, rankingValue)) {

					rankedMatches.incrementScore(matchId);
				}
			}
		}

		private V createOptionalRankingValueQuery(CIdentity slotId, IValue value) {

			V rankingValue = instantiateQuery(getRankingValueClass());
			ISlot slot = rankingValue.getFrame().getSlots().get(slotId);

			slot.getValuesEditor().add(value);

			return rankingValue;
		}
	}

	public DMatchRanker(DModel dModel, DCustomMatcher matcher) {

		super(dModel);

		this.dModel = dModel;
		this.matcher = matcher;
	}

	protected boolean handles(M instance) {

		if (queryInstance(instance)) {

			return getQueryOptionalRankingCell(instance).isSet();
		}

		return false;
	}

	protected void preProcess(M instance) {

		getQueryOptionalRankingCell(instance).clear();
	}

	protected IMatches processMatches(M query, IMatches matches) {

		DCell<V> rankingCell = getQueryOptionalRankingCell(query);

		return rankingCell.isSet()
				? rankMatches(matches, rankingCell.get())
				: matches;
	}

	protected boolean passesMatchesFilter(M query, M instance) {

		throw new HAccessException("Method should never be invoked!");
	}

	protected abstract Class<V> getRankingValueClass();

	protected abstract DCell<V> getMandatoryRankingCell(M instance);

	protected abstract DCell<V> getOptionalRankingCell(Q query);

	private IMatches rankMatches(IMatches matches, V rankingValue) {

		return new Ranker(matches, rankingValue).getMatches();
	}

	private boolean queryInstance(M instance) {

		return getQueryClass().isAssignableFrom(instance.getClass());
	}

	private DCell<V> getQueryOptionalRankingCell(M query) {

		return getOptionalRankingCell(getQueryClass().cast(query));
	}

	private <D extends DObject>D instantiateQuery(Class<D> type) {

		D query = dModel.instantiate(type);

		query.getFrame().resetCategory(IFrameCategory.QUERY);

		return query;
	}

	private boolean matches(DObject query, DObject instance) {

		return matcher.matches(query.getFrame(), instance.getFrame());
	}
}
