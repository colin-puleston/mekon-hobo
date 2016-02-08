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
import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * @author Colin Puleston
 */
public class CFrameTest extends CValueTest<CFrame> {

	static private final List<CFrame> NO_CFRAMES = Collections.emptyList();

	private TestCModel model = new TestCModel();
	private TestCFrames frames = model.cFrames;
	private TestCSlots slots = frames.repeatTypesSlots;

	@Test
	public void test_addAndRemove() {

		CFrame a = frames.create("A");
		CFrame b = frames.create("B");
		CFrame c = frames.create("C");

		addSuperFrame(b, a);
		addSuperFrame(c, b);

		testList(getModelFrames(), Arrays.asList(a, b, c));
		testList(a.getSupers(), getRootFrameAsList());
		testList(a.getSubs(), Arrays.asList(b));
		testList(b.getSupers(), Arrays.asList(a));
		testList(b.getSubs(), Arrays.asList(c));
		testList(c.getSupers(), Arrays.asList(b));
		testList(c.getSubs(), NO_CFRAMES);

		model.model.removeFrame(b.asAtomicFrame());

		testList(getModelFrames(), Arrays.asList(a, c));
		testList(a.getSupers(), getRootFrameAsList());
		testList(a.getSubs(), Arrays.asList(c));
		testList(c.getSupers(), Arrays.asList(a));
		testList(c.getSubs(), NO_CFRAMES);
	}

	@Test(expected = KModelException.class)
	public void test_cycleInHierarchyProducesException_1() {

		CFrame a = frames.create("A");
		CFrame b = frames.create("B");
		CFrame c = frames.create("C");

		addSuperFrame(b, a);
		addSuperFrame(c, b);
		addSuperFrame(a, c);
	}

	@Test(expected = KModelException.class)
	public void test_cycleInHierarchyProducesException_2() {

		CFrame a = frames.create("A");
		CFrame b = frames.createHidden("B");
		CFrame c = frames.create("C");

		addSuperFrame(b, a);
		addSuperFrame(c, b);
		addSuperFrame(a, c);
	}

	@Test
	public void test_subsumptionTesting_1() {

		CFrame a = frames.create("A");
		CFrame b = frames.create("B");
		CFrame c = frames.create("C");
		CFrame d = frames.create("D");

		addSuperFrame(b, a);
		addSuperFrame(c, b);

		testMutualSubsumption(a, a);
		testStrictSubsumption(a, b);
		testStrictSubsumption(a, c);
		testNeitherSubsumption(a, d);
	}

	@Test
	public void test_subsumptionTesting_2() {

		CFrame a = frames.create("A");
		CFrame b = frames.createHidden("B");
		CFrame c = frames.create("C");
		CFrame d = frames.createHidden("D");

		addSuperFrame(b, a);
		addSuperFrame(c, b);

		testMutualSubsumption(a, a);
		testStrictSubsumption(a, b);
		testStrictSubsumption(a, c);
		testNeitherSubsumption(a, d);
	}

	@Test
	public void test_subsumptionTesting_3() {

		CFrame a = frames.createHidden("A");
		CFrame b = frames.createHidden("B");
		CFrame c = frames.createHidden("C");
		CFrame d = frames.createHidden("D");

		addSuperFrame(b, a);
		addSuperFrame(c, b);

		testMutualSubsumption(a, a);
		testStrictSubsumption(a, b);
		testStrictSubsumption(a, c);
		testNeitherSubsumption(a, d);
	}

	@Test
	public void test_hierarchyBuilding() {

		CFrame er = frames.create("EXPOSED-ROOT");
		CFrame hr = frames.createHidden("HIDDEN-ROOT");
		CFrame e1 = frames.create("EXPOSED-1");
		CFrame e2 = frames.create("EXPOSED-2");
		CFrame h1 = frames.createHidden("HIDDEN-1");

		addSuperFrame(e1, er);
		addSuperFrame(e2, er);
		addSuperFrame(h1, er);
		addSuperFrame(h1, hr);

		testSubs(er, Arrays.asList(e1, e2, h1));
		testSubs(hr, Arrays.asList(h1));
		testSupers(e1, Arrays.asList(er));
		testSupers(e2, Arrays.asList(er));
		testSupers(h1, Arrays.asList(er, hr));

		testSubs(er, CVisibility.EXPOSED, Arrays.asList(e1, e2));
		testSubs(hr, CVisibility.EXPOSED, NO_CFRAMES);
		testSupers(e1, CVisibility.EXPOSED, Arrays.asList(er));
		testSupers(e2, CVisibility.EXPOSED, Arrays.asList(er));
		testSupers(h1, CVisibility.EXPOSED, Arrays.asList(er));

		testSubs(er, CVisibility.HIDDEN, Arrays.asList(h1));
		testSubs(hr, CVisibility.HIDDEN, Arrays.asList(h1));
		testSupers(e1, CVisibility.HIDDEN, NO_CFRAMES);
		testSupers(e2, CVisibility.HIDDEN, NO_CFRAMES);
		testSupers(h1, CVisibility.HIDDEN, Arrays.asList(hr));
	}

	@Test
	public void test_hierarchyNormalisation_1() {

		CFrame er = frames.create("EXPOSED-ROOT");
		CFrame e1 = frames.create("EXPOSED-1");
		CFrame e2 = frames.create("EXPOSED-2");
		CFrame e3 = frames.create("EXPOSED-3");

		addSuperFrame(e1, er);
		addSuperFrame(e2, er);
		addSuperFrame(e3, er);

		addSuperFrame(e2, e1);
		addSuperFrame(e3, e2);

		model.normaliseCFramesHierarchy();

		testSupers(e2, Arrays.asList(e1));
		testSupers(e3, Arrays.asList(e2));
	}

