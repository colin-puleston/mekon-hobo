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
abstract class IValuesNode extends GNode {

	private ISlot slot;
	private ValueNodes valueNodes;

	private class DisplayUpdater implements ISlotListener {

		public void onUpdatedValueType(CValue<?> valueType) {

			notifyUpdatedDisplay();
		}
	}

	private abstract class IValuesNodeAction extends GNodeAction {

		protected boolean active() {

			return slot.editable();
		}
	}

	private class AddValueAction extends IValuesNodeAction {

		protected void perform() {

			IValue value = checkObtainValue();

			if (value != null) {

				addValue(value);
			}
		}
	}

	private class RemoveValueAction extends GNodeAction {

		private IValue value;

		protected void perform() {

			removeValue(value);
		}

		RemoveValueAction(IValue value) {

			this.value = value;
		}
	}

	private class ClearValuesAction extends IValuesNodeAction {

		protected void perform() {

			slot.getValuesEditor().clear();
		}
	}

	private class ValueNodes extends KListDerivedChildNodes<IValue> {

		ValueNodes() {

			super(IValuesNode.this, slot.getValues());
		}

		GNode createChildNode(IValue value) {

			return createValueNode(value);
		}
	}

	protected void addInitialChildren() {

		valueNodes.addInitialChildNodes();
	}

	protected GNodeAction getPositiveAction() {

		return new AddValueAction();
	}

	protected GNodeAction getNegativeAction() {

		return new ClearValuesAction();
	}

	IValuesNode(ITree tree, ISlot slot) {

		super(tree);

		this.slot = slot;

		valueNodes = new ValueNodes();

		slot.addListener(new DisplayUpdater());
	}

	abstract IValue checkObtainValue();

	abstract GNode createValueNode(IValue value);

	GNodeAction getRemoveValueAction(IValue value) {

		return slot.editable()
					? new RemoveValueAction(value)
					: GNodeAction.INERT_ACTION;
	}

	void addValue(IValue value) {

		slot.getValuesEditor().add(value);
	}

	void removeValue(IValue value) {

		slot.getValuesEditor().remove(value);
	}
}
