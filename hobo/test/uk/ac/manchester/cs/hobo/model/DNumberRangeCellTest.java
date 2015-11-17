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

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
public class DNumberRangeCellTest extends DFieldTest {

	@Test
	public void test_updatesToSourceCell() {

		DCell<Integer> sourceCell = createSourceCell();
		DCell<DNumberRange<Integer>> rangeCell = createRangeCell(sourceCell);

		sourceCell.set(6);
		testRangeCellAndSlotValue(rangeCell, DNumberRange.exact(6));

		sourceCell.clear();
		testNoCellOrSlotValue(rangeCell);
	}

	@Test
	public void test_concreteUpdatesToRangeCell() {

		DCell<Integer> sourceCell = createSourceCell();
		DCell<DNumberRange<Integer>> rangeCell = createRangeCell(sourceCell);

		rangeCell.set(DNumberRange.exact(6));
		testSourceCellAndSlotValue(sourceCell, 6);

		rangeCell.clear();
		testNoCellOrSlotValue(sourceCell);
	}

	@Test
	public void test_abstractUpdatesToRangeCell() {

		DCell<Integer> sourceCell = createSourceCell();
		DCell<DNumberRange<Integer>> rangeCell = createRangeCell(sourceCell);

		rangeCell.set(DNumberRange.range(1, 5));
		testNoCellValue(sourceCell);
		testSlotValues(sourceCell, CNumber.range(1, 5).asINumber());
	}

	private DCell<Integer> createSourceCell() {

		return createIntegerCell(DNumberRange.INTEGER);
	}

	private DCell<DNumberRange<Integer>> createRangeCell(DCell<Integer> sourceCell) {

		return FieldDeriver.deriveNumberRangeCell(getDModel(), sourceCell);
	}

	private void testSourceCellAndSlotValue(DCell<Integer> cell, Integer expectValue) {

		testSourceCellValue(cell, expectValue);
		testSlotValues(cell, new INumber(expectValue));
	}

	private void testRangeCellAndSlotValue(
					DCell<DNumberRange<Integer>> cell,
					DNumberRange<Integer> expectValue) {

		testRangeCellValue(cell, expectValue);
		testSlotValues(cell, expectValue.asCNumber().asINumber());
	}

	private void testSourceCellValue(DCell<Integer> cell, Integer expectValue) {

		assertTrue("Source-cell value should be set", cell.isSet());
		assertEquals("Unexpected source-cell value", cell.get(), expectValue);
	}

	private void testRangeCellValue(
					DCell<DNumberRange<Integer>> cell,
					DNumberRange<Integer> expectValue) {

		assertTrue("Range-cell value should be set", cell.isSet());
		assertEquals("Unexpected range-cell value", cell.get(), expectValue);
	}
}
