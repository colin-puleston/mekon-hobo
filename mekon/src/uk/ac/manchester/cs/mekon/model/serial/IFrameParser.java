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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;

/**
 * Parser for the standard XML serialisation of {@link IFrame}
 * objects that produces a default instantiation of the frame.
 *
 * @author Colin Puleston
 */
public class IFrameParser extends IFrameParserAbstract {

	private IEditor iEditor;

	/**
	 * Constructor
	 *
	 * @param model Relevant model
	 * @param frameFunction Function of frames to be parsed
	 */
	public IFrameParser(CModel model, IFrameFunction frameFunction) {

		super(model, frameFunction);

		iEditor = ZCModelAccessor.get().getIEditor(model);
	}

	IFrame instantiateFrame(CFrame type, IFrameFunction function) {

		IFrame frame = type.instantiate(function);

		setAutoUpdateEnabled(frame, false);

		return frame;
	}

	ISlot checkResolveIFrameSlot(IFrame container, CIdentity slotId) {

		return lookForSlot(container, slotId);
	}

	ISlot checkResolveINumberSlot(
				IFrame container,
				CIdentity slotId,
				Class<? extends Number> numberType) {

		return lookForSlot(container, slotId);
	}

	ISlot checkResolveIStringSlot(IFrame container, CIdentity slotId) {

		return lookForSlot(container, slotId);
	}

	ISlot checkResolveCFrameSlot(IFrame container, CIdentity slotId) {

		return lookForSlot(container, slotId);
	}

	void checkUpdateFrameSlotSets(List<IFrame> frames) {

		setAutoUpdateEnabled(frames, true);

		for (IFrame frame : frames) {

			frame.update();
		}

		setAutoUpdateEnabled(frames, false);
	}

	void onParseCompletion(IFrame rootFrame, List<IFrame> frames) {

		setAutoUpdateEnabled(frames, true);
	}

	private ISlot lookForSlot(IFrame container, CIdentity slotId) {

		ISlots slots = container.getSlots();

		return slots.containsValueFor(slotId) ? slots.get(slotId) : null;
	}

	private void setAutoUpdateEnabled(List<IFrame> frames, boolean enabled) {

		for (IFrame frame : frames) {

			setAutoUpdateEnabled(frame, enabled);
		}
	}

	private void setAutoUpdateEnabled(IFrame frame, boolean enabled) {

		getFrameEditor(frame).setAutoUpdateEnabled(enabled);
	}

	private IFrameEditor getFrameEditor(IFrame frame) {

		return iEditor.getFrameEditor(frame);
	}
}