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

/**
 * @author Colin Puleston
 */
class ITreeUpdates {

	private IFrameNode rootNode;

	private ISlotNode updateSlotNode = null;
	private boolean valueAdded = false;
	private Map<FEntity, IState<?, ?>> iStates = new HashMap<FEntity, IState<?, ?>>();

	private abstract class IState<E extends FEntity, C extends FEntity> {

		private E entity;
		private List<C> children;

		IState(E entity, Set<IFrame> visitedFrames) {

			iStates.put(entity, this);

			this.entity = entity;
			children = getCurrentChildren();

			for (C child : children) {

				addDescendantStates(child, visitedFrames);
			}
		}

		abstract void addDescendantStates(C child, Set<IFrame> visitedFrames);

		boolean updateSlotInSubTree() {

			if (entity == getUpdateSlot()) {

				return true;
			}

			for (C child : children) {

				IState<?, ?> childState = iStates.get(child);

				if (childState != null && childState.updateSlotInSubTree()) {

					return true;
				}
			}

			return false;
		}

		boolean indirectUpdateInSubTree() {

			if (childrenAdded() || childrenRemoved() || updatedSlotValueType()) {

				return true;
			}

			for (C child : children) {

				IState<?, ?> childState = iStates.get(child);

				if (childState == null || childState.indirectUpdateInSubTree()) {

					return true;
				}
			}

			return false;
		}

		boolean childrenAdded() {

			for (C child : getCurrentChildren()) {

				if (!children.contains(child)) {

					return true;
				}
			}

			return false;
		}

		boolean childrenRemoved() {

			List<C> currentChildren = getCurrentChildren();

			for (C child : children) {

				if (!currentChildren.contains(child)) {

					return true;
				}
			}

			return false;
		}

		abstract List<C> getChildren(E entity);

		abstract boolean updatedSlotValueType(E entity);

		private List<C> getCurrentChildren() {

			return getChildren(entity);
		}

		private boolean updatedSlotValueType() {

			return updatedSlotValueType(entity);
		}
	}

	private class IFrameState extends IState<IFrame, ISlot> {

		IFrameState(IFrame frame, Set<IFrame> visitedFrames) {

			super(frame, visitedFrames);
		}

		void addDescendantStates(ISlot child, Set<IFrame> visitedFrames) {

			new ISlotState(child, visitedFrames);
		}

		List<ISlot> getChildren(IFrame frame) {

			return frame.getSlots().asList();
		}

		boolean updatedSlotValueType(IFrame frame) {

			return false;
		}
	}

	private class RootIFrameState extends IFrameState {

		RootIFrameState() {

			super(rootNode.getValue(), new HashSet<IFrame>());
		}
	}

	private class ISlotState extends IState<ISlot, IValue> {

		private CValue<?> valueType;

		ISlotState(ISlot slot, Set<IFrame> visitedFrames) {

			super(slot, visitedFrames);

			valueType = slot.getType().getValueType();
		}

		void addDescendantStates(IValue child, Set<IFrame> visitedFrames) {

			if (child instanceof IFrame) {

				IFrame frame = (IFrame)child;

				if (visitedFrames.add(frame)) {

					new IFrameState(frame, visitedFrames);
				}
			}
		}

		List<IValue> getChildren(ISlot slot) {

			return slot.getValues().asList();
		}

		boolean updatedSlotValueType(ISlot slot) {

			return !slot.getType().getValueType().equals(valueType);
		}
	}

	ITreeUpdates(IFrameNode rootNode) {

		this.rootNode = rootNode;
	}

	void initialise() {

		updateIStates();
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

		if (showDirectUpdateForExposedNode(node)) {

			return true;
		}

		return node.collapsed() && showDirectUpdateForHiddenSubTree(node);
	}

	boolean showGeneralIndirectUpdate(INode node) {

		if (updateSlotNode == null) {

			return false;
		}

		if (showIndirectUpdateForExposedNode(node)) {

			return true;
		}

		return node.collapsed() && showIndirectUpdateForHiddenSubTree(node);
	}

	boolean showValueTypeIndirectUpdate(INode node) {

		if (updateSlotNode == null) {

			return false;
		}

		IState<?, ?> state = getState(node);

		return state != null && state.updatedSlotValueType();
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

		new RootIFrameState();
	}

	private boolean showDirectUpdateForExposedNode(INode node) {

		if (valueAdded) {

			if (childOfAddedValueNode(node)) {

				return iFrameDisjunctNode(node) && lastChildNode(node);
			}

			return addedValueNode(node);
		}

		return nodeForUpdateSlot(node);
	}

	private boolean showDirectUpdateForHiddenSubTree(INode node) {

		if (valueAdded && nodeForUpdateSlot(node)) {

			return true;
		}

		IState<?, ?> state = getState(node);

		return state != null && state.updateSlotInSubTree();
	}

	private boolean showIndirectUpdateForExposedNode(INode node) {

		if (resultOfDirectUpdate(node) || iFrameDisjunctNode(node)) {

			return false;
		}

		IState<?, ?> state = getState(node);

		if (state == null) {

			return !defaultNode(node);
		}

		return state.childrenRemoved() && !state.childrenAdded();
	}

	private boolean showIndirectUpdateForHiddenSubTree(INode node) {

		if (resultOfDirectUpdate(node)) {

			return false;
		}

		IState<?, ?> state = getState(node);

		return state == null || state.indirectUpdateInSubTree();
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

		if (node instanceof ISlotNode) {

			return ((ISlotNode)node).getISlot() == getUpdateSlot();
		}

		return false;
	}

	private boolean lastChildNode(INode child) {

		List<INode> siblings = child.getIParent().getIChildren();

		return siblings.indexOf(child) == (siblings.size() - 1);
	}

	private IState<?, ?> getState(INode node) {

		return iStates.get(node.getEntity());
	}

	private ISlotValuesEditor getUpdateSlotValuesEditor() {

		return getUpdateSlot().getValuesEditor();
	}

	private ISlot getUpdateSlot() {

		return updateSlotNode.getISlot();
	}
}
