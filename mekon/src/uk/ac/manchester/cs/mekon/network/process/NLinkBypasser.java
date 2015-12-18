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

package uk.ac.manchester.cs.mekon.network.process;

import uk.ac.manchester.cs.mekon.network.*;

/**
 * Abstract processer that modifies the network-based instance
 * representations in order to bypass particular links. The
 * specification of which links to bypass is left to the derived
 * class. When a link is bypassed, it is replaced on the node to
 * which it is attached, by all links that are attached to the
 * nodes that are values for the bypassed link.
 *
 * @author Colin Puleston
 */
public abstract class NLinkBypasser extends NCrawler {

	/**
	 * {@inheritDoc}
	 */
	protected void visit(NNode node) {

		for (NLink link : node.getLinks()) {

			if (bypass(link)) {

				bypassLink(node, link);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void visit(NLink link) {
	}

	/**
	 * {@inheritDoc}
	 */
	protected void visit(NNumeric numeric) {
	}

	/**
	 * Determines whether or not a link is to be bypassed
	 *
	 * @param link Link to test
	 * @return True if link is to be bypassed
	 */
	protected abstract boolean bypass(NLink link);

	private void bypassLink(NNode node, NLink link) {

		node.removeFeature(link);

		for (NNode valueNode : link.getValues()) {

			for (NLink nestedLink : valueNode.getLinks()) {

				if (bypass(nestedLink)) {

					bypassLink(node, nestedLink);
				}
				else {

					node.addFeature(nestedLink);
				}
			}
		}
	}
}
