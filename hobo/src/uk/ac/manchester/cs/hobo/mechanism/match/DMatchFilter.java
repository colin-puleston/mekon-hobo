package uk.ac.manchester.cs.hobo.mechanism.match;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * @author Colin Puleston
 */
public abstract class DMatchFilter<M extends DObject> {

	private DMatcherCustomiser<M, ?> customiser;

	DMatchFilter(DMatcherCustomiser<M, ?> customiser) {

		this.customiser = customiser;
	}

	public IMatches filter(IMatches matches) {

		return matches.ranked()
				? filterRanked(matches)
				: filterUnranked(matches);
	}

	protected abstract boolean pass(M instance);

	private IMatches filterRanked(IMatches matches) {

		IRankedMatches filtered = new IRankedMatches();

		for (IMatchesRank rank : matches.getRanks()) {

			filtered.addNextRank(filterRank(rank));
		}

		return filtered;
	}

	private IMatchesRank filterRank(IMatchesRank rank) {

		List<CIdentity> filteredMatches = filter(rank.getMatches());
		int rankingValue = rank.getRankingValue();

		return new IMatchesRank(filteredMatches, rankingValue);
	}

	private IMatches filterUnranked(IMatches matches) {

		return new IMatches(filter(matches.getAllMatches()));
	}

	private List<CIdentity> filter(List<CIdentity> all) {

		List<CIdentity> filtered = new ArrayList<CIdentity>();

		for (CIdentity id : all) {

			if (pass(customiser.getStoredInstance(id))) {

				filtered.add(id);
			}
		}

		return filtered;
	}
}
