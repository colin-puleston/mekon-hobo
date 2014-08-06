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

package uk.ac.manchester.cs.mekon.owl.frames.preprocess;

import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.classifier.*;
import uk.ac.manchester.cs.mekon.owl.frames.*;

/**
 * Abstract pre-processer that modifies the representations of
 * instances that are about to be classified, in order to bypass
 * particular intermediate slots, with the slots to be bypassed
 * being identified by the extending classes. When a slot is
 * bypassed, it is replaced on the frame to which it is attached,
 * by all slots that are attached the frames that are values
 * for the bypassed frame.
 *
 * @author Colin Puleston
 */
public abstract class OFSlotsBypasser implements OFPreProcessor {

	/**
	 */
	public void process(OModel model, OFFrame rootFrame) {

		process(rootFrame);
	}

	/**
	 * Determines whether or not a slot is to be bypassed
	 *
	 * @param slot Slot to test
	 * @return True if slot is to be bypassed
	 */
	protected abstract boolean bypass(OFConceptSlot slot);

	private void process(OFFrame frame) {

		checkBypassSlots(frame);

		for (OFConceptSlot slot : frame.getConceptSlots()) {

			process(slot);
		}
	}

	private void process(OFConceptSlot slot) {

		for (OFFrame value : slot.getValues()) {

			process(value);
		}
	}

	private void checkBypassSlots(OFFrame parentFrame) {

		for (OFConceptSlot slot : parentFrame.getConceptSlots()) {

			if (bypass(slot)) {

				bypassSlot(parentFrame, slot);
			}
		}
	}

	private void bypassSlot(OFFrame parentFrame, OFConceptSlot slot) {

		parentFrame.removeSlot(slot);

		for (OFFrame frame : slot.getValues()) {

			for (OFConceptSlot nestedSlot : frame.getConceptSlots()) {

				if (bypass(nestedSlot)) {

					bypassSlot(parentFrame, nestedSlot);
				}
				else {

					parentFrame.addSlot(nestedSlot);
				}
			}
		}
	}
}
