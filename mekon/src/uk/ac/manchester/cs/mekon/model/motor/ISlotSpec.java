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

package uk.ac.manchester.cs.mekon.model.motor;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.util.*;

/**
 * @author Colin Puleston
 */
class ISlotSpec {

	private IEditor iEditor;

	private CIdentity identity;
	private CSource source = CSource.UNSPECIFIED;
	private List<CValue<?>> valueTypes = new ArrayList<CValue<?>>();

	private CCardinality cardinality = CCardinality.REPEATABLE_TYPES;
	private CActivation activation = CActivation.INACTIVE;
	private CEditability editability = CEditability.DEFAULT;

	private List<IValue> fixedValues = new ArrayList<IValue>();

	private List<CAnnotations> annotations = new ArrayList<CAnnotations>();

	ISlotSpec(IEditor iEditor, CIdentity identity) {

		this.identity = identity;
		this.iEditor = iEditor;
	}

	void intersectWith(ISlotSpec other) {

		absorbSource(other.source);
		absorbValueTypes(other.valueTypes);
		intersectCardinality(other.cardinality);
		intersectActivation(other.activation);
		absorbEditability(other.editability);
		intersectFixedValues(other.fixedValues);
		absorbAnnotations(other.annotations);
	}

	void absorbSpec(ISlotSpec other) {

		absorbSource(other.source);
		absorbValueTypes(other.valueTypes);
		absorbCardinality(other.cardinality);
		absorbActivation(other.activation);
		absorbEditability(other.editability);
		absorbFixedValues(other.fixedValues);
		absorbAnnotations(other.annotations);
	}

	void absorbType(CSlot slotType) {

		absorbSource(slotType.getSource());
		absorbCardinality(slotType.getCardinality());
		absorbActivation(slotType.getActivation());
		absorbEditability(slotType.getEditability());
		absorbValueType(slotType.getValueType());
		absorbAnnotations(slotType.getAnnotations());
	}

	void absorbFixedValues(List<IValue> newFixedValues) {

		for (IValue value : newFixedValues) {

			if (!fixedValues.contains(value)) {

				fixedValues.add(value);
			}
		}
	}

	ISlotOps checkAddSlot(IFrame container) {

		CValue<?> valueType = getValueTypeOrNull();

		if (valueType == null) {

			return ISlotOps.NONE;
		}

		addSlot(container, valueType);

		return ISlotOps.SLOTS;
	}

	ISlotOps checkUpdateOrRemoveSlot(ISlot slot) {

		CValue<?> valueType = getValueTypeOrNull();

		return valueType == null
				? removeSlot(slot)
				: checkUpdateSlot(slot, valueType);
	}

	ISlotOps checkUpdateSlotValues(ISlot slot) {

		return updateFixedValues(slot) ? ISlotOps.VALUES : ISlotOps.NONE;
	}

	CIdentity getIdentity() {

		return identity;
	}

	private void absorbSource(CSource newSource) {

		source = source.combineWith(newSource);
	}

	private void absorbCardinality(CCardinality newCardinality) {

		cardinality = cardinality.getMoreRestrictive(newCardinality);
	}

	private void absorbActivation(CActivation newActivation) {

		activation = activation.getStrongest(newActivation);
	}

	private void absorbEditability(CEditability newEditability) {

		editability = editability.getStrongest(newEditability);
	}

	private void absorbValueTypes(List<CValue<?>> newValueTypes) {

		for (CValue<?> valueType : newValueTypes) {

			absorbValueType(valueType);
		}
	}

	private void absorbValueType(CValue<?> newValueType) {

		if (!valueTypes.contains(newValueType)) {

			valueTypes.add(newValueType);
		}
	}

	private void absorbAnnotations(List<CAnnotations> newAnnotations) {

		annotations.addAll(newAnnotations);
	}

	private void absorbAnnotations(CAnnotations newAnnotations) {

		annotations.add(newAnnotations);
	}

	private void intersectCardinality(CCardinality newCardinality) {

		cardinality = cardinality.getLessRestrictive(newCardinality);
	}

	private void intersectActivation(CActivation newActivation) {

		activation = activation.getWeakest(newActivation);
	}

	private void intersectFixedValues(List<IValue> newFixedValues) {

		fixedValues.retainAll(newFixedValues);
	}

	private void addSlot(IFrame container, CValue<?> valueType) {

		getFrameEditor(container)
			.addSlot(
				identity,
				source,
				valueType,
				cardinality,
				activation,
				editability,
				annotations);
	}

	private ISlotOps checkUpdateSlot(ISlot slot, CValue<?> valueType) {

		List<IValue> preValues = slot.getValues().asList();

		if (checkUpdateValueType(slot, valueType)) {

			if (valueUpdates(slot, preValues)) {

				return ISlotOps.SLOTS_AND_VALUES;
			}

			return ISlotOps.SLOTS;
		}

		return ISlotOps.NONE;
	}

	private boolean checkUpdateValueType(ISlot slot, CValue<?> valueType) {

		if (valueType.equals(slot.getType().getValueType())) {

			return false;
		}

		getSlotEditor(slot).setValueType(valueType);

		return true;
	}

	private ISlotOps removeSlot(ISlot slot) {

		getFrameEditor(slot.getContainer()).removeSlot(slot);

		return slot.getValues().isEmpty()
					? ISlotOps.SLOTS
					: ISlotOps.SLOTS_AND_VALUES;
	}

	private boolean updateFixedValues(ISlot slot) {

		return getSlotEditor(slot).updateFixedValues(fixedValues);
	}

	private boolean valueUpdates(ISlot slot, List<IValue> preValues) {

		return !slot.getValues().asList().equals(preValues);
	}

	private CValue<?> getValueTypeOrNull() {

		return new CValueIntersection(valueTypes).getCurrent();
	}

	private IFrameEditor getFrameEditor(IFrame frame) {

		return iEditor.getFrameEditor(frame);
	}

	private ISlotEditor getSlotEditor(ISlot slot) {

		return iEditor.getSlotEditor(slot);
	}
}
