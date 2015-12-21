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
	 * Re-sets the value-type for the slot.
	 *
	 * @param valueType New value-type for slot
	 * @return True if value-type has been updated.
	 */
	public boolean setValueType(CValue<?> valueType);

	/**
	 * Re-sets the "active" status of the slot.
	 *
	 * @param active Status to set
	 * @return True if status has been updated.
	 */
	public boolean setActive(boolean active);

	/**
	 * Re-sets the editability of the slot.
	 *
	 * @param editability Editability to set
	 * @return True if editability has been updated.
	 */
	public boolean setEditability(CEditability editability);

	/**
	 * Re-sets the asserted values for the slot.
	 *
	 * @param assertedValues New asserted values for slot
	 * @return True if asserted values set has been updated
	 */
	public boolean setAssertedValues(List<IValue> assertedValues);

	/**
	 * Re-sets the fixed values for the slot.
	 *
	 * @param fixedValues New fixed values for slot
	 * @return True if fixed values set has been updated
	 */
	public boolean setFixedValues(List<IValue> fixedValues);
}
