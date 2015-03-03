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

	private IFrame r = dr.getFrame();
	private IFrame a = da.getFrame();
	private IFrame b = db.getFrame();
	private IFrame c = dc.getFrame();
	private IFrame d = dd.getFrame();

	public DArrayTest() {

		addSuperFrame(a.getType(), r.getType());
		addSuperFrame(b.getType(), r.getType());
		addSuperFrame(c.getType(), r.getType());
	}

	@Test
	public void test_directUpdates() {

		DArray<DObject> array = createDObjectArray(CCardinality.FREE, r.getType());

		array.add(da);
		testSlotValues(array, a);
		testArrayValues(array, da);

		array.addAll(list(db, dc));
		testSlotValues(array, a, b, c);
		testArrayValues(array, da, db, dc);

		array.remove(db);
		testSlotValues(array, a, c);
		testArrayValues(array, da, dc);

		array.update(list(da, db));
		testSlotValues(array, a, b);
		testArrayValues(array, da, db);

		array.clear();
		testSlotValues(array);
		testArrayValues(array);

	}

	@Test
	public void test_backDoorUpdates() {

		DArray<DObject> array = createDObjectArray(CCardinality.FREE, r.getType());

		addSlotValues(array, a);
		testSlotValues(array, a);
		testArrayValues(array, da);

		addSlotValues(array, b, c);
		testSlotValues(array, a, b, c);
		testArrayValues(array, da, db, dc);

		removeSlotValue(array, b);
		testSlotValues(array, a, c);
		testArrayValues(array, da, dc);

		updateSlotValues(array, a, b);
		testSlotValues(array, a, b);
		testArrayValues(array, da, db);

		clearSlotValues(array);
		testSlotValues(array);
		testArrayValues(array);
	}

	@Test(expected = KAccessException.class)
	public void test_illegalDirectUpdateFails() {

		createDObjectArray(CCardinality.FREE, r.getType()).add(dd);
	}

	@Test(expected = KAccessException.class)
	public void test_illegalBackDoorUpdateFails() {

		addSlotValues(createDObjectArray(CCardinality.FREE, r.getType()), d);
	}

	private void testArrayValues(
					DArray<DObject> array,
					DObject... expectValues) {

		testList(array.getAll(), list(expectValues));
	}
}
