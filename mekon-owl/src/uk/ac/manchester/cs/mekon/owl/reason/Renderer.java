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

/**
 * @author Colin Puleston
 */
abstract class Renderer<NR extends OWLObject> {

	private OModel model;
	private ORSemantics semantics;

	private OWLDataFactory dataFactory;
	private NumberRenderer defaultNumberRenderer;

	abstract class NodeRenderer {

		private NNode node;

		NodeRenderer(NNode node) {

			this.node = node;
		}

		abstract NR render(OWLClassExpression type);

		void renderAttributes() {

			for (NLink link : node.getLinks()) {

				renderLinkValues(link);
			}

			for (NNumeric numeric : node.getNumerics()) {

				renderNumericValues(numeric);
			}
		}

		void addHasValueConstructForNode(OWLObjectProperty property, NR rendering) {

			addHasValueConstruct(property, toExpression(rendering));
		}

		void addOnlyValuesConstructForNodes(OWLObjectProperty property, Set<NR> renderings) {

			addOnlyValuesConstruct(property, toExpression(renderings));
		}

		abstract void addHasValueConstruct(
							OWLObjectProperty property,
							OWLClassExpression value);

		abstract void addOnlyValuesConstruct(
							OWLObjectProperty property,
							OWLClassExpression value);

		abstract void addValueConstruct(OWLClassExpression construct);

		abstract OWLClassExpression toExpression(NR rendering);

		abstract OWLClassExpression createUnion(Set<NR> renderings);

		private void renderLinkValues(NLink link) {

			new LinkValuesRenderer(this, link).renderToNode();
		}

		private void renderNumericValues(NNumeric numeric) {

			getNumericValuesRenderer(numeric).renderToNode();
		}

		private ValuesRenderer<INumber, ?> getNumericValuesRenderer(NNumeric numeric) {

			return directNumeric(numeric)
					? new DirectNumericValuesRenderer(this, numeric)
					: new IndirectNumericValuesRenderer(this, numeric);
		}

		private OWLClassExpression toExpression(Set<NR> valueRenderings) {

			if (valueRenderings.isEmpty()) {

				return dataFactory.getOWLNothing();
			}

			if (valueRenderings.size() == 1) {

				return toExpression(valueRenderings.iterator().next());
			}

			return createUnion(valueRenderings);
		}

		private boolean directNumeric(NNumeric numeric) {

			IRI iri = NetworkIRIs.getProperty(numeric);

			return model.getDataProperties().contains(iri);
		}
	}

	private abstract class ValuesRenderer<V, IV> {

		private NAttribute<V> attribute;

		ValuesRenderer(NAttribute<V> attribute) {

			this.attribute = attribute;
		}

		void renderToNode() {

			Set<IV> intermediates = getIntermediateValues(attribute);

			for (IV intermediate : intermediates) {

				addHasValueConstruct(intermediate);
			}

			if (closedWorldSemantics()) {

				addOnlyValuesConstruct(intermediates);
			}
		}

		abstract IV getIntermediateValue(V value);

		abstract void addHasValueConstruct(IV intermediate);

		abstract void addOnlyValuesConstruct(Set<IV> intermediates);

		OWLObjectProperty getObjectProperty() {

			return model.getObjectProperties().get(getAttributeIRI());
		}

		OWLDataProperty getDataProperty() {

			return model.getDataProperties().get(getAttributeIRI());
		}

		private boolean closedWorldSemantics() {

			return semantics.getWorld(getAttributeIRI()).closed();
		}

		private Set<IV> getIntermediateValues(NAttribute<V> attribute) {

			Set<IV> intermediates = new HashSet<IV>();

			for (V value : attribute.getValues()) {

				intermediates.add(getIntermediateValue(value));
			}

			return intermediates;
		}

		private IRI getAttributeIRI() {

			return NetworkIRIs.getProperty(attribute);
		}
	}

	private class LinkValuesRenderer extends ValuesRenderer<NNode, NR> {

