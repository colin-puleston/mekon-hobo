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
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
abstract class OBSlot extends OIdentified {

	private boolean singleValued;

	OBSlot(OBSlotSpec spec) {

		super(spec.getProperty(), spec.getLabel());

		singleValued = spec.singleValued();
	}

	void checkAddCSlotAndValues(
			CBuilder builder,
			CFrame container,
			OBSlot topLevelSlot,
			OBEntityAnnotations annotations) {

		if (!validSlotValueType()) {

			return;
		}

		CProperty cProperty = builder.getProperties().get(getIdentity());
		CValue<?> cValue = ensureCValue(builder, topLevelSlot, annotations);
		CFrameEditor containerEd = builder.getFrameEditor(container);

		if (this == topLevelSlot) {

			containerEd.addSlot(cProperty, getCardinality(), cValue);
		}
		else {

			if (topLevelSlot.singleValued) {

				containerEd.addSlot(cProperty, CCardinality.SINGLETON, cValue);
			}

			if (canBeFixedValue(cValue)) {

				containerEd.addSlotValue(cProperty, cValue);
			}
		}
	}

	abstract boolean validSlotValueType();

	abstract CValue<?> ensureCValue(
							CBuilder builder,
							OBSlot topLevelSlot,
							OBEntityAnnotations annotations);

	boolean canBeFixedValue(CValue<?> cValue) {

		return false;
	}

	private CCardinality getCardinality() {

		return singleValued ? CCardinality.SINGLETON : CCardinality.FREE;
	}
}
