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

package uk.ac.manchester.cs.mekon.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class IDisjunctsSlot extends ISlot {

	static private final String IDENTIFIER = "@DISJUNCTS";
	static private final String LABEL = "disjuncts";
	static private final CIdentity IDENTITY = new CIdentity(IDENTIFIER, LABEL);

	static private CSlot createType(IDisjunction disjunction) {

		CFrame dType = disjunction.getType();

		return new CSlot(dType, IDENTITY, dType, CCardinality.REPEATABLE_TYPES);
	}

	private IDisjunction disjunction;

	private class DisjunctsChecker implements KValuesListener<IValue> {

		public void onAdded(IValue value) {

			checkDisjunct(valueAsIFrame(value));
		}

		public void onRemoved(IValue value) {
		}

		public void onCleared(List<IValue> values) {
		}

		DisjunctsChecker() {

			getValues().addValuesListener(this);
		}
	}

	IDisjunctsSlot(IDisjunction disjunction) {

		super(createType(disjunction), disjunction);

		this.disjunction = disjunction;

		new DisjunctsChecker();
	}

	List<IFrame> getDisjuncts() {

		List<IFrame> disjuncts = new ArrayList<IFrame>();

		for (IValue disjunct : getValues().asList()) {

			disjuncts.add(valueAsIFrame(disjunct));
		}

		return disjuncts;
	}

	private IFrame valueAsIFrame(IValue value) {

		if (value instanceof IFrame) {

			return (IFrame)value;
		}

		throw new Error("Value not of type IFrame: " + value);
	}

	private void checkDisjunct(IFrame value) {

		if (value.getCategory().disjunction()) {

			throw new KAccessException(
						"Attempting to add disjunction frame as "
						+ "disjunct for another disjunction frame: "
						+ disjunction);
		}
	}
}
