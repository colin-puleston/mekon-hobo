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

package uk.ac.manchester.cs.mekon.model.util;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents an intersection of {@link CValue} objects of a
 * specific type.
 *
 * @author Colin Puleston
 */
public abstract class CTypeValueIntersection<V extends CValue<?>> {

	/**
	 * Adds a set of operands.
	 *
	 * @param operands Operands to add
	 */
	public void addOperands(Collection<V> operands) {

		for (V operand : operands) {

			addOperand(operand);
		}
	}

	/**
	 * Adds an operand.
	 *
	 * @param operand Operand to add
	 */
	public abstract void addOperand(V operand);

	/**
	 * Provides the current intersection, if applicable.
	 *
	 * @return Current intersection, or null if intersection is empty
	 */
	public abstract V getCurrent();

	CTypeValueIntersection() {
	}

	void addTypeOperand(CValue<?> operand) {

		addOperand(operand.castAs(getOperandType()));
	}

	abstract Class<V> getOperandType();
}
