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
 * Provides mechanisms for editing a specific {@link ISlot} object.
 *
 * @author Colin Puleston
 */
public interface ISlotEditor {

	/**
	 * Re-sets the value-type for the slot, by resetting the
	 * value-type for the associated slot-type.
	 * <p>
	 * NOTE: The associated slot-type will always be a local copy
	 * of the instantiated slot-type, and hence any edits will only
	 * affect the particular slot.
	 *
	 * @param valueType New value-type for slot
	 * @return True if value-type has been updated.
	 */
	public boolean setValueType(CValue<?> valueType);

	/**
	 * Re-sets the cardinality of the slot, by resetting the
	 * cardinality for the associated slot-type.
	 * <p>
	 * NOTE: The associated slot-type will always be a local copy
	 * of the instantiated slot-type, and hence any edits will only
	 * affect the particular slot.
	 *
	 * @param cardinality Cardinality to set
	 * @return True if cardinality has been updated.
	 */
	public boolean setCardinality(CCardinality cardinality);

	/**
	 * Re-sets the activation of the slot, by resetting the
	 * activation for the associated slot-type.
	 * <p>
	 * NOTE: The associated slot-type will always be a local copy
	 * of the instantiated slot-type, and hence any edits will only
	 * affect the particular slot.
	 *
	 * @param activation Activation to set
	 * @return True if activation has been updated.
	 */
	public boolean setActivation(CActivation activation);

	/**
	 * Re-sets the editability of the slot, by resetting the
	 * editability for the associated slot-type.
	 * <p>
	 * NOTE: The associated slot-type will always be a local copy
	 * of the instantiated slot-type, and hence any edits will only
	 * affect the particular slot.
	 *
	 * @param editability Editability to set
	 * @return True if editability has been updated.
	 */
	public boolean setEditability(CEditability editability);

	/**
	 * Updates the "fixed" value-list for the slot, so that it contains
	 * each of the specified values, and only those values, making any
	 * required additions and deletions, and then updating the
	 * current value-list accordingly. Where relevant, will maintain
	 * the current fixed value-list ordering in preference to the
	 * supplied list.
	 *
	 * @param fixedValues New fixed values for slot
	 * @return True if fixed values set has been updated
	 */
	public boolean updateFixedValues(List<IValue> fixedValues);
}