	@Test
	public void test_hierarchyNormalisation_2() {

		CFrame hr = frames.create("HIDDEN-ROOT");
		CFrame h1 = frames.create("HIDDEN-1");
		CFrame h2 = frames.create("HIDDEN-2");
		CFrame h3 = frames.create("HIDDEN-3");

		addSuperFrame(h1, hr);
		addSuperFrame(h2, hr);
		addSuperFrame(h3, hr);

		addSuperFrame(h2, h1);
		addSuperFrame(h3, h2);

		model.normaliseCFramesHierarchy();

		testSupers(h2, Arrays.asList(h1));
		testSupers(h3, Arrays.asList(h2));
	}

	@Test
	public void test_hierarchyNormalisation_3() {

		CFrame er = frames.create("EXPOSED-ROOT");
		CFrame ea1 = frames.create("EXPOSED-a1");
		CFrame ea2 = frames.create("EXPOSED-a2");
		CFrame h1 = frames.createHidden("HIDDEN-1");
		CFrame h2 = frames.createHidden("HIDDEN-2");
		CFrame h3 = frames.createHidden("HIDDEN-3");
		CFrame eb1 = frames.create("EXPOSED-b1");
		CFrame eb2 = frames.create("EXPOSED-b2");

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

		model.normaliseCFramesHierarchy();

		testSupers(er, getRootFrameAsList());
		testSupers(ea1, Arrays.asList(er));
		testSupers(ea2, Arrays.asList(ea1));
		testSupers(h1, Arrays.asList(ea2));
		testSupers(h2, Arrays.asList(h1));
		testSupers(h3, Arrays.asList(h2));
		testSupers(eb1, Arrays.asList(h3, ea2));
		testSupers(eb2, Arrays.asList(eb1));
	}

	@Test
	public void test_hierarchyNormalisation_4() {

		CFrame er = frames.create("EXPOSED-ROOT");
		CFrame ea1 = frames.create("EXPOSED-a1");
		CFrame ea2 = frames.create("EXPOSED-a2");
		CFrame h1 = frames.createHidden("HIDDEN-1");
		CFrame h2 = frames.createHidden("HIDDEN-2");
		CFrame eb1 = frames.create("EXPOSED-b1");
		CFrame eb2 = frames.create("EXPOSED-b2");

		addSuperFrame(ea1, er);
		addSuperFrame(ea2, er);
		addSuperFrame(h1, ea1);
		addSuperFrame(h2, ea2);
		addSuperFrame(eb1, h1);
		addSuperFrame(eb2, h2);

		model.normaliseCFramesHierarchy();

		testSupers(er, getRootFrameAsList());
		testSupers(ea1, Arrays.asList(er));
		testSupers(ea2, Arrays.asList(er));
		testSupers(h1, Arrays.asList(ea1));
		testSupers(h2, Arrays.asList(ea2));
		testSupers(eb1, Arrays.asList(h1, ea1));
		testSupers(eb2, Arrays.asList(h2, ea2));
	}

	@Test
	public void test_hierarchyNormalisation_5() {

		CFrame er = frames.create("EXPOSED-ROOT");
		CFrame ea1 = frames.create("EXPOSED-a1");
		CFrame ea2 = frames.create("EXPOSED-a2");
		CFrame h1 = frames.createHidden("HIDDEN-1");
		CFrame h2 = frames.createHidden("HIDDEN-2");
		CFrame eb1 = frames.create("EXPOSED-b1");

		addSuperFrame(ea1, er);
		addSuperFrame(ea2, ea1);
		addSuperFrame(h1, ea1);
		addSuperFrame(h2, ea2);
		addSuperFrame(eb1, h1);
		addSuperFrame(eb1, h2);

		model.normaliseCFramesHierarchy();

		testSupers(er, getRootFrameAsList());
		testSupers(ea1, Arrays.asList(er));
		testSupers(ea2, Arrays.asList(ea1));
		testSupers(h1, Arrays.asList(ea1));
		testSupers(h2, Arrays.asList(ea2));
		testSupers(eb1, Arrays.asList(h1, h2, ea2));
	}

	@Test
	public void test_addValidSlots() {

		testAddSlots(true);
	}

	@Test(expected = KModelException.class)
	public void test_addInvalidSlots() {

		testAddSlots(false);
	}

	private void addSuperFrame(CFrame sub, CFrame sup) {

		FramesTestUtils.addSuperFrame(sub, sup);
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

		CAtomicFrame a = frames.create("A");
		CAtomicFrame b = frames.create("B");
		CFrame va = frames.create("VA");
		CFrame vb = frames.create("VB");

		addSuperFrame(b, a);

		if (addValidatingLink) {

			addSuperFrame(vb, va);
		}

		slots.create(a, "S", va);
		slots.create(b, "S", vb);

		b.getSlots().validateAll(b);
	}

	private List<CFrame> getModelFrames() {

		return model.model.getFrames().asList();
	}

	private <E>void testList(List<? extends E> got, List<? extends E> expected) {

		MekonTestUtils.testList(got, expected);
	}

	private <E>void testListContents(List<? extends E> got, List<? extends E> expected) {

		MekonTestUtils.testListContents(got, expected);
	}

	private List<CFrame> getRootFrameAsList() {

		return Arrays.asList(model.model.getRootFrame());
	}
}
