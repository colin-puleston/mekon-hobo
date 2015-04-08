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

/**
 * @author Colin Puleston
 */
public class ISlotValuesTest extends MekonTest {

	static private final List<IValue> NO_IVALUES = Collections.emptyList();

	private CFrame r = createCFrame("ROOT");
	private CFrame a = createCFrame("A");
	private CFrame b = createCFrame("B");
	private CFrame c = createCFrame("C");
	private CFrame cx = createCFrame("CX");

	public ISlotValuesTest() {

		addSuperFrame(a, r);
		addSuperFrame(b, r);
		addSuperFrame(c, r);
		addSuperFrame(cx, c);
	}

	@Test
	public void test_addRemoveValues_singletonCardinality() {

		ISlotValues values = createSlotValues(CCardinality.SINGLETON);

		testAdd(values, a, iValues(a));
		testAdd(values, b, iValues(b));
		testRemove(values, b, NO_IVALUES);
	}

	@Test
	public void test_addRemoveValues_uniqueTypesCardinality() {

		testAddRemoveValues_multiValued(CCardinality.UNIQUE_TYPES);
	}

	@Test
	public void test_addRemoveValues_freeCardinality() {

		testAddRemoveValues_multiValued(CCardinality.FREE);
	}

	@Test
	public void test_updateValues_singletonCardinality() {

		ISlotValues values = createSlotValues(CCardinality.SINGLETON);

		testUpdate(values, iValues(a), iValues(a));
		testUpdate(values, iValues(b), iValues(b));
		testUpdate(values, NO_IVALUES, NO_IVALUES);
	}

	@Test
	public void test_updateValues_uniqueTypesCardinality() {

		testUpdateValues_multiValued(CCardinality.UNIQUE_TYPES);
	}

	@Test
	public void test_updateValues_freeCardinality() {

		testUpdateValues_multiValued(CCardinality.FREE);
	}

	@Test
	public void test_updateFixeds_singletonCardinality() {

		ISlotValues values = createSlotValues(CCardinality.SINGLETON);

		testUpdate(values, iValues(c), iValues(c));

		testUpdateFixeds(values, iValues(a), iValues(a));
		testUpdateFixeds(values, iValues(b), iValues(b));
		testUpdate(values, iValues(cx), iValues(b));
		testUpdateFixeds(values, NO_IVALUES, NO_IVALUES);
	}

	@Test(expected = KModelException.class)
	public void test_updateFixedsFails_singletonCardinality() {

		createSlotValues(CCardinality.SINGLETON).updateFixedValues(iValues(a, b));
	}

	@Test
	public void test_updateFixeds_uniqueTypesCardinality() {

		testUpdateFixeds_multiValued(CCardinality.UNIQUE_TYPES);
	}

	@Test
	public void test_updateFixeds_freeCardinality() {

		testUpdateFixeds_multiValued(CCardinality.FREE);
	}

	@Test(expected = KAccessException.class)
	public void test_illegalUpdateFails() {

		createSlotValues(CCardinality.FREE).add(createIFrame("IllegalValue"), false);
	}

	@Test(expected = KAccessException.class)
	public void test_abstractUpdateFailsForAssertion() {

		createSlotValues(CCardinality.FREE).add(createIFrame("IllegalValue"), false);
	}

	private void testAddRemoveValues_multiValued(CCardinality cardinality) {

		ISlotValues values = createSlotValues(cardinality);

		testAdd(values, a, iValues(a));
		testAdd(values, b, iValues(a, b));
		testRemove(values, a, iValues(b));
		testAdd(values, c, iValues(b, c));

		if (cardinality == CCardinality.UNIQUE_TYPES) {

			testAdd(values, cx, iValues(b, cx));
		}
		else {

			testAdd(values, cx, iValues(b, c, cx));
			testRemove(values, c, iValues(b, cx));
		}

		testRemove(values, cx, iValues(b));
		testRemove(values, b, NO_IVALUES);
	}

	private void testUpdateValues_multiValued(CCardinality cardinality) {

		ISlotValues values = createSlotValues(cardinality);

		testUpdate(values, iValues(a, b), iValues(a, b));
		testUpdate(values, iValues(b, c), iValues(b, c));
		testUpdate(values, iValues(b, c, cx), iValues(b, cx));
		testUpdate(values, iValues(b, c), iValues(b, c));
		testUpdate(values, NO_IVALUES, NO_IVALUES);
	}

	private void testUpdateFixeds_multiValued(CCardinality cardinality) {

		ISlotValues values = createSlotValues(cardinality);

		testUpdate(values, iValues(c), iValues(c));
		testUpdateFixeds(values, iValues(a, b), iValues(a, b, c));
		testUpdateFixeds(values, iValues(a, b, cx), iValues(a, b, cx));
		testUpdateFixeds(values, iValues(a, b), iValues(a, b));
		testUpdate(values, NO_IVALUES, iValues(a, b));
		testUpdateFixeds(values, NO_IVALUES, NO_IVALUES);
	}

	private ISlotValues createSlotValues(CCardinality cardinality) {

		return createISlot(cardinality, r.getType()).getValues();
	}

	private void testAdd(
					ISlotValues slotValues,
					IValue value,
					List<IValue> expectedValues) {

		slotValues.add(value, false);
		testValues(slotValues, expectedValues);
	}

	private void testRemove(
					ISlotValues slotValues,
					IValue value,
					List<IValue> expectedValues) {

		slotValues.remove(value);
		testValues(slotValues, expectedValues);
	}

	private void testUpdate(
					ISlotValues slotValues,
					List<IValue> values,
					List<IValue> expectedValues) {

		slotValues.update(values, false);
		testTypes(slotValues, expectedValues);
	}

	private void testUpdateFixeds(
					ISlotValues slotValues,
					List<IValue> fixedValues,
					List<IValue> expectedValues) {

		slotValues.updateFixedValues(fixedValues);
		testTypes(slotValues, expectedValues);
	}

	private void testValues(ISlotValues slotValues, List<IValue> expectedValues) {

		testList(slotValues.asList(), expectedValues);
	}

	private void testTypes(ISlotValues slotValues, List<IValue> expectedValues) {

		testListContents(slotValues.asList(), expectedValues);
	}
}
