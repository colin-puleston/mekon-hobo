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
	private IValue addedValue = null;
	private boolean replacedValue = false;

	private GeneralUpdateChecker directGeneralUpdateChecker
									= new GeneralUpdateChecker(true);
	private GeneralUpdateChecker indirectGeneralUpdateChecker
									= new GeneralUpdateChecker(false);

	private class GNodeState {

		private List<GNode> children;

		GNodeState(GNode node) {

			children = node.getChildren();
		}

		boolean updated(GNode node) {

			return childrenMissing(node);
		}

		boolean childrenMissing(GNode node) {

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

	private class GeneralUpdateChecker {

		private boolean directUpdatesCheck;

		GeneralUpdateChecker(boolean directUpdatesCheck) {

			this.directUpdatesCheck = directUpdatesCheck;
		}

		boolean markRequired(GNode node) {

			if (nodeStates == null || newParent(node)) {

				return false;
			}

			if (newOrChildrenMissing(node)) {

				return requiredUpdateType(node);
			}

			return node.collapsed() && markRequiredForSubTree(node);
		}

		private boolean markRequiredForSubTree(GNode node) {

			for (GNode child : node.getChildren()) {

				if (markRequiredFor(child) || markRequiredForSubTree(child)) {

					return true;
				}
			}

			return false;
		}

		private boolean markRequiredFor(GNode node) {

			return newOrUpdated(node) && requiredUpdateType(node);
		}

		private boolean requiredUpdateType(GNode node) {

			if (replacedValue && !addedValue(node)) {

				return false;
			}

			return directlyUpdated(node) == directUpdatesCheck;
		}
	}

	ITreeUpdates(GNode rootNode) {

		this.rootNode = rootNode;
	}

	void onSlotToBeUpdated(
			ISlotNode updatedSlotNode,
			IValue addedValue,
			IValue removedValue) {

		this.updatedSlotNode = updatedSlotNode;
		this.addedValue = addedValue;

		replacedValue = (addedValue != null && removedValue != null);

		updateNodeStates();
	}

	boolean directGeneralUpdateMarkRequired(GNode node) {

		return directGeneralUpdateChecker.markRequired(node);
	}

	boolean indirectGeneralUpdateMarkRequired(GNode node) {

		return indirectGeneralUpdateChecker.markRequired(node);
	}

	boolean indirectSlotValueTypeUpdateMarkRequired(ISlotNode node) {

		return nodeStates != null && updatedSlotValueType(node);
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

	private boolean newParent(GNode node) {

		GNode parent = node.getParent();

		return parent != null && nodeStates.get(parent) == null;
	}

	private boolean newOrUpdated(GNode node) {

		GNodeState state = nodeStates.get(node);

		return state == null || state.updated(node);
	}

	private boolean newOrChildrenMissing(GNode node) {

		GNodeState state = nodeStates.get(node);

		return state == null || state.childrenMissing(node);
	}

	private boolean directlyUpdated(GNode node) {

		return node.equals(updatedSlotNode) || addedValue(node);
	}

	private boolean addedValue(GNode node) {

		return addedValue != null && addedValue.equals(getValueOrNull(node));
	}

	private IValue getValueOrNull(GNode node) {

		if (node instanceof IValueNode<?>) {

			return ((IValueNode<?>)node).getValue();
		}

		if (node instanceof DisjunctionIFrameValueNode) {

			return ((DisjunctionIFrameValueNode)node).getValue();
		}

		return null;
	}

	private boolean updatedSlotValueType(ISlotNode node) {

		ISlotNodeState state = (ISlotNodeState)nodeStates.get(node);

		return state != null && state.updatedValueType(node);
	}
}
