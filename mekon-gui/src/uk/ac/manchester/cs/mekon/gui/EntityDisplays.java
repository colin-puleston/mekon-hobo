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

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class EntityDisplays {

	static private final String FIXED_VALUES_LABEL = "fixed-values";

	static private final EntityDisplays singleton = new EntityDisplays();

	static EntityDisplays get() {

		return singleton;
	}

	final GCellDisplay fixedValuesDisplay = createFixedValuesDisplay();

	private EntityIcons icons = EntityIcons.get();

	GCellDisplay get(IFrame frame) {

		return getValueDisplay(frame, icons.get(frame), false);
	}

	GCellDisplay get(INumber number) {

		return getValueDisplay(number, icons.get(number), false);
	}

	GCellDisplay get(CFrame frame, boolean isValueType) {

		return getValueDisplay(frame, icons.get(frame), isValueType);
	}

	GCellDisplay get(CNumber number, boolean isValueType) {

		return getValueDisplay(number, icons.get(number), isValueType);
	}

	GCellDisplay get(MFrame frame, boolean isValueType) {

		return getValueDisplay(frame, icons.get(frame), isValueType);
	}

	GCellDisplay get(CSlot slot) {

		return getSlotDisplay(slot, icons.get(slot));
	}

	GCellDisplay get(ISlot slot) {

		return getSlotDisplay(slot.getType(), icons.get(slot));
	}

	GCellDisplay get(String label, Icon icon, FontStyle fontStyle) {

		return new GCellDisplay(label, icon, fontStyle.getStyleId());
	}

	GCellDisplay forCSlotValues(CProperty property) {

		return get(property.getDisplayLabel(), icons.forCSlotValues(), FontStyle.LINK);
	}

	private GCellDisplay getValueDisplay(FEntity value, Icon icon, boolean isValueType) {

		FontStyle fontStyle = isValueType ? FontStyle.LINK_INFO : FontStyle.NODE;

		return get(value.getDisplayLabel(), icon, fontStyle);
	}

	private GCellDisplay getSlotDisplay(CSlot slot, Icon icon) {

		return get(SlotLabels.get(slot), icon, FontStyle.LINK);
	}

	private GCellDisplay createFixedValuesDisplay() {

		return get(FIXED_VALUES_LABEL, null, FontStyle.LINK_INFO);
	}
}
