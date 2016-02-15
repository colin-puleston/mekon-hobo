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
 * Represents an intersection of {@link CValue} objects.
 *
 * @author Colin Puleston
 */
public class CValueIntersection {

	static private final EmptyIntersection EMPTY_INTERSECTION = new EmptyIntersection();

	static private class TypeIntersectionCreator extends CValueVisitor {

		private CTypeValueIntersection<?> intersection = null;

		protected void visit(CFrame value) {

			intersection = new CFrameIntersection();
		}

		protected void visit(CNumber value) {

			intersection = new CNumberIntersection();
		}

		protected void visit(CString value) {

			intersection = new CStringIntersection();
		}

		protected void visit(MFrame value) {

			intersection = new MFrameIntersection();
		}

		CTypeValueIntersection<?> create(CValue<?> operand) {

			visit(operand);

			return intersection;
		}
	}

	private CTypeValueIntersection<?> typeIntersection = EMPTY_INTERSECTION;

	/**
	 * Constructor.
	 */
	public CValueIntersection() {
	}

	/**
	 * Constructor.
	 *
	 * @param operands Initial operands to add
	 */
	public CValueIntersection(Collection<CValue<?>> operands) {

		addOperands(operands);
	}

	/**
	 * Adds a set of operands.
	 *
	 * @param operands Operands to add
	 */
	public void addOperands(Collection<CValue<?>> operands) {

		for (CValue<?> operand : operands) {

			addOperand(operand);
		}
	}

	/**
	 * Adds an operand.
	 *
	 * @param operand Operand to add
	 */
	public void addOperand(CValue<?> operand) {

		resolveTypeIntersection(operand).addTypeOperand(operand);
	}

	/**
	 * Provides the current intersection, if applicable.
	 *
	 * @return Current intersection, or null if intersection is empty
	 */
	public CValue<?> getCurrent() {

		return typeIntersection.getCurrent();
	}

	private CTypeValueIntersection<?> resolveTypeIntersection(CValue<?> operand) {

		if (typeIntersection == EMPTY_INTERSECTION) {

			typeIntersection = createTypeIntersection(operand);
		}

		return typeIntersection;
	}

	private CTypeValueIntersection<?> createTypeIntersection(CValue<?> operand) {

		return new TypeIntersectionCreator().create(operand);
	}
}

