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

package uk.ac.manchester.cs.hobo.model;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.hobo.*;

/**
 * Represents a single-valued number-valued field in the Object
 * Model (OM). Provides methods that enables numeric-range values
 * to be set and retrieved, in addition to exact-values as handled
 * by the generic base-class.
 * <p>
 * The value can be set via either the {@link DCell#set} method,
 * on the base-class, or the {@link DNumberCell#set} method on this
 * class.
 * <p>
 * The {@link DCell#isSet} method on the base-class, will return
 * true only if there is a current exact-value (which could have been
 * provided either as an exact-value, or as a range-value with equal
 * min and max values), whereas the {@link DNumberCell#rangeSet}
 * method on this class will return true if there is any current
 * value, exact or otherwise.
 *
 * @author Colin Puleston
 */
public class DNumberCell
				<N extends Number>
				extends DCell<N>
				implements DNumberCellView<N> {

	/**
	 * Sets the current value of the cell as a range-value.
	 *
	 * @param value Range-value to set
	 */
	public void set(DNumberRange<N> value) {

		getSlot().getValuesEditor().add(value.asCNumber().asINumber());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean rangeSet() {

		return getSlotValueOrNull() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public DNumberRange<N> getRange() {

		INumber value = getSlotValueOrNull();

		if (value == null) {

			throw new HAccessException("Number range-value not set");
		}

		return new DNumberRange<N>(getValueClass(), value.getType());
	}

	DNumberCell(DModel model, DNumberRange<N> range) {

		super(model, new DNumberValueType<N>(range));
	}

	DNumberCellViewer<N> createViewer() {

		return new DNumberCellViewer<N>(this);
	}

	private INumber getSlotValueOrNull() {

		ISlotValues values = getSlot().getValues();

		return values.isEmpty() ? null : (INumber)values.asList().get(0);
	}
}
