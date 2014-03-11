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

package uk.ac.manchester.cs.mekon.model;

import org.junit.Test;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.*;

/**
 * @author Colin Puleston
 */
public class CSlotValuesTest extends FramesModelTest {

	private CModelFrame container = createCFrame("CONTAINER");

	private CProperty p1 = createCProperty("P1");
	private CProperty p2 = createCProperty("P2");

	private CModelFrame rootValueP1 = createCFrame("ROOT-VALUE-P1");
	private CModelFrame rootValueP2 = createCFrame("ROOT-VALUE-P2");

	private CModelFrame value1P1 = createCFrame("VALUE-1-P1");
	private CModelFrame value2P1 = createCFrame("VALUE-2-P1");
	private CModelFrame value2XP1 = createCFrame("VALUE-2X-P1");
	private CModelFrame value1P2 = createCFrame("VALUE-1-P2");
	private CModelFrame value2P2 = createCFrame("VALUE-2-P2");

	private CProperty badProperty = createCProperty("BAD-PROPERTY");
	private CModelFrame badValue = createCFrame("BAD-VALUE");

	public CSlotValuesTest() {

		addSuperFrame(value1P1, rootValueP1);
		addSuperFrame(value2P1, rootValueP1);
		addSuperFrame(value2XP1, value2P1);

		addSuperFrame(value1P2, rootValueP2);
		addSuperFrame(value2P2, rootValueP2);

		createCSlot(container, p1, CCardinality.FREE, rootValueP1);
		createCSlot(container, p2, CCardinality.FREE, rootValueP2);
	}

	@Test
	public void test_addAndClearValues() {

		addAndValidate(p1, value1P1);
		addAndValidate(p2, value1P2);
		addAndValidate(p1, value2P1);
		addAndValidate(p2, value2P2);

		testCurrentValues(p1, value1P1, value2P1);
		testCurrentValues(p2, value1P2, value2P2);

		container.clearSlotValues();

		assertTrue(getSlotValues().getSlotProperties().isEmpty());
	}

	@Test
	public void test_valuesDefined() {

		assertFalse(getSlotValues().valuesDefined());
		addAndValidate(p1, value1P1);
		assertTrue(getSlotValues().valuesDefined());
	}

	@Test
	public void test_removeSubsumedValues() {

		addAndValidate(p1, value1P1);
		addAndValidate(p1, value2P1);
		addAndValidate(p1, value2XP1);

		testCurrentValues(p1, value1P1, value2XP1);
	}

	@Test
	public void test_dontAddSubsumingValues() {

		addAndValidate(p1, value1P1);
		addAndValidate(p1, value2XP1);
		addAndValidate(p1, value2P1);

		testCurrentValues(p1, value1P1, value2XP1);
	}

	@Test
	public void test_dontAddDuplicateValues() {

		addAndValidate(p1, value1P1);
		addAndValidate(p1, value2P1);
		addAndValidate(p1, value1P1);

		testCurrentValues(p1, value1P1, value2P1);
	}

	@Test(expected = KModelException.class)
	public void test_addValueForInvalidProperty() {

		addAndValidate(badProperty, createCFrame("VALUE-FOR-BAD-PROPERTY"));
	}

	@Test(expected = KModelException.class)
	public void test_addInvalidValueForProperty() {

		addAndValidate(p1, badValue);
	}

	private void addAndValidate(CProperty p, CValue  value) {

		container.addSlotValue(p, value);
		getSlotValues().validateAll(container);
	}

	private void testCurrentValues(CProperty p, CValue<?>... values) {

		assertTrue(getSlotValues().valueFor(p));
		testList(getSlotValues().getValues(p), list(values));
	}

	private CSlotValues getSlotValues() {

		return container.getSlotValues();
	}
}
