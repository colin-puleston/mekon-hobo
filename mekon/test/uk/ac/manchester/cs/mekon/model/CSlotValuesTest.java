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
public class CSlotValuesTest extends MekonTest {

	private CModelFrame container = createCFrame("CONTAINER");

	private CIdentity slot1Name = createIdentity("SLOT-1");
	private CIdentity slot2Name = createIdentity("SLOT-2");

	private CModelFrame rootValueSlot1 = createCFrame("ROOT-VALUE-SLOT-1");
	private CModelFrame rootValueSlot2 = createCFrame("ROOT-VALUE-SLOT-2");

	private CModelFrame value1Slot1 = createCFrame("VALUE-1-SLOT-1");
	private CModelFrame value2Slot1 = createCFrame("VALUE-2-SLOT-1");
	private CModelFrame value2XSlot1 = createCFrame("VALUE-2X-SLOT-1");
	private CModelFrame value1Slot2 = createCFrame("VALUE-1-SLOT-2");
	private CModelFrame value2Slot2 = createCFrame("VALUE-2-SLOT-2");

	private CIdentity badSlotId = createIdentity("BAD-SLOT-NAME");
	private CModelFrame badValue = createCFrame("BAD-VALUE");

	public CSlotValuesTest() {

		addSuperFrame(value1Slot1, rootValueSlot1);
		addSuperFrame(value2Slot1, rootValueSlot1);
		addSuperFrame(value2XSlot1, value2Slot1);

		addSuperFrame(value1Slot2, rootValueSlot2);
		addSuperFrame(value2Slot2, rootValueSlot2);

		createCSlot(container, slot1Name, CCardinality.FREE, rootValueSlot1);
		createCSlot(container, slot2Name, CCardinality.FREE, rootValueSlot2);
	}

	@Test
	public void test_addAndClearValues() {

		addAndValidate(slot1Name, value1Slot1);
		addAndValidate(slot2Name, value1Slot2);
		addAndValidate(slot1Name, value2Slot1);
		addAndValidate(slot2Name, value2Slot2);

		testCurrentValues(slot1Name, value1Slot1, value2Slot1);
		testCurrentValues(slot2Name, value1Slot2, value2Slot2);

		container.clearSlotValues();

		assertTrue(getSlotValues().getSlotIdentities().isEmpty());
	}

	@Test
	public void test_valuesDefined() {

		assertFalse(getSlotValues().valuesDefined());
		addAndValidate(slot1Name, value1Slot1);
		assertTrue(getSlotValues().valuesDefined());
	}

	@Test
	public void test_removeSubsumedValues() {

		addAndValidate(slot1Name, value1Slot1);
		addAndValidate(slot1Name, value2Slot1);
		addAndValidate(slot1Name, value2XSlot1);

		testCurrentValues(slot1Name, value1Slot1, value2XSlot1);
	}

	@Test
	public void test_dontAddSubsumingValues() {

		addAndValidate(slot1Name, value1Slot1);
		addAndValidate(slot1Name, value2XSlot1);
		addAndValidate(slot1Name, value2Slot1);

		testCurrentValues(slot1Name, value1Slot1, value2XSlot1);
	}

	@Test
	public void test_dontAddDuplicateValues() {

		addAndValidate(slot1Name, value1Slot1);
		addAndValidate(slot1Name, value2Slot1);
		addAndValidate(slot1Name, value1Slot1);

		testCurrentValues(slot1Name, value1Slot1, value2Slot1);
	}

	@Test(expected = KModelException.class)
	public void test_addValueForInvalidSlotId() {

		addAndValidate(badSlotId, createCFrame("VALUE-FOR-BAD-SLOT-NAME"));
	}

	@Test(expected = KModelException.class)
	public void test_addInvalidValueForSlotId() {

		addAndValidate(slot1Name, badValue);
	}

	private void addAndValidate(CIdentity slotId, CValue<?> value) {

		container.addSlotValue(slotId, value);
		getSlotValues().validateAll(container);
	}

	private void testCurrentValues(CIdentity slotId, CValue<?>... values) {

		assertTrue(getSlotValues().valueFor(slotId));
		testList(getSlotValues().getValues(slotId), list(values));
	}

	private CSlotValues getSlotValues() {

		return container.getSlotValues();
	}
}
