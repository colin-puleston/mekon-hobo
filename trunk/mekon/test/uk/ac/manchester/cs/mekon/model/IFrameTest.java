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

import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
public class IFrameTest extends MekonTest {

	static private final List<CFrame> NO_IFRAMES = Collections.emptyList();

	private MonitorIReasoner monitorIReasoner;

	private class CFrameListMonitor implements KValuesListener<CFrame> {

		List<CFrame> added = new ArrayList<CFrame>();
		List<CFrame> removed = new ArrayList<CFrame>();

		public void onAdded(CFrame value) {

			added.add(value);
		}

		public void onRemoved(CFrame value) {

			removed.add(value);
		}

		public void onCleared(List<CFrame> values) {
		}
	}

	public IFrameTest() {

		this(new MonitorIReasoner());
	}

//	@Test
	public void test_updateInferredTypes() {

		IFrame f = createIFrame("F");

		CFrame ta = createCFrame("A");
		CFrame tb = createCFrame("B");
		CFrame tc = createCFrame("C");
		CFrame td = createCFrame("D");

		testUpdateInferredTypes(f, list(ta, tb), list(ta, tb), NO_IFRAMES);
		testUpdateInferredTypes(f, list(tb, tc, td), list(tc, td), list(ta));
		testUpdateInferredTypes(f, list(tc), NO_IFRAMES, list(tb, td));
		testUpdateInferredTypes(f, list(ta), list(ta), list(tc));
		testUpdateInferredTypes(f, NO_IFRAMES, NO_IFRAMES, list(ta));
	}

//	@Test
	public void test_updateSlotValue() {

		IFrame fa = createIFrame("A");
		IFrame fb = createIFrame("B");
		IFrame fc = createIFrame("C");

		monitorIReasoner.resetRegisters();
		createISlotWithValue(fa, "sab", fb);
		testList(monitorIReasoner.updateds, list(fa));

		monitorIReasoner.resetRegisters();
		createISlotWithValue(fb, "sbc", fc);
		testList(monitorIReasoner.updateds, list(fb, fa));
	}

	@Test
	public void test_copyAndMatch() {

		IFrame i = createComplexInstance();
		IFrame iCopy = i.copy();

		assertFalse(i == iCopy);
		assertTrue(iCopy.matches(i));
		assertTrue(i.matches(iCopy));
		assertTrue(i.matches(i));
	}

	private IFrameTest(MonitorIReasoner monitorIReasoner) {

		super(monitorIReasoner);

		this.monitorIReasoner = monitorIReasoner;
	}

	private void testUpdateInferredTypes(
					IFrame frame,
					List<CFrame> required,
					List<CFrame> expectedAdded,
					List<CFrame> expectedRemoveds) {

		CFrameListMonitor monitor = new CFrameListMonitor();

		frame.getInferredTypes().addValuesListener(monitor);
		frame.createEditor().updateInferredTypes(required);

		testList(frame.getInferredTypes().asList(), required);
		testList(monitor.added, expectedAdded);
		testList(monitor.removed, expectedRemoveds);
	}
}
