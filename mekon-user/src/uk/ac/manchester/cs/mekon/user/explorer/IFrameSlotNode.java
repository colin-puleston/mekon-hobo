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

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class IFrameSlotNode extends FFrameSlotNode<IFrame> {

	private ITree tree;
	private ISlot slot;

	private class CrossLinkStartAction extends ISlotNodeAction {

		void performDefaultAction() {

			tree.getCrossLinks().checkStartLinking(IFrameSlotNode.this);
		}
	}

	private class CrossLinkEndAction extends ISlotNodeAction {

		private IFrame value;

		CrossLinkEndAction(IFrame value) {

			this.value = value;
		}

		void performDefaultAction() {
		}

		void performCrossLinkAction() {

			tree.getCrossLinks().checkCreateLink(value);
		}
	}

	private class CrossLinkDisplayStartAction extends ISlotNodeAction {

		private IFrameNode displayNode;

		CrossLinkDisplayStartAction(IFrameNode displayNode) {

			this.displayNode = displayNode;
		}

		void performDefaultAction() {

			tree.getCrossLinks().checkStartShowingLinked(displayNode);
		}
	}

	private class AddIFrameDisjunctAction extends ITreeNodeAction {

		private IFrame value;

		AddIFrameDisjunctAction(IFrame value) {

			super(tree);

			this.value = value;
		}

		void performDefaultAction() {

			IFrame disjunct = checkObtainIFrameValue();

			if (!disjunct.equals(value)) {

				IFrame disjunction = instantiateDisjunction();

				replaceValue(value, disjunction);

				addDisjunct(disjunction, value);
				addDisjunct(disjunction, disjunct);
			}
		}

		private IFrame instantiateDisjunction() {

			return getValueType().instantiateDisjunction();
		}

		private void addDisjunct(IFrame disjunction, IFrame disjunct) {

			getDisjunctsEditor(disjunction).add(disjunct);
		}

		private ISlotValuesEditor getDisjunctsEditor(IFrame disjunction) {

			return disjunction.getDisjunctsSlot().getValuesEditor();
		}
	}

	private class AtomicValueNode extends IFrameNode {

		protected GNodeAction getPositiveAction1() {

			if (tree.getCrossLinks().linking()) {

				return new CrossLinkEndAction(getValue());
			}

			return getCFrameAdditionAction(getValue());
		}

		protected GNodeAction getPositiveAction2() {

			return getIFrameAdditionAction(getValue());
		}

		protected GNodeAction getPositiveAction3() {

			return new CrossLinkDisplayStartAction(this);
		}

		protected GNodeAction getNegativeAction1() {

			return getFFrameRemovalAction(getValue());
		}

		AtomicValueNode(IFrame value) {

			super(tree, value);
		}
	}

	protected boolean autoExpand() {

		return false;
	}

	protected GNodeAction getPositiveAction3() {

		return new CrossLinkStartAction();
	}

	IFrameSlotNode(ITree tree, ISlot slot) {

		super(tree, slot);

		this.tree = tree;
		this.slot = slot;
	}

	GNode createValueNode(IValue value) {

		IFrame frameValue = (IFrame)value;

		if (frameValue.getCategory().disjunction()) {

			return new DisjunctionIFrameValueNode(tree, frameValue);
		}

		return new AtomicValueNode(frameValue);
	}

	IValue checkObtainValue() {

		return checkObtainIFrameValue();
	}

	String getCFrameRole() {

		return "Value-Type";
	}

	CFrame getRootCFrame() {

		return getValueType();
	}

	CFrame valueToCFrame(IFrame value) {

		return value.getType();
	}

	IFrame checkUpdateValue(IFrame value, CFrame updatedCFrame) {

		IFrame newValue = instantiate(updatedCFrame);

		copyAssertedSlotValues(value, newValue);

		return newValue;
	}

	boolean addIFrameDisjunctActionRequired() {

		return queryInstantiation() && abstractEditableSlot();
	}

	CFrame getValueType() {

		return (CFrame)slot.getValueType();
	}

	private GNodeAction getIFrameAdditionAction(IFrame value) {

		return addIFrameDisjunctActionRequired()
				? new AddIFrameDisjunctAction(value)
				: GNodeAction.INERT_ACTION;
	}

	private IFrame checkObtainIFrameValue() {

		CFrameInstances instances = getInstances();

		return instances.any()
				? getInstantiationSelectionOrNull(instances)
				: checkObtainAndInstantiateType();
	}

	private IFrame getInstantiationSelectionOrNull(CFrameInstances instances) {

		return new CFrameInstantiationSelector(
					tree,
					getRootCFrame(),
					instances,
					getInstantiationFunction())
						.getInstantiationOrNull();
	}

	private IFrame checkObtainAndInstantiateType() {

		CFrame type = checkObtainCFrameAddition();

		return type != null ? instantiate(type) : null;
	}

	private void copyAssertedSlotValues(IFrame from, IFrame to) {

		ISlots toSlots = to.getSlots();

		for (ISlot slot : from.getSlots().asList()) {

			List<IValue> values = slot.getValues().getAssertedValues();

			if (!values.isEmpty()) {

				CIdentity id = slot.getType().getIdentity();

				if (toSlots.containsValueFor(id)) {

					toSlots.get(id).getValuesEditor().addAll(values);
				}
			}
		}
	}

	private CFrameInstances getInstances() {

		return new CFrameInstances(getRootCFrame(), tree.getStoreActions());
	}

	private IFrame instantiate(CFrame type) {

		return type.instantiate(slot.getContainer().getFunction());
	}

	private boolean queryInstantiation() {

		return getInstantiationFunction().query();
	}

	private IFrameFunction getInstantiationFunction() {

		return slot.getContainer().getFunction();
	}

	private boolean abstractEditableSlot() {

		return slot.getEditability().abstractEditable();
	}
}
