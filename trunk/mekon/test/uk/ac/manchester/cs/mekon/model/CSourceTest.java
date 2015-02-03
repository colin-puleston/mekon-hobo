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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Colin Puleston
 */
public class CSourceTest {

	@Test
	public void test_combineWith() {

		testCombine(CSource.INTERNAL, CSource.INTERNAL, CSource.INTERNAL);
		testCombine(CSource.EXTERNAL, CSource.EXTERNAL, CSource.EXTERNAL);
		testCombine(CSource.DUAL, CSource.DUAL, CSource.DUAL);
		testCombine(CSource.UNSPECIFIED, CSource.UNSPECIFIED, CSource.UNSPECIFIED);

		testCombine(CSource.INTERNAL, CSource.EXTERNAL, CSource.DUAL);

		testCombine(CSource.INTERNAL, CSource.UNSPECIFIED, CSource.INTERNAL);
		testCombine(CSource.EXTERNAL, CSource.UNSPECIFIED, CSource.EXTERNAL);
		testCombine(CSource.DUAL, CSource.UNSPECIFIED, CSource.DUAL);

		testCombine(CSource.INTERNAL, CSource.DUAL, CSource.DUAL);
		testCombine(CSource.EXTERNAL, CSource.DUAL, CSource.DUAL);
		testCombine(CSource.UNSPECIFIED, CSource.DUAL, CSource.DUAL);
	}

	private void testCombine(CSource s1, CSource s2, CSource expect) {

		testCombineOneWay(s1, s2, expect);
		testCombineOneWay(s2, s1, expect);
	}

	private void testCombineOneWay(CSource s1, CSource s2, CSource expect) {

		CSource combo = s1.combineWith(s2);

		assertTrue("Unexpected source-combination: " + combo, combo == expect);
	}
}
