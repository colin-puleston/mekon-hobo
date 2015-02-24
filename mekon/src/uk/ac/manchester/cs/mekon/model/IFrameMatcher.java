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
class IFrameMatcher {

	private Set<IFrame> visited = new HashSet<IFrame>();

	boolean match(IFrame frame1, IFrame frame2) {

		return !visited.add(frame1) || framesMatch(frame1, frame2);
	}

	private boolean framesMatch(IFrame frame1, IFrame frame2) {

		return typesMatch(frame1, frame2) && slotsMatch(frame1, frame2);
	}

	private boolean typesMatch(IFrame frame1, IFrame frame2) {

		return frame1.getType().equals(frame2.getType());
	}

	private boolean slotsMatch(IFrame frame1, IFrame frame2) {

		ISlots slots1 = frame1.getSlots();
		ISlots slots2 = frame2.getSlots();

		if (slots1.size() != slots2.size()) {

			return false;
		}

		Iterator<ISlot> s1 = slots1.asList().iterator();
		Iterator<ISlot> s2 = slots2.asList().iterator();

		while (s1.hasNext()) {

			if (!slotValuesMatch(
					s1.next().getValues(),
					s2.next().getValues())) {

				return false;
			}
		}

		return true;
	}

	private boolean slotValuesMatch(ISlotValues values1, ISlotValues values2) {

		if (values1.size() != values2.size()) {

			return false;
		}

		Iterator<IValue> v1 = values1.asList().iterator();
		Iterator<IValue> v2 = values2.asList().iterator();

		while (v1.hasNext()) {

			if (!valuesMatch(v1.next(), v2.next())) {

				return false;
			}
		}

		return true;
	}

	private boolean valuesMatch(IValue value1, IValue value2) {

		if (value1 instanceof IFrame) {

			return match((IFrame)value1, (IFrame)value2);
		}

		return value1.equals(value2);
	}
}