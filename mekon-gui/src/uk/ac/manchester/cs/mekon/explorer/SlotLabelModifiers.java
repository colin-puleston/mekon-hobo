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

package uk.ac.manchester.cs.mekon.explorer;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class SlotLabelModifiers {

	static String forCardinality(CSlot cSlot) {

		CCardinality cardinality = cSlot.getCardinality();
		boolean onePossibleValue = cSlot.getValueType().onePossibleValue();

		return forCardinality(cardinality, onePossibleValue);
	}

	static String forCardinality(
					CCardinality cardinality,
					boolean onePossibleValue) {

		switch (cardinality) {

			case SINGLE_VALUE:
				return "[x]";

			case UNIQUE_TYPES:
				return "[x,y,z]";

			case REPEATABLE_TYPES:
				return onePossibleValue ? "[x,x,x]" : "[x,y,y]";
		}

		throw new Error("Unrecognised cardinality value: " + cardinality);
	}

	static String forValueType(CSlot cSlot) {

		CValue<?> valueType = cSlot.getValueType();
		boolean conceptValued = valueType instanceof MFrame;

		return forValueType(valueType.getDisplayLabel(), conceptValued);
	}

	static String forValueType(String valueType, boolean conceptValued) {

		return conceptValued ? "(" + valueType + ")" : "<" + valueType + ">";
	}
}
