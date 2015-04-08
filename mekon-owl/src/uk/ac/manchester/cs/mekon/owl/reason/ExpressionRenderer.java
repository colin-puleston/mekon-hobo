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

package uk.ac.manchester.cs.mekon.owl.reason;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * @author Colin Puleston
 */
class ExpressionRenderer extends Renderer<OWLClassExpression> {

	private OWLDataFactory dataFactory;

	private ArrayDeque<ORFrame> frameStack = new ArrayDeque<ORFrame>();

	private class FrameToExpressionRenderer extends FrameRenderer {

		private Set<OWLClassExpression> conjuncts
					= new HashSet<OWLClassExpression>();

		FrameToExpressionRenderer(ORFrame frame) {

			super(frame);

			startRecurse(frame);
		}

		OWLClassExpression render(OWLClassExpression type) {

			renderSlots();
			endRecurse();

			if (conjuncts.isEmpty()) {

				return type;
			}

			conjuncts.add(type);

			return dataFactory.getOWLObjectIntersectionOf(conjuncts);
		}

		void addHasValueConstruct(OWLObjectProperty property, OWLClassExpression value) {

			conjuncts.add(
				dataFactory.
					getOWLObjectSomeValuesFrom(
						property,
						value));
		}

		void addOnlyValuesConstruct(OWLObjectProperty property, OWLClassExpression values) {

			conjuncts.add(
				dataFactory.
					getOWLObjectAllValuesFrom(
						property,
						values));
		}

		void addValueConstruct(OWLClassExpression construct) {

			conjuncts.add(construct);
		}

		OWLClassExpression toExpression(OWLClassExpression rendering) {

			return rendering;
		}

		OWLClassExpression createUnion(Set<OWLClassExpression> renderings) {

			return dataFactory.getOWLObjectUnionOf(renderings);
		}
	}

	ExpressionRenderer(OModel model) {

		super(model);

		dataFactory = model.getDataFactory();
	}

	OWLClassExpression render(ORFrame frame) {

		frameStack.clear();

		return renderFrame(frame);
	}

	FrameRenderer createFrameRenderer(ORFrame frame) {

		return new FrameToExpressionRenderer(frame);
	}

	private void startRecurse(ORFrame frame) {

		if (frameStack.contains(frame)) {

			throw new KModelException(
						"Cannot handle cyclic description involving: "
						+ frame);
		}

		frameStack.push(frame);
	}

	private void endRecurse() {

		frameStack.pop();
	}
}