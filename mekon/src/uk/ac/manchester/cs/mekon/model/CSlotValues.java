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

import uk.ac.manchester.cs.mekon.util.*;

/**
 * Represents an ordered set of "fixed" concept-level values for
 * specific slots on a particular frame, instantiations of which
 * are required for all instantiations of those slots.
 *
 * @author Colin Puleston
 */
public class CSlotValues {

	static final CSlotValues INERT_INSTANCE = new CSlotValues() {

		void add(CProperty property, CValue<?> value) {

			onAttemptedUpdate();
		}

		void clear() {

			onAttemptedUpdate();
		}

		void removeAll(CValue<?> value) {

			onAttemptedUpdate();
		}

		private void onAttemptedUpdate() {

			throw new Error("Illegal updating of inert object!");
		}
	};

	private List<CProperty> properties = new ArrayList<CProperty>();
	private KListMap<CProperty, CValue<?>> valuesByProperty
							= new KListMap<CProperty, CValue<?>>();

	/**
	 * Specifies whether any fixed slot-values have been defined
	 * for instantiations of any slots attached to the frame.
	 *
	 * @return True if any slot-values defined for frame
	 */
	public boolean valuesDefined() {

		return !properties.isEmpty();
	}

	/**
	 * Checks whether there are fixed slot-values defined for the
	 * slot with the specified property.
	 *
	 * @param property Property for which the existance of values
	 * is to be tested for
	 * @return True if fixed slot-values are defined for property
	 */
	public boolean valueFor(CProperty property) {

		return properties.contains(property);
	}

	/**
	 * Provides the set of properties for which fixed slot-values
	 * have been defined for slots attached to the frame.
	 *
	 * @return Relevant set of properties
	 */
	public List<CProperty> getSlotProperties() {

		return properties;
	}

	/**
	 * Provides the set of fixed values for the slot associated with
	 * the specified property.
	 *
	 * @param property Property for which values are required
	 * @return Relevant set of values
	 */
	public List<CValue<?>> getValues(CProperty property) {

		return valuesByProperty.getList(property);
	}

	/**
	 * Provides the set of fixed values for the slot associated with
	 * the specified property, with the returned set being cast to the
	 * known type.
	 *
	 * @param property Property for which values are required
	 * @param valueClass Known class of values
	 * @return Relevant set of values
	 */
	public <V extends CValue<?>>List<V> getValues(
											CProperty property,
											Class<V> valueClass) {

		List<V> values = new ArrayList<V>();

		for (CValue<?> value : getValues(property)) {

			values.add(valueClass.cast(value));
		}

		return values;
	}

	/**
	 * Provides a set of default instantiations of the fixed values
	 * for the slot associated with the specified property.
	 *
	 * @param property Property for which default value-instantiations
	 * are required
	 * @return Relevant set of default value-instantiations
	 * @throws KAccessException if it is not possible to obtain default
	 * value-instantiations for any of the relevent fixed values
	 */
	public List<IValue> getIValues(CProperty property) {

		List<IValue> iValues = new ArrayList<IValue>();

		for (CValue<?> value : getValues(property)) {

			iValues.add(value.getDefaultValue());
		}

		return iValues;
	}

	CSlotValues() {
	}

	void add(CProperty property, CValue<?> value) {

		if (valuesByProperty.containsKey(property)) {

			absorb(property, value);
		}
		else {

			properties.add(property);
			valuesByProperty.add(property, value);
		}
	}

	void clear() {

		properties.clear();
		valuesByProperty.clear();
	}

	void removeAll(CValue<?> value) {

		for (CProperty property : properties) {

			valuesByProperty.remove(property, value);
		}
	}

	boolean equalSlotValues(CSlotValues other) {

		if (!properties.equals(other.properties)) {

			return false;
		}

		for (CProperty property : properties) {

			if (!getValues(property).equals(other.getValues(property))) {

				return false;
			}
		}

		return true;
	}

	boolean subsumes(CSlotValues testSubsumed) {

		for (CProperty property : properties) {

			if (!subsumedValuesFor(property, testSubsumed)) {

				return false;
			}
		}

		return true;
	}

	void validateAll(CFrame container) {

		for (CProperty property : properties) {

			for (CValue<?> value : getValues(property)) {

				validate(container, property, value);
			}
		}
	}

	void validate(CFrame container, CProperty property, CValue<?> value) {

		new CSlotValueTypeValidator(property, value).checkValidFor(container);
	}

	int createHashCode() {

		int hashCode = properties.hashCode();

		for (CProperty property : properties) {

			hashCode += getValues(property).hashCode();
		}

		return hashCode;
	}

	private void absorb(CProperty property, CValue<?> newValue) {

		for (CValue<?> value : getValues(property)) {

			if (newValue.subsumes(value)) {

				return;
			}

			if (newValue.subsumedBy(value)) {

				valuesByProperty.remove(property, value);
			}
		}

		valuesByProperty.add(property, newValue);
	}

	private boolean subsumedValuesFor(
						CProperty property,
						CSlotValues testSubsumed) {

		if (!testSubsumed.valueFor(property)) {

			return false;
		}

		List<CValue<?>> values = getValues(property);
		List<CValue<?>> testSubValues = testSubsumed.getValues(property);

		return CValue.allSubsumptions(values, testSubValues);
	}
}
