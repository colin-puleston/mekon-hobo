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
class IFrameValuesNode extends IValuesNode {

	private ITree tree;
	private ISlot slot;

	private class ValueNode extends IFrameNode {

		private IFrame frame;

		private class AddDisjunctAction extends AddCFrameDisjunctAction {

			AddDisjunctAction() {

				super(frame.getType());
			}

			CFrame checkObtainNewDisjunct() {

				return checkObtainValueType();
			}

			void onDisjunctAdded(CFrame updatedType) {

				IFrame updatedFrame = checkUpdateValueType(frame, updatedType);

				if (updatedFrame != null) {

					removeValue(frame);
					addValue(updatedFrame);

					frame = updatedFrame;
				}
			}
		}

		protected GNodeAction getPositiveAction() {

			return addActionRequired()
					? new AddDisjunctAction()
					: GNodeAction.INERT_ACTION;
		}

		protected GNodeAction getNegativeAction() {

			return getRemoveValueAction(frame);
		}

		ValueNode(IFrame frame) {

			super(tree, frame);

			this.frame = frame;
		}

		private boolean addActionRequired() {

			return AddDisjunctAction.actionRequired(slot, getValueType());
		}
	}

	protected GCellDisplay getDisplay() {

		return EntityDisplays.get().get(getValueType(), true);
	}

	IFrameValuesNode(ITree tree, ISlot slot) {

		super(tree, slot);

		this.tree = tree;
		this.slot = slot;
	}

	GNode createValueNode(IValue value) {

		return new ValueNode(getIFrameValue(value));
	}

	IValue checkObtainValue() {

		CFrame type = checkObtainValueType();

		return type != null ? type.instantiate() : null;
	}

	private IFrame checkUpdateValueType(IFrame value, CFrame newType) {

		if (newType.instantiable()) {

			IFrame newValue = newType.instantiate();

			copySlotValues(value, newValue);

			return newValue;
		}

		JOptionPane.showMessageDialog(
			null,
			"Cannot instantiate: " + newType.getDisplayLabel());

		return null;
	}

	private void copySlotValues(IFrame from, IFrame to) {

		ISlots toSlots = to.getSlots();

		for (ISlot slot : from.getSlots().asList()) {

			List<IValue> values = slot.getValues().asList();

			if (!values.isEmpty()) {

				CProperty p = slot.getType().getProperty();

				if (toSlots.containsSlotFor(p)) {

					toSlots.getSlotFor(p).getValuesEditor().addAll(values);
				}
			}
		}
	}

	private CFrame checkObtainValueType() {

		return CFrameSelector.checkSelect(tree, getValueType());
	}

	private IFrame getIFrameValue(IValue value) {

		return getValueType().castValue(value);
	}

	private CFrame getValueType() {

		return slot.getValueType().castAs(CFrame.class);
	}
}
