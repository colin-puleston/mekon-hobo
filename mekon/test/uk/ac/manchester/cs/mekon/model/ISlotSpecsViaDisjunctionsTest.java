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

import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.test_util.*;

/**
 * @author Colin Puleston
 */
public class ISlotSpecsViaDisjunctionsTest {

	static private final CIdentity SLOT1_ID = new CIdentity("SLOT-1");
	static private final CIdentity SLOT2_ID = new CIdentity("SLOT-2");
	static private final CIdentity SLOT3_ID = new CIdentity("SLOT-3");

	private TestCModel model = new TestCModel();
	private TestCFrames frameTypes = model.cFrames;
	private TestIFrames frames = model.iFrameAssertions;

	private TestCSlots repeatTypesSlotTypes = frameTypes.repeatTypesSlots;
	private TestCSlots singleValueSlotTypes = frameTypes.singleValueSlots;

	private CAtomicFrame ta = frameTypes.create("A");
	private CAtomicFrame tb = frameTypes.create("B");
	private CAtomicFrame tc = frameTypes.create("C");
	private CAtomicFrame td = frameTypes.create("D");
	private CAtomicFrame te = frameTypes.create("E");

	private CAtomicFrame tx = frameTypes.create("X");
	private CAtomicFrame ty1 = frameTypes.create("Y1");
	private CAtomicFrame ty2 = frameTypes.create("Y2");
	private CAtomicFrame tz = frameTypes.create("Z");

	private CSlot ta_slot1 = repeatTypesSlotTypes.create(ta, SLOT1_ID, tx.getType());
	private CSlot ta_slot2 = repeatTypesSlotTypes.create(ta, SLOT2_ID, ty1.getType());
	private CSlot tc_slot2 = singleValueSlotTypes.create(tc, SLOT2_ID, ty2.getType());
	private CSlot tb_slot3 = repeatTypesSlotTypes.create(tb, SLOT3_ID, tz.getType());

	private IFrame iContainer = frames.create("CONTAINER");

	public ISlotSpecsViaDisjunctionsTest() {

		addSuperFrame(td, ta);
		addSuperFrame(td, tb);
		addSuperFrame(te, tb);
		addSuperFrame(te, tc);

		addSuperFrame(ty2, ty1);
	}

	@Test
	public void test_slotUpdates() {

		testSlotCount(0);
		updateContainerSlots();
		testSlotCount(2);
		testSlotValueType(SLOT2_ID, ty2);
		testSlotValueType(SLOT3_ID, tz);
	}

	@Test
	public void test_slotValueUpdates() {

		CAtomicFrame ty3a = frameTypes.create("Y3A");
		CAtomicFrame ty3b = frameTypes.create("Y3B");

		addSuperFrame(ty3a, ty2);
		addSuperFrame(ty3b, ty2);

		td.addSlotValue(SLOT2_ID, ty3a.getType());
		td.addSlotValue(SLOT2_ID, ty3b.getType());
		te.addSlotValue(SLOT2_ID, ty3a.getType());

		updateContainerSlots();
		testSlotValues(SLOT2_ID, ty3a);
	}

	@Test
	public void test_slotAttributesUpdates() {

		ta_slot2.setActivation(CActivation.INACTIVE);
		ta_slot2.setAllEditability(IEditability.NONE);

		updateContainerSlots();
		testCardinality(SLOT2_ID, CCardinality.REPEATABLE_TYPES);
		testSlotActivation(SLOT2_ID, CActivation.INACTIVE);
		testSlotEditability(SLOT2_ID, IEditability.NONE);
	}

	private void addSuperFrame(CFrame sub, CFrame sup) {

		FramesTestUtils.addSuperFrame(sub, sup);
	}

	private void updateContainerSlots() {

		ISlotSpecs specs = new ISlotSpecs(model.iEditor);

		specs.absorb(createDisjunction(td, te));
		specs.update(iContainer, ISlotOps.SLOTS_AND_VALUES);
	}

	private void testSlotCount(int expected) {

		assertEquals(expected, iContainer.getSlots().size());
	}

	private void testSlotValueType(CIdentity slotId, CFrame rootValue) {

		MFrame expected = rootValue.getType();
		CValue<?> got = testFindSlot(slotId).getValueType();

		assertEquals(expected, got);
	}

	private void testSlotValues(CIdentity slotId, IValue... expected) {

		List<IValue> got = testFindSlot(slotId).getValues().asList();

		testListContents(got, Arrays.asList(expected));
	}

	private void testCardinality(CIdentity slotId, CCardinality expected) {

		assertEquals(expected, testFindSlot(slotId).getType().getCardinality());
	}

	private void testSlotActivation(CIdentity slotId, CActivation expected) {

		assertEquals(expected, testFindSlot(slotId).getType().getActivation());
	}

	private void testSlotEditability(CIdentity slotId, IEditability expectedAll) {

		CEditability cEd = testFindSlot(slotId).getType().getEditability();

		assertEquals(expectedAll, cEd.forAssertions());
		assertEquals(expectedAll, cEd.forQueries());
	}

	private ISlot testFindSlot(CIdentity slotId) {

		ISlots slots = iContainer.getSlots();

		assertTrue(slots.containsValueFor(slotId));

		return slots.get(slotId);
	}

	private CFrame createDisjunction(CFrame... disjuncts) {

		return CFrame.resolveDisjunction(Arrays.asList(disjuncts));
	}

	private <E>void testListContents(List<? extends E> got, List<? extends E> expected) {

		MekonTestUtils.testListContents(got, expected);
	}
}
