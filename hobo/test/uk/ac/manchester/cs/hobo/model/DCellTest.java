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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
public class DCellTest extends DCellGeneralTest {

	@Test
	public void test_directUpdates() {

		DCell<Integer> cell = createRange_0_10_Cell();

		testSetCell(cell, 3);
		testSetCell(cell, 7);

		cell.clear();
		testNoCellOrSlotValue(cell);
	}

	@Test
	public void test_backDoorUpdates() {

		DCell<Integer> cell = createRange_0_10_Cell();

		testSetSlot(cell, 3);
		testSetSlot(cell, 7);

		clearSlotValues(cell);
		testNoCellOrSlotValue(cell);
	}

	@Test(expected = KAccessException.class)
	public void test_illegalDirectUpdateFails() {

		createRange_0_10_Cell().set(11);
	}

	@Test(expected = KAccessException.class)
	public void test_illegalBackDoorUpdateFails() {

		addSlotValue(createRange_0_10_Cell(), 11);
	}

	private DNumberCell<Integer> createRange_0_10_Cell() {

		return createIntegerCell(DNumberRange.range(0, 10));
	}

	private void testSetCell(DCell<Integer> cell, int value) {

		cell.set(value);
		testCellAndSlotValue(cell, value);
	}

	private void testSetSlot(DCell<Integer> cell, int value) {

		addSlotValue(cell, value);
		testCellAndSlotValue(cell, value);
	}

	private void addSlotValue(DCell<Integer> cell, int value) {

		addSlotValues(cell, new INumber(value));
	}
}
