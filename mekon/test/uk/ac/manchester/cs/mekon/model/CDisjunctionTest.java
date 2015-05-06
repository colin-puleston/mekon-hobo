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

/**
 * @author Colin Puleston
 */
public class CDisjunctionTest extends CValueTest<CFrame> {

	static private final List<CFrame> NO_CFRAMES = Collections.emptyList();

	private CFrame a = createCFrame("A");
	private CFrame b = createCFrame("B");
	private CFrame c = createCFrame("C");
	private CFrame d = createCFrame("D");

	private CFrame a_sup = createCFrame("A-SUPER");
	private CFrame b_sub = createCFrame("B-SUB");

	public CDisjunctionTest() {

		addSuperFrame(a, a_sup);
		addSuperFrame(b_sub, b);
	}

	@Test
	public void testSupers() {

		CFrame ab_sup = createCFrame("AB_SUP");
		CFrame ac_sup = createCFrame("BC_SUP");
		CFrame abc_sup_1 = createCFrame("ABC_SUP_1");
		CFrame abc_sup_2 = createCFrame("ABC_SUP_2");
		CFrame singleCommonAnc = createCFrame("SINGLE_COMMON_ANC");

		List<CFrame> expectSups = Arrays.asList(abc_sup_1, abc_sup_2);

		addSuperFrame(a, ab_sup);
		addSuperFrame(b, ab_sup);
		addSuperFrame(a, ac_sup);
		addSuperFrame(c, ac_sup);
		addSuperFrame(a, abc_sup_1);
		addSuperFrame(b, abc_sup_1);
		addSuperFrame(c, abc_sup_1);
		addSuperFrame(ab_sup, abc_sup_2);
		addSuperFrame(ac_sup, abc_sup_2);
		addSuperFrame(abc_sup_1, singleCommonAnc);
		addSuperFrame(abc_sup_2, singleCommonAnc);

		CDisjunction abc = createDisjunction(a, b, c);

		testSupers(abc, CVisibility.ALL, expectSups);
		testSupers(abc, CVisibility.EXPOSED, expectSups);
		testSupers(abc, CVisibility.HIDDEN, NO_CFRAMES);

		assertEquals(abc.getModelFrame(), singleCommonAnc);
	}

	@Test
	public void testSubs() {

		CDisjunction abc = createDisjunction(a, b, c);

		testSubs(abc, CVisibility.ALL, Arrays.asList(a, b, c));
		testSubs(abc, CVisibility.EXPOSED, Arrays.asList(a, b, c));
		testSubs(abc, CVisibility.HIDDEN, NO_CFRAMES);
	}

	@Test
	public void testEquality() {

		CDisjunction ab = createDisjunction(a, b);
		CDisjunction abc = createDisjunction(a, b, c);
		CDisjunction abc_2 = createDisjunction(a, b, c);

		testMutualEquality(abc, abc_2);

		testMutualInequality(abc, a);
		testMutualInequality(abc, ab);
	}

	@Test
	public void testSimpleSubsumptions() {

		CDisjunction ab = createDisjunction(a, b);
		CDisjunction abc = createDisjunction(a, b, c);
		CDisjunction abc_2 = createDisjunction(a, b, c);
		CDisjunction abd = createDisjunction(a, b, d);

		testStrictSubsumption(abc, a);
		testStrictSubsumption(abc, ab);

		testMutualSubsumption(abc, abc);
		testMutualSubsumption(abc_2, abc);

		testNeitherSubsumption(abc, abd);
		testNeitherSubsumption(a_sup, abc);
	}

	@Test
	public void testComplexSubsumptions() {

		CDisjunction abc = createDisjunction(a, b, c);
		CDisjunction a_sup_bc = createDisjunction(a_sup, b, c);
		CDisjunction ab_sub_c = createDisjunction(a, b_sub, c);

		testStrictSubsumption(a_sup_bc, a);
		testStrictSubsumption(a_sup_bc, b);

		testStrictSubsumption(ab_sub_c, a);
		testNeitherSubsumption(ab_sub_c, b);

		testStrictSubsumption(abc, ab_sub_c);
		testStrictSubsumption(a_sup_bc, ab_sub_c);
	}

	private CDisjunction createDisjunction(CFrame... disjuncts) {

		return (CDisjunction)CFrame.resolveDisjunction(Arrays.asList(disjuncts));
	}

	private void testSupers(CFrame frame, CVisibility visibility, List<CFrame> expected) {

		testListContents(frame.getSupers(visibility), expected);
	}

	private void testSubs(CFrame frame, CVisibility visibility, List<CFrame> expected) {

		testListContents(frame.getSubs(visibility), expected);
	}

	private void testMutualEquality(CFrame f1, CFrame f2) {

		assertTrue(f1.equals(f2));
		assertTrue(f2.equals(f1));
	}

	private void testMutualInequality(CFrame f1, CFrame f2) {

		assertFalse(f1.equals(f2));
		assertFalse(f2.equals(f1));
	}
}
