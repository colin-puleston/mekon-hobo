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
 * Implementation of {@link IMatches} representing unranked set of
 * matches.
 *
 * @author Colin Puleston
 */
public class IUnrankedMatches implements IMatches {

	private List<CIdentity> matches = new ArrayList<CIdentity>();
	private List<IMatchesRank> ranks;

	private class SingleRank implements IMatchesRank {

		public List<CIdentity> getMatches() {

			return new ArrayList<CIdentity>(matches);
		}

		public int getRankingValue() {

			return 0;
		}

		List<IMatchesRank> asRankList() {

			return Collections.singletonList(this);
		}
	}

	/**
	 * Constructor.
	 *
	 * @param matches Identities of matching instances
	 */
	public IUnrankedMatches(List<CIdentity> matches) {

		this.matches.addAll(matches);

		ranks = asZeroOrOneRanks();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean ranked() {

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean anyMatches() {

		return !matches.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CIdentity> getAllMatches() {

		return new ArrayList<CIdentity>(matches);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IMatchesRank> getRanks() {

		return ranks;
	}

	private List<IMatchesRank> asZeroOrOneRanks() {

		return matches.isEmpty() ? Collections.emptyList() : new SingleRank().asRankList();
	}
}
