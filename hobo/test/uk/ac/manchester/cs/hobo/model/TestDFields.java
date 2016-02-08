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

package uk.ac.manchester.cs.hobo.model;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

import uk.ac.manchester.cs.hobo.model.motor.*;

/**
 * @author Colin Puleston
 */
class TestDFields {

	private DModel model;

	private TestISlots repeatTypesSlots;
	private TestISlots singleValueSlots;

	TestDFields(DModel model, TestIFrames frames) {

		this.model = model;

		repeatTypesSlots = frames.repeatTypesSlots;
		singleValueSlots = frames.singleValueSlots;
	}

	DCell<DObject> createDObjectCell(CFrame rootValueType) {

		DValueType<DObject> valueType = createDObjectValueType(rootValueType);
		DCell<DObject> cell = new DCell<DObject>(model, valueType);

		cell.setSlot(createObjectsSlot(rootValueType));

		return cell;
	}

	DArray<DObject> createDObjectArray(CFrame rootValueType) {

		DValueType<DObject> valueType = createDObjectValueType(rootValueType);
		DArray<DObject> array = new DArray<DObject>(model, valueType);

		array.setSlot(createObjectsSlot(rootValueType));

		return array;
	}

	DCell<Integer> createIntegerCell(int min, int max) {

		return createIntegerCell(DNumberRange.range(min, max));
	}

	DCell<Integer> createIntegerCell(DNumberRange<Integer> range) {

		DNumberValueType<Integer> valueType = new DNumberValueType<Integer>(range);
		DCell<Integer> cell = new DCell<Integer>(model, valueType);

		cell.setSlot(createNumberSlot(range.asCNumber()));

		return cell;
	}

	DModel getDModel() {

		return model;
	}

	private ISlot createObjectsSlot(CFrame rootValueType) {

		return repeatTypesSlots.create(createSlotName(rootValueType), rootValueType);
	}

	private ISlot createNumberSlot(CNumber type) {

		return singleValueSlots.create(createSlotName(type), type);
	}

	private String createSlotName(CFrame rootValueType) {

		return "SLOT-FOR-" + rootValueType.getIdentity();
	}

	private String createSlotName(CNumber type) {

		return "SLOT-FOR-" + type.toString();
	}

	private DValueType<DObject> createDObjectValueType(CFrame rootValueType) {

		return new DObjectValueType<DObject>(model, DObject.class, rootValueType);
	}
}
