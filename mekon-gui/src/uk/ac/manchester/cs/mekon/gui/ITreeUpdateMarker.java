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

import java.awt.Color;
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class ITreeUpdateMarker {

	static final Color DIRECT_UPDATES_CLR = Color.blue;
	static final Color INDIRECT_UPDATES_CLR = Color.green.darker().darker();

	private INode rootNode = null;
	private Map<INode, GNodeState> nodeStates = null;

	private ISlotNode updatedSlotNode = null;
	private IValue addedValue = null;

	private class GNodeState {

		private String label;
		private List<INode> children;

		GNodeState(INode node) {

			label = getLabel(node);
			children = getChildren(node);
		}

		boolean updated(INode node) {

			return !label.equals(getLabel(node)) || missingChildren(node);
		}

		private boolean missingChildren(INode node) {

			List<INode> currentChildren = getChildren(node);

			for (INode child : children) {

				if (!currentChildren.contains(child)) {

					return true;
				}
			}

			return false;
		}
	}

	void initialise(INode rootNode) {

		this.rootNode = rootNode;
	}

	void registerAction(ISlotNode updatedSlotNode, IValue addedValue) {

		this.updatedSlotNode = updatedSlotNode;
		this.addedValue = addedValue;
	}

	void update() {

		if (rootNode == null) {

			return;
		}

		if (nodeStates == null) {

			nodeStates = new HashMap<INode, GNodeState>();
		}
		else {

			nodeStates.clear();
		}

		addNodeStates(rootNode);
	}

	void checkMark(INode node, GCellDisplay display) {

		if (requiresMark(node)) {

			display.setTextColour(getMarkColour(node));
		}
	}

	private void addNodeStates(INode node) {

		nodeStates.put(node, new GNodeState(node));

		for (INode child : getChildren(node)) {

			addNodeStates(child);
		}
	}

	private boolean requiresMark(INode node) {

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

	private Color getMarkColour(INode node) {

		return directlyUpdated(node) ? DIRECT_UPDATES_CLR : INDIRECT_UPDATES_CLR;
	}

	private boolean directlyUpdated(INode node) {

		if (node.equals(updatedSlotNode)) {

			return true;
		}

		if (node instanceof IValueNode) {

			IValueNode<?> valueNode = (IValueNode)node;

			return valueNode.getValue().equals(addedValue);
		}

		return false;
	}

	private boolean newParent(INode node) {

		INode parent = (INode)node.getParent();

		return parent != null && nodeStates.get(parent) == null;
	}

	private boolean newOrUpdatedDescendants(INode node) {

		for (INode child : getChildren(node)) {

			if (newOrUpdated(child) || newOrUpdatedDescendants(child)) {

				return true;
			}
		}

		return false;
	}

	private boolean newOrUpdated(INode node) {

		GNodeState state = nodeStates.get(node);

		return state == null || state.updated(node);
	}

	private String getLabel(INode node) {

		return node.getDefaultDisplay().getText();
	}

	private List<INode> getChildren(INode node) {

		List<INode> children = new ArrayList<INode>();

		for (GNode child : node.getChildren()) {

			children.add((INode)child);
		}

		return children;
	}
}
