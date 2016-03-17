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

	private INode rootNode = null;

	private ISlotNode updateSlotNode = null;
	private boolean valueAdded = false;
	private Map<FEntity, IState> iStates = new HashMap<FEntity, IState>();

	private class IState {

		private List<? extends FEntity> children;

		IState(INode node) {

			children = node.getChildEntities();
		}

		boolean childrenAdded(INode node) {

			for (FEntity child : node.getChildEntities()) {

				if (!children.contains(child)) {

					return true;
				}
			}

			return false;
		}

		boolean childrenRemoved(INode node) {

			List<? extends FEntity> currentChildren = node.getChildEntities();

			for (FEntity child : children) {

				if (!currentChildren.contains(child)) {

					return true;
				}
			}

			return false;
		}

		boolean updatedSlotValueType(INode node) {

			return false;
		}
	}

	private class ISlotState extends IState {

		private CValue<?> valueType;

		ISlotState(ISlotNode node) {

			super(node);

			valueType = getSlotValueType(node);
		}

		boolean updatedSlotValueType(INode node) {

			return !getSlotValueType((ISlotNode)node).equals(valueType);
		}

		private CValue<?> getSlotValueType(ISlotNode node) {

			return node.getISlot().getType().getValueType();
		}
	}

	ITreeUpdates(INode rootNode) {

		this.rootNode = rootNode;
	}

	void update(ISlotNode slotNode, IValue valueToAdd, IValue valueToRemove) {

		updateSlotNode = slotNode;
		valueAdded = valueToAdd != null;

		updateIStates();
		updateValues(valueToAdd, valueToRemove);
	}

	boolean showDirectUpdate(INode node) {

		if (updateSlotNode == null) {

			return false;
		}

		if (valueAdded) {

			if (childOfAddedValueNode(node)) {

				return iFrameDisjunctNode(node) && lastChildNode(node);
			}

			return addedValueNode(node);
		}

		return nodeForUpdateSlot(node);
	}

	boolean showGeneralIndirectUpdate(INode node) {

		if (updateSlotNode == null) {

			return false;
		}

		if (resultOfDirectUpdate(node) || iFrameDisjunctNode(node)) {

			return false;
		}

		IState state = getState(node);

		if (state == null) {

			return !defaultNode(node);
		}

		if (state.childrenRemoved(node) && !state.childrenAdded(node)) {

			return true;
		}

		return node.collapsed() && showIndirectUpdateForHiddenSubTree(node);
	}

	boolean showValueTypeIndirectUpdate(INode node) {

		if (updateSlotNode == null) {

			return false;
		}

		IState state = getState(node);

		return state != null && state.updatedSlotValueType(node);
	}

	private void updateValues(IValue valueToAdd, IValue valueToRemove) {

		ISlotValuesEditor editor = getUpdateSlotValuesEditor();

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

	private void updateIStates() {

		iStates.clear();

		addIStates(rootNode, new HashSet<FEntity>());
	}

	private void addIStates(INode node, Set<FEntity> visitedEntities) {

		FEntity entity = node.getEntity();

		if (visitedEntities.add(entity)) {

			iStates.put(entity, createIState(node));

			for (INode child : node.getIChildren()) {

				addIStates(child, visitedEntities);
			}
		}
	}

	private IState createIState(INode node) {

		if (node instanceof ISlotNode) {

			return new ISlotState((ISlotNode)node);
		}

		return new IState(node);
	}

	private boolean showIndirectUpdateForHiddenSubTree(INode node) {

		for (INode child : node.getIChildren()) {

			if (showIndirectUpdateForHiddenNode(child)
				|| showIndirectUpdateForHiddenSubTree(child)) {

				return true;
			}
		}

		return false;
	}

	private boolean showIndirectUpdateForHiddenNode(INode node) {

		if (resultOfDirectUpdate(node)) {

			return false;
		}

		IState state = getState(node);

		if (state == null) {

			return true;
		}

		return state.childrenRemoved(node) || state.updatedSlotValueType(node);
	}

	private boolean defaultNode(INode node) {

		if (node instanceof ISlotNode) {

			return getState(node.getIParent()) == null;
		}

		return false;
	}

	private boolean iFrameDisjunctNode(INode node) {

		return node.getIParent() instanceof DisjunctionIFrameValueNode;
	}

	private boolean resultOfDirectUpdate(INode node) {

		return nodeForUpdateSlot(node)
				|| addedValueNode(node)
				|| childOfAddedValueNode(node);
	}

	private boolean childOfAddedValueNode(INode node) {

		INode parent = node.getIParent();

		return parent != null && addedValueNode(parent);
	}

	private boolean addedValueNode(INode node) {

		return valueAdded
				&& nodeForUpdateSlot(node.getIParent())
				&& lastChildNode(node);
	}

	private boolean nodeForUpdateSlot(INode node) {

		return node instanceof ISlotNode
				&& nodeForUpdateSlot((ISlotNode)node);
	}

	private boolean nodeForUpdateSlot(ISlotNode node) {

		return node.getISlot() == getUpdateSlot();
	}

	private boolean lastChildNode(INode child) {

		List<INode> siblings = child.getIParent().getIChildren();

		return siblings.indexOf(child) == (siblings.size() - 1);
	}

	private IState getState(INode node) {

		return iStates.get(node.getEntity());
	}

	private ISlotValuesEditor getUpdateSlotValuesEditor() {

		return getUpdateSlot().getValuesEditor();
	}

	private ISlot getUpdateSlot() {

		return updateSlotNode.getISlot();
	}
}
