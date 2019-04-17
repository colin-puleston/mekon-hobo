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

import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
abstract class IStructureTester {

	private Set<IFrame[]> visited = new HashSet<IFrame[]>();

	boolean match(IFrame frame1, IFrame frame2) {

		return checkVisitedPair(frame1, frame2) || framesMatch(frame1, frame2);
	}

	boolean slotValuesMatch(ISlot slot1, ISlot slot2) {

		ISlotValues values1 = slot1.getValues();
		ISlotValues values2 = slot2.getValues();

		if (!listSizesMatch(values1, values2)) {

			return false;
		}

		return valuesMatch(values1.asList(), values2.asList());
	}

	boolean valuesMatch(IValue value1, IValue value2) {

		if (value1 instanceof IFrame) {

			return match((IFrame)value1, (IFrame)value2);
		}

		return typesMatch(value1, value2);
	}

	abstract boolean typesMatch(CValue<?> type1, CValue<?> type2);

	abstract boolean listSizesMatch(KList<?> list1, KList<?> list2);

	abstract boolean slotsMatch(ISlots slots1, ISlots slots2);

	abstract boolean valuesMatch(List<IValue> values1, List<IValue> values2);

	private boolean checkVisitedPair(IFrame frame1, IFrame frame2) {

		return !visited.add(new IFrame[]{frame1, frame2});
	}

	private boolean framesMatch(IFrame frame1, IFrame frame2) {

		return typesMatch(frame1, frame2) && frameSlotsMatch(frame1, frame2);
	}

	private boolean frameSlotsMatch(IFrame frame1, IFrame frame2) {

		ISlots slots1 = frame1.getSlots();
		ISlots slots2 = frame2.getSlots();

		return listSizesMatch(slots1, slots2) && slotsMatch(slots1, slots2);
	}

	private boolean typesMatch(IValue value1, IValue value2) {

		return typesMatch(value1.getType(), value2.getType());
	}
}
