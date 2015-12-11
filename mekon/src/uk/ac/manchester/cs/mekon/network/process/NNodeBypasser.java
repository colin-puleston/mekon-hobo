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
 * representations in order to bypass particular nodes. The
 * specification of which nodes to bypass is left to the derived
 * class. When a node is bypassed, it is replaced in the link for
 * which it is a value, by all nodes that are values for any links
 * attached to the bypassed node.
 *
 * @author Colin Puleston
 */
public abstract class NNodeBypasser extends NNetworkVisitor {

	/**
	 * {@inheritDoc}
	 */
	protected void visit(NNode node) {
	}

	/**
	 * {@inheritDoc}
	 */
	protected void visit(NLink link) {

		for (NNode valueNode : link.getValues()) {

			if (bypass(valueNode)) {

				bypassNode(link, valueNode);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void visit(NNumeric numeric) {
	}

	/**
	 * Determines whether or not a node is to be bypassed
	 *
	 * @param node Node to test
	 * @return True if node is to be bypassed
	 */
	protected abstract boolean bypass(NNode node);

	private void bypassNode(NLink parentLink, NNode node) {

		parentLink.removeValue(node);

		for (NLink nestedLink : node.getLinks()) {

			for (NNode nestedNode : nestedLink.getValues()) {

				if (bypass(nestedNode)) {

					bypassNode(parentLink, nestedNode);
				}
				else {

					parentLink.addValue(nestedNode);
				}
			}
		}
	}
}
