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

package uk.ac.manchester.cs.mekon.mechanism;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class ISlotSpec {

	private IEditor iEditor;

	private CProperty property;
	private CSource source = CSource.UNSPECIFIED;
	private CCardinality cardinality = CCardinality.FREE;
	private Set<CValue<?>> valueTypes = new HashSet<CValue<?>>();
	private List<IValue> fixedValues = new ArrayList<IValue>();
	private boolean active = false;
	private boolean editable = true;

	ISlotSpec(IEditor iEditor, CProperty property) {

		this.property = property;
		this.iEditor = iEditor;
	}

	void absorbType(CSlot slotType) {

		source = source.combineWith(slotType.getSource());
		cardinality = cardinality.getMoreRestrictive(slotType.getCardinality());
		valueTypes.add(slotType.getValueType());
		active |= slotType.active();
		editable &= slotType.editable();
	}

	void absorbFixedValues(List<IValue> newFixedValues) {

		for (IValue value : newFixedValues) {

			if (!fixedValues.contains(value)) {

				fixedValues.add(value);
			}
		}
	}

	ISlot addSlot(IFrame container) {

		CValue<?> valueType = getValueType();
		IFrameEditor containerEd = getFrameEditor(container);
		ISlot slot = containerEd.addSlot(property, source, cardinality, valueType);
		ISlotEditor slotEd = getSlotEditor(slot);

		slotEd.setActive(active);
		slotEd.setEditable(editable);
		slotEd.setFixedValues(fixedValues);

		return slot;
	}

	void updateSlot(ISlot slot) {

		ISlotEditor slotEd = getSlotEditor(slot);

		slotEd.setValueType(getValueType());
		slotEd.setFixedValues(fixedValues);
	}

	CProperty getProperty() {

		return property;
	}

	private CValue<?> getValueType() {

		return new CValueIntersection(valueTypes).getSingleValue();
	}

	private IFrameEditor getFrameEditor(IFrame frame) {

		return iEditor.getFrameEditor(frame);
	}

	private ISlotEditor getSlotEditor(ISlot slot) {

		return iEditor.getSlotEditor(slot);
	}
}
