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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * @author Colin Puleston
 */
public class ISlotSpecsViaDisjunctionsTest extends GeneralFramesModelTest {

	private CAtomicFrame ta = createCFrame("A");
	private CAtomicFrame tb = createCFrame("B");
	private CAtomicFrame tc = createCFrame("C");
	private CAtomicFrame td = createCFrame("D");
	private CAtomicFrame te = createCFrame("E");

	private CAtomicFrame tx = createCFrame("X");
	private CAtomicFrame ty1 = createCFrame("Y1");
	private CAtomicFrame ty2 = createCFrame("Y2");
	private CAtomicFrame tz = createCFrame("Z");

	private CIdentity slot1Id = new CIdentity("SLOT-1");
	private CIdentity slot2Id = new CIdentity("SLOT-2");
	private CIdentity slot3Id = new CIdentity("SLOT-3");

	private CSlot ta_slot1 = createCSlot(ta, slot1Id, CCardinality.REPEATABLE_TYPES, tx);
	private CSlot ta_slot2 = createCSlot(ta, slot2Id, CCardinality.REPEATABLE_TYPES, ty1);
	private CSlot tc_slot2 = createCSlot(tc, slot2Id, CCardinality.SINGLE_VALUE, ty2);
	private CSlot tb_slot3 = createCSlot(tb, slot3Id, CCardinality.REPEATABLE_TYPES, tz);

	private IFrame iContainer = createIFrame("CONTAINER");

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
		testSlotValueType(slot2Id, ty2);
		testSlotValueType(slot3Id, tz);
	}

	@Test
	public void test_slotValueUpdates() {

		CAtomicFrame ty3a = createCFrame("Y3A");
		CAtomicFrame ty3b = createCFrame("Y3B");

		addSuperFrame(ty3a, ty2);
		addSuperFrame(ty3b, ty2);

		td.addSlotValue(slot2Id, ty3a.getType());
		td.addSlotValue(slot2Id, ty3b.getType());
		te.addSlotValue(slot2Id, ty3a.getType());

		updateContainerSlots();
		testSlotValues(slot2Id, ty3a);
	}

	@Test
	public void test_slotAttributesUpdates() {

		ta_slot2.setActive(false);
		ta_slot2.setEditability(CEditability.FULL);

		updateContainerSlots();
		testCardinality(slot2Id, CCardinality.REPEATABLE_TYPES);
		testActiveSlot(slot2Id, false);
		testSlotEditability(slot2Id, CEditability.FULL);
	}

	private void addSuperFrame(CFrame sub, CFrame sup) {

		FramesTestUtils.addSuperFrame(sub, sup);
	}

	private void updateContainerSlots() {

		ISlotSpecs specs = new ISlotSpecs(getModel().getIEditor());

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

	private void testActiveSlot(CIdentity slotId, boolean expected) {

		assertEquals(expected, testFindSlot(slotId).getType().active());
	}

	private void testSlotEditability(CIdentity slotId, CEditability expected) {

		assertEquals(expected, testFindSlot(slotId).getType().getEditability());
	}

	private ISlot testFindSlot(CIdentity slotId) {

		ISlots slots = iContainer.getSlots();

		assertTrue(slots.containsValueFor(slotId));

		return slots.get(slotId);
	}

	private CSlot createCSlot(
					CAtomicFrame container,
					CIdentity slotId,
					CCardinality cardinality,
					CAtomicFrame rootValue) {

		return createCSlot(container, slotId, cardinality, rootValue.getType());
	}

	private CFrame createDisjunction(CFrame... disjuncts) {

		return CFrame.resolveDisjunction(Arrays.asList(disjuncts));
	}

	private <E>void testListContents(List<? extends E> got, List<? extends E> expected) {

		MekonTestUtils.testListContents(got, expected);
	}
}
