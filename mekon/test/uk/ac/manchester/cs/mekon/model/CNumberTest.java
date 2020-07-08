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

import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * @author Colin Puleston
 */
public class CNumberTest extends CValueTest<CNumber> {

	@Test
	public void test_subsumptionTesting() {

		CNumber unbound = getNumber();
		CNumber r1_10 = getNumber(1, 10);
		CNumber r1_9 = getNumber(1, 9);
		CNumber r2_10 = getNumber(2, 10);
		CNumber r2_9 = getNumber(2, 9);

		testMutualSubsumption(unbound, getNumber());
		testMutualSubsumption(r1_10, r1_10);
		testStrictSubsumption(unbound, r1_10);
		testStrictSubsumption(r1_10, r1_9);
		testStrictSubsumption(r1_10, r2_10);
		testStrictSubsumption(r1_10, r2_9);
		testNeitherSubsumption(r1_9, r2_10);
	}

	@Test
	public void test_intersectionTesting() {

		CNumber r1_10 = getNumber(1, 10);
		CNumber r0_9 = getNumber(0, 9);
		CNumber r2_9 = getNumber(2, 9);
		CNumber r2_10 = getNumber(2, 10);
		CNumber r2_11 = getNumber(2, 11);
		CNumber r11_20 = getNumber(11, 20);

		testIntersecting(r1_10, r0_9);
		testIntersecting(r1_10, r2_9);
		testIntersecting(r1_10, r2_10);
		testIntersecting(r1_10, r2_11);
		testNonIntersecting(r1_10, r11_20);
	}

	private CNumber getNumber() {

		return CNumberFactory.INTEGER;
	}

	private CNumber getNumber(Integer min, Integer max) {

		return CNumberFactory.range(min, max);
	}

	private void testIntersecting(CNumber n1, CNumber n2) {

		assertTrue(n1.intersectsWith(n2));
	}

	private void testNonIntersecting(CNumber n1, CNumber n2) {

		assertFalse(n1.intersectsWith(n2));
	}
}
