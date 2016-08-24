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

	private ISlot updateSlot = null;
	private boolean valueAdded = false;

	private Map<FEntity, IState<?, ?>> iStates = new HashMap<FEntity, IState<?, ?>>();

	private UpdateType directUpdate = new DirectUpdate();
	private UpdateType generalIndirectUpdate = new GeneralIndirectUpdate();
	private UpdateType valueTypeIndirectUpdate = new ValueTypeIndirectUpdate();

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

			if (entity == updateSlot) {

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

		boolean updatesInSubTree() {

			if (childrenAdded() || childrenRemoved() || updatedSlotValueType()) {

				return true;
			}

			for (C child : children) {

				IState<?, ?> childState = iStates.get(child);

				if (childState == null || childState.updatesInSubTree()) {

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

		boolean updatedSlotValueType(E entity) {

			return false;
		}

		private List<C> getCurrentChildren() {

			return getChildren(entity);
		}

		private boolean updatedSlotValueType() {

			return updatedSlotValueType(entity);
		}
	}

	private class IAtomicValueState extends IState<IValue, FEntity> {

		IAtomicValueState(IValue frame, Set<IFrame> visitedFrames) {

			super(frame, visitedFrames);
		}

		void addDescendantStates(FEntity child, Set<IFrame> visitedFrames) {
		}

		List<FEntity> getChildren(IValue frame) {

			return Collections.<FEntity>emptyList();
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
			else {

				new IAtomicValueState(child, visitedFrames);
			}
		}

		List<IValue> getChildren(ISlot slot) {

			return slot.getValues().asList();
		}

		boolean updatedSlotValueType(ISlot slot) {

			return !slot.getType().getValueType().equals(valueType);
		}
	}

	private abstract class UpdateType {

		boolean show(INode node) {

			if (updateSlot == null) {

				return false;
			}

			if (showForExposedNode(node)) {

				return true;
			}

			return node.collapsed() && showForHiddenSubTree(node);
		}

		abstract boolean showForExposedNode(INode node);

		abstract boolean showForHiddenSubTree(INode node);
	}

	private class DirectUpdate extends UpdateType {

		boolean showForExposedNode(INode node) {

			if (valueAdded) {

				if (childOfAddedValueNode(node)) {

					return iFrameDisjunctNode(node) && lastChildNode(node);
				}

				return addedValueNode(node);
			}

			return updateSlot(node);
		}

		boolean showForHiddenSubTree(INode node) {

			if (valueAdded && updateSlot(node)) {

				return true;
			}

			IState<?, ?> state = getState(node);

			return state != null && state.updateSlotInSubTree();
		}

		private boolean childOfAddedValueNode(INode node) {

			INode parent = node.getIParent();

			return parent != null && addedValueNode(parent);
		}

		private boolean addedValueNode(INode node) {

			return valueAdded && updateSlotChild(node) && lastChildNode(node);
		}

		private boolean updateSlotChild(INode node) {

			INode parent = node.getIParent();

			return parent != null && updateSlot(parent);
		}

		private boolean updateSlot(INode node) {

			return node.getEntity() == updateSlot;
		}

		private boolean lastChildNode(INode child) {

			List<INode> siblings = child.getIParent().getIChildren();

			return siblings.indexOf(child) == (siblings.size() - 1);
		}
	}

	private class GeneralIndirectUpdate extends UpdateType {

		boolean showForExposedNode(INode node) {

			if (iFrameDisjunctNode(node)) {

				return false;
			}

			IState<?, ?> state = getState(node);

			if (state == null) {

				return !defaultSlotNode(node);
			}

			return state.childrenRemoved() && !state.childrenAdded();
		}

		boolean showForHiddenSubTree(INode node) {

			IState<?, ?> state = getState(node);

			if (state == null) {

				return !node.getChildEntities().isEmpty();
			}

			return state.updatesInSubTree();
		}

		private boolean defaultSlotNode(INode node) {

			if (node instanceof ISlotNode) {

				return getState(node.getIParent()) == null;
			}

			return false;
		}
	}

	private class ValueTypeIndirectUpdate extends UpdateType {

		boolean showForExposedNode(INode node) {

			IState<?, ?> state = getState(node);

			return state != null && state.updatedSlotValueType();
		}

		boolean showForHiddenSubTree(INode node) {

			return false;
		}
	}

	ITreeUpdates(IFrameNode rootNode) {

		this.rootNode = rootNode;
	}

	void initialise() {

		updateIStates();
	}

	void update(ISlotNode slotNode, IValue valueToAdd, IValue valueToRemove) {

		updateSlot = slotNode.getISlot();
		valueAdded = valueToAdd != null;

		updateIStates();
		updateValues(valueToAdd, valueToRemove);
	}

	boolean showDirectUpdate(INode node) {

		return directUpdate.show(node);
	}

	boolean showGeneralIndirectUpdate(INode node) {

		return generalIndirectUpdate.show(node);
	}

	boolean showValueTypeIndirectUpdate(INode node) {

		return valueTypeIndirectUpdate.show(node);
	}

	private void updateValues(IValue valueToAdd, IValue valueToRemove) {

		ISlotValuesEditor editor = updateSlot.getValuesEditor();

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

	private IState<?, ?> getState(INode node) {

		return iStates.get(node.getEntity());
	}

	private boolean iFrameDisjunctNode(INode node) {

		return node.getIParent() instanceof DisjunctionIFrameValueNode;
	}
}
