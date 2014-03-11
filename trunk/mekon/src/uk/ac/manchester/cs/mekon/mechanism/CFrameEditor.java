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
	 * Creates a slot and adds it to the frame.
	 *
	 * @param property Property for slot
	 * @param cardinality Cardinality for slot
	 * @param valueType Value-type for slot
	 * @return Created and added slot
	 */
	public CSlot addSlot(
					CProperty property,
					CCardinality cardinality,
					CValue<?> valueType);

	/**
	 * Removes specified slot from the frame. Does nothing if slot
	 * is not attached to the frame.
	 *
	 * @param slot Slot to be removed
	 */
	public void removeSlot(CSlot slot);

	/**
	 * Removes all slots for specified property from the frame.
	 *
	 * @param property Property for which slots are to be removed
	 */
	public void removeSlots(CProperty property);

	/**
	 * Removes all slots for specified property from all descendant
	 * frames.
	 *
	 * @param property Property for which slots are to be removed
	 */
	public void removeSlotsFromDescendants(CProperty property);

	/**
	 * Removes all slots from the frame.
	 */
	public void clearSlots();

	/**
	 * Removes all slots from all descendant frames.
	 */
	public void clearSlotsFromDescendants();

	/**
	 * Adds a default slot-value for the frame, to be automatically
	 * assigned to the relevant slots on all instantiations of the
	 * frame.
	 *
	 * @param property Property of relevant slot
	 * @param value Fixed value for slot
	 */
	public void addSlotValue(CProperty property, CValue<?> value);

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
