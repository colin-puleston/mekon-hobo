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

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents the results of an instance-matching query executed
 * via an {@link IStore} object. The set of results can optionally
 * be ranked to reflect some measure of the degree to which each
 * result matches the query.
 *
 * @author Colin Puleston
 */
public interface IMatches {

	/**
	 * Specifies whether matches are ranked.
	 *
	 * @return True if matches are ranked
	 */
	public boolean ranked();

	/**
	 * Specifies whether any matches have been found.
	 *
	 * @return True if matches found
	 */
	public boolean anyMatches();

	/**
	 * Provides a list of all instances that match the relevant
	 * query. If the matches have been ranked then those with a
	 * greater ranking-value will appear earlier in the list.
	 *
	 * @return Identities of all relevant instances
	 */
	public List<CIdentity> getAllMatches();

	/**
	 * Provides a list of the ranks of matches ordered by
	 * ranking-value, highest first. If the matches are not ranked
	 * then they will be provided as a single rank with a
	 * ranking-value of zero.
	 *
	 * @return Ranks of matches ordered by ranking-value, highest
	 * first
	 */
	public List<IMatchesRank> getRanks();
}
