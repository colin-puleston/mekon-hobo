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

package uk.ac.manchester.cs.mekon.model;

import java.util.*;

/**
 * Represents a sub-set of the results of an instance-matching
 * query executed via an {@link IStore} object, all of which
 * have been assigned an identical ranking-value by the matching
 * mechanism.
 *
 * @author Colin Puleston
 */
public class IMatchesRank {

	private List<CIdentity> matches;
	private int rankingValue;

	/**
	 * Constructor.
	 *
	 * @param matches Identities of all instances in rank
	 * @param rankingValue Ranking-value associated with matches in
	 * rank
	 */
	public IMatchesRank(List<CIdentity> matches, int rankingValue) {

		this.matches = new ArrayList<CIdentity>(matches);
		this.rankingValue = rankingValue;
	}

	/**
	 * Provides the identities of all instances in rank.
	 *
	 * @return Identities of all instances in rank
	 */
	public List<CIdentity> getMatches() {

		return new ArrayList<CIdentity>(matches);
	}

	/**
	 * Provides the ranking-value associated with the matches in
	 * the rank.
	 *
	 * @return Ranking-value associated with matches in rank
	 */
	public int getRankingValue() {

		return rankingValue;
	}

	void resolveLabels(Map<CIdentity, String> labels) {

		List<CIdentity> resolvedMatches = new ArrayList<CIdentity>();

		for (CIdentity match : matches) {

			resolvedMatches.add(resolveLabel(match, labels.get(match)));
		}

		matches = resolvedMatches;
	}

	private CIdentity resolveLabel(CIdentity match, String label) {

		return label != null
					? new CIdentity(match.getIdentifier(), label)
					: match;
	}
}
