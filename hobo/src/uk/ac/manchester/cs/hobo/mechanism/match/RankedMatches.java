package uk.ac.manchester.cs.hobo.mechanism.match;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class RankedMatches {

	private Map<CIdentity, Match> matches = new HashMap<CIdentity, Match>();

	private class Match implements Comparable<Match> {

		private CIdentity identity;
		private Integer score = 0;

		public int compareTo(Match other) {

			int c = other.score - score;

			if (c == 0) {

				c = compareLabels(other);

				if (c == 0) {

					c = compareIdentifiers(other);
				}
			}

			return c;
		}

		Match(CIdentity identity) {

			this.identity = identity;
		}

		CIdentity getIdentity() {

			return identity;
		}

		void incrementScore() {

			score++;
		}

		int getScore() {

			return score;
		}

		private int compareIdentifiers(Match other) {

			return getIdentifier().compareTo(other.getIdentifier());
		}

		private int compareLabels(Match other) {

			return getLabel().compareTo(other.getLabel());
		}

		private String getIdentifier() {

			return identity.getIdentifier();
		}

		private String getLabel() {

			return identity.getLabel();
		}
	}

	private class Rank {

		private int score;
		private List<CIdentity> ids = new ArrayList<CIdentity>();

		Rank(int score) {

			this.score = score;
		}

		Rank checkAddAndReplace(IRankedMatches iMatches, Match nextMatch) {

			Rank nextRank = this;
			int nextScore = nextMatch.getScore();

			if (nextScore != score) {

				if (!ids.isEmpty()) {

					add(iMatches);
				}

				nextRank = new Rank(nextScore);
			}

			nextRank.ids.add(nextMatch.getIdentity());

			return nextRank;
		}

		void add(IRankedMatches iMatches) {

			iMatches.addNextRank(new IMatchesRank(ids, score));
		}
	}

	RankedMatches(IMatches unrankedMatches) {

		for (CIdentity matchId : unrankedMatches.getAllMatches()) {

			matches.put(matchId, new Match(matchId));
		}
	}

	void incrementScore(CIdentity matchId) {

		matches.get(matchId).incrementScore();
	}

	Set<CIdentity> getMatches() {

		return matches.keySet();
	}

	IMatches toIMatches() {

		IRankedMatches iMatches = new IRankedMatches();
		Rank rank = new Rank(-1);

		for (Match match : getSortedMatches()) {

			rank = rank.checkAddAndReplace(iMatches, match);
		}

		rank.add(iMatches);

		return iMatches;
	}

	private SortedSet<Match> getSortedMatches() {

		SortedSet<Match> sorted = new TreeSet<Match>();

		sorted.addAll(matches.values()) ;

		return sorted;
	}
}
