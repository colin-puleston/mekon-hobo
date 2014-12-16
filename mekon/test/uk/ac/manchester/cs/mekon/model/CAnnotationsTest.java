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
public class CAnnotationsTest implements CAnnotatable {

	private CAnnotations annotations = new CAnnotations(this);

    public CAnnotationsTest() {

		CAnnotationsEditor e = annotations.createEditor();

		e.add("A", "A1");
		e.add("B", "B1");
		e.add("B", "B2");
    }

	public CAnnotations getAnnotations() {

		return annotations;
	}

	@Test
	public void test_getOneWhenOne() {

		testAValue(annotations.getOne("A"));
	}

	@Test(expected = KAccessException.class)
	public void test_getOneWhenNone() {

		annotations.getOne("C");
	}

	@Test(expected = KAccessException.class)
	public void test_getOneWhenMoreThanOne() {

		annotations.getOne("B");
	}

	@Test
	public void test_getOneOrNoneWhenOne() {

		testAValue(annotations.getOneOrNone("A"));
	}

	@Test
	public void test_getOneOrNoneWhenNone() {

		testNullValue(annotations.getOneOrNone("C"));
	}

	@Test(expected = KAccessException.class)
	public void test_getOneOrNoneWhenMoreThanOne() {

		annotations.getOneOrNone("B");
	}

	@Test
	public void test_getOneExpectedType() {

		testAValue(annotations.getOne("A", String.class));
	}

	@Test(expected = KAccessException.class)
	public void test_getOneUnexpectedType() {

		annotations.getOne("A", Integer.class);
	}

	@Test
	public void test_getAll() {

		testBValues(annotations.getAll("B"));
	}

	@Test
	public void test_getAllExpectedType() {

		testBValues(annotations.getAll("B", String.class));
	}

	@Test(expected = KAccessException.class)
	public void test_getAllUnexpectedType() {

		annotations.getAll("B", Integer.class);
	}

	private void testAValue(Object got) {

		assertTrue("Unexpected value: " + got, got.equals("A1"));
	}

	private void testNullValue(Object got) {

		assertTrue("Unexpected non-null value: " + got, got == null);
	}

	private void testBValues(List<?> got) {

		List<Object> expected = new ArrayList<Object>();

		expected.add("B1");
		expected.add("B2");

		assertEquals(expected, got);
	}
}
