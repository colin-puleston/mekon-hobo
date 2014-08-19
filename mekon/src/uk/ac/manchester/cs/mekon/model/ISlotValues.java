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

	private class ConcreteOnlyUpdateListener implements KUpdateListener {

		private KUpdateListener clientListener;

		public void onUpdated() {

			if (!slot.queryInstance()) {

				clientListener.onUpdated();
			}
		}

		ConcreteOnlyUpdateListener(KUpdateListener clientListener) {

			this.clientListener = clientListener;
		}
	}

	private class ConcreteOnlyValuesListener implements KValuesListener<IValue> {

		private KValuesListener<IValue> clientListener;

		public void onAdded(IValue value) {

			if (!slot.queryInstance()) {

				clientListener.onAdded(value);
			}
		}

		public void onRemoved(IValue value) {

			if (!slot.queryInstance()) {

				clientListener.onRemoved(value);
			}
		}

		public void onCleared(List<IValue> values) {

			if (!slot.queryInstance()) {

				clientListener.onCleared(values);
			}
		}

		ConcreteOnlyValuesListener(KValuesListener<IValue> clientListener) {

			this.clientListener = clientListener;
		}
	}

	/**
	 * Adds a general-update listener that will only fire if the
	 * slot's container-frame is a concrete-instance.
	 *
	 * @param listener Listener to add
	 */
	public void addConcreteOnlyUpdateListener(KUpdateListener listener) {

		addUpdateListener(new ConcreteOnlyUpdateListener(listener));
	}

	/**
	 * Adds a listener for specific types of value-updates that will
	 * only fire if the slot's container-frame is a concrete-instance.
	 *
	 * @param listener Listener to add
	 */
	public void addConcreteOnlyValuesListener(KValuesListener<IValue> listener) {

		addValuesListener(new ConcreteOnlyValuesListener(listener));
	}

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

		List<IValue> additions = addAllAsserteds(getMostSpecifics(values));

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

	void clearAllFixedAndAsserteds() {

		fixedValues.clear();
		clear();
	}

	boolean update(Collection<? extends IValue> values) {

		values = getMostSpecifics(values);

		if (!valuesAsSet(values).equals(valuesAsSet(assertedValues))) {

			assertedValues.clear();
			addAllAsserteds(values);

			updateSlotValues();

			return true;
		}

		return false;
	}

	boolean updateFixedValues(Collection<? extends IValue> values) {

		values = getMostSpecifics(values);

		if (!fixedValues.equals(values)) {

			validateValues(values);
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

			if (!validTypeValue(value)) {

				values.remove(value);
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

	private boolean validTypeValue(IValue value) {

		return getValueType().validValue(value);
	}

	private void validateValues(Collection<? extends IValue> values) {

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

		if (!slot.queryInstance() && value.abstractValue()) {

			throwInvalidValueException(
				value,
				"cannot set abstract slot-value for concrete-instance");
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

	private void throwInvalidValueException(IValue value, String extraMsg) {

		throw new KAccessException(
					"Invalid slot-value: " + value
					+ ", for slot: " + slot
					+ ": " + extraMsg);
	}

	private Set<IValue> valuesAsSet(Collection<? extends IValue> values) {

		return new HashSet<IValue>(values);
	}

	private List<IValue> getMostSpecifics(Collection<? extends IValue> values) {

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