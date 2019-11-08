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
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
abstract class Renderer<NR extends OWLObject> {

	private OModel model;
	private ORSemantics semantics;

	private OWLDataFactory dataFactory;
	private NumberRenderer indirectNumberRenderer;

	private StringValueProxies stringValueProxies = null;

	abstract class NodeRenderer {

		private NNode node;

		NodeRenderer(NNode node) {

			this.node = node;
		}

		abstract NR render(OWLClassExpression type);

		void renderFeatures() {

			for (NLink link : node.getLinks()) {

				new LinkValuesRenderer(this, link).render();
			}

			for (NNumber number : node.getNumbers()) {

				createNumberValuesRenderer(number).render();
			}

			if (stringValueProxies != null) {

				for (NString string : node.getStrings()) {

					new StringValuesRenderer(this, string).render();
				}
			}
		}

		void addHasValueConstructForNode(OWLObjectProperty property, NR rendering) {

			addHasValueConstruct(property, nodeRenderingToExpression(rendering));
		}

		void addOnlyValuesConstructForNodes(
					OWLObjectProperty property,
					Set<NR> renderings) {

			addOnlyValuesConstruct(property, renderNormalisedUnion(renderings));
		}

		abstract void addHasValueConstruct(
							OWLObjectProperty property,
							OWLClassExpression value);

		abstract void addOnlyValuesConstruct(
							OWLObjectProperty property,
							OWLClassExpression value);

		abstract void addValueConstruct(OWLClassExpression construct);

		private ValuesRenderer<INumber> createNumberValuesRenderer(NNumber number) {

			IRI iri = NetworkIRIs.getAtomicType(number);

			if (model.getDataProperties().contains(iri)) {

				return new DirectNumberValuesRenderer(this, number);
			}

			if (indirectNumberRenderer != null) {

				return new IndirectNumberValuesRenderer(this, number);
			}

			throw new KModelException(
						"Cannot handle numeric values for property: " + iri
						+ " (since (a) not a recognised data-property, and"
						+ " (b) indirect-numeric-property not defined)");
		}
	}

	private abstract class ValuesRenderer<V> {

		private NFeature<V> feature;

		ValuesRenderer(NFeature<V> feature) {

			this.feature = feature;
		}

		void render() {

			Set<V> values = new HashSet<V>(feature.getValues());

			if (!values.isEmpty()) {

				addHasValuesConstructs(values);
			}

			if (closedWorldSemantics()) {

				addOnlyValuesConstruct(values);
			}
		}

		abstract void addHasValuesConstructs(Set<V> values);

		abstract void addOnlyValuesConstruct(Set<V> values);

		OWLObjectProperty getObjectProperty() {

			return model.getObjectProperties().get(getFeatureIRI());
		}

		OWLDataProperty getDataProperty() {

			return model.getDataProperties().get(getFeatureIRI());
		}

		private boolean closedWorldSemantics() {

			return semantics.getWorld(model, getFeatureIRI()).closed();
		}

		private IRI getFeatureIRI() {

			return NetworkIRIs.getAtomicType(feature);
		}
	}

	private class LinkValuesRenderer extends ValuesRenderer<NNode> {

		private NodeRenderer nodeRenderer;
		private OWLObjectProperty property;
		private boolean disjunctionLink;

		LinkValuesRenderer(NodeRenderer nodeRenderer, NLink link) {

			super(link);

			this.nodeRenderer = nodeRenderer;

			property = getObjectProperty();
			disjunctionLink = link.disjunctionLink();
		}

		void addHasValuesConstructs(Set<NNode> values) {

			if (disjunctionLink) {

				addHasUnionValueConstruct(values);
			}
			else {

				addHasEachValueConstruct(values);
			}
		}

		void addOnlyValuesConstruct(Set<NNode> values) {

			nodeRenderer.addOnlyValuesConstructForNodes(property, renderValues(values));
		}

		private void addHasEachValueConstruct(Set<NNode> values) {

			for (NNode value : values) {

				nodeRenderer.addHasValueConstructForNode(property, renderNode(value));
			}
		}

		private void addHasUnionValueConstruct(Set<NNode> values) {

			nodeRenderer.addHasValueConstruct(property, renderValueUnion(values));
		}

		private OWLClassExpression renderValueUnion(Set<NNode> values) {

			return renderNormalisedUnion(renderValues(values));
		}

		private Set<NR> renderValues(Set<NNode> values) {

			Set<NR> renderings = new HashSet<NR>();

			for (NNode value : values) {

				renderings.add(renderNode(value));
			}

			return renderings;
		}
	}

	private class DirectNumberValuesRenderCore {

		private NodeRenderer nodeRenderer;
		private NumberRenderer numberRenderer;

		DirectNumberValuesRenderCore(NodeRenderer nodeRenderer, OWLDataProperty property) {

			this.nodeRenderer = nodeRenderer;

			numberRenderer = new NumberRenderer(model, property);
		}

		void addHasValuesConstructs(Set<INumber> values) {

			for (INumber value : values) {

				nodeRenderer.addValueConstruct(numberRenderer.renderHasValue(value));
			}
		}

