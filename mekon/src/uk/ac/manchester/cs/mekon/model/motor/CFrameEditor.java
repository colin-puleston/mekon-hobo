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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Provides mechanisms for editing a specific {@link CFrame} object.
 *
 * @author Colin Puleston
 */
public interface CFrameEditor {

	/**
	 * Sets the source for the frame.
	 *
	 * @param source Source for frame
	 */
	public void setSource(CSource source);

	/**
	 * Resets the label for the frame (as represented within the
	 * associated {@link CIdentity} object).
	 *
	 * @param newLabel New-label for frame
	 */
	public void resetLabel(String newLabel);

	/**
	 * Adds a direct super-frame for the frame.
	 *
	 * @param sup Direct super-frame to add
	 */
	public void addSuper(CFrame sup);

	/**
	 * Removes a direct super-frame for the frame. Does nothing if
	 * specified frame is not a super-frame.
	 *
	 * @param sup Direct super-frame to remove
	 */
	public void removeSuper(CFrame sup);

	/**
	 * Removes all direct super-frames for the frame.
	 */
	public void clearSupers();

	/**
	 * Either adds a direct sub-frame for the frame, positioning it at
	 * the specified index within the current set of sub-frames, or, if
	 * the supplied frame is already a sub-frame, repositions it to the
	 * required index.
	 *
	 * @param sub Sub-frame to insert
	 * @param index Required index within current sub-frames
	 * @return Previous index within sub-frames, if applicable, or -1
	 * if new sub-frame
	 * @throws KAccessException if specified index is invalid
	 */
	public int insertSub(CFrame sub, int index);

	/**
	 * Creates a slot and adds it to the frame.
	 *
	 * @param slotId Identity for slot
	 * @param valueType Value-type for slot
	 * @param cardinality Cardinality for slot
	 * @return Created and added slot
	 */
	public CSlot addSlot(
					CIdentity slotId,
					CValue<?> valueType,
					CCardinality cardinality);

	/**
	 * Creates a slot and adds it to the frame, positioning it at the
	 * specified index within the current set of slots.
	 *
	 * @param slotId Identity for slot
	 * @param valueType Value-type for slot
	 * @param cardinality Cardinality for slot
	 * @param index Required index within current slots
	 * @return Created and added slot
	 * @throws KAccessException if specified index is invalid
	 */
	public CSlot insertSlot(
					CIdentity slotId,
					CValue<?> valueType,
					CCardinality cardinality,
					int index);

	/**
	 * Moves an existing slot to the specified index within the current
	 * set of slots.
	 *
	 * @param slotId Identity of slot to be moved
	 * @param index Required index within current slots
	 * @return Previous index of moved slot
	 */
	public int positionSlot(CIdentity slotId, int index);

	/**
	 * Re-orders the current set of slots on the frame.
	 *
	 * @param reorderedSlots List containing all current slots ordered
	 * as required
	 * @throws KAccessException If provided list does not contain
	 * all current slots and only current slots
	 */
	public void reorderSlots(List<CSlot> reorderedSlots);

	/**
	 * Removes specified slot from the frame, if possible. Does
	 * nothing if slot has a {@link CSource#internal} source, or if no
	 * such slot is attached to the frame.
	 *
	 * @param slotId Identity of slot to be removed
	 * @return True if slot was removed
	 */
	public boolean removeSlot(CIdentity slotId);

	/**
	 * Removes all slots with specified identity from all descendant
	 * frames, except for any such slots with {@link CSource#internal}
	 * sources.
	 *
	 * @param slotId Identity of slots are to be removed
	 * @return True if all relevant slots were removed
	 */
	public boolean removeSlotsFromDescendants(CIdentity slotId);

	/**
	 * Removes all slots from the frame, except for any slots with
	 * {@link CSource#internal} sources.
	 *
	 * @return True if all relevant slots were removed
	 */
	public boolean clearSlots();

	/**
	 * Removes all slots from all descendant frames, except for any
	 * slots with {@link CSource#internal} sources.
	 *
	 * @return True if all relevant slots were removed
	 */
	public boolean clearSlotsFromDescendants();

	/**
	 * Adds a fixed slot-value for the frame, to be automatically
	 * assigned to the relevant slots on all instantiations of the
	 * frame.
	 *
	 * @param slotId Identity of relevant slot
	 * @param value Fixed value for slot
	 */
	public void addSlotValue(CIdentity slotId, CValue<?> value);

	/**
	 * Removes all default slot-value specifications for the frame.
	 */
	public void clearSlotValues();

	/**
	 * Removes all default slot-value specifications for all
	 * descendant frames.
	 */
	public void clearSlotValuesFromDescendants();
}
