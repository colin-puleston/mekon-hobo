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
public class DNumberCellTest extends DCellGeneralTest {

	@Test
	public void test_assertion_directUpdates() {

		DNumberCell<Integer> cell = createUnconstrainedAssertionCell();

		testSetCellValue(cell, DNumberRange.exact(6));
		testClearCellValue(cell);
	}

	@Test
	public void test_assertion_backDoorUpdates() {

		DNumberCell<Integer> cell = createUnconstrainedAssertionCell();

		testSetSlotValue(cell, CIntegerDef.exact(6));
		testClearSlotValue(cell);
	}

	@Test(expected = KAccessException.class)
	public void test_assertion_directNonExactRangeUpdateFails() {

		createUnconstrainedAssertionCell().set(DNumberRange.range(2, 8));
	}

	@Test(expected = KAccessException.class)
	public void test_assertion_backDoorNonExactRangeUpdateFails() {

		addSlotValue(createUnconstrainedAssertionCell(), CIntegerDef.range(2, 8));
	}

	@Test
	public void test_query_directUpdates() {

		DNumberCell<Integer> cell = createUnconstrainedQueryCell();

		testSetCellValue(cell, DNumberRange.min(3));
		testSetCellValue(cell, DNumberRange.max(7));
		testSetCellValue(cell, DNumberRange.exact(6));
		testSetCellValue(cell, DNumberRange.range(2, 8));

		testClearCellValue(cell);
	}

	@Test
	public void test_query_backDoorUpdates() {

		DNumberCell<Integer> cell = createUnconstrainedQueryCell();

		testSetSlotValue(cell, CIntegerDef.min(3));
		testSetSlotValue(cell, CIntegerDef.max(7));
		testSetSlotValue(cell, CIntegerDef.exact(6));
		testSetSlotValue(cell, CIntegerDef.range(2, 8));

		testClearSlotValue(cell);
	}

	@Test(expected = KAccessException.class)
	public void test_query_illegalDirectRangeUpdateFails() {

		createRange_5_10_QueryCell().set(DNumberRange.range(2, 8));
	}

	@Test(expected = KAccessException.class)
	public void test_query_illegalBackRangeDoorUpdateFails() {

		addSlotValue(createRange_5_10_QueryCell(), CIntegerDef.range(2, 8));
	}

	private DNumberCell<Integer> createUnconstrainedAssertionCell() {

		return createIntegerCell(CIntegerDef.UNCONSTRAINED);
	}

	private DNumberCell<Integer> createUnconstrainedQueryCell() {

		return createQueryCell(CIntegerDef.UNCONSTRAINED);
	}

	private DNumberCell<Integer> createRange_5_10_QueryCell() {

		return createQueryCell(CIntegerDef.range(5, 10));
	}

	private DNumberCell<Integer> createQueryCell(CIntegerDef def) {

		DNumberCell<Integer> cell = createIntegerCell(def);

		cell.getSlot().getContainer().resetCategory(IFrameCategory.QUERY);

		return cell;
	}

	private void testSetCellValue(
					DNumberCell<Integer> cell,
					DNumberRange<Integer> range) {

		cell.set(range);
		testCellValue(cell, range.asCNumber());
	}

	private void testClearCellValue(DNumberCell<Integer> cell) {

		cell.clear();
		testNoCellOrSlotValue(cell);
	}

	private void testSetSlotValue(
					DNumberCell<Integer> cell,
					CIntegerDef cRangeDef) {

		CNumber cRange = cRangeDef.createNumber();

		addSlotValues(cell, cRange.asINumber());
		testCellValue(cell, cRange);
	}

	private void testClearSlotValue(DNumberCell<Integer> cell) {

		clearSlotValues(cell);
		testNoCellValue(cell);
	}

	private void testCellValue(DNumberCell<Integer> cell, CNumber cRange) {

		testRangeValue(cell, cRange);

		if (cRange.exactValue()) {

			testCellAndSlotValue(cell, cRange.getMin());
		}
		else {

			testNoCellValue(cell);
			testSlotValues(cell, cRange.asINumber());
		}
	}

	private void addSlotValue(
					DNumberCell<Integer> cell,
					CIntegerDef valueDef) {

		addSlotValues(cell, valueDef.createNumber().asINumber());
	}

	private void testRangeValue(
					DNumberCell<Integer> cell,
					CNumber cRange) {

		assertTrue(
			"Cell range-value should be set",
			cell.rangeSet());

		DNumberRange<Integer> value = cell.getRange();

		assertTrue(
			"Unexpected cell range-value: " + value,
			value.asCNumber().equals(cRange));
	}
}
