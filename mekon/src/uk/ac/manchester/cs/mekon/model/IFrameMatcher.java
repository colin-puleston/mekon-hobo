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

		return typesMatch(frame1, frame2)
				&& inferredTypesMatch(frame1, frame2)
				&& slotValuesMatch(frame1, frame2);
	}

	private boolean typesMatch(IFrame frame1, IFrame frame2) {

		return frame1.getType().equals(frame2.getType());
	}

	private boolean inferredTypesMatch(IFrame frame1, IFrame frame2) {

		return frame1.getInferredTypes().equals(frame2.getInferredTypes());
	}

	private boolean slotValuesMatch(IFrame frame1, IFrame frame2) {

		Iterator<ISlot> slots1 = frame1.getSlots().asList().iterator();
		Iterator<ISlot> slots2 = frame2.getSlots().asList().iterator();

		while (slots1.hasNext()) {

			if (!slotValuesMatch(slots1.next(), slots2.next())) {

				return false;
			}
		}

		return true;
	}

	private boolean slotValuesMatch(ISlot slot1, ISlot slot2) {

		List<IValue> values1 = slot1.getValues().asList();
		List<IValue> values2 = slot2.getValues().asList();

		if (values1.size() != values2.size()) {

			return false;
		}

		Iterator<IValue> vs1 = values1.iterator();
		Iterator<IValue> vs2 = values2.iterator();

		while (vs1.hasNext()) {

			if (!valuesMatch(vs1.next(), vs2.next())) {

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