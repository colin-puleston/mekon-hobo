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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * Renderer for the standard XML serialisation of a
 * {@link IMatches} object.
 *
 * @author Colin Puleston
 */
public class IMatchesRenderer extends CIdentitySerialiser {

	static final String MATCHES_ID = "Matches";
	static final String RANK_ID = "Rank";
	static final String MATCH_ID = "Match";

	static final String RANKED_ATTR = "ranked";
	static final String RANK_VALUE_ATTR = "rankValue";

	/**
	 * Renders the specified matches-object to produce an XML
	 * document.
	 *
	 * @param matches Matches-object to render
	 * @return Rendered document
	 */
	public XDocument render(IMatches matches) {

		XDocument document = new XDocument(MATCHES_ID);

		renderMatches(matches, document.getRootNode());

		return document;
	}

	/**
	 * Renders the specified matches-object to the specified
	 * parent-node.
	 *
	 * @param matches Matches-object to render
	 * @param parentNode Parent-node for rendering
	 */
	public void render(IMatches matches, XNode parentNode) {

		renderMatches(matches, parentNode.addChild(MATCHES_ID));
	}

	private void renderMatches(IMatches matches, XNode node) {

		node.addValue(RANKED_ATTR, matches.ranked());

		for (IMatchesRank rank : matches.getRanks()) {

			renderRank(rank, node.addChild(RANK_ID));
		}
	}

	private void renderRank(IMatchesRank rank, XNode node) {

		node.addValue(RANK_VALUE_ATTR, rank.getRankingValue());

		for (CIdentity match : rank.getMatches()) {

			renderIdentity(match, node.addChild(MATCH_ID));
		}
	}
}
