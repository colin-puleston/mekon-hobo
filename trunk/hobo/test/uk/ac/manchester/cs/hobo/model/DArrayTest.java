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

	private CFrame r = createCFrame("ROOT");
	private CFrame a = createCFrame("A");
	private CFrame b = createCFrame("B");
	private CFrame c = createCFrame("C");
	private CFrame d = createCFrame("D");

	public DArrayTest() {

		addSuperFrame(a, r);
		addSuperFrame(b, r);
		addSuperFrame(c, r);
	}

	@Test
	public void test_directUpdates() {

		DArray<CFrame> array = createConceptArray(CCardinality.FREE, r);

		array.add(a);
		testArrayValues(array, a);

		array.addAll(list(b, c));
		testArrayValues(array, a, b, c);

		array.remove(b);
		testArrayValues(array, a, c);

		array.update(list(a, b));
		testArrayValues(array, a, b);

		array.clear();
		testArrayValues(array);

	}

	@Test
	public void test_backDoorUpdates() {

		DArray<CFrame> array = createConceptArray(CCardinality.FREE, r);

		addSlotValues(array, a);
		testArrayValues(array, a);

		addSlotValues(array, b, c);
		testArrayValues(array, a, b, c);

		removeSlotValue(array, b);
		testArrayValues(array, a, c);

		updateSlotValues(array, a, b);
		testArrayValues(array, a, b);

		clearSlotValues(array);
		testArrayValues(array);
	}

	@Test(expected = KAccessException.class)
	public void test_illegalDirectUpdateFails() {

		createConceptArray(CCardinality.FREE, r).add(d);
	}

	@Test(expected = KAccessException.class)
	public void test_illegalBackDoorUpdateFails() {

		addSlotValues(createConceptArray(CCardinality.FREE, r), d);
	}

	private void testArrayValues(DArray<CFrame> array, CFrame... expectValues) {

		testList(array.getAll(), list(expectValues));
		testSlotValues(array, expectValues);
	}
}
