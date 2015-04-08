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

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class SlotLabeller {

	static String getCardinalityModifier(
					CCardinality cardinality,
					boolean singleType) {

		switch (cardinality) {

			case SINGLE_VALUE:
				return "[x]";

			case UNIQUE_TYPES:
				return "[x,y,z]";

			case REPEATABLE_TYPES:
				return singleType ? "[x,x,x]" : "[x,x,y]";
		}

		throw new Error("Unrecognised cardinality value: " + cardinality);
	}

	static String modifyForValueLevel(String label, boolean conceptLevel) {

		return conceptLevel ? "(" + label + ")" : "<" + label + ">";
	}

	private CSlot slot;

	SlotLabeller(CSlot slot) {

		this.slot = slot;
	}

	String get(boolean forISlot) {

		return slot.getDisplayLabel() + " " + getSuffix(forISlot);
	}

	private String getSuffix(boolean forISlot) {

		return forISlot ? getISlotSuffix() : getCardinalityModifier();
	}

	private String getISlotSuffix() {

		String label = slot.getValueType().getDisplayLabel();

		label = modifyForValueLevel(label);
		label = modifyForCardinality(label);

		return label;
	}

	private String modifyForValueLevel(String label) {

		return modifyForValueLevel(label, conceptLevelValue());
	}

	private String modifyForCardinality(String label) {

		return label + " " + getCardinalityModifier();
	}

	private String getCardinalityModifier() {

		return getCardinalityModifier(slot.getCardinality(), singleType());
	}

	private boolean conceptLevelValue() {

		return slot.getValueType() instanceof MFrame;
	}

	private boolean singleType() {

		CValue<?> type = slot.getValueType();

		if (type instanceof CFrame) {

			return ((CFrame)type).getSubs(CVisibility.EXPOSED).isEmpty();
		}

		return true;
	}
}
