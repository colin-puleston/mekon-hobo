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
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class IFrameSlotNode extends FFrameSlotNode<IFrame> {

	private ITree tree;
	private ISlot slot;

	private class AtomicValueNode extends IFrameNode {

		protected GNodeAction getPositiveAction1() {

			return getCFrameAdditionAction(getValue());
		}

		protected GNodeAction getPositiveAction2() {

			return getIFrameAdditionAction(getValue());
		}

		protected GNodeAction getNegativeAction1() {

			return getFFrameRemovalAction(getValue());
		}

		AtomicValueNode(IFrame value) {

			super(tree, value);
		}
	}

	private class DisjunctionValueNode extends IFrameNode {

		protected GNodeAction getNegativeAction1() {

			return getRemoveValueAction(getValue());
		}

		DisjunctionValueNode(IFrame value) {

			super(tree, value);
		}

		void replaceIfSingleDisjunct() {

			IFrame disjunctionValue = getValue();
			List<IFrame> disjuncts = disjunctionValue.asDisjuncts();

			if (disjuncts.size() == 1) {

				replaceValue(disjunctionValue, disjuncts.get(0));
			}
		}
	}

	private class AddIFrameDisjunctAction extends GNodeAction {

		private IFrame value;

		protected void perform() {

			CFrame type = checkObtainCFrameAddition();

			if (type != null) {

				checkAddDisjunct(type);
			}
		}

		AddIFrameDisjunctAction(IFrame value) {

			this.value = value;
		}

		private void checkAddDisjunct(CFrame disjunctType) {

			IFrame disjunct = instantiate(disjunctType);

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

	protected boolean autoExpand() {

		return disjunctsSlot();
	}

	IFrameSlotNode(ITree tree, ISlot slot) {

		super(tree, slot);

		this.tree = tree;
		this.slot = slot;
	}

	GNode createValueNode(IValue value) {

		IFrame frameValue = asIFrame(value);

		if (frameValue.getCategory().disjunction()) {

			return new DisjunctionValueNode(frameValue);
		}

		return new AtomicValueNode(frameValue);
	}

	IValue checkObtainValue() {

		CFrame type = checkObtainCFrameAddition();

		return type != null ? instantiate(type) : null;
	}

	void removeValue(IValue value) {

		super.removeValue(value);

		if (disjunctsSlot()) {

			((DisjunctionValueNode)getParent()).replaceIfSingleDisjunct();
		}
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

	private GNodeAction getIFrameAdditionAction(IFrame value) {

		return addIFrameDisjunctActionRequired()
				? new AddIFrameDisjunctAction(value)
				: GNodeAction.INERT_ACTION;
	}

	private boolean addIFrameDisjunctActionRequired() {

		return queryInstance() && abstractEditableSlot();
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

	private IFrame instantiate(CFrame type) {

		return type.instantiate(slot.getContainer().getFunction());
	}

	private IFrame asIFrame(IValue value) {

		return getValueType().castValue(value);
	}

	private CFrame getValueType() {

		return (CFrame)slot.getValueType();
	}

	private boolean queryInstance() {

		return slot.getContainer().getFunction().query();
	}

	private boolean abstractEditableSlot() {

		return slot.getEditability().abstractEditable();
	}

	private boolean disjunctsSlot() {

		return slot.getContainer().getCategory().disjunction();
	}
}
