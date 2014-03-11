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

package uk.ac.manchester.cs.mekon.mechanism;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Provides mechanisms for editing an instantiation of a {@link CModel}.
 *
 * @author Colin Puleston
 */
public interface IEditor {

	/**
	 * Provides an editor for the specified frame.
	 *
	 * @param frame Frame for which editor is required
	 * @return Editor for specified frame
	 */
	public IFrameEditor getFrameEditor(IFrame frame);

	/**
	 * Provides an editor for the specified slot.
	 *
	 * @param slot Slot for which editor is required
	 * @return Editor for specified slot
	 */
	public ISlotEditor getSlotEditor(ISlot slot);

	/**
	 * Retrieves the values-editor for the specified slot.
	 *
	 * @param slot Slot for which values-editor is required
	 * @return Values-editor for slot
	 */
	public ISlotValuesEditor getSlotValuesEditor(ISlot slot);

	/**
	 * Retrieves a {@link CFrame} object to be used as a value-type
	 * for a frame-valued slot. The returned frame will have the specified
	 * super-frame and sub-frame types, will have no associated slots or
	 * default slot-values, and will be {@link CFrame#hidden}. If no such
	 * frame has been previously generated via this method then it will be
	 * dynamically created with an auto-generated name based on that of the
	 * super-frame.
	 *
	 * @param superType Super-type for slot value-type frame
	 * @param subTypes sub-types for slot value-type frame
	 * @return Required slot value-type frame
	 */
	public CFrame getDynamicFrameSlotValueType(CFrame superType, List<CFrame> subTypes);
}
