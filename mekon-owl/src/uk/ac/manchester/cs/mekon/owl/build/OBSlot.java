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
abstract class OBSlot extends OIdentified {

	private OBSlotSpec spec;
	private boolean singleValued;

	private class CStructureCreator {

		private CBuilder builder;
		private OBAnnotations annotations;
		private CFrame container;
		private CFrameEditor containerEd;
		private CValue<?> value;

		CStructureCreator(
			CBuilder builder,
			CFrame container,
			OBSlot topLevelSlot,
			OBAnnotations annotations) {

			this.builder = builder;
			this.annotations = annotations;
			this.container = container;

			containerEd = builder.getFrameEditor(container);
			value = ensureCValue(builder, topLevelSlot, annotations);

			if (OBSlot.this == topLevelSlot) {

				addOrUpdateSlot(getDefaultCardinalityForTopLevelSlot());
			}
			else {

				if (topLevelSlot.singleValued) {

					addOrUpdateSlot(CCardinality.SINGLETON);
				}

				if (canBeFixedValue(value)) {

					checkAddSlotValue();
				}
			}
		}

		private void addOrUpdateSlot(CCardinality defaultCardinality) {

			CIdentity id = getIdentity();
			CSlot slot = container.getSlots().getOrNull(id);

			if (slot == null) {

				slot = containerEd.addSlot(id, defaultCardinality, value);

				annotations.checkAdd(builder, slot, spec.getProperty());
			}

			absorbSlotOverrides(slot);
		}

		private void absorbSlotOverrides(CSlot slot) {

			OBPropertyAttributes propAttrs = spec.getPropertyAttributes();

			CCardinality cardOverride = propAttrs.getSlotCardinality();
			CEditability editOverride = propAttrs.getSlotEditability();

			CSlotEditor slotEd = builder.getSlotEditor(slot);

			slotEd.absorbCardinality(cardOverride);
			slotEd.absorbEditability(editOverride);
		}

		private void checkAddSlotValue() {

			containerEd.addSlotValue(getIdentity(), value);
		}
	}

	OBSlot(OBSlotSpec spec) {

		this(spec, spec.singleValued());
	}

	OBSlot(OBSlotSpec spec, boolean singleValued) {

		super(spec.getProperty(), spec.getLabel());

		this.spec = spec;
		this.singleValued = singleValued;
	}

	void ensureCStructure(
			CBuilder builder,
			CFrame container,
			OBSlot topLevelSlot,
			OBAnnotations annotations) {

		if (validSlotValueType()) {

			new CStructureCreator(builder, container, topLevelSlot, annotations);
		}
	}

	abstract boolean validSlotValueType();

	abstract CCardinality getDefaultCardinalityForMultiValuedTopLevelSlot();

	abstract CValue<?> ensureCValue(
							CBuilder builder,
							OBSlot topLevelSlot,
							OBAnnotations annotations);

	boolean canBeFixedValue(CValue<?> cValue) {

		return false;
	}

	private CCardinality getDefaultCardinalityForTopLevelSlot() {

		return singleValued
				? CCardinality.SINGLETON
				: getDefaultCardinalityForMultiValuedTopLevelSlot();
	}
}
