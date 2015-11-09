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
public class CSlotTest extends FramesModelTest {

	@Test
	public void test_addAndRemoveSlots() {

		CModel model = getModel();

		CAtomicFrame a = createCFrame("A");

		CAtomicFrame v1 = createCFrame("V1");
		CAtomicFrame v2 = createCFrame("V2");
		CAtomicFrame v3 = createCFrame("V3");
		CAtomicFrame v4 = createCFrame("V4");

		CSlot s1 = createCSlot(a, CCardinality.REPEATABLE_TYPES, v1);
		CSlot s2 = createCSlot(a, CCardinality.REPEATABLE_TYPES, v2);
		CSlot s3 = createCSlot(a, CCardinality.REPEATABLE_TYPES, v3);
		CSlot s4 = createCSlot(a, CCardinality.REPEATABLE_TYPES, v4);

		testList(a.getSlots().asList(), Arrays.asList(s1, s2, s3, s4));

		model.removeFrame(v1);
		a.removeSlot(s3);

		testList(a.getSlots().asList(), Arrays.asList(s2, s4));
	}

	@Test
	public void test_absorbCardinality() {

		testAbsorbCardinality(
			CCardinality.SINGLE_VALUE,
			CCardinality.SINGLE_VALUE,
			CCardinality.SINGLE_VALUE);
		testAbsorbCardinality(
			CCardinality.SINGLE_VALUE,
			CCardinality.UNIQUE_TYPES,
			CCardinality.SINGLE_VALUE);
		testAbsorbCardinality(
			CCardinality.SINGLE_VALUE,
			CCardinality.REPEATABLE_TYPES,
			CCardinality.SINGLE_VALUE);

		testAbsorbCardinality(
			CCardinality.UNIQUE_TYPES,
			CCardinality.SINGLE_VALUE,
			CCardinality.SINGLE_VALUE);
		testAbsorbCardinality(
			CCardinality.UNIQUE_TYPES,
			CCardinality.UNIQUE_TYPES,
			CCardinality.UNIQUE_TYPES);
		testAbsorbCardinality(
			CCardinality.UNIQUE_TYPES,
			CCardinality.REPEATABLE_TYPES,
			CCardinality.UNIQUE_TYPES);

		testAbsorbCardinality(
			CCardinality.REPEATABLE_TYPES,
			CCardinality.SINGLE_VALUE,
			CCardinality.SINGLE_VALUE);
		testAbsorbCardinality(
			CCardinality.REPEATABLE_TYPES,
			CCardinality.UNIQUE_TYPES,
			CCardinality.UNIQUE_TYPES);
		testAbsorbCardinality(
			CCardinality.REPEATABLE_TYPES,
			CCardinality.REPEATABLE_TYPES,
			CCardinality.REPEATABLE_TYPES);
	}

	@Test
	public void test_absorbValueType() {

		CFrame a = createCFrame("A");
		CFrame b = createCFrame("B");
		CSlot s = createCSlot(CCardinality.REPEATABLE_TYPES, a);

		addSuperFrame(b, a);

		testAbsorbValueType(s, b, b);
		testAbsorbValueType(s, a, b);
	}

	@Test(expected = KModelException.class)
	public void test_absorbValueTypeFail() {

		CFrame a = createCFrame("A");
		CFrame b = createCFrame("B");
		CSlot s = createCSlot(CCardinality.REPEATABLE_TYPES, a);

		s.createEditor().absorbValueType(b);
	}

	private void testAbsorbCardinality(
					CCardinality initial,
					CCardinality absorb,
					CCardinality expect) {

		CSlot slot = createCSlot(initial);

		slot.createEditor().absorbCardinality(absorb);

		CCardinality result = slot.getCardinality();

		assertTrue("Unexpected cardinality: " + result, result == expect);
	}

	private void testAbsorbValueType(CSlot slot, CValue<?> absorb, CValue<?> expect) {

		slot.createEditor().absorbValueType(absorb);

		assertEquals(expect, slot.getValueType());
	}
}
