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
public class ISlotSpecsTest extends FramesModelTest {

	private CModelFrame ta = createCFrame("A");
	private CModelFrame tb = createCFrame("B");
	private CModelFrame tc = createCFrame("C");
	private CModelFrame td = createCFrame("D");

	private CModelFrame tx = createCFrame("X");
	private CModelFrame ty1 = createCFrame("Y1");
	private CModelFrame ty2 = createCFrame("Y2");
	private CModelFrame tz = createCFrame("Z");

	private CProperty p = createCProperty("P");

	private CSlot sa = createCSlot(ta, CCardinality.FREE, tx);
	private CSlot sb = createCSlot(tb, CCardinality.UNIQUE_TYPES, ty1);
	private CSlot sc = createCSlot(tc, CCardinality.FREE, tz);
	private CSlot sd = createCSlot(td, CCardinality.SINGLETON, ty2);

	private IFrame iContainer = createIFrame("Container");

	public ISlotSpecsTest() {

		addSuperFrame(tb, ta);
		addSuperFrame(tc, tb);

		addSuperFrame(ty1, tx);
		addSuperFrame(ty2, tx);
		addSuperFrame(tz, ty1);
		addSuperFrame(tz, ty2);
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
	public void test_convergingValueTypes() {

		updateContainerSlots(tc, td);
		testSlotValueType(tz);
	}

	@Test(expected = KModelException.class)
	public void test_nonConvergingValueTypesFails() {

		updateContainerSlots(tb, td);
	}

	@Test
	public void test_valueUpdates() {

		tb.addSlotValue(p, tz.getType());
		td.addSlotValue(p, tz.getType());

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
		testCardinality(CCardinality.SINGLETON);
	}

	@Test
	public void test_cardinalitySubsequentNonUpdate() {

		updateContainerSlots(ta);
		testCardinality(CCardinality.FREE);
		updateContainerSlots(tc, td);
		testCardinality(CCardinality.FREE);
	}

	@Test
	public void test_activeStatusInitialUpdate() {

		sa.getAttributes().setActive(false);

		updateContainerSlots(tc, td);
		testSlotActive(true);
	}

	@Test
	public void test_activeStatusSubsequentNonUpdate() {

		sa.getAttributes().setActive(false);

		updateContainerSlots(ta);
		testSlotActive(false);
		updateContainerSlots(tc, td);
		testSlotActive(false);
	}

	@Test
	public void test_editableStatusInitialUpdate() {

		sc.getAttributes().setEditable(false);

		updateContainerSlots(tc, td);
		testSlotEditable(false);
	}

	@Test
	public void test_editableStatusSubsequentNonUpdate() {

		sc.getAttributes().setEditable(false);

		updateContainerSlots(ta);
		testSlotEditable(true);
		updateContainerSlots(tc, td);
		testSlotEditable(true);
	}

	private void updateContainerSlots(CFrame... containerTypes) {

		ISlotSpecs specs = new ISlotSpecs(getModel().getIEditor());

		specs.absorbAll(list(containerTypes), true);
		specs.updateSlots(iContainer);
	}

	private void testSlotValueType(CFrame rootValue) {

		MFrame expected = rootValue.getType();
		CValue<?> got = testSingleSlot().getValueType();

		assertTrue("Unexpected slot value-type: " + got, got == expected);
	}

	private void testSlotValues(IValue... expected) {

		List<IValue> got = testSingleSlot().getValues().asList();

		testListContents(got, list(expected));
	}

	private void testCardinality(CCardinality expected) {

		CCardinality got = testSingleSlot().getType().getCardinality();

		assertTrue("Unexpected slot cardinality: " + got, got == expected);
	}

	private void testSlotActive(boolean expected) {

		boolean got = testSingleSlot().active();

		assertTrue("Unexpected slot active-status: " + got, got == expected);
	}

	private void testSlotEditable(boolean expected) {

		boolean got = testSingleSlot().editable();

		assertTrue("Unexpected slot editable-status: " + got, got == expected);
	}

	private ISlot testSingleSlot() {

		testSlotCount(1);

		return iContainer.getSlots().asList().get(0);
	}

	private void testSlotCount(int expected) {

		int got = iContainer.getSlots().size();

		assertTrue("Unexpected number of slots: " + got, got == expected);
	}

	private CSlot createCSlot(
					CModelFrame container,
					CCardinality cardinality,
					CModelFrame rootValue) {

		return createCSlot(container, p, cardinality, rootValue.getType());
	}
}
