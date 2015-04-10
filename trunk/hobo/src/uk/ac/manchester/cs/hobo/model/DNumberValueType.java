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

package uk.ac.manchester.cs.hobo.model;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class DNumberValueType<N extends Number> extends DValueType<N> {

	private CNumber slotValueType;
	private Class<N> numberType;

	DNumberValueType(CNumberDef definition, Class<N> numberType) {

		this.numberType = numberType;

		slotValueType = createNumber(definition);
	}

	CNumber getSlotValueType() {

		return slotValueType;
	}

	IValue toSlotValue(N value) {

		return INumber.create(value);
	}

	N toFieldValue(IValue value) {

		return numberType.cast(slotValueType.castValue(value).asTypeNumber());
	}

	boolean convertibleToFieldValue(IValue value) {

		return !slotValueType.castValue(value).indefinite();
	}

	CCardinality getDefaultCardinalityForArrays() {

		throw new Error("Method should never be invoked!");
	}

	private CNumber createNumber(CNumberDef definition) {

		checkNumberDef(definition);

		return definition.createNumber();
	}

	private void checkNumberDef(CNumberDef definition) {

		Class<? extends Number> defNumberType = definition.getNumberType();

		if (defNumberType != numberType) {

			throw new Error(
						"Incompatible number-types: "
						+ " Specfied type: "
						+ numberType
						+ ", Type for specified CNumberDef: "
						+ defNumberType);
		}
	}
}