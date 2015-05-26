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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * @author Colin Puleston
 */
class OBFrameSlot extends OBSlot {

	private OBSlotSpec spec;
	private OBFrame valueType;

	OBFrameSlot(OBSlotSpec spec, OBFrame valueType) {

		super(spec);

		this.spec = spec;
		this.valueType = valueType;
	}

	boolean canProvideSlot() {

		return valueType.canBeSlotValueType();
	}

	boolean couldProvideFixedValue(OBSlot topLevelSlot) {

		return valueType.couldBeFixedValueForSlot(topLevelSlot);
	}

	boolean canBeFixedValue(CValue<?> cValue) {

		return valueType.canBeFixedValueForSlot(cValue);
	}

	boolean defaultToUniqueTypesIfMultiValuedTopLevelSlot() {

		return isCFrameValuedIfTopLevelSlot();
	}

	boolean isCFrameValuedIfTopLevelSlot() {

		switch (spec.getFrameSlotsPolicy()) {

			case CFRAME_VALUED_ONLY:
				return true;

			case CFRAME_VALUED_IF_NO_STRUCTURE:
				return !valueType.slotsInHierarchy();
		}

		return false;
	}

	CValue<?> ensureCValue(
				CBuilder builder,
				OBSlot topLevelSlot,
				OBAnnotations annotations) {

		CFrame cFrame = valueType.ensureCStructure(builder, annotations);

		return topLevelSlotIsCFrameValued(topLevelSlot) ? cFrame.getType() : cFrame;
	}

	private boolean topLevelSlotIsCFrameValued(OBSlot topLevelSlot) {

		return ((OBFrameSlot)topLevelSlot).isCFrameValuedIfTopLevelSlot();
	}
}
