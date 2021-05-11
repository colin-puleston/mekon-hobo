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

		CStructureCreator(CBuilder builder, OBSlot topLevelSlot, OBAnnotations annotations) {

			this.builder = builder;
			this.topLevelSlot = topLevelSlot;
			this.annotations = annotations;
		}

		void checkCreate(CFrame container) {

			if (OBSlot.this == topLevelSlot || !spec.valueRequired()) {

				if (valueType.canBeSlotValueType()) {

					addOrUpdateSlot(container);
				}
			}
			else {

				if (topLevelSlot.valueType.canHaveFixedSlotValuesIfTopLevelValueType()) {

					addSlotValue(container);
				}
			}
		}

		void create(CExtender container) {

			container.addSlotValue(getIdentity(), ensureCValue());
		}

		private void addOrUpdateSlot(CFrame container) {

			CSlot slot = container.getSlots().getOrNull(getIdentity());

			if (slot == null) {

				slot = addSlot(container);
			}

			absorbSlotOverrides(slot);
		}

		private CSlot addSlot(CFrame container) {

			CCardinality cardinality = topLevelSlot.getCardinalityIfTopLevelSlot();

			return getEditor(container).addSlot(getIdentity(), ensureCValue(), cardinality);
		}

		private void addSlotValue(CFrame container) {

			getEditor(container).addSlotValue(getIdentity(), ensureCValue());
		}

		private CValue<?> ensureCValue() {

			return valueType
						.ensureCSlotValueType(
							builder,
							annotations,
							spec,
							topLevelSlot.valueType);
		}

		private void absorbSlotOverrides(CSlot slot) {

			CSlotEditor slotEd = builder.getSlotEditor(slot);
			OBPropertyAttributes overrides = spec.getPropertyAttributes();

			slotEd.absorbCardinality(overrides.getSlotCardinality());
			slotEd.absorbAssertionsEditability(overrides.getSlotAssertionsEditability());
			slotEd.absorbQueriesEditability(overrides.getSlotQueriesEditability());
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

		new CStructureCreator(builder, topLevelSlot, annotations).checkCreate(container);
	}

	void ensureCStructure(
			CBuilder builder,
			CExtender container,
			OBSlot topLevelSlot,
			OBAnnotations annotations) {

		new CStructureCreator(builder, topLevelSlot, annotations).create(container);
	}

	private CCardinality getCardinalityIfTopLevelSlot() {

		return valueType.getCardinalityIfTopLevelValueType(spec);
	}
}
