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

package uk.ac.manchester.cs.mekon.model;

import uk.ac.manchester.cs.mekon.*;

/**
 * @author Colin Puleston
 */
class ISlotAttributes {

	private CValue<?> valueType;
	private boolean active;
	private boolean editable;

	ISlotAttributes(CValue<?> valueType) {

		this(valueType, true, true);
	}

	ISlotAttributes copy() {

		return new ISlotAttributes(valueType, active, editable);
	}

	CValue<?> getValueType() {

		return valueType;
	}

	boolean active() {

		return active;
	}

	boolean editable() {

		return editable;
	}

	void setValueType(CValue<?> valueType) {

		this.valueType = valueType;
	}

	void setActive(boolean active) {

		this.active = active;
	}

	void setEditable(boolean editable) {

		this.editable = editable;
	}

	void absorbValueType(CSlot slotType, CValue<?> otherValueType) {

		CValue<?> mergedType = valueType.mergeWith(otherValueType);

		if (mergedType == null) {

			throw new KModelException(
						"Incompatible value-types for: " + slotType
						+ " (current type = " + valueType
						+ ", supplied type = " + otherValueType + ")");
		}

		valueType = mergedType;
	}

	void absorbActive(boolean otherActive) {

		active &= otherActive;
	}

	void absorbEditable(boolean otherEditable) {

		editable &= otherEditable;
	}

	private ISlotAttributes(CValue<?> valueType, boolean active, boolean editable) {

		this.valueType = valueType;
		this.active = active;
		this.editable = editable;
	}
}
