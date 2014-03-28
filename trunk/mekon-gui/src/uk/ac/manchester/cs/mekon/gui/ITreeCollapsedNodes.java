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

package uk.ac.manchester.cs.mekon.gui;

import java.util.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class ITreeCollapsedNodes {

	private GNode rootNode;
	private Map<GNode, Set<GNodeState>> collapsedsToDescendants
							= new HashMap<GNode, Set<GNodeState>>();

	private class GNodeState {

		private GNode node;
		private String label;

		public boolean equals(Object other) {

			GNodeState o = (GNodeState)other;

			return node.equals(o.node) && label.equals(o.label);
		}

		public int hashCode() {

			return node.hashCode() + label.hashCode();
		}

		GNodeState(GNode node) {

			this.node = node;

			label = node.getLabel();
		}
	}

	ITreeCollapsedNodes(GNode rootNode) {

		this.rootNode = rootNode;
	}

	void update(GNode updated) {

		collapsedsToDescendants.clear();

		findAll(rootNode, updated);
	}

	void restore() {

		restore(rootNode);
	}

	boolean updatedDescendants(GNode node) {

		Set<GNodeState> start = getDescendants(node);

		return start != null && !start.equals(findDescendants(node));
	}

	private void findAll(GNode current, GNode updated) {

		if (current.collapsed()) {

			add(current);
		}

		for (GNode child : current.getChildren()) {

			if (!child.equals(updated)) {

				findAll(child, updated);
			}
		}
	}

	private void restore(GNode current) {

		if (current.expanded()) {

			if (collapsed(current)) {

				collapseDescendantsLowestFirst(current);
				current.collapse();
			}
			else {

				for (GNode child : current.getChildren()) {

					restore(child);
				}
			}
		}
	}

	private void collapseDescendantsLowestFirst(GNode node) {

		for (GNode child : node.getChildren()) {

			collapseDescendantsLowestFirst(child);
			node.collapse();
		}
	}

	private void add(GNode node) {

		collapsedsToDescendants.put(node, findDescendants(node));
	}

	private boolean collapsed(GNode node) {

		return collapsedsToDescendants.containsKey(node);
	}

	private Set<GNodeState> getDescendants(GNode node) {

		return collapsedsToDescendants.get(node);
	}

	private Set<GNodeState> findDescendants(GNode node) {

		Set<GNodeState> descendants = new HashSet<GNodeState>();

		for (GNode child : node.getChildren()) {

			descendants.add(new GNodeState(child));
			descendants.addAll(findDescendants(child));
		}

		return descendants;
	}
}
