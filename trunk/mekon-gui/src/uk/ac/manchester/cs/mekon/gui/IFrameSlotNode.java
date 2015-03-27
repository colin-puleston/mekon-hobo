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

	private class ValueNode extends IFrameNode {

		private IFrame value;

		protected GNodeAction getPositiveAction() {

			return getAdditionAction(value);
		}

		protected GNodeAction getNegativeAction() {

			return getRemovalAction(value);
		}

		ValueNode(IFrame value) {

			super(tree, value);

			this.value = value;
		}
	}

	IFrameSlotNode(ITree tree, ISlot slot) {

		super(tree, slot);

		this.tree = tree;
		this.slot = slot;
	}

	GNode createValueNode(IValue value) {

		return new ValueNode(asIFrame(value));
	}

	IValue checkObtainValue() {

		CFrame type = checkObtainCFrameAddition();

		return type != null ? instantiate(type) : null;
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

		IFrame value = type.instantiate();

		value.alignCategory(slot.getContainer());

		return value;
	}

	private IFrame asIFrame(IValue value) {

		return getValueType().castValue(value);
	}

	private CFrame getValueType() {

		return slot.getValueType().castAs(CFrame.class);
	}
}
