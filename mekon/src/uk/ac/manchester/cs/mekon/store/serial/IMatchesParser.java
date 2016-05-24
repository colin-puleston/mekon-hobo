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

package uk.ac.manchester.cs.mekon.store.serial;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * Parser for the standard XML serialisation of a {@link IMatches}
 * object.
 *
 * @author Colin Puleston
 */
public class IMatchesParser extends IMatchesSerialiser {

	/**
	 * Parses a matches object from the root-node of the specified
	 * XML document.
	 *
	 * @param document Document for parsing
	 * @return Generated matches object
	 */
	public IMatches parse(XDocument document) {

		return parseMatches(document.getRootNode());
	}

	/**
	 * Parses a matches object from the appropriately tagged child
	 * of the specified parent-node.
	 *
	 * @param parentNode Parent of root-node for parsing
	 * @return Generated matches object
	 */
	public IMatches parse(XNode parentNode) {

		return parseMatches(parentNode.getChild(MATCHES_ID));
	}

	private IMatches parseMatches(XNode node) {

		boolean ranked = node.getBoolean(RANKED_ATTR);

		return ranked ? parseRanked(node) : parseUnranked(node);
	}

	private IMatches parseUnranked(XNode node) {

		return IMatches.unranked(parseMatchIds(node.getChild(RANK_ID)));
	}

	private IMatches parseRanked(XNode node) {

		List<IMatchesRank> ranks = new ArrayList<IMatchesRank>();

		for (XNode rankNode : node.getChildren(RANK_ID)) {

			ranks.add(parseRank(rankNode));
		}

		return IMatches.ranked(ranks);
	}

	private IMatchesRank parseRank(XNode node) {

		int rankValue = node.getInteger(RANK_VALUE_ATTR);

		return new IMatchesRank(parseMatchIds(node), rankValue);
	}

	private List<CIdentity> parseMatchIds(XNode node) {

		List<CIdentity> ids = new ArrayList<CIdentity>();

		for (XNode idNode : node.getChildren(MATCH_ID)) {

			ids.add(parseIdentity(idNode));
		}

		return ids;
	}
}
