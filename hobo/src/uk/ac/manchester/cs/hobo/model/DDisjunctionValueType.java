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
class DDisjunctionValueType<D extends DObject> extends DValueType<DDisjunction<D>> {

	private DModel model;
	private Class<D> disjunctsClass;
	private CFrame slotValueType;

	DDisjunctionValueType(DModel model, DObjectValueType<D> objectValueType) {

		this.model = model;

		disjunctsClass = objectValueType.getValueClass();
		slotValueType = objectValueType.getSlotValueType();
	}

	CFrame getSlotValueType() {

		return slotValueType;
	}

	IValue toSlotValue(DDisjunction<D> value) {

		return value.asDisjunctionIFrame();
	}

	DDisjunction<D> toFieldValue(IValue value) {

		return new DDisjunction<D>(model, disjunctsClass, (IFrame)value);
	}

	CCardinality getDefaultCardinalityForArrays() {

		return CCardinality.REPEATABLE_TYPES;
	}
}
