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
class OBExtensionFrame extends OBExpressionFrame {

	private OBAtomicFrame baseFrame;
	private Set<OBSlot> slots = new HashSet<OBSlot>();
	private boolean forDefinition;

	OBExtensionFrame(OBAtomicFrame baseFrame, boolean forDefinition) {

		this.baseFrame = baseFrame;
		this.forDefinition = forDefinition;
	}

	void addSlot(OBSlot slot) {

		slots.add(slot);
	}

	boolean canBeSlotValueType() {

		return false;
	}

	boolean canBeFixedSlotValue(
				CValue<?> cValue,
				boolean valueStructureAllowed) {

		return !valueStructureAllowed;
	}

	boolean valueStructureAllowedIfSlotValueType() {

		return true;
	}

	CFrame ensureCStructure(CBuilder builder, OBAnnotations annotations) {

		CFrame baseCFrame = ensureBaseCFrame(builder, annotations);
		CExtender extender = new CExtender(baseCFrame);

		for (OBSlot slot : slots) {

			OBSlot topSlot = baseFrame.findTopLevelSlot(slot);

			slot.ensureCStructure(
				builder,
				extender,
				topSlot,
				annotations,
				forDefinition);
		}

		return extender.extend();
	}

	private CFrame ensureBaseCFrame(CBuilder builder, OBAnnotations annotations) {

		return baseFrame.ensureCStructure(builder, annotations);
	}
}