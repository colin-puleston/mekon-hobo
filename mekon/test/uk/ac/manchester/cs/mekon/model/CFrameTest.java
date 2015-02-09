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
public class CFrameTest extends CValueTest<CFrame> {

	static private final List<CFrame> NO_CFRAMES = Collections.emptyList();

	@Test
	public void test_addAndRemove() {

		CModel model = getModel();

		CFrame a = createCFrame("A");
		CFrame b = createCFrame("B");
		CFrame c = createCFrame("C");

		addSuperFrame(b, a);
		addSuperFrame(c, b);

		testList(model.getFrames().asList(), list(a, b, c));
		testList(a.getSupers(), getRootFrameAsList());
		testList(a.getSubs(), list(b));
		testList(b.getSupers(), list(a));
		testList(b.getSubs(), list(c));
		testList(c.getSupers(), list(b));
		testList(c.getSubs(), NO_CFRAMES);

		model.removeFrame(b.asModelFrame());

		testList(model.getFrames().asList(), list(a, c));
		testList(a.getSupers(), getRootFrameAsList());
		testList(a.getSubs(), list(c));
		testList(c.getSupers(), list(a));
		testList(c.getSubs(), NO_CFRAMES);
	}

	@Test(expected = KModelException.class)
	public void test_cycleInHierarchyProducesException_1() {

		CFrame a = createCFrame("A");
		CFrame b = createCFrame("B");
		CFrame c = createCFrame("C");

		addSuperFrame(b, a);
		addSuperFrame(c, b);
		addSuperFrame(a, c);
	}

	@Test(expected = KModelException.class)
	public void test_cycleInHierarchyProducesException_2() {

		CFrame a = createCFrame("A");
		CFrame b = createHiddenCFrame("B");
		CFrame c = createCFrame("C");

		addSuperFrame(b, a);
		addSuperFrame(c, b);
		addSuperFrame(a, c);
	}

	@Test
	public void test_subsumptionTesting_1() {

		CFrame a = createCFrame("A");
		CFrame b = createCFrame("B");
		CFrame c = createCFrame("C");
		CFrame d = createCFrame("D");

		addSuperFrame(b, a);
		addSuperFrame(c, b);

		testMutualSubsumption(a, a);
		testStrictSubsumption(a, b);
		testStrictSubsumption(a, c);
		testNeitherSubsumption(a, d);
	}

	@Test
	public void test_subsumptionTesting_2() {

		CFrame a = createCFrame("A");
		CFrame b = createHiddenCFrame("B");
		CFrame c = createCFrame("C");
		CFrame d = createHiddenCFrame("D");

		addSuperFrame(b, a);
		addSuperFrame(c, b);

		testMutualSubsumption(a, a);
		testStrictSubsumption(a, b);
		testStrictSubsumption(a, c);
		testNeitherSubsumption(a, d);
	}

	@Test
	public void test_subsumptionTesting_3() {

		CFrame a = createHiddenCFrame("A");
		CFrame b = createHiddenCFrame("B");
		CFrame c = createHiddenCFrame("C");
		CFrame d = createHiddenCFrame("D");

		addSuperFrame(b, a);
		addSuperFrame(c, b);

		testMutualSubsumption(a, a);
		testStrictSubsumption(a, b);
		testStrictSubsumption(a, c);
		testNeitherSubsumption(a, d);
	}

	@Test
	public void test_hierarchyBuilding() {

		CFrame er = createCFrame("EXPOSED-ROOT");
		CFrame hr = createHiddenCFrame("HIDDEN-ROOT");
		CFrame e1 = createCFrame("EXPOSED-1");
		CFrame e2 = createCFrame("EXPOSED-2");
		CFrame h1 = createHiddenCFrame("HIDDEN-1");

		addSuperFrame(e1, er);
		addSuperFrame(e2, er);
		addSuperFrame(h1, er);
		addSuperFrame(h1, hr);

		testSubs(er, list(e1, e2, h1));
		testSubs(hr, list(h1));
		testSupers(e1, list(er));
		testSupers(e2, list(er));
		testSupers(h1, list(er, hr));

		testSubs(er, CVisibility.EXPOSED, list(e1, e2));
		testSubs(hr, CVisibility.EXPOSED, NO_CFRAMES);
		testSupers(e1, CVisibility.EXPOSED, list(er));
		testSupers(e2, CVisibility.EXPOSED, list(er));
		testSupers(h1, CVisibility.EXPOSED, list(er));

		testSubs(er, CVisibility.HIDDEN, list(h1));
		testSubs(hr, CVisibility.HIDDEN, list(h1));
		testSupers(e1, CVisibility.HIDDEN, NO_CFRAMES);
		testSupers(e2, CVisibility.HIDDEN, NO_CFRAMES);
		testSupers(h1, CVisibility.HIDDEN, list(hr));
	}

	@Test
	public void test_hierarchyNormalisation_1() {

		CFrame er = createCFrame("EXPOSED-ROOT");
		CFrame e1 = createCFrame("EXPOSED-1");
		CFrame e2 = createCFrame("EXPOSED-2");
		CFrame e3 = createCFrame("EXPOSED-3");

		addSuperFrame(e1, er);
		addSuperFrame(e2, er);
		addSuperFrame(e3, er);

		addSuperFrame(e2, e1);
		addSuperFrame(e3, e2);

		normaliseCFramesHierarchy();

		testSupers(e2, list(e1));
		testSupers(e3, list(e2));
	}

