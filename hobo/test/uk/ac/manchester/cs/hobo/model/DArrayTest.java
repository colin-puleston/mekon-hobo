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

package uk.ac.manchester.cs.hobo.model;

import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
public class DArrayTest extends DFieldTest {

	private DObject dr = createDObject("ROOT");
	private DObject da = createDObject("A");
	private DObject db = createDObject("B");
	private DObject dc = createDObject("C");
	private DObject dd = createDObject("D");

	private IFrame fr = dr.getFrame();
	private IFrame fa = da.getFrame();
	private IFrame fb = db.getFrame();
	private IFrame fc = dc.getFrame();
	private IFrame fd = dd.getFrame();

	private DArray<DObject> array = createArray();

	public DArrayTest() {

		addSuperFrame(fa.getType(), fr.getType());
		addSuperFrame(fb.getType(), fr.getType());
		addSuperFrame(fc.getType(), fr.getType());
	}

	@Test
	public void test_directUpdates() {

		array.add(da);
		testSlotValues(fa);
		testArrayValues(da);

		array.addAll(Arrays.asList(db, dc));
		testSlotValues(fa, fb, fc);
		testArrayValues(da, db, dc);

		array.remove(db);
		testSlotValues(fa, fc);
		testArrayValues(da, dc);

		array.update(Arrays.asList(da, db));
		testSlotValues(fa, fb);
		testArrayValues(da, db);

		array.clear();
		testSlotValues();
		testArrayValues();
	}

	@Test
	public void test_backDoorUpdates() {

		addSlotValues(fa);
		testSlotValues(fa);
		testArrayValues(da);

		addSlotValues(fb, fc);
		testSlotValues(fa, fb, fc);
		testArrayValues(da, db, dc);

		removeSlotValue(fb);
		testSlotValues(fa, fc);
		testArrayValues(da, dc);

		updateSlotValues(fa, fb);
		testSlotValues(fa, fb);
		testArrayValues(da, db);

		clearSlotValues();
		testSlotValues();
		testArrayValues();
	}

	@Test(expected = KAccessException.class)
	public void test_illegalDirectUpdateFails() {

		array.add(dd);
	}

	@Test(expected = KAccessException.class)
	public void test_illegalBackDoorUpdateFails() {

		addSlotValues(fd);
	}

	private DArray<DObject> createArray() {

		return createDObjectArray(CCardinality.REPEATABLE_TYPES, fr.getType());
	}

	private void addSuperFrame(CFrame sub, CFrame sup) {

		FramesTestUtils.addSuperFrame(sub, sup);
	}

	private void addSlotValues(IValue... values) {

		addSlotValues(array, values);
	}

	private void updateSlotValues(IValue... values) {

		updateSlotValues(array, values);
	}

	private void removeSlotValue(IValue value) {

		removeSlotValue(array, value);
	}

	private void clearSlotValues() {

		clearSlotValues(array);
	}

	private void testSlotValues(IValue... values) {

		testSlotValues(array, values);
	}

	private void testArrayValues(DObject... expectValues) {

		testList(array.getAll(), Arrays.asList(expectValues));
	}

	private <E>void testList(List<? extends E> got, List<? extends E> expected) {

		MekonTestUtils.testList(got, expected);
	}
}
