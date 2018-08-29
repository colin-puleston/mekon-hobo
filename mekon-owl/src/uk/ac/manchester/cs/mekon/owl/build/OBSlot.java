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

package uk.ac.manchester.cs.mekon.owl.build;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class OBSlot extends OIdentified {

	private OBSlotSpec spec;
	private OBValue<?> valueType;

	private class CStructureCreator {

		private CBuilder builder;
		private OBSlot topLevelSlot;
		private OBAnnotations annotations;
		private CValue<?> cValue = null;

		CStructureCreator(
			CBuilder builder,
			OBSlot topLevelSlot,
			OBAnnotations annotations) {

			this.builder = builder;
			this.topLevelSlot = topLevelSlot;
			this.annotations = annotations;
		}

		void checkCreate(CFrame container) {

			if (canBeFixedValue()) {

				addSlotValue(container);
			}
			else {

				if (valueType.canBeSlotValueType()) {

					addOrUpdateSlot(container);
				}
			}
		}

		void checkCreate(CExtender container) {

			container.addSlotValue(getIdentity(), getCValue());
		}

		private void addOrUpdateSlot(CFrame container) {

			CSlot slot = container.getSlots().getOrNull(getIdentity());

			if (slot == null) {

				slot = addSlot(container);
			}

			absorbSlotOverrides(slot);
		}

		private CSlot addSlot(CFrame container) {

			return getEditor(container).addSlot(getIdentity(), getCValue(), getCardinality());
		}

		private void addSlotValue(CFrame container) {

			getEditor(container).addSlotValue(getIdentity(), getCValue());
		}

		private void absorbSlotOverrides(CSlot slot) {

			OBPropertyAttributes propAttrs = spec.getPropertyAttributes();

			CCardinality cardOverride = propAttrs.getSlotCardinality();
			CEditability editOverride = propAttrs.getSlotEditability();

			CSlotEditor slotEd = builder.getSlotEditor(slot);

			slotEd.absorbCardinality(cardOverride);
			slotEd.absorbEditability(editOverride);
		}

		private boolean canBeFixedValue() {

			return OBSlot.this != topLevelSlot
						&& spec.valueRequired()
						&& valueTypeCanProvideFixedValue();
		}

		private boolean valueTypeCanProvideFixedValue() {

			return valueType
					.canBeFixedSlotValue(
						getCValue(),
						valueStructureAllowed());
		}

		private CCardinality getCardinality() {

			return topLevelSlot.getCardinalityIfTopLevelSlot();
		}

		private CValue<?> getCValue() {

			if (cValue == null) {

				cValue = ensureCValue();
			}

			return cValue;
		}

		private CValue<?> ensureCValue() {

			return valueType
						.ensureCSlotValueType(
							builder,
							annotations,
							topLevelSlot.valueType,
							valueStructureAllowed());
		}

		private boolean valueStructureAllowed() {

			return topLevelSlot.valueStructureAllowedIfTopLevelSlot();
		}

		private CFrameEditor getEditor(CFrame container) {

			return builder.getFrameEditor(container);
		}
	}

	public int compareTo(OIdentified other) {

		int comp = super.compareTo(other);

		return comp == 0 ? 1 : comp;
	}

	OBSlot(OBSlotSpec spec, OBValue<?> valueType) {

		super(spec.getProperty(), spec.getLabel());

		this.spec = spec;
		this.valueType = valueType;
	}

	void ensureCStructure(
			CBuilder builder,
			CFrame container,
			OBSlot topLevelSlot,
			OBAnnotations annotations) {

		new CStructureCreator(
				builder,
				topLevelSlot,
				annotations)
					.checkCreate(container);
	}

	void ensureCStructure(
			CBuilder builder,
			CExtender container,
			OBSlot topLevelSlot,
			OBAnnotations annotations) {

		new CStructureCreator(
				builder,
				topLevelSlot,
				annotations)
					.checkCreate(container);
	}

	private CCardinality getCardinalityIfTopLevelSlot() {

		if (spec.singleValued()) {

			return CCardinality.SINGLE_VALUE;
		}

		if (valueStructureAllowedIfTopLevelSlot()) {

			return CCardinality.REPEATABLE_TYPES;
		}

		return CCardinality.UNIQUE_TYPES;
	}

	private boolean valueStructureAllowedIfTopLevelSlot() {

		switch (spec.getFrameSlotsPolicy()) {

			case CFRAME_VALUED_ONLY:
				return false;

			case CFRAME_VALUED_IF_NO_STRUCTURE:
				return valueType.valueStructureAllowedIfSlotValueType();
		}

		return true;
	}
}
