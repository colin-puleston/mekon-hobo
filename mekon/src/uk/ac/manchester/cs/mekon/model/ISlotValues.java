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
import uk.ac.manchester.cs.mekon.model.util.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Represents ordered list of current values for an
 * instance-level model-slot. Values are either "asserted"
 * (provided by the client) or "fixed" (provided by the model,
 * based on the current values of other slots in the model).
 * <p>
 * See super class for details of list-operations and associated
 * listening mechanisms.
 *
 * @author Colin Puleston
 */
public class ISlotValues extends KList<IValue> {

	private ISlot slot;

	private List<IValue> fixedValues = new ArrayList<IValue>();
	private List<IValue> assertedValues = new ArrayList<IValue>();

	/**
	 * Provides the current set of fixed values.
	 *
	 * @return Current set of fixed values
	 */
	public List<IValue> getFixedValues() {

		return filterForCurrentValues(fixedValues);
	}

	/**
	 * Provides an ordered list of the currently asserted values.
	 *
	 * @return List of currently asserted values
	 */
	public List<IValue> getAssertedValues() {

		return filterForCurrentValues(assertedValues);
	}

	ISlotValues(ISlot slot) {

		this.slot = slot;
	}

	void reinitialise(List<IValue> fixedValues, List<IValue> assertedValues) {

		this.fixedValues.addAll(fixedValues);
		this.assertedValues.addAll(assertedValues);

		updateSlotValues();
	}

	boolean addAssertedValue(IValue value, boolean privilegedAccess) {

		if (addToAsserteds(value, privilegedAccess)) {

			updateSlotValues();

			return true;
		}

		return false;
	}

	List<IValue> addAssertedValues(
					Collection<? extends IValue> values,
					boolean privilegedAccess) {

		values = getMostSpecifics(values, false);

		List<IValue> additions = addToAsserteds(values, privilegedAccess);

		if (!additions.isEmpty()) {

			updateSlotValues();
		}

		return additions;
	}

	boolean removeAssertedValue(IValue value) {

		if (assertedValues.remove(value)) {

			updateSlotValues();

			return true;
		}

		return false;
	}

	void removeAssertedValue(int index) {

		removeAssertedValue(asList().get(index));
	}

	List<IValue> removeAssertedValues(Collection<? extends IValue> values) {

		List<IValue> removals = removeFromAsserteds(values);

		if (!removals.isEmpty()) {

			updateSlotValues();
		}

		return removals;
	}

	void clearAssertedValues() {

		if (!assertedValues.isEmpty() || !fixedValues.isEmpty()) {

			assertedValues.clear();

			if (fixedValues.isEmpty()) {

				clearValues();
			}
			else {

				updateSlotValues();
			}
		}
	}

	void clearAllFixedAndAssertedValues() {

		fixedValues.clear();
		assertedValues.clear();

		clearValues();
	}

	boolean updateAssertedValues(
				Collection<? extends IValue> values,
				boolean privilegedAccess) {

		values = getMostSpecifics(values, false);

		if (!valuesAsSet(values).equals(valuesAsSet(assertedValues))) {

			assertedValues.clear();
			addToAsserteds(values, privilegedAccess);

			updateSlotValues();

			return true;
		}

		return false;
	}

	boolean updateFixedValues(Collection<? extends IValue> values) {

		values = getMostSpecifics(values, true);

		if (!matchesFixedValues(values)) {

			validateValues(values, true);
			validateFixedValueCombination(values);

			fixedValues.clear();
			fixedValues.addAll(values);

			removeRedundantAsserteds();
			updateSlotValues();

			return true;
		}

		return false;
	}

	void removeInvalidValues() {

		boolean anyRemoved = false;

		anyRemoved |= removeInvalidValues(fixedValues);
		anyRemoved |= removeInvalidValues(assertedValues);

		if (anyRemoved) {

			updateSlotValues();
		}
	}

	private List<IValue> addToAsserteds(
							Collection<? extends IValue> asserteds,
							boolean privilegedAccess) {

		List<IValue> additions = new ArrayList<IValue>();

		for (IValue asserted : asserteds) {

			if (addToAsserteds(asserted, privilegedAccess)) {

				additions.add(asserted);
			}
		}

		return additions;
	}

	private boolean addToAsserteds(IValue asserted, boolean privilegedAccess) {

		validateValue(asserted, privilegedAccess);

		if (!assertedValues.contains(asserted) && !redundantAsserted(asserted)) {

			removeConflictingAsserteds(asserted);
			assertedValues.add(asserted);

			return true;
		}

		return false;
	}

