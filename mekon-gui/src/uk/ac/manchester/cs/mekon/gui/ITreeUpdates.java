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

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class ITreeUpdates {

	private GNode rootNode = null;
	private Map<GNode, GNodeState> nodeStates = null;

	private ISlotNode updatedSlotNode = null;
	private boolean valueAdded = false;

	private class GNodeState {

		private List<GNode> children;

		GNodeState(GNode node) {

			children = node.getChildren();
		}

		boolean childrenAdded(GNode node) {

			for (GNode child : node.getChildren()) {

				if (!children.contains(child)) {

					return true;
				}
			}

			return false;
		}

		boolean childrenRemoved(GNode node) {

			List<GNode> currentChildren = node.getChildren();

			for (GNode child : children) {

				if (!currentChildren.contains(child)) {

					return true;
				}
			}

			return false;
		}

		boolean updatedSlotValueType(GNode node) {

			return false;
		}
	}

	private class ISlotNodeState extends GNodeState {

		private CValue<?> valueType;

		ISlotNodeState(ISlotNode node) {

			super(node);

			valueType = getSlotValueType(node);
		}

		boolean updatedSlotValueType(GNode node) {

			return !getSlotValueType((ISlotNode)node).equals(valueType);
		}

		private CValue<?> getSlotValueType(ISlotNode node) {

			return node.getISlot().getType().getValueType();
		}
	}

	ITreeUpdates(GNode rootNode) {

		this.rootNode = rootNode;
	}

	void update(ISlotNode slotNode, IValue valueToAdd, IValue valueToRemove) {

		updatedSlotNode = slotNode;
		valueAdded = valueToAdd != null;

		updateNodeStates();
		updateValues(valueToAdd, valueToRemove);
	}

	boolean showDirectUpdate(GNode node) {

		if (valueAdded) {

			if (childOfAddedValueNode(node)) {

				return iFrameDisjunctNode(node) && lastChildNode(node);
			}

			return addedValueNode(node);
		}

		return node == updatedSlotNode;
	}

	boolean showGeneralIndirectUpdate(GNode node) {

		if (nodeStates == null) {

			return false;
		}

		if (resultOfDirectUpdate(node) || iFrameDisjunctNode(node)) {

			return false;
		}

		GNodeState state = nodeStates.get(node);

		if (state == null) {

			return !defaultNode(node);
		}

		if (state.childrenRemoved(node) && !state.childrenAdded(node)) {

			return true;
		}

		return node.collapsed() && showIndirectUpdateForHiddenSubTree(node);
	}

	boolean showValueTypeIndirectUpdate(ISlotNode node) {

		if (nodeStates == null) {

			return false;
		}

		GNodeState state = nodeStates.get(node);

		return state != null && state.updatedSlotValueType(node);
	}

	private void updateValues(IValue valueToAdd, IValue valueToRemove) {

		ISlotValuesEditor editor = getSlotValuesEditor();

		if (valueToAdd != null || valueToRemove != null) {

			if (valueToAdd != null) {

				editor.add(valueToAdd);
			}

			if (valueToRemove != null) {

				editor.remove(valueToRemove);
			}
		}
		else {

			editor.clear();
		}
	}

	private void updateNodeStates() {

		if (nodeStates == null) {

			nodeStates = new HashMap<GNode, GNodeState>();
		}
		else {

			nodeStates.clear();
		}

		addNodeStates(rootNode);
	}

	private void addNodeStates(GNode node) {

		nodeStates.put(node, createNodeState(node));

		for (GNode child : node.getChildren()) {

			addNodeStates(child);
		}
	}

	private GNodeState createNodeState(GNode node) {

		if (node instanceof ISlotNode) {

			return new ISlotNodeState((ISlotNode)node);
		}

		return new GNodeState(node);
	}

	private boolean showIndirectUpdateForHiddenSubTree(GNode node) {

		for (GNode child : node.getChildren()) {

			if (showIndirectUpdateForHiddenNode(child)
				|| showIndirectUpdateForHiddenSubTree(child)) {

				return true;
			}
		}

		return false;
	}

	private boolean showIndirectUpdateForHiddenNode(GNode node) {

		if (resultOfDirectUpdate(node)) {

			return false;
		}

		GNodeState state = nodeStates.get(node);

		if (state == null) {

			return true;
		}

		return state.childrenRemoved(node) || state.updatedSlotValueType(node);
	}

	private boolean defaultNode(GNode node) {

		if (node instanceof ISlotNode) {

			return nodeStates.get(node.getParent()) == null;
		}

		return false;
	}

	private boolean iFrameDisjunctNode(GNode node) {

		return node.getParent() instanceof DisjunctionIFrameValueNode;
	}

	private boolean resultOfDirectUpdate(GNode node) {

		return node == updatedSlotNode
				|| addedValueNode(node)
				|| childOfAddedValueNode(node);
	}

	private boolean childOfAddedValueNode(GNode node) {

		GNode parent = node.getParent();

		return parent != null && addedValueNode(parent);
	}

	private boolean addedValueNode(GNode node) {

		return valueAdded
				&& node.getParent() == updatedSlotNode
				&& lastChildNode(node);
	}

	private boolean lastChildNode(GNode child) {

		List<GNode> siblings = child.getParent().getChildren();

		return siblings.indexOf(child) == (siblings.size() - 1);
	}

	private ISlotValuesEditor getSlotValuesEditor() {

		return updatedSlotNode.getISlot().getValuesEditor();
	}
}
