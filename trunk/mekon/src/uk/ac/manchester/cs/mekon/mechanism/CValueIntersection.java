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

package uk.ac.manchester.cs.mekon.mechanism;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class CValueIntersection {

	private CValue<?> intersection;

	private class IntersectorCreator extends CValueVisitor {

		private CTypeValueIntersector<?> intersector = null;

		protected void visit(CFrame value) {

			intersector = new CFrameIntersector();
		}

		protected void visit(CNumber value) {

			intersector = new CNumberIntersector();
		}

		protected void visit(MFrame value) {

			intersector = new MFrameIntersector();
		}

		CTypeValueIntersector<?> create(CValue<?> firstOp) {

			visit(firstOp);

			return intersector;
		}
	}

	CValueIntersection(Set<CValue<?>> operands) {

		intersection = getIntersectionOrNull(operands);
	}

	CValue<?> getOrNull() {

		return intersection;
	}

	private CValue<?> getIntersectionOrNull(Set<CValue<?>> operands) {

		if (operands.isEmpty()) {

			return null;
		}

		CValue<?> firstOp = operands.iterator().next();

		if (operands.size() == 1) {

			return firstOp;
		}

		CTypeValueIntersector<?> intersector = createIntersector(firstOp);

		intersector.addOperands(operands);

		return intersector.getIntersectionOrNull();
	}

	private CTypeValueIntersector<?> createIntersector(CValue<?> firstOp) {

		return new IntersectorCreator().create(firstOp);
	}
}

