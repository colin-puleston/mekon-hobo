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

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents a multi-valued field in the Object Model (OM).
 *
 * @author Colin Puleston
 */
public class DArray<V> extends DField<V> implements DArrayView<V> {

	private CCardinality cardinality;

	/**
	 * Adds specified value to the field (if not already present).
	 *
	 * @param value Value to add
	 * @return True if value was added (i.e. not already present)
	 */
	public boolean add(V value) {

		return super.add(value);
	}

	/**
	 * Adds all specified values to the field (if not already present).
	 *
	 * @param values Values to add
	 * @return All values that were added (i.e. those not already
	 * present)
	 */
	public List<V> addAll(Collection<? extends V> values) {

		return super.addAll(values);
	}

	/**
	 * Removes specified value from the field (if present).
	 *
	 * @param value Value to remove
	 * @return True if value was removed (i.e. if present)
	 */
	public boolean remove(V value) {

		return super.remove(value);
	}

	/**
	 * Removes value at specified index from the field.
	 *
	 * @param index Index of value to remove
	 * @throws KAccessException if illegal index
	 */
	public void remove(int index) {

		super.remove(index);
	}

	/**
	 * Removes all specified values from the field (if not already
	 * present).
	 *
	 * @param values Values to remove
	 * @return All values that were removed (i.e. those that were
	 * previously present)
	 */
	public List<V> removeAll(Collection<? extends V> values) {

		return super.removeAll(values);
	}

	/**
	 * Updates the field so that it contains each of the specified
	 * values, and only those values, making any required additions
	 * and deletions. Where relevant, will maintain the current field
	 * ordering in preference to the supplied field.
	 *
	 * @param latestValues Values that field is to contain
	 */
	public void update(Collection<? extends V> latestValues) {

		super.update(latestValues);
	}

	/**
	 * {@inheritDoc}
	 */
	public <S extends V>List<S> getAll(Class<S> subValueType) {

		List<S> selected = new ArrayList<S>();

		for (V value : super.getAll()) {

			if (subValueType.isAssignableFrom(value.getClass())) {

				selected.add(subValueType.cast(value));
			}
		}

		return selected;
	}

	DArray(DModel model, DValueType<V> valueType) {

		super(model, valueType);

		cardinality = valueType.getDefaultCardinalityForArrays();
	}

	void setUniqueTypes(boolean uniqueTypes) {

		cardinality = uniqueTypes ? CCardinality.UNIQUE_TYPES : CCardinality.REPEATABLE_TYPES;
	}

	DArrayViewer<V> createViewer() {

		return new DArrayViewer<V>(this);
	}

	CCardinality getCardinality() {

		return cardinality;
	}
}
