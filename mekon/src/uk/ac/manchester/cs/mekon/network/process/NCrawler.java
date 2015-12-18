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

import java.util.*;

import uk.ac.manchester.cs.mekon.network.*;

/**
 * Abstract base-class for processers that visit each node and
 * feature in a network instance representation in turn,
 * via a depth-first traversal starting from the root-node.
 *
 * @author Colin Puleston
 */
public abstract class NCrawler implements NProcessor {

	/**
	 * {@inheritDoc}
	 */
	public void process(NNode rootNode) {

		visitAll(rootNode, new HashSet<NNode>());
	}

	/**
	 * Visitor for nodes in network instance representation.
	 *
	 * @param node Visited node
	 */
	protected abstract void visit(NNode node);

	/**
	 * Visitor for node-valued links in network instance
	 * representation.
	 *
	 * @param link Visited link
	 */
	protected abstract void visit(NLink link);

	/**
	 * Visitor for number-valued links in network instance
	 * representation.
	 *
	 * @param numeric Visited numeric
	 */
	protected abstract void visit(NNumeric numeric);

	private void visitAll(NNode node, Set<NNode> visited) {

		if (visited.add(node)) {

			visit(node);

			for (NLink link : node.getLinks()) {

				visit(link);

				for (NNode value : link.getValues()) {

					visitAll(value, visited);
				}
			}

			for (NNumeric numeric : node.getNumerics()) {

				visit(numeric);
			}
		}
	}
}
