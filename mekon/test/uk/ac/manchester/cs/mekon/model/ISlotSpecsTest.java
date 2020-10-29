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
public class ISlotSpecsTest {

	static private final CIdentity SLOT_ID = new CIdentity("SLOT", "SLOT");

	private TestCModel model = new TestCModel();
	private TestCFrames frameTypes = model.getClientCFrames();
	private TestIFrames frames = model.createAssertionIFrames();

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
	public void test_editabilityInitialisation() {

		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(IEditability.CONCRETE_ONLY, IEditability.FULL);


		setSlotAllEditabilityFromDefault(sb, IEditability.NONE);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(IEditability.NONE, IEditability.NONE);

		setSlotAllEditabilityFromDefault(sb, IEditability.CONCRETE_ONLY);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(IEditability.CONCRETE_ONLY, IEditability.CONCRETE_ONLY);

		setSlotAllEditabilityFromDefault(sb, IEditability.FULL);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(IEditability.FULL, IEditability.FULL);


		setSlotAssertionsEditabilityFromDefault(sb, IEditability.NONE);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(IEditability.NONE, IEditability.FULL);

		setSlotAssertionsEditabilityFromDefault(sb, IEditability.CONCRETE_ONLY);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(IEditability.CONCRETE_ONLY, IEditability.FULL);

		setSlotAssertionsEditabilityFromDefault(sb, IEditability.FULL);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(IEditability.FULL, IEditability.FULL);


		setSlotQueriesEditabilityFromDefault(sb, IEditability.NONE);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(IEditability.CONCRETE_ONLY, IEditability.NONE);

		setSlotQueriesEditabilityFromDefault(sb, IEditability.CONCRETE_ONLY);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(IEditability.CONCRETE_ONLY, IEditability.CONCRETE_ONLY);

		setSlotQueriesEditabilityFromDefault(sb, IEditability.FULL);
		initialiseContainerAndSlots(ta, tb, tc, td);
		testSlotEditability(IEditability.CONCRETE_ONLY, IEditability.FULL);
	}

	@Test
	public void test_editabilityNonUpdating() {

		updateContainerSlots(ta, tb, tc, td);
		testSlotEditability(IEditability.CONCRETE_ONLY, IEditability.FULL);

		sa.setAllEditability(IEditability.FULL);
		updateContainerSlots(ta, tb, tc, td);
		testSlotEditability(IEditability.CONCRETE_ONLY, IEditability.FULL);
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

	private void setSlotAllEditabilityFromDefault(CSlot slot, IEditability status) {

		slot.setEditability(CEditability.DEFAULT);
		slot.setAllEditability(status);
	}

	private void setSlotAssertionsEditabilityFromDefault(CSlot slot, IEditability status) {

		slot.setEditability(CEditability.DEFAULT);
		slot.setAssertionsEditability(status);
	}

	private void setSlotQueriesEditabilityFromDefault(CSlot slot, IEditability status) {

		slot.setEditability(CEditability.DEFAULT);
		slot.setQueriesEditability(status);
	}

	private void updateContainerSlots(CFrame... containerTypes) {

		ISlotSpecs specs = new ISlotSpecs(model.getIEditor());

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

	private void testSlotEditability(
					IEditability expectedAssertions,
					IEditability expectedQueries) {

		CEditability cEd = testSingleSlot().getType().getEditability();

		assertEquals(expectedAssertions, cEd.forAssertions());
		assertEquals(expectedQueries, cEd.forQueries());
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
