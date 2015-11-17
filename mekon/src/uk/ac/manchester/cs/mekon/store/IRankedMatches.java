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

/**
 * Represents the ranked results of an instance-matching query
 * executed via an {@link IStore} object.
 *
 * @author Colin Puleston
 */
public class IRankedMatches extends IMatches {

	/**
	 * Constructs object to represent a set of un-ranked matches.
	 */
	public IRankedMatches() {

		super(true);
	}

	/**
	 * Constructs object to represent a set of ranked matches.
	 *
	 * @param ranks Ranks of matches ordered by ranking-value,
	 * highest first
	 * @throws KModelException If ranks are not strictly ordered
	 * by ranking-value
	 */
	public IRankedMatches(List<IMatchesRank> ranks) {

		super(true);

		addRanks(ranks);
	}

	/**
	 * Adds an additional rank of matches to the relevant place in the
	 * ordered list.
	 *
	 * @param rank Rank of matches to add
	 */
	public void addRank(IMatchesRank rank) {

		addSingleRank(rank);
	}
}
