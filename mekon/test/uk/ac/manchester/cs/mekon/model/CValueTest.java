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

import static org.junit.Assert.*;

/**
 * @author Colin Puleston
 */
public abstract class CValueTest<V extends CValue<?>> extends GeneralFramesModelTest {

	void testStrictSubsumption(V subsumer, V subsumed) {

		testSubsumption(subsumer, subsumed);
		testNonSubsumption(subsumed, subsumer);
	}

	void testMutualSubsumption(V value1, V value2) {

		testSubsumption(value1, value2);
		testSubsumption(value2, value1);
	}

	void testNeitherSubsumption(V value1, V value2) {

		testNonSubsumption(value1, value2);
		testNonSubsumption(value2, value1);
	}

	private void testSubsumption(V subsumer, V subsumed) {

		assertTrue(
			getSubsumptionTestFailMessage(subsumer, subsumed, true),
			subsumer.subsumes(subsumed));
		assertTrue(
			getSubsumptionTestFailMessage(subsumer, subsumed, true),
			subsumed.subsumedBy(subsumer));
	}

	private void testNonSubsumption(V subsumer, V subsumed) {

		assertFalse(
			getSubsumptionTestFailMessage(subsumer, subsumed, false),
			subsumer.subsumes(subsumed));
		assertFalse(
			getSubsumptionTestFailMessage(subsumer, subsumed, false),
			subsumed.subsumedBy(subsumer));
	}

	private String getSubsumptionTestFailMessage(
						V subsumer,
						V subsumed,
						boolean expectedSubsumtion) {

		String modifier = expectedSubsumtion ? "" : "non-";

		return "Expected " + modifier + "subsumption fails: "
					+ "\nSubsumer: " + subsumer
					+ "\nSubsumed: " + subsumed;
	}
}
