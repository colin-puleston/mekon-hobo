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

import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
abstract class DCellGeneralTest extends DFieldTest {

	void testCellAndSlotValue(DCell<Integer> cell, int expectValue) {

		testCellAndSlotValue(cell, INumber.create(expectValue));
	}

	void testCellAndSlotValue(DCell<Integer> cell, INumber expectValue) {

		testCellValue(cell, expectValue);
		testSlotValues(cell, expectValue);
	}

	void testCellValue(DCell<Integer> cell, INumber expectValue) {

		assertTrue(
			"Cell-value should be set",
			cell.isSet());
		assertTrue(
			"Unexpected cell-value: " + cell.get(),
			cell.get() == expectValue.asInteger());
	}

	void testNoCellOrSlotValue(DCell<Integer> cell) {

		testNoCellValue(cell);
		testSlotValues(cell);
	}

	void testNoCellValue(DCell<Integer> cell) {

		assertTrue("Cell-value should not be set", !cell.isSet());
	}
}
