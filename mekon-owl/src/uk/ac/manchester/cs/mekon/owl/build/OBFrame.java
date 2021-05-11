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

/**
 * @author Colin Puleston
 */
abstract class OBFrame extends OBValue<CFrame> {

	OBFrame() {
	}

	OBFrame(OWLEntity sourceEntity, String label) {

		super(sourceEntity, label);
	}

	CValue<?> ensureCSlotValueType(
					CBuilder builder,
					OBAnnotations annotations,
					OBSlotSpec slotSpec,
					OBValue<?> topLevelValueType) {

		CFrame cFrame = ensureCFrame(builder, annotations);

		if (valueStructureAllowed(slotSpec, (OBFrame)topLevelValueType)) {

			return cFrame;
		}

		return cFrame.getType();
	}

	CCardinality getCardinalityIfTopLevelValueType(OBSlotSpec slotSpec) {

		if (slotSpec.singleValued()) {

			return CCardinality.SINGLE_VALUE;
		}

		if (repeatValuesAllowedIfTopLevelValueType(slotSpec)) {

			return CCardinality.REPEATABLE_TYPES;
		}

		return CCardinality.UNIQUE_TYPES;
	}

	abstract CFrame ensureCFrame(CBuilder builder, OBAnnotations annotations);

	abstract boolean valueStructurePossibleIfSlotValueType();

	private boolean valueStructureAllowed(OBSlotSpec slotSpec, OBFrame topLevelValueType) {

		switch (slotSpec.getFrameSlotsPolicy()) {

			case CFRAME_VALUED_ONLY:
				return false;

			case IFRAME_VALUED_ONLY:
				return true;
		}

		return topLevelValueType.valueStructurePossibleIfSlotValueType();
	}

	private boolean repeatValuesAllowedIfTopLevelValueType(OBSlotSpec slotSpec) {

		if (slotSpec.getFrameSlotsPolicy() == OBFrameSlotsPolicy.CFRAME_VALUED_ONLY) {

			return false;
		}

		return valueStructurePossibleIfSlotValueType();
	}
}
