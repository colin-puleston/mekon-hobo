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
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * Renderer for the standard XML serialisation of a {@link IMatches}
 * object.
 *
 * @author Colin Puleston
 */
public class IMatchesRenderer extends IMatchesSerialiser {

	/**
	 * Renders a matches-object to produce an XML document.
	 *
	 * @param matches Matches object to render
	 * @return Rendered document
	 */
	static public XDocument render(IMatches matches) {

		XDocument document = new XDocument(MATCHES_ID);

		renderToNode(matches, document.getRootNode());

		return document;
	}

	/**
	 * Renders a matches-object to an appropriately tagged child
	 * of the specified parent-node.
	 *
	 * @param matches Matches object to render
	 * @param parentNode Parent-node for rendering
	 */
	static public void render(IMatches matches, XNode parentNode) {

		renderToNode(matches, parentNode.addChild(MATCHES_ID));
	}

	static private void renderToNode(IMatches matches, XNode node) {

		node.setValue(RANKED_ATTR, matches.ranked());

		for (IMatchesRank rank : matches.getRanks()) {

			renderRank(rank, node.addChild(RANK_ID));
		}
	}

	static private void renderRank(IMatchesRank rank, XNode node) {

		node.setValue(RANK_VALUE_ATTR, rank.getRankingValue());

		renderMatchIds(rank.getMatches(), node);
	}

	static private void renderMatchIds(List<CIdentity> matches, XNode node) {

		FSerialiser.renderIdentities(matches, node, MATCH_ID);
	}

	static private void renderIdentity(CIdentity identity, XNode node) {

		FSerialiser.renderIdentity(identity, node);
	}
}
