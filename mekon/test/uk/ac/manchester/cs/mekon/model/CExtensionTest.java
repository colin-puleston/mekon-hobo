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
public class CExtensionTest extends CValueTest<CFrame> {

	private CModelFrame a = createCFrame("A");
	private CModelFrame b = createCFrame("B");
	private CModelFrame c = createCFrame("C");
	private CModelFrame cx = createCFrame("CX");

	private CIdentity sab = createIdentity("SAB");
	private CIdentity sac = createIdentity("SAC");
	private CIdentity sbb = createIdentity("SBB");
	private CIdentity sbc = createIdentity("SBC");

	public CExtensionTest() {

		addSuperFrame(cx, c);

		createCSlot(a, sab, CCardinality.REPEATABLE_TYPES, b);
		createCSlot(a, sac, CCardinality.REPEATABLE_TYPES, c);
		createCSlot(b, sbb, CCardinality.REPEATABLE_TYPES, b);
		createCSlot(b, sbc, CCardinality.REPEATABLE_TYPES, c);
	}

	@Test
	public void testEqualities() {

		CExtension extn_a = createAExtension(null);
		CExtension extn_a_c = createAExtension(c);
		CExtension extn_a_cx = createAExtension(cx);

		assertEquals(extn_a, extn_a);
		assertEquals(extn_a_c, extn_a_c);
		assertEquals(extn_a_cx, extn_a_cx);

		assertEquals(extn_a, createAExtension(null));
		assertEquals(extn_a_c, createAExtension(c));
		assertEquals(extn_a_cx, createAExtension(cx));

		assertFalse(a.equals(extn_a_c));
		assertFalse(a.equals(extn_a_cx));

		assertFalse(extn_a.equals(extn_a_c));
		assertFalse(extn_a.equals(extn_a_cx));
		assertFalse(extn_a_c.equals(extn_a_cx));
	}

	@Test
	public void testSubsumptions() {

		CExtension extn_a = createAExtension(null);
		CExtension extn_a_c = createAExtension(c);
		CExtension extn_a_cx = createAExtension(cx);

		CDisjunction disj_abc = createDisjunction(a, b, c);

		testStrictSubsumption(a, extn_a);
		testStrictSubsumption(a, extn_a_c);
		testStrictSubsumption(a, extn_a_cx);

		testStrictSubsumption(disj_abc, extn_a);
		testStrictSubsumption(disj_abc, extn_a_c);
		testStrictSubsumption(disj_abc, extn_a_cx);

		testStrictSubsumption(extn_a, extn_a_c);
		testStrictSubsumption(extn_a, extn_a_cx);
		testStrictSubsumption(extn_a_c, extn_a_cx);

		testMutualSubsumption(extn_a, extn_a);
		testMutualSubsumption(extn_a_c, extn_a_c);
		testMutualSubsumption(extn_a_cx, extn_a_cx);

		testMutualSubsumption(extn_a, createAExtension(null));
		testMutualSubsumption(extn_a_c, createAExtension(c));
		testMutualSubsumption(extn_a_cx, createAExtension(cx));
	}

	private CExtension createAExtension(CFrame fcValue) {

		CExtender extender = new CExtender(a);

		extender.addSlotValue(sac, c);

		if (fcValue != null) {

			extender.addSlotValue(sab, createBExtension(fcValue));
		}

		return (CExtension)extender.extend();
	}

	private CExtension createBExtension(CFrame fcValue) {

		CExtender extender = new CExtender(b);

		extender.addSlotValue(sbb, b);
		extender.addSlotValue(sbc, fcValue);

		return (CExtension)extender.extend();
	}

	private CDisjunction createDisjunction(CFrame... disjuncts) {

		return (CDisjunction)CFrame.resolveDisjunction(Arrays.asList(disjuncts));
	}
}
