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

package uk.ac.manchester.cs.mekon.model.util;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents a slot-values-update operation.
 *
 * @author Colin Puleston
 */
public class IValuesUpdate {

	/**
	 * Specifies no slot-values updates.
	 */
	static public final IValuesUpdate NO_UPDATE = new IValuesUpdate(null);

	/**
	 * Creates a representation of a slot-value-addition operation.
	 *
	 * @param slot Slot to which value was added
	 * @param addedValue Value that was added to slot
	 * @return Created update representation
	 */
	static public IValuesUpdate createAddition(ISlot slot, IValue addedValue) {

		return new IValuesUpdate(slot, addedValue);
	}

	/**
	 * Creates a representation of a slot-value(s)-removal operation,
	 * either a single removal or a clear operation.
	 *
	 * @param slot Slot from which value(s) were removed
	 * @return Created update representation
	 */
	static public IValuesUpdate createRemovals(ISlot slot) {

		return new IValuesUpdate(slot);
	}

	private ISlot slot;
	private IValue addedValue = null;

	/**
	 * Provides the slot whose values were updated.
	 *
	 * @return Slot whose values were updated, or null if this is the
	 * {@link NO_UPDATE} object
	 */
	public ISlot getSlot() {

		return slot;
	}

	/**
	 * Specifies whether this object represents a slot-value-addition
	 * operation.
	 *
	 * @return True if slot-value-addition operation
	 */
	public boolean addition() {

		return addedValue != null;
	}

	/**
	 * Specifies whether this object represents a slot-value(s)-removal
	 * operation.
	 *
	 * @return True if slot-value(s)-removal operation
	 */
	public boolean removal() {

		return slot != null && addedValue == null;
	}

	/**
	 * Provides the index of the added slot-value, for slot-value-addition
	 * operations.
	 *
	 * @return Added slot-value, or null if not a slot-value-addition
	 * operation
	 */
	public IValue getAddedValue() {

		return addedValue;
	}

	/**
	 * Provides the index of the added slot-value, for slot-value-addition
	 * operations.
	 *
	 * @return Index of added slot-value, or -1 if not a slot-value-addition
	 * operation
	 */
	public int getAddedValueIndex() {

		return addition() ? getNonNullAddedValueIndex() : -1;
	}

	private IValuesUpdate(ISlot slot) {

		this.slot = slot;
	}

	private IValuesUpdate(ISlot slot, IValue addedValue) {

		this.slot = slot;
		this.addedValue = addedValue;

		checkAddedValueIsSlotValue();
	}

	private void checkAddedValueIsSlotValue() {

		if (getNonNullAddedValueIndex() == -1) {

			throw new KAccessException(
						"Invalid value-addition for slot: " + slot
						+ ", specified-value not found: " + addedValue);
		}
	}

	private int getNonNullAddedValueIndex() {

		return slot.getValues().asList().indexOf(addedValue);
	}
}
