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

package uk.ac.manchester.cs.mekon.store;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents the results of an instance-matching query executed
 * via an {@link IStore} object. The set of results can optionally
 * be ranked to reflect some measure of the degree to which each
 * result matches the query (such a set can be specified via the
 * {@link IRankedMatches} class, which extends this one).
 *
 * @author Colin Puleston
 */
public class IMatches {

	/**
	 * Object representing no matches.
	 */
	static public final IMatches NO_MATCHES = new IMatches();

	private List<IMatchesRank> ranks = new ArrayList<IMatchesRank>();
	private boolean ranked;

	/**
	 * Constructs object to represent a set of un-ranked matches.
	 *
	 * @param matches Identities of all matching instances
	 */
	public IMatches(List<CIdentity> matches) {

		this(false);

		addRank(new IMatchesRank(matches, 0));
	}

	/**
	 * Specifies whether matches are ranked.
	 *
	 * @return True if matches are ranked
	 */
	public boolean ranked() {

		return ranked;
	}

	/**
	 * Specifies whether any matches have been found.
	 *
	 * @return True if matches found
	 */
	public boolean anyMatches() {

		return !ranks.isEmpty();
	}

	/**
	 * Provides a list of all instances that match the relevant
	 * query. If the matches have been ranked then those with a
	 * greater ranking-value will appear earlier in the list.
	 *
	 * @return Identities of all relevant instances
	 */
	public List<CIdentity> getAllMatches() {

		List<CIdentity> matches = new ArrayList<CIdentity>();

		for (IMatchesRank rank : ranks) {

			matches.addAll(rank.getMatches());
		}

		return matches;
	}

	/**
	 * Provides a list of the ranks of matches ordered by
	 * ranking-value, highest first. If the matches are not ranked
	 * then they will be provided as a single rank with a
	 * ranking-value of zero.
	 *
	 * @return Ranks of matches ordered by ranking-value, highest
	 * first
	 */
	public List<IMatchesRank> getRanks() {

		return new ArrayList<IMatchesRank>(ranks);
	}

	IMatches(boolean ranked) {

		this.ranked = ranked;
	}

	void addRank(IMatchesRank rank) {

		if (!ranks.isEmpty()) {

			IMatchesRank previousRank = ranks.get(ranks.size() - 1);

			if (rank.getRankingValue() >= previousRank.getRankingValue()) {

				throw new KModelException(
							"Attempting to add rank whose ranking-value "
							+ "is not greater than that of last added rank");
			}
		}

		ranks.add(rank);
	}

	private IMatches() {

		this(Collections.<CIdentity>emptyList());
	}
}
