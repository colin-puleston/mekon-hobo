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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class CValueIntersection {

	private Set<CValue<?>> intersection = null;

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

		CTypeValueIntersector<?> create(CValue<?> value) {

			visit(value);

			return intersector;
		}
	}

	CValueIntersection(Set<CValue<?>> values) {

		intersection = createIntersection(values);
	}

	Set<CValue<?>> getValues() {

		return intersection;
	}

	CValue<?> getSingleValue() {

		if (intersection.size() == 1) {

			return intersection.iterator().next();
		}

		if (intersection.isEmpty()) {

			throw new KModelException("Intersection is empty-set");
		}

		throw new KModelException(
					"Intersection consists of multiple values "
					+ "(" + intersection + ")");
	}

	private Set<CValue<?>> createIntersection(Set<CValue<?>> values) {

		CTypeValueIntersector<?> intersector = createIntersector(values);

		for (CValue<?> value : values) {

			intersector.addOperand(value);
		}

		return new HashSet<CValue<?>>(intersector.getIntersection());
	}

	private CTypeValueIntersector<?> createIntersector(Set<CValue<?>> values) {

		if (values.isEmpty()) {

			throw new KModelException("Cannot create intersection for empty value-set");
		}

		return new IntersectorCreator().create(values.iterator().next());
	}
}

