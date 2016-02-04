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
public abstract class AbstractDCellTest<SV, DV> extends DFieldTest {

	@Test
	public void test_updatesToSourceCell() {

		DCell<SV> sourceCell = createSourceCell();
		DCell<DV> derivedCell = createDerivedCell(sourceCell);

		SV sourceValue = getSourceTestValue();
		DV derivedValue = sourceToDerivedValue(sourceValue);

		sourceCell.set(sourceValue);
		testDerivedCellAndSlotValue(derivedCell, derivedValue);

		sourceCell.clear();
		testNoCellOrSlotValue(derivedCell);
	}

	@Test
	public void test_concreteUpdatesToDerivedCell() {

		DCell<SV> sourceCell = createSourceCell();
		DCell<DV> derivedCell = createDerivedCell(sourceCell);

		SV sourceValue = getSourceTestValue();
		DV derivedValue = sourceToDerivedValue(sourceValue);

		derivedCell.set(derivedValue);
		testSourceCellAndSlotValue(sourceCell, sourceValue);

		derivedCell.clear();
		testNoCellOrSlotValue(sourceCell);
	}

	@Test
	public void test_abstractUpdatesToDerivedCell() {

		DCell<SV> sourceCell = createSourceCell();
		DCell<DV> derivedCell = createDerivedCell(sourceCell);

		DV derivedValue = getDerivedOnlyTestValue();

		derivedCell.set(derivedValue);
		testNoCellValue(sourceCell);
		testSlotValueViaDerivedValue(derivedCell, derivedValue);
	}

	abstract DCell<SV> createSourceCellAsAssertion();

	abstract DCell<DV> createDerivedCell(DCell<SV> sourceCell);

	abstract SV getSourceTestValue();

	abstract DV getDerivedOnlyTestValue();

	abstract DV sourceToDerivedValue(SV sourceValue);

	abstract IValue sourceToSlotValue(SV sourceValue);

	abstract IValue derivedToSlotValue(DV derivedValue);

	private DCell<SV> createSourceCell() {

		DCell<SV> cell = createSourceCellAsAssertion();

		cell.getSlot().getContainer().resetFunction(IFrameFunction.QUERY);

		return cell;
	}

	private void testSourceCellAndSlotValue(DCell<SV> cell, SV expectValue) {

		testSourceCellValue(cell, expectValue);
		testSlotValueViaSourceValue(cell, expectValue);
	}

	private void testDerivedCellAndSlotValue(DCell<DV> cell, DV expectValue) {

		testDerivedCellValue(cell, expectValue);
		testSlotValueViaDerivedValue(cell, expectValue);
	}

	private void testSourceCellValue(DCell<SV> cell, SV expectValue) {

		assertTrue("Source-cell value should be set", cell.isSet());
		assertEquals("Unexpected source-cell value", cell.get(), expectValue);
	}

	private void testDerivedCellValue(DCell<DV> cell, DV expectValue) {

		assertTrue("Derived-cell value should be set", cell.isSet());
		assertEquals("Unexpected derived-cell value", cell.get(), expectValue);
	}

	private void testSlotValueViaSourceValue(DCell<SV> cell, SV expectValue) {

		testSlotValues(cell, sourceToSlotValue(expectValue));
	}

	private void testSlotValueViaDerivedValue(DCell<DV> cell, DV expectValue) {

		testSlotValues(cell, derivedToSlotValue(expectValue));
	}
}