		void addOnlyValuesConstruct(Set<INumber> values) {

			nodeRenderer.addValueConstruct(numberRenderer.renderOnlyValues(values));
		}
	}

	private class DirectNumberValuesRenderer extends ValuesRenderer<INumber> {

		private DirectNumberValuesRenderCore core;

		DirectNumberValuesRenderer(NodeRenderer nodeRenderer, NNumber number) {

			super(number);

			core = new DirectNumberValuesRenderCore(nodeRenderer, getDataProperty());
		}

		void addHasValuesConstructs(Set<INumber> values) {

			core.addHasValuesConstructs(values);
		}

		void addOnlyValuesConstruct(Set<INumber> values) {

			core.addOnlyValuesConstruct(values);
		}
	}

	private class IndirectNumberValuesRenderer extends ValuesRenderer<INumber> {

		private NodeRenderer nodeRenderer;
		private OWLObjectProperty property;

		IndirectNumberValuesRenderer(NodeRenderer nodeRenderer, NNumber number) {

			super(number);

			this.nodeRenderer = nodeRenderer;

			property = getObjectProperty();
		}

		void addHasValuesConstructs(Set<INumber> values) {

			for (INumber value : values) {

				nodeRenderer.addHasValueConstruct(property, renderValue(value));
			}
		}

		void addOnlyValuesConstruct(Set<INumber> values) {

			nodeRenderer.addOnlyValuesConstruct(property, renderValueUnion(values));
		}

		private OWLClassExpression renderValueUnion(Set<INumber> values) {

			return dataFactory.getOWLObjectUnionOf(renderValues(values));
		}

		private Set<OWLClassExpression> renderValues(Set<INumber> values) {

			Set<OWLClassExpression> renderings = new HashSet<OWLClassExpression>();

			for (INumber value : values) {

				renderings.add(renderValue(value));
			}

			return renderings;
		}

		private OWLClassExpression renderValue(INumber value) {

			return indirectNumberRenderer.renderHasValue(value);
		}
	}

	private class StringValuesRenderer extends ValuesRenderer<String> {

		private DirectNumberValuesRenderCore core;

		StringValuesRenderer(NodeRenderer nodeRenderer, NString string) {

			super(string);

			core = new DirectNumberValuesRenderCore(nodeRenderer, getProxyProperty());
		}

		void addHasValuesConstructs(Set<String> values) {

			core.addHasValuesConstructs(toProxyValues(values));
		}

		void addOnlyValuesConstruct(Set<String> values) {

			core.addOnlyValuesConstruct(toProxyValues(values));
		}

		private OWLDataProperty getProxyProperty() {

			return stringValueProxies.toProxyProperty(getDataProperty());
		}

		private Set<INumber> toProxyValues(Set<String> values) {

			Set<INumber> proxyValues = new HashSet<INumber>();

			for (String value : values) {

				proxyValues.add(stringValueProxies.toProxyValue(value));
			}

			return proxyValues;
		}
	}

	Renderer(ReasoningModel reasoningModel, StringValueProxies stringValueProxies) {

		model = reasoningModel.getModel();
		semantics = reasoningModel.getSemantics();

		this.stringValueProxies = stringValueProxies;

		dataFactory = model.getDataFactory();
		indirectNumberRenderer = checkCreateIndirectNumberRenderer();
	}

	NR renderNode(NNode node) {

		return renderNode(node, getTypeExpression(node));
	}

	abstract NodeRenderer createNodeRenderer(NNode node);

	abstract OWLClassExpression nodeRenderingToExpression(NR rendering);

	abstract OWLClassExpression renderUnion(Set<NR> operands);

	private NumberRenderer checkCreateIndirectNumberRenderer() {

		OWLDataProperty property = model.getIndirectNumericProperty();

		return property != null ? new NumberRenderer(model, property) : null;
	}

	private NR renderNode(NNode node, OWLClassExpression type) {

		return createNodeRenderer(node).render(type);
	}

	private OWLClassExpression getTypeExpression(NNode node) {

		return node.atomicType()
				? getAtomicTypeExpression(node)
				: getUnionTypeExpression(node);
	}

	private OWLClass getAtomicTypeExpression(NNode node) {

		return getConcept(NetworkIRIs.getAtomicType(node));
	}

	private OWLObjectUnionOf getUnionTypeExpression(NNode node) {

		Set<OWLClass> ops = new HashSet<OWLClass>();

		for (IRI typeDisjunctIRI : NetworkIRIs.getTypeDisjuncts(node)) {

			ops.add(getConcept(typeDisjunctIRI));
		}

		return dataFactory.getOWLObjectUnionOf(ops);
	}

	private OWLClassExpression renderNormalisedUnion(Set<NR> operands) {

		if (operands.isEmpty()) {

			return dataFactory.getOWLNothing();
		}

		if (operands.size() == 1) {

			return nodeRenderingToExpression(operands.iterator().next());
		}

		return renderUnion(operands);
	}

	private OWLClass getConcept(IRI iri) {

		return model.getConcepts().get(iri);
	}
}
