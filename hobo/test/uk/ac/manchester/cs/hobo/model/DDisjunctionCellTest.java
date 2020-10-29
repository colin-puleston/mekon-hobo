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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
public class DDisjunctionCellTest extends AbstractDCellTest<DObject, DDisjunction<DObject>> {

	private CFrame rootFrame = frameTypes.create("ROOT");

	private DObject a = createObjectWithSubRootType("A");
	private DObject b = createObjectWithSubRootType("B");
	private DObject c = createObjectWithSubRootType("C");

	DCell<DObject> createSourceCell() {

		return fields.createDObjectCell(rootFrame);
	}

	DCell<DDisjunction<DObject>> createDerivedCell(DCell<DObject> sourceCell) {

		return new DDisjunctionCell<DObject>(sourceCell);
	}

	DObject getSourceTestValue() {

		return a;
	}

	DDisjunction<DObject> getDerivedOnlyTestValue() {

		return createDisjunction(a, b, c);
	}

	DDisjunction<DObject> sourceToDerivedValue(DObject sourceValue) {

		return createDisjunction(sourceValue);
	}

	IValue sourceToSlotValue(DObject sourceValue) {

		return sourceValue.getFrame();
	}

	IValue derivedToSlotValue(DDisjunction<DObject> derivedValue) {

		return derivedValue.asDisjunctionIFrame().normalise();
	}

	private DObject createObjectWithSubRootType(String name) {

		DObject obj = createDObject(name);

		addSuperFrame(obj.getFrame().getType(), rootFrame);

		return obj;
	}

	private DDisjunction<DObject> createDisjunction(DObject... disjuncts) {

		return new DDisjunction<DObject>(Arrays.asList(disjuncts));
	}

	private void addSuperFrame(CFrame sub, CFrame sup) {

		FramesTestUtils.addSuperFrame(sub, sup);
	}
}
