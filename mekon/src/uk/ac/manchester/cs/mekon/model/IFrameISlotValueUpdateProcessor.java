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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class IFrameISlotValueUpdateProcessor implements KValuesListener<IValue> {

	private ISlot slot;

	public void onAdded(IValue value) {

		IFrame frame = extractFrame(value);

		if (frame.getType().hidden()) {

			throw new KAccessException(
						"Illegal value for slot: " + slot
						+ " (attempting to add \"hidden\" frame as value: "
						+ frame + ")");
		}

		frame.addReferencingSlot(slot);
	}

	public void onRemoved(IValue value) {

		extractFrame(value).removeReferencingSlot(slot);
	}

	public void onCleared(List<IValue> values) {

		for (IValue value : values) {

			onRemoved(value);
		}
	}

	IFrameISlotValueUpdateProcessor(ISlot slot) {

		this.slot = slot;

		slot.getValues().addValuesListener(this);
	}

	private IFrame extractFrame(IValue value) {

		return castValueType().castValue(value);
	}

	private CFrame castValueType() {

		return slot.getValueType().castAs(CFrame.class);
	}
}
