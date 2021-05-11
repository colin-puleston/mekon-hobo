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

import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * @author Colin Puleston
 */
class IEditorImpl implements IEditor {

	private DynamicSlotValueTypeFrames dynamicSlotValueTypeFrames;

	public IFrameEditor getFrameEditor(IFrame frame) {

		return frame.createEditor();
	}

	public ISlotEditor getSlotEditor(ISlot slot) {

		return slot.createEditor();
	}

	public ISlotValuesEditor getSlotValuesEditor(ISlot slot) {

		return slot.getValuesEditor(true);
	}

	public CFrame getDynamicFrameSlotValueType(CFrame superType, List<CFrame> subTypes) {

		return dynamicSlotValueTypeFrames.get(superType, subTypes);
	}

	IEditorImpl(CModel model) {

		dynamicSlotValueTypeFrames = new DynamicSlotValueTypeFrames(model);
	}
}