	private List<IValue> removeFromAsserteds(Collection<? extends IValue> asserteds) {

		List<IValue> removals = new ArrayList<IValue>();

		for (IValue asserted : asserteds) {

			if (assertedValues.remove(asserted)) {

				removals.add(asserted);
			}
		}

		return removals;
	}

	private boolean removeInvalidValues(List<IValue> values) {

		boolean anyRemoved = false;

		for (IValue value : new ArrayList<IValue>(values)) {

			if (!validTypeValue(value)) {

				values.remove(value);

				anyRemoved = true;
			}
		}

		return anyRemoved;
	}

	private void removeConflictingAsserteds(IValue newAsserted) {

		for (IValue asserted : copyAssertedValues()) {

			if (conflictingAsserteds(newAsserted, asserted)) {

				assertedValues.remove(asserted);
			}
		}
	}

	private void removeRedundantAsserteds() {

		for (IValue asserted : copyAssertedValues()) {

			if (redundantAsserted(asserted)) {

				assertedValues.remove(asserted);
			}
		}
	}

	private boolean redundantAsserted(IValue asserted) {

		if (fixedValues.isEmpty()) {

			return false;
		}

		return singleValued() || subsumesFixed(asserted);
	}

	private void updateSlotValues() {

		List<IValue> values = copyAssertedValues();

		for (IValue fixed : fixedValues) {

			if (!subsumesAsserted(fixed)) {

				values.add(fixed);
			}
		}

		updateValues(values);
	}

	private boolean subsumesFixed(IValue asserted) {

		for (IValue fixed : fixedValues) {

			if (asserted.subsumes(fixed)) {

				return true;
			}
		}

		return false;
	}

	private boolean subsumesAsserted(IValue fixed) {

		for (IValue asserted : assertedValues) {

			if (fixed.subsumes(asserted)) {

				return true;
			}
		}

		return false;
	}

	private boolean matchesFixedValues(Collection<? extends IValue> values) {

		return IValueStructuresMatcher.elementsMatch(fixedValues, values);
	}

	private boolean validTypeValue(IValue value) {

		return getValueType().validValue(value);
	}

	private void validateValues(
					Collection<? extends IValue> values,
					boolean privilegedAccess) {

		for (IValue value : values) {

			validateValue(value, privilegedAccess);
		}
	}

	private void validateValue(IValue value, boolean privilegedAccess) {

		if (!validTypeValue(value)) {

			throw createInvalidValueException(
				value,
				"expected value of type: " + getValueType());
		}

		if (!privilegedAccess
				&& value.abstractValue()
				&& !slot.getEditability().abstractEditable()) {

			throw createInvalidValueException(
				value,
				"cannot set abstract values for slots "
				+ "of this type on assertion-instances");
		}
	}

	private void validateFixedValueCombination(Collection<? extends IValue> fixedValues) {

		if (singleValued() && fixedValues.size() > 1) {

			throw new KModelException(
						"Cannot set multiple fixed-values "
						+ "for single-valued slot: "
						+ slot);
		}
	}

	private KAccessException createInvalidValueException(IValue value, String extraMsg) {

		return new KAccessException(
					"Invalid slot-value: " + value
					+ ", for slot: " + slot
					+ ": " + extraMsg);
	}

	private List<IValue> copyAssertedValues() {

		return new ArrayList<IValue>(assertedValues);
	}

	private List<IValue> filterForCurrentValues(List<IValue> values) {

		List<IValue> all = asList();

		all.retainAll(values);

		return all;
	}

	private Set<IValue> valuesAsSet(Collection<? extends IValue> values) {

		return new HashSet<IValue>(values);
	}

	private List<IValue> getMostSpecifics(
							Collection<? extends IValue> values,
							boolean structureSubsumption) {

		return new MostSpecificIValues(
						getValueType(),
						values,
						structureSubsumption)
							.get();
	}

	private CValue<?> getValueType() {

		return slot.getValueType();
	}

	private boolean singleValued() {

		return getCardinality().singleValue();
	}

	private boolean conflictingAsserteds(IValue value1, IValue value2) {

		return getCardinality().conflictingAsserteds(value1, value2);
	}

	private CCardinality getCardinality() {

		return slot.getType().getCardinality();
	}
}