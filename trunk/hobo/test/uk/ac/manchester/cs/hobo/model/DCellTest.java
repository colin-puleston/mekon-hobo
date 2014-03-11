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

import org.junit.Test;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
public class DCellTest extends DFieldTest {

	@Test
	public void test_directUpdates() {

		DCell<Integer> cell = createIntegerCell(0, 10);

		cell.set(3);
		testCellValue(cell, 3);

		cell.set(7);
		testCellValue(cell, 7);

		cell.clear();
		testNoCellValue(cell);
	}

	@Test
	public void test_backDoorUpdates() {

		DCell<Integer> cell = createIntegerCell(0, 10);

		addSlotValue(cell, 3);
		testCellValue(cell, 3);

		addSlotValue(cell, 7);
		testCellValue(cell, 7);

		clearSlotValues(cell);
		testNoCellValue(cell);
	}

	@Test(expected = KAccessException.class)
	public void test_illegalDirectUpdateFails() {

		createIntegerCell(0, 10).set(11);
	}

	@Test(expected = KAccessException.class)
	public void test_illegalBackDoorUpdateFails() {

		addSlotValue(createIntegerCell(0, 10), 11);
	}

	private void addSlotValue(DCell<Integer> cell, int value) {

		addSlotValues(cell, INumber.create(value));
	}

	private void testCellValue(DCell<Integer> cell, int expectValue) {

		assertTrue("Cell-value should be set", cell.isSet());
		assertTrue("Unexpected cell-value: " + cell.get(), cell.get() == expectValue);

		testSlotValues(cell, INumber.create(expectValue));
	}

	private void testNoCellValue(DCell<Integer> cell) {

		assertTrue("Cell-value should not be set", !cell.isSet());

		testSlotValues(cell);
	}
}
