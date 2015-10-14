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
import uk.ac.manchester.cs.mekon.mechanism.network.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class ExpressionRenderer extends Renderer<OWLClassExpression> {

	private OWLDataFactory dataFactory;

	private ArrayDeque<NNode> nodeStack = new ArrayDeque<NNode>();

	private class NodeToExpressionRenderer extends NodeRenderer {

		private Set<OWLClassExpression> conjuncts
					= new HashSet<OWLClassExpression>();

		NodeToExpressionRenderer(NNode node) {

			super(node);

			startRecurse(node);
		}

		OWLClassExpression render(OWLClassExpression type) {

			renderAttributes();
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

	ExpressionRenderer(OModel model, ORSemantics semantics) {

		super(model, semantics);

		dataFactory = model.getDataFactory();
	}

	OWLClassExpression render(NNode node) {

		nodeStack.clear();

		return renderNode(node);
	}

	NodeRenderer createNodeRenderer(NNode node) {

		return new NodeToExpressionRenderer(node);
	}

	private void startRecurse(NNode node) {

		if (nodeStack.contains(node)) {

			throw new KModelException(
						"Cannot handle cyclic description involving: "
						+ node);
		}

		nodeStack.push(node);
	}

	private void endRecurse() {

		nodeStack.pop();
	}
}