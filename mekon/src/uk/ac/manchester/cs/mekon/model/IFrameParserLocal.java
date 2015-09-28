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

import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * @author Colin Puleston
 */
class IFrameParserLocal extends IFrameParser {

	protected IFrame instantiateFrame(CFrame type, IFrameCategory category) {

		return type.instantiateNoAutoUpdate(category);
	}

	protected void setSlotValues(ISlot slot, List<IValue> values) {

		slot.getValues().update(values, true);
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

	IFrameParserLocal(CModel model, IFrameCategory frameCategory) {

		super(model, frameCategory);
	}

	private void setAutoUpdateEnabled(List<IFrame> frames, boolean enabled) {

		for (IFrame frame : frames) {

			frame.setAutoUpdateEnabled(enabled);
		}
	}
}