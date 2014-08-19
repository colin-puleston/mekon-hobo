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

package uk.ac.manchester.cs.mekon.owl.reason.preprocess;

import java.util.*;

import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * Abstract pre-processer that modifies intermediate instance
 * representations, in order to bypass particular frames, with
 * the frames to be bypassed being identified by the extending
 * classes. When a frame is  bypassed, it is replaced in the slot
 * for which it is a value, by all frames that are values for any
 * slots attached to the bypassed frame.
 *
 * @author Colin Puleston
 */
public abstract class ORFramesBypasser implements ORPreProcessor {

	/**
	 */
	public void process(OModel model, ORFrame rootFrame) {

		process(rootFrame, new HashSet<ORFrame>());
	}

	/**
	 * Determines whether or not a frame is to be bypassed
	 *
	 * @param frame Frame to test
	 * @return True if frame is to be bypassed
	 */
	protected abstract boolean bypass(ORFrame frame);

	private void process(ORFrame frame, Set<ORFrame> visited) {

		if (visited.add(frame)) {

			for (ORConceptSlot slot : frame.getConceptSlots()) {

				process(slot, visited);
			}
		}
	}

	private void process(ORConceptSlot slot, Set<ORFrame> visited) {

		checkBypassFrames(slot);

		for (ORFrame value : slot.getValues()) {

			process(value, visited);
		}
	}

	private void checkBypassFrames(ORConceptSlot parentSlot) {

		for (ORFrame frame : parentSlot.getValues()) {

			if (bypass(frame)) {

				bypassFrame(parentSlot, frame);
			}
		}
	}

	private void bypassFrame(ORConceptSlot parentSlot, ORFrame frame) {

		parentSlot.removeValue(frame);

		for (ORConceptSlot nestedSlot : frame.getConceptSlots()) {

			for (ORFrame nestedFrame : nestedSlot.getValues()) {

				if (bypass(nestedFrame)) {

					bypassFrame(parentSlot, nestedFrame);
				}
				else {

					parentSlot.addValue(nestedFrame);
				}
			}
		}
	}
}