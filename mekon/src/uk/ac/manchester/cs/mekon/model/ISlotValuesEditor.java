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

import uk.ac.manchester.cs.mekon.*;

/**
 * Enables the editing of the current set of values for a specific
 * instance-level model-slot.
 *
 * @author Colin Puleston
 */
public class ISlotValuesEditor {

	private ISlotValues slotValues;
	private boolean privilegedAccess;

	/**
	 * Adds the specified value to the "asserted" value-list, if
	 * value was not already asserted, and updates the current
	 * value-list accordingly.
	 *
	 * @param value Asserted value to add
	 * @return True if value was added (i.e. not already asserted)
	 */
	public boolean add(IValue value) {

		return slotValues.addAssertedValue(value, privilegedAccess);
	}

	/**
	 * Adds all of the specified values that are not currently
	 * asserted, to the "asserted" value-list, and updates the
	 * current value-list accordingly.
	 *
	 * @param values Asserted values to add
	 * @return All values that were added (i.e. those not already
	 * asserted)
	 */
	public List<IValue> addAll(Collection<? extends IValue> values) {

		return slotValues.addAssertedValues(values, privilegedAccess);
	}

	/**
	 * Removes the specified value from the "asserted" value-list,
	 * if previously asserted, and updates the current value-list
	 * accordingly.
	 *
	 * @param value Asserted value to remove
	 * @return True if value was removed (i.e. was previously
	 * asserted)
	 */
	public boolean remove(IValue value) {

		return slotValues.removeAssertedValue(value);
	}

	/**
	 * Removes the specified value from the "asserted" value-list,
	 * and updates the current value-list accordingly.
	 *
	 * @param index Index in current value-list of asserted value
	 * to remove
	 * @throws KAccessException if illegal index
	 */
	public void remove(int index) {

		slotValues.removeAssertedValue(index);
	}

	/**
	 * Removes all of the specified values that are currently
	 * asserted, from the "asserted" value-list, and updates the
	 * current value-list accordingly.
	 *
	 * @param values Asserted values to remove
	 * @return All values that were removed (i.e. those that were
	 * previously asserted)
	 */
	public List<IValue> removeAll(Collection<? extends IValue> values) {

		return slotValues.removeAssertedValues(values);
	}

	/**
	 * Clears the "asserted" value-list, and updates the current
	 * value-list accordingly (this may leave some inferred-values
	 * present).
	 */
	public void clear() {

		slotValues.clearAssertedValues();
	}

	/**
	 * Updates the "asserted" value-list, so that it contains each
	 * of the specified values, and only those values, making any
	 * required additions and deletions, and then updating the
	 * current value-list accordingly. Where relevant, will maintain
	 * the current asserted value-list ordering in preference to the
	 * supplied list.
	 *
	 * @param latestValues Values that "asserted" value-list is to
	 * contain
	 */
	public void update(Collection<? extends IValue> latestValues) {

		slotValues.updateAssertedValues(latestValues, privilegedAccess);
	}

	ISlotValuesEditor(ISlotValues slotValues, boolean privilegedAccess) {

		this.slotValues = slotValues;
		this.privilegedAccess = privilegedAccess;
	}
}
