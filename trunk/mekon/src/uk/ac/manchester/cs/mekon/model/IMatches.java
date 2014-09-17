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
 * Represents the results of an instance-matching query
 * executed via an {@link IStore} object.
 *
 * @author Colin Puleston
 */
public class IMatches {

	/**
	 * Object representing no matches.
	 */
	static public final IMatches NO_MATCHES = new IMatches();

	private List<CIdentity> matches;
	private boolean ranked;

	/**
	 * Constructor.
	 *
	 * @param matches Identities of all matching instances (see
	 * {@link #getMatches}
	 * @param ranked True if matches-list is ranked (see
	 * {@link #ranked}
	 */
	public IMatches(List<CIdentity> matches, boolean ranked) {

		this.matches = new ArrayList<CIdentity>(matches);
		this.ranked = ranked;
	}

	/**
	 * Specifies whether any matches have been found.
	 *
	 * @return True if matches found
	 */
	public boolean anyMatches() {

		return !matches.isEmpty();
	}

	/**
	 * Provides the identities of all instances that match the
	 * relevant query.
	 *
	 * @return Identities of all relevant instances
	 */
	public List<CIdentity> getMatches() {

		return new ArrayList<CIdentity>(matches);
	}

	/**
	 * Specifies whether order of matches-list represents some
	 * form of relevance ranking (most-relevant first).
	 *
	 * @return True if matches-list is ranked
	 */
	public boolean ranked() {

		return ranked;
	}

	private IMatches() {

		this(Collections.<CIdentity>emptyList(), false);
	}
}
