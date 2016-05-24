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
 * Abstract base-class whose extensions provided specific types
 * of filtering for sets of {@link IMatches} being processed by
 * particlar implementations of the
 * {@link DMatcherCustomiser#processMatches} method.
 *
 * @author Colin Puleston
 */
public abstract class DMatchFilter<M extends DObject> {

	private DMatcherCustomiser<M> customiser;

	/**
	 * Constructor.
	 *
	 * @param customiser Relevant customiser
	 */
	public DMatchFilter(DMatcherCustomiser<M> customiser) {

		this.customiser = customiser;
	}

	/**
	 * Peforms filtering process on instances represented by set of
	 * match results.
	 *
	 * @param matches Raw match results
	 * @return Filtered version of match results
	 */
	public IMatches filter(IMatches matches) {

		return matches.ranked() ? filterRanked(matches) : filterUnranked(matches);
	}

	/**
	 * Determines whether the specified instance passes the filter.
	 *
	 * @param instance Instance to test
	 * @return True if instance passes filter
	 */
	protected abstract boolean pass(M instance);

	private IMatches filterRanked(IMatches matches) {

		List<IMatchesRank> filteredRanks = new ArrayList<IMatchesRank>();

		for (IMatchesRank rank : matches.getRanks()) {

			filteredRanks.add(filterRank(rank));
		}

		return IMatches.ranked(filteredRanks);
	}

	private IMatchesRank filterRank(IMatchesRank rank) {

		List<CIdentity> filteredMatches = filter(rank.getMatches());
		int rankingValue = rank.getRankingValue();

		return new IMatchesRank(filteredMatches, rankingValue);
	}

	private IMatches filterUnranked(IMatches matches) {

		return IMatches.unranked(filter(matches.getAllMatches()));
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