		private NodeRenderer nodeRenderer;
		private OWLObjectProperty property;

		LinkValuesRenderer(
			NodeRenderer nodeRenderer,
			NAttribute<NNode> attribute) {

			super(attribute);

			this.nodeRenderer = nodeRenderer;

			property = getObjectProperty();
		}

		NR getIntermediateValue(NNode value) {

			return renderNode(value);
		}

		void addHasValueConstruct(NR intermediate) {

			nodeRenderer.addHasValueConstructForNode(property, intermediate);
		}

		void addOnlyValuesConstruct(Set<NR> intermediates) {

			nodeRenderer.addOnlyValuesConstructForNodes(property, intermediates);
		}
	}

	private class IndirectNumericValuesRenderer extends ValuesRenderer<INumber, OWLClassExpression> {

		private NodeRenderer nodeRenderer;
		private OWLObjectProperty property;

		IndirectNumericValuesRenderer(
			NodeRenderer nodeRenderer,
			NAttribute<INumber> attribute) {

			super(attribute);

			this.nodeRenderer = nodeRenderer;

			property = getObjectProperty();
		}

		OWLClassExpression getIntermediateValue(INumber value) {

			return defaultNumberRenderer.renderHasValue(value);
		}

		void addHasValueConstruct(OWLClassExpression intermediate) {

			nodeRenderer.addHasValueConstruct(property, intermediate);
		}

		void addOnlyValuesConstruct(Set<OWLClassExpression> intermediates) {

			nodeRenderer.addOnlyValuesConstruct(property, createUnion(intermediates));
		}

		private OWLClassExpression createUnion(Set<OWLClassExpression> exprs) {

			return dataFactory.getOWLObjectUnionOf(exprs);
		}
	}

	private class DirectNumericValuesRenderer extends ValuesRenderer<INumber, INumber> {

		private NodeRenderer nodeRenderer;
		private NumberRenderer numberRenderer;

		DirectNumericValuesRenderer(
			NodeRenderer nodeRenderer,
			NAttribute<INumber> attribute) {

			super(attribute);

			this.nodeRenderer = nodeRenderer;

			numberRenderer = new NumberRenderer(model, getDataProperty());
		}

		INumber getIntermediateValue(INumber value) {

			return value;
		}

		void addHasValueConstruct(INumber intermediate) {

			nodeRenderer.addValueConstruct(numberRenderer.renderHasValue(intermediate));
		}

		void addOnlyValuesConstruct(Set<INumber> intermediates) {

			nodeRenderer.addValueConstruct(numberRenderer.renderOnlyValues(intermediates));
		}
	}

	Renderer(OModel model, ORSemantics semantics) {

		this.model = model;
		this.semantics = semantics;

		dataFactory = model.getDataFactory();
		defaultNumberRenderer = new NumberRenderer(model);
	}

	NR renderNode(NNode node) {

		return renderNode(node, getTypeExpression(node));
	}

	abstract NodeRenderer createNodeRenderer(NNode node);

	private NR renderNode(NNode node, OWLClassExpression type) {

		return createNodeRenderer(node).render(type);
	}

	private OWLClassExpression getTypeExpression(NNode node) {

		return node.atomicConcept()
				? getAtomicTypeExpression(node)
				: getUnionTypeExpression(node);
	}

	private OWLClass getAtomicTypeExpression(NNode node) {

		return getConcept(NetworkIRIs.getAtomicConcept(node));
	}

	private OWLObjectUnionOf getUnionTypeExpression(NNode node) {

		return createUnion(NetworkIRIs.getConceptDisjuncts(node));
	}

	private OWLObjectUnionOf createUnion(List<IRI> operandIRIs) {

		Set<OWLClass> ops = new HashSet<OWLClass>();

		for (IRI iri : operandIRIs) {

			ops.add(getConcept(iri));
		}

		return dataFactory.getOWLObjectUnionOf(ops);
	}

	private OWLClass getConcept(IRI iri) {

		return model.getConcepts().get(iri);
	}
}
