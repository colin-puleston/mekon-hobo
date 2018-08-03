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

package uk.ac.manchester.cs.mekon.store.disk;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class InstanceIndexes extends KIndexes<CIdentity> implements IMatcherIndexes {

	public CIdentity getIdentity(int index) {

		return getElement(index);
	}

	public List<CIdentity> getIdentities(List<Integer> indexes) {

		return getElements(indexes);
	}

	protected KRuntimeException createException(String message) {

		return new KSystemConfigException(message);
	}

	IMatches ensureOriginalLabelsInMatches(IMatches matches) {

		if (matches.ranked()) {

			return ensureOriginalLabelsInRankedMatches(matches);
		}

		return IMatches.unranked(ensureOriginalLabels(matches.getAllMatches()));
	}

	private IMatches ensureOriginalLabelsInRankedMatches(IMatches matches) {

		List<IMatchesRank> updatedRanks = new ArrayList<IMatchesRank>();

		for (IMatchesRank rank : matches.getRanks()) {

			updatedRanks.add(ensureOriginalLabelsInMatchesRank(rank));
		}

		return IMatches.ranked(updatedRanks);
	}

	private IMatchesRank ensureOriginalLabelsInMatchesRank(IMatchesRank rank) {

		List<CIdentity> updatedIds = ensureOriginalLabels(rank.getMatches());

		return new IMatchesRank(updatedIds, rank.getRankingValue());
	}

	private List<CIdentity> ensureOriginalLabels(List<CIdentity> ids) {

		List<CIdentity> updatedIds = new ArrayList<CIdentity>();

		for (CIdentity id : ids) {

			updatedIds.add(ensureOriginalLabel(id));
		}

		return ids;
	}

	private CIdentity ensureOriginalLabel(CIdentity id) {

		return getIdentity(getIndex(id));
	}
}
