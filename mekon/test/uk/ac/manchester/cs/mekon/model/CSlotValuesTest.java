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

import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.test_util.*;

/**
 * @author Colin Puleston
 */
public class CSlotValuesTest {

	private TestCModel model = new TestCModel();
	private TestCFrames frames = model.cFrames;
	private TestCSlots slots = frames.repeatTypesSlots;

	private CAtomicFrame container = frames.create("CONTAINER");

	private CIdentity slot1Name = new CIdentity("SLOT-1");
	private CIdentity slot2Name = new CIdentity("SLOT-2");

	private CAtomicFrame rootValueSlot1 = frames.create("ROOT-VALUE-SLOT-1");
	private CAtomicFrame rootValueSlot2 = frames.create("ROOT-VALUE-SLOT-2");

	private CAtomicFrame value1Slot1 = frames.create("VALUE-1-SLOT-1");
	private CAtomicFrame value2Slot1 = frames.create("VALUE-2-SLOT-1");
	private CAtomicFrame value2XSlot1 = frames.create("VALUE-2X-SLOT-1");
	private CAtomicFrame value1Slot2 = frames.create("VALUE-1-SLOT-2");
	private CAtomicFrame value2Slot2 = frames.create("VALUE-2-SLOT-2");

	private CIdentity badSlotId = new CIdentity("BAD-SLOT-NAME");
	private CAtomicFrame badValue = frames.create("BAD-VALUE");

	public CSlotValuesTest() {

		addSuperFrame(value1Slot1, rootValueSlot1);
		addSuperFrame(value2Slot1, rootValueSlot1);
		addSuperFrame(value2XSlot1, value2Slot1);

		addSuperFrame(value1Slot2, rootValueSlot2);
		addSuperFrame(value2Slot2, rootValueSlot2);

		slots.create(container, slot1Name, rootValueSlot1);
		slots.create(container, slot2Name, rootValueSlot2);
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

		addAndValidate(badSlotId, frames.create("VALUE-FOR-BAD-SLOT-NAME"));
	}

	@Test(expected = KModelException.class)
	public void test_addInvalidValueForSlotId() {

		addAndValidate(slot1Name, badValue);
	}

	private void addSuperFrame(CFrame sub, CFrame sup) {

		FramesTestUtils.addSuperFrame(sub, sup);
	}

	private void addAndValidate(CIdentity slotId, CValue<?> value) {

		container.addSlotValue(slotId, value);
		getSlotValues().validateAll(container);
	}

	private void testCurrentValues(CIdentity slotId, CValue<?>... values) {

		assertTrue(getSlotValues().valueFor(slotId));
		testList(getSlotValues().getValues(slotId), Arrays.asList(values));
	}

	private <E>void testList(List<? extends E> got, List<? extends E> expected) {

		MekonTestUtils.testList(got, expected);
	}

	private CSlotValues getSlotValues() {

		return container.getSlotValues();
	}
}
