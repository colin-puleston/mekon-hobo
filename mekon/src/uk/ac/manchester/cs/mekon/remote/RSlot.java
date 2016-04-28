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

package uk.ac.manchester.cs.mekon.remote;

import java.util.*;

/**
 * Represents a slot in the remote frames-based representation.
 *
 * @author Colin Puleston
 */
public class RSlot {

	private RIdentity type;
	private RValueType valueType;

	private List<RValue> values = new ArrayList<RValue>();

	/**
	 * Adds a value for the slot, as long as specified value is not
	 * already present.
	 *
	 * @param value Value to add
	 * @return true if value was not already present, and hence was added
	 */
	public boolean addValue(RValue value) {

		if (values.contains(value)) {

			return false;
		}

		if (valueType.getCardinality() == RCardinality.SINGLE_VALUE) {

			clearValues();
		}

		values.add(value);

		return false;
	}

	/**
	 * Removes a value from the slot.
	 *
	 * @param value Value to remove
	 * @return true if value was present, and hence was removed
	 */
	public boolean removeValue(RValue value) {

		return values.remove(value);
	}

	/**
	 * Removes all values from the slot.
	 */
	public void clearValues() {

		values.clear();
	}

	/**
	 */
	public String toString() {

		return getClass().getSimpleName() + "(" + type.toInnerString() + ")";
	}

	/**
	 * Provides the identity representing the type of the slot.
	 *
	 * @return Identity representing type of slot
	 */
	public RIdentity getType() {

		return type;
	}

	/**
	 * Provides the value-type for the slot.
	 *
	 * @return Value-type for slot
	 */
	public RValueType getValueType() {

		return valueType;
	}

	/**
	 * Provides all current values for the slot.
	 *
	 * @return All current values
	 */
	public List<RValue> getValues() {

		return new ArrayList<RValue>(values);
	}

	RSlot(RIdentity type, RValueType valueType) {

		this.type = type;
		this.valueType = valueType;
	}

	void addValueNoChecks(RValue value) {

		values.add(value);
	}
}
