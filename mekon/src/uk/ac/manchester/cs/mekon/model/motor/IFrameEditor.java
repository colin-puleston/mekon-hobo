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

package uk.ac.manchester.cs.mekon.model.motor;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Provides mechanisms for editing a specific {@link IFrame} object.
 *
 * @author Colin Puleston
 */
public interface IFrameEditor {

	/**
	 * Updates the set of concept-level frames representing the
	 * currently inferred types for the frame.
	 *
	 * @param updates Latest inferred types for frame
	 * @return True if inferred types were updated
	 */
	public boolean updateInferredTypes(List<CFrame> updates);

	/**
	 * Updates the set of concept-level frames representing the
	 * currently suggested types for the frame.
	 *
	 * @param updates Latest suggested types for frame
	 * @return True if suggested types were updated
	 */
	public boolean updateSuggestedTypes(List<CFrame> updates);

	/**
	 * Adds a new slot to the frame, where the slot is created by
	 * directly instantiating the specified concept-level slot.
	 *
	 * @param slotType Concept-level slot to instantiate
	 * @return Created and added instance-level slot
	 */
	public ISlot addSlot(CSlot slotType);

	/**
	 * Adds a new slot to the frame, where the slot is created by
	 * directly instantiating the specified concept-level slot,
	 * which is to be dynamically created.
	 *
	 * @param identity Identity for concept-level slot
	 * @param source Source-type for concept-level slot
	 * @param cardinality Cardinality for concept-level slot
	 * @param valueType Value-type for concept-level slot
	 * @param active Active status for concept-level slot (see {@link
	 * CSlot#active})
	 * @param editability Editability status for concept-level slot
	 * @return Created and added instance-level slot
	 */
	public ISlot addSlot(
					CIdentity identity,
					CSource source,
					CCardinality cardinality,
					CValue<?> valueType,
					boolean active,
					CEditability editability);

	/**
	 * Removes the specified slot from the frame.
	 *
	 * @param slot Slot to be removed
	 */
	public void removeSlot(ISlot slot);

	/**
	 * Sets the auto-update status of all slots on the the frame,
	 * which determines whether or not any automatic updates can
	 * occur when slot-values are updated.
	 *
	 * @param enabled Required auto-update status
	 */
	public void setAutoUpdateEnabled(boolean enabled);
}
