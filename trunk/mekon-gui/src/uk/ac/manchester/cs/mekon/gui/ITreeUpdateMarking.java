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
class ITreeUpdateMarking {

	private GNode rootNode;
	private Map<GNode, GNodeState> nodeStates = null;

	private class GNodeState {

		private String label;
		private List<GNode> children;

		GNodeState(GNode node) {

			label = node.getLabel();
			children = node.getChildren();
		}

		boolean updated(GNode node) {

			return !label.equals(node.getLabel()) || missingChildren(node);
		}

		private boolean missingChildren(GNode node) {

			List<GNode> currentChildren = node.getChildren();

			for (GNode child : children) {

				if (!currentChildren.contains(child)) {

					return true;
				}
			}

			return false;
		}
	}

	ITreeUpdateMarking(GNode rootNode) {

		this.rootNode = rootNode;
	}

	void update() {

		if (nodeStates == null) {

			nodeStates = new HashMap<GNode, GNodeState>();
		}
		else {

			nodeStates.clear();
		}

		addNodeStates(rootNode);
	}

	boolean requiresUpdateMarker(GNode node) {

		if (nodeStates == null) {

			return false;
		}

		if (newParent(node)) {

			return false;
		}

		if (newOrUpdated(node)) {

			return true;
		}

		return node.collapsed() && newOrUpdatedDescendants(node);
	}

	private void addNodeStates(GNode node) {

		nodeStates.put(node, new GNodeState(node));

		for (GNode child : node.getChildren()) {

			addNodeStates(child);
		}
	}

	private boolean newParent(GNode node) {

		GNode parent = node.getParent();

		return parent != null && nodeStates.get(parent) == null;
	}

	private boolean newOrUpdatedDescendants(GNode node) {

		for (GNode child : node.getChildren()) {

			if (newOrUpdated(child) || newOrUpdatedDescendants(child)) {

				return true;
			}
		}

		return false;
	}

	private boolean newOrUpdated(GNode node) {

		GNodeState state = nodeStates.get(node);

		return state == null || state.updated(node);
	}
}
