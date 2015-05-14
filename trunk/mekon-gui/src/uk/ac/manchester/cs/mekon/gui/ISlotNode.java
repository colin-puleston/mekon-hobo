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
abstract class ISlotNode extends GNode {

	private ITree tree;
	private ISlot slot;
	private ValueNodes valueNodes;

	private class DisplayUpdater implements ISlotListener {

		public void onUpdatedValueType(CValue<?> valueType) {

			updateNodeDisplay();
		}

		public void onUpdatedActiveStatus(boolean active) {

			updateNodeDisplay();
		}

		public void onUpdatedEditability(CEditability editability) {

			updateNodeDisplay();
		}
	}

	private abstract class ISlotNodeAction extends GNodeAction {

		protected boolean active() {

			return editableSlot();
		}
	}

	private class AddValueAction extends ISlotNodeAction {

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

	private class ClearValuesAction extends ISlotNodeAction {

		protected void perform() {

			clearValues();
		}
	}

	private class ValueNodes extends KListDerivedChildNodes<IValue> {

		ValueNodes() {

			super(ISlotNode.this, slot.getValues());
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

	protected GCellDisplay getDisplay() {

		GCellDisplay main = EntityDisplays.get().get(slot);
		GCellDisplay valType = EntityDisplays.get().forSlotValueTypeModifier(slot);
		GCellDisplay card = EntityDisplays.get().forSlotCardinalityModifier(slot);

		main.setModifier(valType);
		valType.setModifier(card);

		ITreeUpdateMarker updateMarker = tree.getUpdateMarker();

		updateMarker.checkMarkForGeneralUpdate(this, main);
		updateMarker.checkMarkForSlotValueTypeUpdate(this, valType);

		return main;
	}

	ISlotNode(ITree tree, ISlot slot) {

		super(tree);

		this.tree = tree;
		this.slot = slot;

		valueNodes = new ValueNodes();

		slot.addListener(new DisplayUpdater());
	}

	void addValue(IValue value) {

		onUpdateStart();
		getValuesEditor().add(value);
		onUpdateEnd(value);
	}

	void removeValue(IValue value) {

		onUpdateStart();
		getValuesEditor().remove(value);
		onUpdateEnd(null);
	}

	abstract IValue checkObtainValue();

	abstract GNode createValueNode(IValue value);

	GNodeAction getRemoveValueAction(IValue value) {

		return editableSlot()
					? new RemoveValueAction(value)
					: GNodeAction.INERT_ACTION;
	}

	ISlot getISlot() {

		return slot;
	}

	private void clearValues() {

		onUpdateStart();
		getValuesEditor().clear();
		onUpdateEnd(null);
	}

	private void onUpdateStart() {

		tree.onSlotValuesUpdateStart(this);
	}

	private void onUpdateEnd(IValue addedValue) {

		tree.onSlotValuesUpdateEnd(this, addedValue);
	}

	private boolean editableSlot() {

		return slot.getEditability().editable();
	}

	private ISlotValuesEditor getValuesEditor() {

		return slot.getValuesEditor();
	}
}
