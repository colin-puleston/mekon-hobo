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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class ExpressionRenderer extends Renderer<OWLClassExpression> {

	private OModel model;
	private OWLDataFactory dataFactory;

	private ArrayDeque<NNode> nodeStack = new ArrayDeque<NNode>();

	private class NodeToExpressionRenderer extends NodeRenderer {

		private NNode node;
		private Set<OWLClassExpression> conjuncts = new HashSet<OWLClassExpression>();

		NodeToExpressionRenderer(NNode node) {

			super(node);

			this.node = node;
		}

		OWLClassExpression render(OWLClassExpression type) {

			IRI refIRI = getInstanceIRIOrNull();

			if (refIRI != null) {

				return renderInstanceRef(refIRI);
			}

			startRecurse(node);
			renderFeatures();
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

		private IRI getInstanceIRIOrNull() {

			if (node.instanceRef()) {

				return OStoredInstanceIRIs.toIRI(node.getInstanceRef());
			}

			return null;
		}

		private OWLClassExpression renderInstanceRef(IRI iri) {

			OWLNamedIndividual ind = createIndividual(iri);

			addAxiom(dataFactory.getOWLDeclarationAxiom(ind));

			return dataFactory.getOWLObjectOneOf(ind);
		}

		private OWLNamedIndividual createIndividual(IRI iri) {

			return dataFactory.getOWLNamedIndividual(iri);
		}

		private void addAxiom(OWLAxiom axiom) {

			model.addInstanceAxiom(axiom);
		}
	}

	ExpressionRenderer(ReasoningModel reasoningModel) {

		this(reasoningModel, null);
	}

	ExpressionRenderer(ReasoningModel reasoningModel, StringValueProxies stringValueProxies) {

		super(reasoningModel, stringValueProxies);

		model = reasoningModel.getModel();
		dataFactory = model.getDataFactory();
	}

	OWLClassExpression render(NNode node) {

		nodeStack.clear();

		return renderNode(node);
	}

	NodeRenderer createNodeRenderer(NNode node) {

		return new NodeToExpressionRenderer(node);
	}

	OWLClassExpression nodeRenderingToExpression(OWLClassExpression rendering) {

		return rendering;
	}

	OWLClassExpression renderUnion(Set<OWLClassExpression> operands) {

		return dataFactory.getOWLObjectUnionOf(operands);
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