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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * @author Colin Puleston
 */
abstract class OBFrameSlot extends OBSlot {

	private OBSlotSpec spec;

	OBFrameSlot(OBSlotSpec spec) {

		super(spec);

		this.spec = spec;
	}

	boolean defaultToUniqueTypesIfMultiValuedTopLevelSlot() {

		return cFrameValuedIfTopLevelSlot();
	}

	abstract boolean anyStructuredValues();

	abstract CFrame ensureCFrame(CBuilder builder, OBAnnotations annotations);

	CValue<?> ensureCValue(
				CBuilder builder,
				OBSlot topLevelSlot,
				OBAnnotations annotations) {

		CFrame cFrame = ensureCFrame(builder, annotations);

		return topLevelSlotIsCFrameValued(topLevelSlot) ? cFrame.getType() : cFrame;
	}

	boolean topLevelSlotIsCFrameValued(OBSlot topLevelSlot) {

		return ((OBFrameSlot)topLevelSlot).cFrameValuedIfTopLevelSlot();
	}

	private boolean cFrameValuedIfTopLevelSlot() {

		switch (spec.getFrameSlotsPolicy()) {

			case CFRAME_VALUED_ONLY:
				return true;

			case CFRAME_VALUED_IF_NO_STRUCTURE:
				return !anyStructuredValues();
		}

		return false;
	}
}
