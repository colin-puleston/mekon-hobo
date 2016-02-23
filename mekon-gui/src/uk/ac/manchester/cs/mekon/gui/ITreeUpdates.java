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

		boolean updated(GNode node) {

			return childrenRemoved(node);
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
	}

	private class ISlotNodeState extends GNodeState {

		private CValue<?> valueType;

		ISlotNodeState(ISlotNode node) {

			super(node);

			valueType = getValueType(node);
		}

		boolean updated(GNode node) {

			return super.updated(node) || updatedValueType((ISlotNode)node);
		}

		boolean updatedValueType(ISlotNode node) {

			return !getValueType(node).equals(valueType);
		}

		private CValue<?> getValueType(ISlotNode node) {

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

	boolean directGeneralUpdateMarkRequired(GNode node) {

		if (valueAdded) {

			GNode parent = node.getParent();

			if (parent != null && addedValueNode(parent)) {

				return iFrameDisjunctNode(node) && lastChildNode(node);
			}

			return addedValueNode(node);
		}

		return node == updatedSlotNode;
	}

	boolean indirectGeneralUpdateMarkRequired(GNode node) {

		if (node == updatedSlotNode || addedValueNode(node)) {

			return false;
		}

		if (iFrameDisjunctNode(node)) {

			return false;
		}

		if (localUpdate(node)) {

			return true;
		}

		return node.collapsed() && updatesInSubTree(node);
	}

	boolean indirectSlotValueTypeUpdateMarkRequired(ISlotNode node) {

		if (nodeStates == null) {

			return false;
		}

		ISlotNodeState state = (ISlotNodeState)nodeStates.get(node);

		return state != null && state.updatedValueType(node);
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

	private boolean localUpdate(GNode node) {

		if (nodeStates == null) {

			return false;
		}

		GNodeState state = nodeStates.get(node);

		if (state == null) {

			return node instanceof IValueNode;
		}

		return state.childrenRemoved(node) && !state.childrenAdded(node);
	}

	private boolean updatesInSubTree(GNode node) {

		for (GNode child : node.getChildren()) {

			if (newOrUpdatedNode(child) || updatesInSubTree(child)) {

				return true;
			}
		}

		return false;
	}

	private boolean newOrUpdatedNode(GNode node) {

		GNodeState state = nodeStates.get(node);

		return state == null || state.updated(node);
	}

	private boolean iFrameDisjunctNode(GNode node) {

		return node.getParent() instanceof DisjunctionIFrameValueNode;
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
