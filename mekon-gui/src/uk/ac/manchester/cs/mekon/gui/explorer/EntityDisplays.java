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

package uk.ac.manchester.cs.mekon.gui.explorer;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class EntityDisplays {

	static private final String DISJUNCTS_SLOT_LABEL = "OR...";
	static private final String FIXED_VALUES_LABEL = "fixed-values";

	static private final EntityDisplays singleton = new EntityDisplays();

	static EntityDisplays get() {

		return singleton;
	}

	final GCellDisplay fixedValuesDisplay = forFixedValues();

	private EntityIcons icons = EntityIcons.get();

	GCellDisplay get(IFrame frame) {

		return forValue(frame, icons.get(frame));
	}

	GCellDisplay get(INumber number) {

		return forValue(number, icons.get(number));
	}

	GCellDisplay get(IString string) {

		return forValue(string, icons.get(string));
	}

	GCellDisplay get(CFrame frame) {

		return forValue(frame, icons.get(frame));
	}

	GCellDisplay get(CNumber number) {

		return forValue(number, icons.get(number));
	}

	GCellDisplay get(CString string) {

		return forValue(string, icons.get(string));
	}

	GCellDisplay get(MFrame frame) {

		return forValue(frame, icons.get(frame));
	}

	GCellDisplay get(CSlot slot) {

		return forSlot(slot, icons.get(slot));
	}

	GCellDisplay get(ISlot slot) {

		if (slot.getContainer().getCategory().disjunction()) {

			return forDisjunctsSlot();
		}

		return forSlot(slot, icons.get(slot));
	}

	GCellDisplay get(String label, Icon icon, NodeTextDisplay textDisplay) {

		GCellDisplay display = new GCellDisplay(label);

		display.setIcon(icon);
		display.setTextColour(textDisplay.getColour());
		display.setFontStyle(textDisplay.getStyle());

		return display;
	}

	GCellDisplay forInstanceRef(CIdentity instanceRef) {

		return get(instanceRef.getLabel(), icons.forInstanceRef(), NodeTextDisplay.VALUE);
	}

	GCellDisplay forCSlotValues(CIdentity slotId) {

		return get(slotId.getLabel(), icons.forCSlotValues(), NodeTextDisplay.SLOT);
	}

	GCellDisplay forSlotValueTypeModifier(CSlot slot) {

		String label = SlotLabelModifiers.forValueType(slot);

		return get(label, null, NodeTextDisplay.SLOT_VALUE_TYPE_MODIFIER);
	}

	GCellDisplay forSlotCardinalityModifier(CSlot slot) {

		String label = SlotLabelModifiers.forCardinality(slot);

		return get(label, null, NodeTextDisplay.SLOT_CARDINALITY_MODIFIER);
	}

	GCellDisplay forSlotValueTypeModifier(ISlot slot) {

		return forSlotValueTypeModifier(slot.getType());
	}

	GCellDisplay forSlotCardinalityModifier(ISlot slot) {

		return forSlotCardinalityModifier(slot.getType());
	}

	private GCellDisplay forValue(FEntity value, Icon icon) {

		return get(value.getDisplayLabel(), icon, NodeTextDisplay.VALUE);
	}

	private GCellDisplay forSlot(FEntity slot, Icon icon) {

		return get(slot.getDisplayLabel(), icon, NodeTextDisplay.SLOT);
	}

	private GCellDisplay forDisjunctsSlot() {

		return get(DISJUNCTS_SLOT_LABEL, null, NodeTextDisplay.DISJUNCTS_SLOT);
	}

	private GCellDisplay forFixedValues() {

		return get(FIXED_VALUES_LABEL, null, NodeTextDisplay.SLOT_VALUES);
	}
}
