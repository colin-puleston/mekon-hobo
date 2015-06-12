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

	static final Color DIRECT_UPDATES_CLR = Color.yellow;
	static final Color INDIRECT_UPDATES_CLR = Color.cyan;

	private GNode rootNode = null;
	private Map<GNode, GNodeState> nodeStates = null;

	private ISlotNode updatedSlotNode = null;
	private IValue addedValue = null;

	private class GNodeState {

		private List<GNode> children;

		GNodeState(GNode node) {

			children = node.getChildren();
		}

		boolean updated(GNode node) {

			return missingChildren(node);
		}

		boolean missingChildren(GNode node) {

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

	void initialise(GNode rootNode) {

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

			nodeStates = new HashMap<GNode, GNodeState>();
		}
		else {

			nodeStates.clear();
		}

		addNodeStates(rootNode);
	}

	void checkMarkForGeneralUpdate(GNode node, GCellDisplay display) {

		if (requiresGeneralUpdateMark(node)) {

			markForUpdate(node, display);
		}
	}

	void checkMarkForSlotValueTypeUpdate(ISlotNode node, GCellDisplay display) {

		if (requiresSlotValueTypeUpdateMark(node)) {

			markForUpdate(node, display);
		}
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

	private boolean requiresGeneralUpdateMark(GNode node) {

		if (nodeStates == null) {

			return false;
		}

		if (hasNewParent(node)) {

			return false;
		}

		if (newOrHasMissingChildren(node)) {

			return true;
		}

		return node.collapsed() && hasNewOrUpdatedDescendants(node);
	}

	private boolean requiresSlotValueTypeUpdateMark(ISlotNode node) {

		return nodeStates != null && updatedSlotValueType(node);
	}

	private void markForUpdate(GNode node, GCellDisplay display) {

		display.setBackgroundColour(getMarkColour(node));
	}

	private Color getMarkColour(GNode node) {

		return directUpdateInTree(node)
				? DIRECT_UPDATES_CLR
				: INDIRECT_UPDATES_CLR;
	}

	private boolean directUpdateInTree(GNode node) {

		return node.equals(updatedSlotNode)
				|| addedValueNode(node)
				|| directUpdateInSubTree(node);
	}

	private boolean directUpdateInSubTree(GNode node) {

		for (GNode child : node.getChildren()) {

			if (directUpdateInTree(child)) {

				return true;
			}
		}

		return false;
	}

	private boolean addedValueNode(GNode node) {

		if (node instanceof IValueNode<?>) {

			IValueNode<?> valueNode = (IValueNode<?>)node;

			return valueNode.getValue().equals(addedValue);
		}

		return false;
	}

	private boolean hasNewParent(GNode node) {

		GNode parent = node.getParent();

		return parent != null && nodeStates.get(parent) == null;
	}

	private boolean hasNewOrUpdatedDescendants(GNode node) {

		for (GNode child : node.getChildren()) {

			if (newOrUpdated(child) || hasNewOrUpdatedDescendants(child)) {

				return true;
			}
		}

		return false;
	}

	private boolean newOrUpdated(GNode node) {

		GNodeState state = nodeStates.get(node);

		return state == null || state.updated(node);
	}

	private boolean newOrHasMissingChildren(GNode node) {

		GNodeState state = nodeStates.get(node);

		return state == null || state.missingChildren(node);
	}

	private boolean updatedSlotValueType(ISlotNode node) {

		ISlotNodeState state = (ISlotNodeState)nodeStates.get(node);

		return state != null && state.updatedValueType(node);
	}
}
