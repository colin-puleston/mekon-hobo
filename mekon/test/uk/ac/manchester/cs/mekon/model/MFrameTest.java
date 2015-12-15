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

/**
 * @author Colin Puleston
 */
public class MFrameTest extends CValueTest<MFrame> {

	@Test
	public void test_subsumptionTesting() {

		CFrame a = createCFrame("A");
		CFrame b = createHiddenCFrame("B");
		CFrame c = createCFrame("C");
		CFrame d = createCFrame("D");

		addSuperFrame(b, a);
		addSuperFrame(c, b);

		testMutualSubsumption(a.getType(), a.getType());
		testStrictSubsumption(a.getType(), b.getType());
		testStrictSubsumption(a.getType(), c.getType());
		testNeitherSubsumption(a.getType(), d.getType());
	}

	private void addSuperFrame(CFrame sub, CFrame sup) {

		FramesTestUtils.addSuperFrame(sub, sup);
	}
}
