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
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.hobo.mechanism.*;

/**
 * @author Colin Puleston
 */
class DirectModelTest extends FramesModelTest {

	private DModel model;

	DirectModelTest() {

		this(new DModel(){});
	}

	DObject createDObject(String frameTypeName) {

		IFrame frame = createIFrame(frameTypeName);
		DObject dObject = new DObjectDefault(model, frame);

		setIFrameMappedObject(frame, dObject);

		return dObject;
	}

	DCell<DObject> createDObjectCell(CCardinality cardinality, CFrame rootFrame) {

		DValueType<DObject> valueType = createDObjectValueType(rootFrame);
		DCell<DObject> cell = new DCell<DObject>(model, valueType);

		cell.setSlot(createISlot(cardinality, rootFrame));

		return cell;
	}

	DArray<DObject> createDObjectArray(CCardinality cardinality, CFrame rootFrame) {

		DValueType<DObject> valueType = createDObjectValueType(rootFrame);
		DArray<DObject> array = new DArray<DObject>(model, valueType);

		array.setSlot(createISlot(cardinality, rootFrame));

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

	private DirectModelTest(DModel model) {

		super(model.getCModel());

		this.model = model;
	}

	private DValueType<DObject> createDObjectValueType(CFrame rootFrame) {

		return new DObjectValueType<DObject>(model, DObject.class, rootFrame);
	}

	private ISlot createNumberSlot(CNumber type) {

		return createISlot(CCardinality.SINGLE_VALUE, type);
	}
}
