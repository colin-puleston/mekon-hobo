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

package uk.ac.manchester.cs.mekon.model.serial;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class IUpdate {

	static final IUpdate NO_UPDATE = new IUpdate(null, 0);

	static IUpdate createAddition(ISlot slot, IValue addedValue) {

		return new IUpdate(slot, getAddedValueIndex(slot, addedValue));
	}

	static IUpdate createRemovals(ISlot slot) {

		return new IUpdate(slot, -1);
	}

	private static int getAddedValueIndex(ISlot slot, IValue addedValue) {

		int index = slot.getValues().asList().indexOf(addedValue);

		if (index != -1) {

			return index;
		}

		throw new KAccessException(
					"Invalid value-addition for slot: " + slot
					+ ", specified-value not found: " + addedValue);
	}

	private ISlot slot;
	private int addedValueIndex;

	boolean refersToSlot(ISlot testSlot) {

		return testSlot == slot;
	}

	boolean addition() {

		return addedValueIndex != -1;
	}

	int getAddedValueIndex() {

		return addedValueIndex;
	}

	private IUpdate(ISlot slot, int addedValueIndex) {

		this.slot = slot;
		this.addedValueIndex = addedValueIndex;
	}
}
