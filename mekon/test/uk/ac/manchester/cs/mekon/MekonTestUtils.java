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

package uk.ac.manchester.cs.mekon;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Colin Puleston
 */
public class MekonTestUtils {

	static public <E>void testList(List<? extends E> got, List<? extends E> expected) {

		assertTrue(
			"Unexpected list: expected: " + expected + ", got: " + got,
			got.equals(expected));
	}

	static public <E>void testListContents(List<? extends E> got, List<? extends E> expected) {

		assertTrue(
			"Unexpected list-contents: expected: " + expected + ", got: " + got,
			got.size() == expected.size());

		testSet(new HashSet<E>(got), new HashSet<E>(expected));
	}

	static public <E>void testSet(Set<? extends E> got, Set<? extends E> expected) {

		assertTrue(
			"Unexpected set: expected: " + expected + ", got: " + got,
			got.equals(expected));
	}
}
