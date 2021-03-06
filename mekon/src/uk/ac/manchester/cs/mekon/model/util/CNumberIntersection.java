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
 * Represents an intersection of {@link CNumber} objects.
 *
 * @author Colin Puleston
 */
public class CNumberIntersection extends CTypeValueIntersection<CNumber> {

	private Set<CNumber> currents = new HashSet<CNumber>();

	/**
	 * Constructor.
	 */
	public CNumberIntersection() {
	}

	/**
	 * Constructor.
	 *
	 * @param operands Initial operands to add
	 */
	public CNumberIntersection(Collection<CNumber> operands) {

		addOperands(operands);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addOperand(CNumber operand) {

		for (CNumber mostSpecific : currents) {

			if (operand.intersectsWith(mostSpecific)) {

				operand = operand.getIntersection(mostSpecific);
				currents.remove(mostSpecific);

				break;
			}
		}

		currents.add(operand);
	}

	/**
	 * {@inheritDoc}
	 */
	public CNumber getCurrent() {

		return currents.size() == 1 ? currents.iterator().next() : null;
	}

	Class<CNumber> getOperandType() {

		return CNumber.class;
	}
}
