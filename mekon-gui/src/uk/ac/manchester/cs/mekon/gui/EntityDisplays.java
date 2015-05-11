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

		return getValueDisplay(frame, icons.get(frame));
	}

	GCellDisplay get(INumber number) {

		return getValueDisplay(number, icons.get(number));
	}

	GCellDisplay get(CFrame frame) {

		return getValueDisplay(frame, icons.get(frame));
	}

	GCellDisplay get(CNumber number) {

		return getValueDisplay(number, icons.get(number));
	}

	GCellDisplay get(MFrame frame) {

		return getValueDisplay(frame, icons.get(frame));
	}

	GCellDisplay get(CSlot slot) {

		return getSlotDisplay(slot, icons.get(slot), false);
	}

	GCellDisplay get(ISlot slot) {

		return getSlotDisplay(slot.getType(), icons.get(slot), true);
	}

	GCellDisplay get(String label, Icon icon, FontStyle fontStyle) {

		GCellDisplay display = new GCellDisplay(label);

		display.setIcon(icon);
		display.setFontStyleId(fontStyle.getStyleId());

		return display;
	}

	GCellDisplay forCSlotValues(CIdentity slotId) {

		return get(slotId.getLabel(), icons.forCSlotValues(), FontStyle.LINK);
	}

	private GCellDisplay getValueDisplay(FEntity value, Icon icon) {

		return get(value.getDisplayLabel(), icon, FontStyle.NODE);
	}

	private GCellDisplay getSlotDisplay(CSlot slot, Icon icon, boolean forISlot) {

		return get(getSlotLabel(slot, forISlot), icon, FontStyle.LINK);
	}

	private String getSlotLabel(CSlot slot, boolean forISlot) {

		return new SlotLabeller(slot).get(forISlot);
	}

	private GCellDisplay createFixedValuesDisplay() {

		return get(FIXED_VALUES_LABEL, null, FontStyle.LINK_INFO);
	}
}
