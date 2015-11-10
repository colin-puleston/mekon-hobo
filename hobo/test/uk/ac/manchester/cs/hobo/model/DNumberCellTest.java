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

		testSetSlotValue(cell, CNumber.exact(6));
		testClearSlotValue(cell);
	}

	@Test(expected = KAccessException.class)
	public void test_assertion_directNonExactRangeUpdateFails() {

		createUnconstrainedAssertionCell().set(DNumberRange.range(2, 8));
	}

	@Test(expected = KAccessException.class)
	public void test_assertion_backDoorNonExactRangeUpdateFails() {

		addSlotValue(createUnconstrainedAssertionCell(), CNumber.range(2, 8));
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

		testSetSlotValue(cell, CNumber.min(3));
		testSetSlotValue(cell, CNumber.max(7));
		testSetSlotValue(cell, CNumber.exact(6));
		testSetSlotValue(cell, CNumber.range(2, 8));

		testClearSlotValue(cell);
	}

	@Test(expected = KAccessException.class)
	public void test_query_illegalDirectRangeUpdateFails() {

		createRange_5_10_QueryCell().set(DNumberRange.range(2, 8));
	}

	@Test(expected = KAccessException.class)
	public void test_query_illegalBackRangeDoorUpdateFails() {

		addSlotValue(createRange_5_10_QueryCell(), CNumber.range(2, 8));
	}

	private DNumberCell<Integer> createUnconstrainedAssertionCell() {

		return createIntegerCell(DNumberRange.INTEGER);
	}

	private DNumberCell<Integer> createUnconstrainedQueryCell() {

		return createQueryCell(DNumberRange.INTEGER);
	}

	private DNumberCell<Integer> createRange_5_10_QueryCell() {

		return createQueryCell(DNumberRange.range(5, 10));
	}

	private DNumberCell<Integer> createQueryCell(DNumberRange<Integer> range) {

		DNumberCell<Integer> cell = createIntegerCell(range);

		cell.getSlot().getContainer().resetCategory(IFrameCategory.QUERY);

		return cell;
	}

	private void testSetCellValue(
					DNumberCell<Integer> cell,
					DNumberRange<Integer> rangeValue) {

		cell.set(rangeValue);
		testCellValue(cell, rangeValue.asCNumber());
	}

	private void testClearCellValue(DNumberCell<Integer> cell) {

		cell.clear();
		testNoCellOrSlotValue(cell);
	}

	private void testSetSlotValue(DNumberCell<Integer> cell, CNumber valueType) {

		addSlotValues(cell, valueType.asINumber());
		testCellValue(cell, valueType);
	}

	private void testClearSlotValue(DNumberCell<Integer> cell) {

		clearSlotValues(cell);
		testNoCellValue(cell);
	}

	private void testCellValue(DNumberCell<Integer> cell, CNumber valueType) {

		testRangeValue(cell, valueType);

		if (valueType.exactValue()) {

			testCellAndSlotValue(cell, valueType.getMin());
		}
		else {

			testNoCellValue(cell);
			testSlotValues(cell, valueType.asINumber());
		}
	}

	private void addSlotValue(DNumberCell<Integer> cell, CNumber valueType) {

		addSlotValues(cell, valueType.asINumber());
	}

	private void testRangeValue(DNumberCell<Integer> cell, CNumber valueType) {

		assertTrue(
			"Cell range-value should be set",
			cell.rangeSet());

		DNumberRange<Integer> value = cell.getRange();

		assertTrue(
			"Unexpected cell range-value: " + value,
			value.asCNumber().equals(valueType));
	}
}