	@Test
	public void test_hierarchyNormalisation_2() {

		CFrame hr = createCFrame("HIDDEN-ROOT");
		CFrame h1 = createCFrame("HIDDEN-1");
		CFrame h2 = createCFrame("HIDDEN-2");
		CFrame h3 = createCFrame("HIDDEN-3");

		addSuperFrame(h1, hr);
		addSuperFrame(h2, hr);
		addSuperFrame(h3, hr);

		addSuperFrame(h2, h1);
		addSuperFrame(h3, h2);

		normaliseCFramesHierarchy();

		testSupers(h2, list(h1));
		testSupers(h3, list(h2));
	}

	@Test
	public void test_hierarchyNormalisation_3() {

		CFrame er = createCFrame("EXPOSED-ROOT");
		CFrame ea1 = createCFrame("EXPOSED-a1");
		CFrame ea2 = createCFrame("EXPOSED-a2");
		CFrame h1 = createHiddenCFrame("HIDDEN-1");
		CFrame h2 = createHiddenCFrame("HIDDEN-2");
		CFrame h3 = createHiddenCFrame("HIDDEN-3");
		CFrame eb1 = createCFrame("EXPOSED-b1");
		CFrame eb2 = createCFrame("EXPOSED-b2");

		addSuperFrame(ea1, er);
		addSuperFrame(ea2, er);
		addSuperFrame(h1, er);
		addSuperFrame(h2, er);
		addSuperFrame(h3, er);
		addSuperFrame(eb1, er);
		addSuperFrame(eb2, er);

		addSuperFrame(ea2, ea1);
		addSuperFrame(h1, ea2);
		addSuperFrame(h2, h1);
		addSuperFrame(h3, h2);
		addSuperFrame(eb1, h3);
		addSuperFrame(eb2, eb1);

		normaliseCFramesHierarchy();

		testSupers(er, getRootFrameAsList());
		testSupers(ea1, list(er));
		testSupers(ea2, list(ea1));
		testSupers(h1, list(ea2));
		testSupers(h2, list(h1));
		testSupers(h3, list(h2));
		testSupers(eb1, list(h3, ea2));
		testSupers(eb2, list(eb1));
	}

	@Test
	public void test_hierarchyNormalisation_4() {

		CFrame er = createCFrame("EXPOSED-ROOT");
		CFrame ea1 = createCFrame("EXPOSED-a1");
		CFrame ea2 = createCFrame("EXPOSED-a2");
		CFrame h1 = createHiddenCFrame("HIDDEN-1");
		CFrame h2 = createHiddenCFrame("HIDDEN-2");
		CFrame eb1 = createCFrame("EXPOSED-b1");
		CFrame eb2 = createCFrame("EXPOSED-b2");

		addSuperFrame(ea1, er);
		addSuperFrame(ea2, er);
		addSuperFrame(h1, ea1);
		addSuperFrame(h2, ea2);
		addSuperFrame(eb1, h1);
		addSuperFrame(eb2, h2);

		normaliseCFramesHierarchy();

		testSupers(er, getRootFrameAsList());
		testSupers(ea1, list(er));
		testSupers(ea2, list(er));
		testSupers(h1, list(ea1));
		testSupers(h2, list(ea2));
		testSupers(eb1, list(h1, ea1));
		testSupers(eb2, list(h2, ea2));
	}

	@Test
	public void test_hierarchyNormalisation_5() {

		CFrame er = createCFrame("EXPOSED-ROOT");
		CFrame ea1 = createCFrame("EXPOSED-a1");
		CFrame ea2 = createCFrame("EXPOSED-a2");
		CFrame h1 = createHiddenCFrame("HIDDEN-1");
		CFrame h2 = createHiddenCFrame("HIDDEN-2");
		CFrame eb1 = createCFrame("EXPOSED-b1");

		addSuperFrame(ea1, er);
		addSuperFrame(ea2, ea1);
		addSuperFrame(h1, ea1);
		addSuperFrame(h2, ea2);
		addSuperFrame(eb1, h1);
		addSuperFrame(eb1, h2);

		normaliseCFramesHierarchy();

		testSupers(er, getRootFrameAsList());
		testSupers(ea1, list(er));
		testSupers(ea2, list(ea1));
		testSupers(h1, list(ea1));
		testSupers(h2, list(ea2));
		testSupers(eb1, list(h1, h2, ea2));
	}

	@Test
	public void test_addValidSlots() {

		testAddSlots(true);
	}

	@Test(expected = KModelException.class)
	public void test_addInvalidSlots() {

		testAddSlots(false);
	}

	private void testSupers(CFrame frame, List<CFrame> expected) {

		testSupers(frame, CVisibility.ALL, expected);
	}

	private void testSubs(CFrame frame, List<CFrame> expected) {

		testSubs(frame, CVisibility.ALL, expected);
	}

	private void testSupers(
					CFrame frame,
					CVisibility visibility,
					List<CFrame> expected) {

		testListContents(frame.getSupers(visibility), expected);
	}

	private void testSubs(
					CFrame frame,
					CVisibility visibility,
					List<CFrame> expected) {

		testListContents(frame.getSubs(visibility), expected);
	}

	private void testAddSlots(boolean addValidatingLink) {

		CModelFrame a = createCFrame("A");
		CModelFrame b = createCFrame("B");
		CFrame va = createCFrame("VA");
		CFrame vb = createCFrame("VB");

		addSuperFrame(b, a);

		if (addValidatingLink) {

			addSuperFrame(vb, va);
		}

		createCSlot(a, "S", CCardinality.FREE, va);
		createCSlot(b, "S", CCardinality.FREE, vb);

		b.getSlots().validateAll(b);
	}

	private List<CFrame> getRootFrameAsList() {

		return list(getModel().getRootFrame());
	}
}
