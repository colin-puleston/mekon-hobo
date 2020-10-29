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
public class CSlotTest {

	private TestCModel model = new TestCModel();
	private TestCFrames frames = model.serverCFrames;

	private TestCSlots repeatTypesSlots = frames.repeatTypesSlots;
	private TestCSlots uniqueTypesSlots = frames.uniqueTypesSlots;
	private TestCSlots singleValueSlots = frames.singleValueSlots;

	@Test
	public void test_addAndRemoveSlots() {

		CAtomicFrame a = frames.create("A");

		CAtomicFrame v1 = frames.create("V1");
		CAtomicFrame v2 = frames.create("V2");
		CAtomicFrame v3 = frames.create("V3");
		CAtomicFrame v4 = frames.create("V4");

		CSlot s1 = repeatTypesSlots.create(a, "s1", v1);
		CSlot s2 = repeatTypesSlots.create(a, "s2", v2);
		CSlot s3 = repeatTypesSlots.create(a, "s3", v3);
		CSlot s4 = repeatTypesSlots.create(a, "s4", v4);

		testList(a.getSlots().asList(), Arrays.asList(s1, s2, s3, s4));

		model.serverModel.removeFrame(v1);
		a.removeSlot(s3);

		testList(a.getSlots().asList(), Arrays.asList(s2, s4));
	}

	@Test
	public void test_absorbCardinality() {

		testAbsorbCardinality(
			singleValueSlots,
			CCardinality.SINGLE_VALUE,
			CCardinality.SINGLE_VALUE);
		testAbsorbCardinality(
			singleValueSlots,
			CCardinality.UNIQUE_TYPES,
			CCardinality.SINGLE_VALUE);
		testAbsorbCardinality(
			singleValueSlots,
			CCardinality.REPEATABLE_TYPES,
			CCardinality.SINGLE_VALUE);

		testAbsorbCardinality(
			uniqueTypesSlots,
			CCardinality.SINGLE_VALUE,
			CCardinality.SINGLE_VALUE);
		testAbsorbCardinality(
			uniqueTypesSlots,
			CCardinality.UNIQUE_TYPES,
			CCardinality.UNIQUE_TYPES);
		testAbsorbCardinality(
			uniqueTypesSlots,
			CCardinality.REPEATABLE_TYPES,
			CCardinality.UNIQUE_TYPES);

		testAbsorbCardinality(
			repeatTypesSlots,
			CCardinality.SINGLE_VALUE,
			CCardinality.SINGLE_VALUE);
		testAbsorbCardinality(
			repeatTypesSlots,
			CCardinality.UNIQUE_TYPES,
			CCardinality.UNIQUE_TYPES);
		testAbsorbCardinality(
			repeatTypesSlots,
			CCardinality.REPEATABLE_TYPES,
			CCardinality.REPEATABLE_TYPES);
	}

	@Test
	public void test_absorbValueType() {

		CFrame a = frames.create("A");
		CFrame b = frames.create("B");
		CSlot s = repeatTypesSlots.create("s", a);

		addSuperFrame(b, a);

		testAbsorbValueType(s, b, b);
		testAbsorbValueType(s, a, b);
	}

	@Test(expected = KModelException.class)
	public void test_absorbValueTypeFail() {

		CFrame a = frames.create("A");
		CFrame b = frames.create("B");
		CSlot s = repeatTypesSlots.create("s", a);

		s.createEditor().absorbValueType(b);
	}

	private void addSuperFrame(CFrame sub, CFrame sup) {

		FramesTestUtils.addSuperFrame(sub, sup);
	}

	private void testAbsorbCardinality(
					TestCSlots slots,
					CCardinality absorb,
					CCardinality expect) {

		CSlot slot = slots.create("s");

		slot.createEditor().absorbCardinality(absorb);

		CCardinality result = slot.getCardinality();

		assertTrue("Unexpected cardinality: " + result, result == expect);
	}

	private void testAbsorbValueType(CSlot slot, CValue<?> absorb, CValue<?> expect) {

		slot.createEditor().absorbValueType(absorb);

		assertEquals(expect, slot.getValueType());
	}

	private <E>void testList(List<? extends E> got, List<? extends E> expected) {

		MekonTestUtils.testList(got, expected);
	}
}
