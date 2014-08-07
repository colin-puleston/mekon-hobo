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

/**
 * @author Colin Puleston
 */
class IFrameCycleTester {

	private IFrame startFrame;

	private Set<IFrame> visited = new HashSet<IFrame>();
	private Deque<IFrame> stack = new ArrayDeque<IFrame>();

	IFrameCycleTester(IFrame startFrame) {

		this.startFrame = startFrame;
	}

	boolean leadsToCycle() {

		return leadsToCycle(startFrame);
	}

	private boolean leadsToCycle(IFrame frame) {

		if (stack.contains(frame)) {

			return true;
		}

		if (visited.add(frame)) {

			stack.push(frame);

			if (slotsLeadToCycle(frame)) {

				return true;
			}

			stack.pop();
		}

		return false;
	}

	private boolean slotsLeadToCycle(IFrame frame) {

		for (ISlot slot : frame.getSlots().asList()) {

			if (slotLeadsToCycle(slot)) {

				return true;
			}
		}

		return false;
	}

	private boolean slotLeadsToCycle(ISlot slot) {

		if (slot.getValueType() instanceof CFrame) {

			frameValuedSlotLeadsToCycle(slot);
		}

		return false;
	}

	private boolean frameValuedSlotLeadsToCycle(ISlot slot) {

		for (IValue value : slot.getValues().asList()) {

			if (leadsToCycle((IFrame)value)) {

				return true;
			}
		}

		return false;
	}
}
