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
import uk.ac.manchester.cs.mekon.mechanism.*;
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

			if (OBSlot.this == topLevelSlot) {

				if (canProvideSlot()) {

					addOrUpdateSlot(container, getCardinalityIfTopLevelSlot());
				}
			}
			else {

				if (topLevelSlot.spec.singleValued() && canProvideSlot()) {

					addOrUpdateSlot(container, CCardinality.SINGLE_VALUE);
				}

				if (spec.valuedRequired() && canProvideFixedValue()) {

					getEditor(container).addSlotValue(getIdentity(), getCValue());
				}
			}
		}

		void checkCreate(CExtender container) {

			if (canProvideFixedValue()) {

				container.addSlotValue(getIdentity(), getCValue());
			}
		}

		private void addOrUpdateSlot(CFrame container, CCardinality cardinality) {

			CSlot slot = container.getSlots().getOrNull(getIdentity());

			if (slot == null) {

				slot = addSlot(container, cardinality);

				annotations.checkAdd(builder, slot, spec.getProperty());
			}

			absorbSlotOverrides(slot);
		}

		private CSlot addSlot(CFrame container, CCardinality cardinality) {

			return getEditor(container).addSlot(getIdentity(), cardinality, getCValue());
		}

		private void absorbSlotOverrides(CSlot slot) {

			OBPropertyAttributes propAttrs = spec.getPropertyAttributes();

			CCardinality cardOverride = propAttrs.getSlotCardinality();
			CEditability editOverride = propAttrs.getSlotEditability();

			CSlotEditor slotEd = builder.getSlotEditor(slot);

			slotEd.absorbCardinality(cardOverride);
			slotEd.absorbEditability(editOverride);
		}

		private boolean canProvideSlot() {

			return valueType.canBeSlotValueType();
		}

		private boolean canProvideFixedValue() {

			return valueType
					.canBeFixedSlotValue(
						getCValue(),
						valueStructureAllowed());
		}

		private boolean valueStructureAllowed() {

			return topLevelSlot.valueStructureAllowedIfTopLevelSlot();
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

		private CFrameEditor getEditor(CFrame container) {

			return builder.getFrameEditor(container);
		}
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
