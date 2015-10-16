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

import uk.ac.manchester.cs.mekon.serial.*;

/**
 * @author Colin Puleston
 */
class IFrameParserReasoning extends IFrameParserLocal {

	protected IFrame instantiateFrame(CFrame type, IFrameCategory category) {

		IFrame frame = type.instantiate(category);

		frame.setAutoUpdateEnabled(false);

		return frame;
	}

	protected ISlot checkResolveIFrameSlot(IFrame frame, CIdentity slotId) {

		return lookForSlot(frame, slotId);
	}

	protected ISlot checkResolveCFrameSlot(IFrame frame, CIdentity slotId) {

		return lookForSlot(frame, slotId);
	}

	protected ISlot checkResolveINumberSlot(
						IFrame frame,
						CIdentity slotId,
						Class<? extends Number> numberType) {

		return lookForSlot(frame, slotId);
	}

	protected void checkUpdateFrameSlotSets(List<IFrame> frames) {

		setAutoUpdateEnabled(frames, true);

		for (IFrame frame : frames) {

			frame.update();
		}

		setAutoUpdateEnabled(frames, false);
	}

	protected void checkUpdateFramesOnParseCompletion(List<IFrame> frames) {

		setAutoUpdateEnabled(frames, true);
	}

	IFrameParserReasoning(CModel model, IFrameCategory frameCategory) {

		super(model, frameCategory);
	}

	private ISlot lookForSlot(IFrame frame, CIdentity slotId) {

		ISlots slots = frame.getSlots();

		return slots.containsValueFor(slotId) ? slots.get(slotId) : null;
	}

	private void setAutoUpdateEnabled(List<IFrame> frames, boolean enabled) {

		for (IFrame frame : frames) {

			frame.setAutoUpdateEnabled(enabled);
		}
	}
}