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

package uk.ac.manchester.cs.mekon.user.explorer;

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

	private GeneralUpdate directUpdate = new DirectUpdate();
	private GeneralUpdate generalIndirectUpdate = new GeneralIndirectUpdate();

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

		boolean updateInSubTree(boolean direct) {

			if (updateHere(direct)) {

				return true;
			}

			for (C child : children) {

				IState<?, ?> childState = iStates.get(child);

				if (childState == null || childState.updateInSubTree(direct)) {

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

		private boolean updateHere(boolean direct) {

			return entity == updateSlot ? direct : (!direct && anyUpdateHere());
		}

		private boolean anyUpdateHere() {

			return childrenAdded() || childrenRemoved() || updatedSlotValueType();
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

	private abstract class GeneralUpdate {

		boolean show(INode node) {

			if (updateSlot == null) {

				return false;
			}

			if (showForExposedNode(node)) {

				return true;
			}

			return node.collapsed() && showForCollapsedSubTree(node);
		}

		abstract boolean direct();

		abstract boolean showForDefaultTypeExposedNode(INode node);

		abstract boolean showForIFrameDisjunctExposedNode(INode node);

		abstract boolean showForCollapsedNode(INode node);

		abstract boolean showForNewHiddenSubTree(INode node);

		private boolean showForExposedNode(INode node) {

			return iFrameDisjunctNode(node)
					? showForIFrameDisjunctExposedNode(node)
					: showForDefaultTypeExposedNode(node);
		}

		private boolean showForCollapsedSubTree(INode node) {

			return showForCollapsedNode(node) || showForHiddenSubTree(node);
		}

		private boolean showForHiddenSubTree(INode node) {

			IState<?, ?> state = getState(node);

			return state != null
					? state.updateInSubTree(direct())
					: showForNewHiddenSubTree(node);
		}

		private boolean iFrameDisjunctNode(INode node) {

			return node.getIParent() instanceof DisjunctionIFrameValueNode;
		}
	}

	private class DirectUpdate extends GeneralUpdate {

		boolean direct() {

			return true;
		}

		boolean showForDefaultTypeExposedNode(INode node) {

			return valueAdded ? addedValueNode(node) : updateSlotNode(node);
		}

		boolean showForIFrameDisjunctExposedNode(INode node) {

			if (childOfAddedValueNode(node) && lastChildNode(node)) {

				return true;
			}

			return showForDefaultTypeExposedNode(node);
		}

		boolean showForCollapsedNode(INode node) {

			return valueAdded && updateSlotNode(node);
		}

		boolean showForNewHiddenSubTree(INode node) {

			return false;
		}

		private boolean childOfAddedValueNode(INode node) {

			INode parent = node.getIParent();

			return parent != null && addedValueNode(parent);
		}
	}

	private class GeneralIndirectUpdate extends GeneralUpdate {

		boolean direct() {

			return false;
		}

		boolean showForDefaultTypeExposedNode(INode node) {

			if (updateSlotNode(node) || addedValueNode(node)) {

				return false;
			}

			IState<?, ?> state = getState(node);

			if (state == null) {

				return !defaultSlotNode(node);
			}

			return state.childrenRemoved() && !state.childrenAdded();
		}

		boolean showForIFrameDisjunctExposedNode(INode node) {

			return false;
		}

		boolean showForCollapsedNode(INode node) {

			return false;
		}

		boolean showForNewHiddenSubTree(INode node) {

			return hasDescendantValue(node.getEntity());
		}

		private boolean defaultSlotNode(INode node) {

			if (node instanceof ISlotNode) {

				return getState(node.getIParent()) == null;
			}

			return false;
		}

		private boolean hasDescendantValue(FEntity entity) {

			if (entity instanceof IFrame) {

				return anySlotValues((IFrame)entity);
			}

			if (entity instanceof ISlot) {

				return anyValues((ISlot)entity);
			}

			return false;
		}

		private boolean anySlotValues(IFrame frame) {

			for (ISlot slot : frame.getSlots().asList()) {

				if (anyValues(slot)) {

					return true;
				}
			}

			return false;
		}

		private boolean anyValues(ISlot slot) {

			return !slot.getValues().isEmpty();
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

		if (updateSlot == null) {

			return false;
		}

		IState<?, ?> state = getState(node);

		return state != null && state.updatedSlotValueType();
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

	private boolean updateSlotNode(INode node) {

		return node.getEntity() == updateSlot;
	}

	private boolean addedValueNode(INode node) {

		return valueAdded && updateSlotChild(node) && lastChildNode(node);
	}

	private boolean updateSlotChild(INode node) {

		INode parent = node.getIParent();

		return parent != null && updateSlotNode(parent);
	}

	private boolean lastChildNode(INode child) {

		List<INode> siblings = child.getIParent().getIChildren();

		return siblings.indexOf(child) == (siblings.size() - 1);
	}
}
