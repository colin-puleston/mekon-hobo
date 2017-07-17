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

		void add(CIdentity slotId, CValue<?> value) {

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

	private List<CIdentity> slotIds = new ArrayList<CIdentity>();
	private KListMap<CIdentity, CValue<?>> valuesBySlotId
						= new KListMap<CIdentity, CValue<?>>();

	/**
	 * Specifies whether any fixed slot-values have been defined
	 * for instantiations of any slots attached to the frame.
	 *
	 * @return True if any slot-values defined for frame
	 */
	public boolean valuesDefined() {

		return !slotIds.isEmpty();
	}

	/**
	 * Checks whether there are fixed slot-values defined for the
	 * specified slot.
	 *
	 * @param slotId Identity of slot for which the existance of
	 * values is to be tested for
	 * @return True if fixed slot-values are defined for relevant slot
	 */
	public boolean valueFor(CIdentity slotId) {

		return slotIds.contains(slotId);
	}

	/**
	 * Provides the set of identities for which fixed slot-values
	 * have been defined for slots attached to the frame.
	 *
	 * @return Relevant set of identities
	 */
	public List<CIdentity> getSlotIdentities() {

		return new ArrayList<CIdentity>(slotIds);
	}

	/**
	 * Provides the set of fixed values for the specified slot.
	 *
	 * @param slotId Identity of slot for which values are required
	 * @return Relevant set of values
	 */
	public List<CValue<?>> getValues(CIdentity slotId) {

		return valuesBySlotId.getList(slotId);
	}

	/**
	 * Provides the set of fixed values for the specified slot, with
	 * the returned set being cast to a known type.
	 *
	 * @param <V> Generic version of valueClass
	 * @param slotId Identity of slot for which values are required
	 * @param valueClass Known class of values
	 * @return Relevant set of values
	 */
	public <V extends CValue<?>>List<V> getValues(
											CIdentity slotId,
											Class<V> valueClass) {

		List<V> values = new ArrayList<V>();

		for (CValue<?> value : getValues(slotId)) {

			values.add(valueClass.cast(value));
		}

		return values;
	}

	/**
	 * Provides a set of default instantiations of the fixed values
	 * for the specified slot.
	 *
	 * @param slotId Identity of slot for which default
	 * value-instantiations are required
	 * @return Relevant set of default value-instantiations
	 * @throws KAccessException if it is not possible to obtain default
	 * value-instantiations for any of the relevent fixed values
	 */
	public List<IValue> getIValues(CIdentity slotId) {

		List<IValue> iValues = new ArrayList<IValue>();

		for (CValue<?> value : getValues(slotId)) {

			iValues.add(value.getDefaultValue());
		}

		return iValues;
	}

	CSlotValues() {
	}

	void add(CIdentity slotId, CValue<?> value) {

		if (valuesBySlotId.containsKey(slotId)) {

			absorb(slotId, value);
		}
		else {

			slotIds.add(slotId);
			valuesBySlotId.add(slotId, value);
		}
	}

	void clear() {

		slotIds.clear();
		valuesBySlotId.clear();
	}

	void removeAll(CValue<?> value) {

		for (CIdentity slotId : slotIds) {

			valuesBySlotId.remove(slotId, value);
		}
	}

	boolean equalSlotValues(CSlotValues other) {

		if (!slotIds.equals(other.slotIds)) {

			return false;
		}

		for (CIdentity slotId : slotIds) {

			if (!getValues(slotId).equals(other.getValues(slotId))) {

				return false;
			}
		}

		return true;
	}

	boolean subsumes(CSlotValues testSubsumed) {

		for (CIdentity slotId : slotIds) {

			if (!subsumedValuesFor(slotId, testSubsumed)) {

				return false;
			}
		}

		return true;
	}

	void validateAll(CFrame container) {

		for (CIdentity slotId : slotIds) {

			for (CValue<?> value : getValues(slotId)) {

				validate(container, slotId, value);
			}
		}
	}

	void validate(CFrame container, CIdentity slotId, CValue<?> value) {

		new CSlotValueTypeValidator(slotId, value).checkValidFor(container);
	}

	int createHashCode() {

		int hashCode = slotIds.hashCode();

		for (CIdentity slotId : slotIds) {

			hashCode += getValues(slotId).hashCode();
		}

		return hashCode;
	}

	private void absorb(CIdentity slotId, CValue<?> newValue) {

		for (CValue<?> value : getValues(slotId)) {

			if (newValue.subsumes(value)) {

				return;
			}

			if (newValue.subsumedBy(value)) {

				valuesBySlotId.remove(slotId, value);
			}
		}

		valuesBySlotId.add(slotId, newValue);
	}

	private boolean subsumedValuesFor(CIdentity slotId, CSlotValues testSubsumed) {

		if (!testSubsumed.valueFor(slotId)) {

			return false;
		}

		List<CValue<?>> values = getValues(slotId);
		List<CValue<?>> testSubValues = testSubsumed.getValues(slotId);

		return CValue.allSubsumptions(values, testSubValues);
	}
}
