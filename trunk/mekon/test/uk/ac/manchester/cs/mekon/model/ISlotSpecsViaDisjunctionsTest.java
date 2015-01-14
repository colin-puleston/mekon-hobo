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
public class ISlotSpecsViaDisjunctionsTest extends MekonTest {

	private CModelFrame ta = createCFrame("A");
	private CModelFrame tb = createCFrame("B");
	private CModelFrame tc = createCFrame("C");
	private CModelFrame td = createCFrame("D");
	private CModelFrame te = createCFrame("E");

	private CModelFrame tx = createCFrame("X");
	private CModelFrame ty1 = createCFrame("Y1");
	private CModelFrame ty2 = createCFrame("Y2");
	private CModelFrame tz = createCFrame("Z");

	private CIdentity slot1Id = createIdentity("SLOT-1");
	private CIdentity slot2Id = createIdentity("SLOT-2");
	private CIdentity slot3Id = createIdentity("SLOT-3");

	private CSlot sa1 = createCSlot(ta, slot1Id, tx);
	private CSlot sa2 = createCSlot(ta, slot2Id, ty1);
	private CSlot sb = createCSlot(tb, slot3Id, tz);
	private CSlot sc = createCSlot(tc, slot2Id, ty2);

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
	public void test_valueUpdates() {

		CModelFrame ty3a = createCFrame("Y3A");
		CModelFrame ty3b = createCFrame("Y3B");

		addSuperFrame(ty3a, ty2);
		addSuperFrame(ty3b, ty2);

		td.addSlotValue(slot2Id, ty3a.getType());
		td.addSlotValue(slot2Id, ty3b.getType());
		te.addSlotValue(slot2Id, ty3a.getType());

		updateContainerSlots();
		testSlotValues(slot2Id, ty3a);
	}

	private void updateContainerSlots() {

		ISlotSpecs specs = new ISlotSpecs(getModel().getIEditor());

		specs.absorb(createDisjunction(td, te));
		specs.updateSlots(iContainer);
		specs.updateSlotValues(iContainer);
	}

	private void testSlotValueType(CIdentity slotId, CFrame rootValue) {

		MFrame expected = rootValue.getType();
		CValue<?> got = testFindSlot(slotId).getValueType();

		assertEquals(expected, got);
	}

	private void testSlotValues(CIdentity slotId, IValue... expected) {

		List<IValue> got = testFindSlot(slotId).getValues().asList();

		testListContents(got, list(expected));
	}

	private void testSlotCount(int expected) {

		assertEquals(expected, iContainer.getSlots().size());
	}

	private ISlot testFindSlot(CIdentity slotId) {

		ISlots slots = iContainer.getSlots();

		assertTrue(slots.containsValueFor(slotId));

		return slots.get(slotId);
	}

	private CSlot createCSlot(
					CModelFrame container,
					CIdentity slotId,
					CModelFrame rootValue) {

		return createCSlot(container, slotId, CCardinality.FREE, rootValue.getType());
	}

	private CFrame createDisjunction(CFrame... disjuncts) {

		return CFrame.resolveDisjunction(list(disjuncts));
	}
}
