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
import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * @author Colin Puleston
 */
public class ISlotSpecsTest {

	static private final CIdentity SLOT_ID = new CIdentity("SLOT", "SLOT");

	private TestCModel model = new TestCModel();
	private TestCFrames frameTypes = model.cFrames;
	private TestIFrames frames = model.iFrameAssertions;

	private TestCSlots repeatTypesSlotTypes = frameTypes.repeatTypesSlots;
	private TestCSlots uniqueTypesSlotTypes = frameTypes.uniqueTypesSlots;
	private TestCSlots singleValueSlotTypes = frameTypes.singleValueSlots;

	private CAtomicFrame ta = frameTypes.create("A");
	private CAtomicFrame tb = frameTypes.create("B");
	private CAtomicFrame tc = frameTypes.create("C");
	private CAtomicFrame td = frameTypes.create("D");

	private CAtomicFrame tx = frameTypes.create("X");
	private CAtomicFrame ty1 = frameTypes.create("Y1");
	private CAtomicFrame ty2 = frameTypes.create("Y2");
	private CAtomicFrame tz = frameTypes.create("Z");

	private CSlot sa = repeatTypesSlotTypes.create(ta, SLOT_ID, tx.getType());
	private CSlot sb = uniqueTypesSlotTypes.create(tb, SLOT_ID, ty1.getType());
	private CSlot sc = repeatTypesSlotTypes.create(tc, SLOT_ID, tz.getType());
	private CSlot sd = singleValueSlotTypes.create(td, SLOT_ID, ty2.getType());

	private IFrame iContainer;

	public ISlotSpecsTest() {

		addSuperFrame(tb, ta);
		addSuperFrame(tc, tb);

		addSuperFrame(ty1, tx);
		addSuperFrame(ty2, tx);
		addSuperFrame(tz, ty1);
		addSuperFrame(tz, ty2);

		initialiseContainer();
	}

	@Test
	public void test_simpleValueTypeUpdates() {

		testSlotCount(0);
		updateContainerSlots(ta);
		testSlotValueType(tx);
		updateContainerSlots(tc);
		testSlotValueType(tz);
		updateContainerSlots();
		testSlotCount(0);
	}

	@Test
	public void test_intersectionOfSubsumingValueTypes() {

		updateContainerSlots(tc, td);
		testSlotValueType(tz);
	}

	@Test
	public void test_intersectionOfValueTypesWithCommonDescendant() {

		updateContainerSlots(tb, td);
		testSlotValueType(tz);
	}

	@Test
	public void test_valueUpdates() {

		tb.addSlotValue(SLOT_ID, tz.getType());
		td.addSlotValue(SLOT_ID, tz.getType());

		updateContainerSlots(ta);
		testSlotValues();
		updateContainerSlots(tb);
		testSlotValues(tz);
		updateContainerSlots(tc, td);
		testSlotValues(tz);
		updateContainerSlots(ta);
		testSlotValues();
	}

	@Test
	public void test_cardinalityInitialUpdate() {

		updateContainerSlots(tc, td);
		testCardinality(CCardinality.SINGLE_VALUE);
	}

	@Test
	public void test_cardinalitySubsequentNonUpdate() {

		updateContainerSlots(ta);
		testCardinality(CCardinality.REPEATABLE_TYPES);
		updateContainerSlots(tc, td);
		testCardinality(CCardinality.REPEATABLE_TYPES);
	}

	@Test
	public void test_activationUpdates() {

		sa.setActivation(CActivation.INACTIVE);

		updateContainerSlots(ta);
		testSlotActivation(CActivation.INACTIVE);
		updateContainerSlots(ta, tb);
		testSlotActivation(CActivation.ACTIVE);
		updateContainerSlots(ta);
		testSlotActivation(CActivation.INACTIVE);
	}

	@Test
	public void test_editabilityInitialisation() {

		sa.setEditability(CEditability.DEFAULT);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(CEditability.DEFAULT);

		sb.setEditability(CEditability.FULL);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(CEditability.FULL);

		sc.setEditability(CEditability.NONE);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(CEditability.NONE);

		sd.setEditability(CEditability.QUERY_ONLY);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(CEditability.QUERY_ONLY);

		sd.setEditability(CEditability.DEFAULT);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(CEditability.NONE);

		sc.setEditability(CEditability.DEFAULT);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(CEditability.FULL);

		sb.setEditability(CEditability.DEFAULT);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(CEditability.DEFAULT);
	}

	@Test
	public void test_editabilityNonUpdating() {

		updateContainerSlots(ta, tb, tc, td);
		testSlotEditability(CEditability.DEFAULT);

		sa.setEditability(CEditability.FULL);
		updateContainerSlots(ta, tb, tc, td);
		testSlotEditability(CEditability.DEFAULT);
	}

	private void addSuperFrame(CFrame sub, CFrame sup) {

		FramesTestUtils.addSuperFrame(sub, sup);
	}

	private void initialiseContainer() {

		iContainer = frames.create("CONTAINER");
	}

	private void initialiseContainerAndSlots(CFrame... containerTypes) {

		initialiseContainer();
		updateContainerSlots(containerTypes);
	}

	private void updateContainerSlots(CFrame... containerTypes) {

		ISlotSpecs specs = new ISlotSpecs(model.iEditor);

		specs.absorbAll(Arrays.asList(containerTypes));
		specs.update(iContainer, ISlotOps.SLOTS_AND_VALUES);
	}

	private void testSlotValueType(CFrame rootValue) {

		MFrame expected = rootValue.getType();
		CValue<?> got = testSingleSlot().getValueType();

		assertEquals(expected, got);
	}

	private void testSlotValues(IValue... expected) {

		List<IValue> got = testSingleSlot().getValues().asList();

		testListContents(got, Arrays.asList(expected));
	}

	private void testCardinality(CCardinality expected) {

		assertEquals(expected, testSingleSlot().getType().getCardinality());
	}

	private void testSlotActivation(CActivation expected) {

		assertEquals(expected, testSingleSlot().getType().getActivation());
	}

	private void testSlotEditability(CEditability expected) {

		assertEquals(expected, testSingleSlot().getType().getEditability());
	}

	private ISlot testSingleSlot() {

		testSlotCount(1);

		return iContainer.getSlots().asList().get(0);
	}

	private void testSlotCount(int expected) {

		assertEquals(expected, iContainer.getSlots().size());
	}

	private <E>void testListContents(
						List<? extends E> got,
						List<? extends E> expected) {

		MekonTestUtils.testListContents(got, expected);
	}
}
