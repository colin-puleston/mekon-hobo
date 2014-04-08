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
public abstract class ISlotValues extends KList<IValue> {

	private ISlot slot;

	private List<IValue> fixedValues = new ArrayList<IValue>();
	private List<IValue> assertedValues = new ArrayList<IValue>();

	/**
	 * Provides the current set of fixed values.
	 *
	 * @return Current set of fixed values
	 */
	public List<IValue> getFixedValues() {

		return new ArrayList<IValue>(fixedValues);
	}

	/**
	 * Provides an ordered list of the currently asserted values.
	 *
	 * @return List of currently asserted values
	 */
	public List<IValue> getAssertedValues() {

		return new ArrayList<IValue>(assertedValues);
	}

	ISlotValues(ISlot slot) {

		this.slot = slot;
	}

	boolean add(IValue value) {

		if (addNewAsserted(value)) {

			updateSlotValues();

			return true;
		}

		return false;
	}

	List<IValue> addAll(Collection<? extends IValue> values) {

		List<IValue> additions = addAllAsserteds(values);

		if (!additions.isEmpty()) {

			updateSlotValues();
		}

		return additions;
	}

	boolean remove(IValue value) {

		if (assertedValues.remove(value)) {

			updateSlotValues();

			return true;
		}

		return false;
	}

	void remove(int index) {

		remove(asList().get(index));
	}

	List<IValue> removeAll(Collection<? extends IValue> values) {

		List<IValue> removals = removeAllAsserteds(values);

		if (!removals.isEmpty()) {

			updateSlotValues();
		}

		return removals;
	}

	void clear() {

		assertedValues.clear();

		if (fixedValues.isEmpty()) {

			clearValues();
		}
		else {

			updateSlotValues();
		}
	}

	boolean update(Collection<? extends IValue> values) {

		if (!valuesAsSet(values).equals(valuesAsSet(assertedValues))) {

			assertedValues.clear();
			addAllAsserteds(values);

			updateSlotValues();

			return true;
		}

		return false;
	}

	boolean updateFixedValues(Collection<IValue> newFixedValues) {

		if (!slot.abstractInstance()) {

			retainOnlyMostSpecificValues(newFixedValues);

			if (!fixedValues.equals(newFixedValues)) {

				validateValues(newFixedValues);
				validateFixedValueCombination(newFixedValues);

				fixedValues.clear();
				fixedValues.addAll(newFixedValues);

				removeRedundantAsserteds();
				updateSlotValues();

				return true;
			}
		}

		return false;
	}

	void removeInvalidValues() {

		removeInvalidValues(fixedValues);
		removeInvalidValues(assertedValues);

		updateSlotValues();
	}

	boolean singleValued() {

		return false;
	}

	abstract boolean conflictingAsserteds(IValue value1, IValue value2);

	private List<IValue> addAllAsserteds(Collection<? extends IValue> asserteds) {

		List<IValue> additions = new ArrayList<IValue>();

		for (IValue asserted : asserteds) {

			if (addNewAsserted(asserted)) {

				additions.add(asserted);
			}
		}

		return additions;
	}

	private boolean addNewAsserted(IValue asserted) {

		validateValue(asserted);

		if (!assertedValues.contains(asserted)
			&& !redundantAsserted(asserted)) {

			removeConflictingAsserteds(asserted);
			assertedValues.add(asserted);

			return true;
		}

		return false;
	}

	private List<IValue> removeAllAsserteds(Collection<? extends IValue> asserteds) {

		List<IValue> removals = new ArrayList<IValue>();

		for (IValue asserted : asserteds) {

			if (assertedValues.remove(asserted)) {

				removals.add(asserted);
			}
		}

		return removals;
	}

	private void removeInvalidValues(List<IValue> values) {

		for (IValue value : new ArrayList<IValue>(values)) {

			if (!validValue(value)) {

				values.remove(value);
			}
		}
	}

	private void removeInvalidFixeds() {

		for (IValue fixed : getFixedValues()) {

			if (!validValue(fixed)) {

				fixedValues.remove(fixed);
			}
		}
	}

	private void removeConflictingAsserteds(IValue newAsserted) {

		for (IValue asserted : getAssertedValues()) {

			if (conflictingAsserteds(newAsserted, asserted)) {

				assertedValues.remove(asserted);
			}
		}
	}

	private void removeRedundantAsserteds() {

		for (IValue asserted : getAssertedValues()) {

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

		List<IValue> values = getAssertedValues();

		for (IValue fixed : fixedValues) {

			if (!subsumesAsserted(fixed)) {

				values.add(fixed);
			}
		}

		updateValues(values);
	}

	private void retainOnlyMostSpecificValues(Collection<IValue> values) {

		values.retainAll(getMostSpecificValues(values));
	}

	private boolean subsumesFixed(IValue asserted) {

		for (IValue fixed : fixedValues) {

			if (valueSubsumption(asserted, fixed)) {

				return true;
			}
		}

		return false;
	}

	private boolean subsumesAsserted(IValue fixed) {

		for (IValue asserted : assertedValues) {

			if (valueSubsumption(fixed, asserted)) {

				return true;
			}
		}

		return false;
	}

	private boolean validValue(IValue value) {

		return validTypeValue(value) && validValueAbstractionLevel(value);
	}

	private boolean validTypeValue(IValue value) {

		return getValueType().validValue(value);
	}

	private boolean validValueAbstractionLevel(IValue value) {

		return slot.abstractInstance() || !value.abstractValue();
	}

	private void validateValues(Collection<IValue> values) {

		for (IValue value : values) {

			validateValue(value);
		}
	}

	private void validateValue(IValue value) {

		if (!validTypeValue(value)) {

			throwInvalidValueException(
				value,
				"expected value of type: " + getValueType());
		}

		if (!validValueAbstractionLevel(value)) {

			throwInvalidValueException(
				value,
				"cannot set abstract slot-value "
				+ "for non-abstract instance");
		}
	}

	private void validateFixedValueCombination(Collection<IValue> fixedValues) {

		if (singleValued() && fixedValues.size() > 1) {

			throw new KModelException(
						"Cannot set multiple fixed-values "
						+ "for single-valued slot: "
						+ slot);
		}
	}

	private void throwInvalidValueException(IValue value, String extraMsg) {

		throw new KAccessException(
					"Invalid slot-value: " + value
					+ ", for slot: " + slot
					+ ": " + extraMsg);
	}

	private Set<IValue> valuesAsSet(Collection<? extends IValue> values) {

		return new HashSet<IValue>(values);
	}

	private List<IValue> getMostSpecificValues(Collection<IValue> values) {

		return IValueSubsumptions.getMostSpecifics(values, getValueType());
	}

	private boolean valueSubsumption(IValue testSubsumer, IValue testSubsumed) {

		return IValueSubsumptions.subsumption(testSubsumer, testSubsumed);
	}

	private CModel getModel() {

		return slot.getContainer().getType().getModel();
	}

	private CValue<?> getValueType() {

		return slot.getValueType();
	}
}