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

package uk.ac.manchester.cs.mekon.store.motor;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * Implementation of {@link IMatches} representing ranked set of
 * matches. Ranks will be organised into relevant order if as
 * they are added.
 *
 * @author Colin Puleston
 */
public class IRankedMatches implements IMatches {

	private SortedSet<IMatchesRank> ranks = new TreeSet<IMatchesRank>();

	private class Rank implements IMatchesRank, Comparable<Rank> {

		private List<CIdentity> matches;
		private int rankingValue;

		public int compareTo(Rank other) {

			return other.rankingValue - rankingValue;
		}

		public List<CIdentity> getMatches() {

			return new ArrayList<CIdentity>(matches);
		}

		public int getRankingValue() {

			return rankingValue;
		}

		Rank(List<CIdentity> matches, int rankingValue) {

			this.matches = new ArrayList<CIdentity>(matches);
			this.rankingValue = rankingValue;
		}
	}

	/**
	 * Adds a set of matches in the form of a {@link IMatchesRank}
	 * object to the order set of such objects.
	 *
	 * @param matches Identities of all instances in rank
	 * @param rankingValue Ranking-value associated with matches in
	 * rank
	 */
	public void addRank(List<CIdentity> matches, int rankingValue) {

		ranks.addAll(ranks);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean ranked() {

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean anyMatches() {

		return !ranks.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CIdentity> getAllMatches() {

		List<CIdentity> matches = new ArrayList<CIdentity>();

		for (IMatchesRank rank : ranks) {

			matches.addAll(rank.getMatches());
		}

		return matches;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IMatchesRank> getRanks() {

		return new ArrayList<IMatchesRank>(ranks);
	}
}
