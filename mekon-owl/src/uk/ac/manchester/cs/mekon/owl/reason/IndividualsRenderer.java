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
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class IndividualsRenderer {

	private OModel model;
	private ORSemantics semantics;

	private OWLDataFactory dataFactory;

	private Map<IRI, OWLNamedIndividual> rootIndividualsByIRI
							= new HashMap<IRI, OWLNamedIndividual>();

	private Map<OWLNamedIndividual, IRI> rootIRIsByIndividual
							= new HashMap<OWLNamedIndividual, IRI>();

	private Map<OWLNamedIndividual, Set<OWLAxiom>> axiomsByRootIndividual
							= new HashMap<OWLNamedIndividual, Set<OWLAxiom>>();

	private class GroupRenderer extends Renderer<OWLNamedIndividual> {

		private NNode rootNode;
		private IRI rootIRI;

		private IndividualIRIs individualIRIs;

		private Map<NNode, OWLNamedIndividual> individuals
						= new HashMap<NNode, OWLNamedIndividual>();
		private Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		private class NodeToIndividualRenderer extends NodeRenderer {

			private NNode node;
			private OWLNamedIndividual individual;

			NodeToIndividualRenderer(NNode node) {

				super(node);

				this.node = node;
			}

			OWLNamedIndividual render(OWLClassExpression type) {

				individual = individuals.get(node);

				if (individual == null) {

					individual = addIndividual();

					individuals.put(node, individual);

					addTypeAssignment(type);
					renderAttributes();
				}

				return individual;
			}

			void addHasValueConstruct(OWLObjectProperty property, OWLClassExpression value) {

				OWLIndividual indValue = toIndividualValue(value);

				if (indValue != null) {

					addAxiom(
						dataFactory
							.getOWLObjectPropertyAssertionAxiom(
								property,
								individual,
								indValue));
				}
				else {

					addTypeAssignment(
						dataFactory
							.getOWLObjectSomeValuesFrom(
								property,
								value));
				}
			}

			void addOnlyValuesConstruct(OWLObjectProperty property, OWLClassExpression values) {

				addTypeAssignment(
					dataFactory
						.getOWLObjectAllValuesFrom(
							property,
							values));
			}

			void addValueConstruct(OWLClassExpression construct) {

				addTypeAssignment(construct);
			}

			OWLClassExpression toExpression(OWLNamedIndividual rendering) {

				return dataFactory.getOWLObjectOneOf(rendering);
			}

			OWLClassExpression createUnion(Set<OWLNamedIndividual> renderings) {

				return dataFactory.getOWLObjectOneOf(renderings);
			}

			private OWLNamedIndividual addIndividual() {

				OWLNamedIndividual ind = createIndividual();

				addAxiom(dataFactory.getOWLDeclarationAxiom(ind));

				return ind;
			}

			private OWLNamedIndividual createIndividual() {

				return dataFactory.getOWLNamedIndividual(getIRI());
			}

			private void addTypeAssignment(OWLClassExpression type) {

				addAxiom(dataFactory.getOWLClassAssertionAxiom(type, individual));
			}

			private OWLIndividual toIndividualValue(OWLClassExpression value) {

				if (value instanceof OWLObjectOneOf) {

					return toIndividualValue((OWLObjectOneOf)value);
				}

				if (value instanceof OWLDataHasValue) {

					return toIndividualValue((OWLDataHasValue)value);
				}

				return null;
			}

			private OWLIndividual toIndividualValue(OWLObjectOneOf oneOf) {

				return oneOf.getIndividuals().iterator().next();
			}

			private OWLIndividual toIndividualValue(OWLDataHasValue hasValue) {

				OWLNamedIndividual indValue = addIndividual();
				OWLDataPropertyExpression numericProp = hasValue.getProperty();
				OWLLiteral number = hasValue.getValue();

				addAxiom(
					dataFactory
						.getOWLDataPropertyAssertionAxiom(
							numericProp,
							indValue,
							number));

				return indValue;
			}

			private void addAxiom(OWLAxiom axiom) {

				model.addAxiom(axiom);
				axioms.add(axiom);
			}

			private IRI getIRI() {

				return individualIRIs.getFor(node);
			}
		}

		GroupRenderer(NNode rootNode, IRI rootIRI) {

			super(model, semantics);

			this.rootNode = rootNode;
			this.rootIRI = rootIRI;

			individualIRIs = new IndividualIRIs(rootNode, rootIRI);
		}

		OWLNamedIndividual render() {

			OWLNamedIndividual rootIndividual = renderNode(rootNode);

			rootIndividualsByIRI.put(rootIRI, rootIndividual);
			rootIRIsByIndividual.put(rootIndividual, rootIRI);
			axiomsByRootIndividual.put(rootIndividual, axioms);

			return rootIndividual;
		}

		NodeRenderer createNodeRenderer(NNode node) {

			return new NodeToIndividualRenderer(node);
		}
	}

	IndividualsRenderer(OModel model, ORSemantics semantics) {

		this.model = model;
		this.semantics = semantics;

		dataFactory = model.getDataFactory();
	}

	OWLNamedIndividual render(NNode node, IRI rootIRI) {

		return new GroupRenderer(node, rootIRI).render();
	}

	void removeGroup(IRI rootIRI) {

		OWLNamedIndividual rootIndividual = rootIndividualsByIRI.remove(rootIRI);

		if (rootIndividual != null) {

			rootIRIsByIndividual.remove(rootIndividual);
			removeAxioms(rootIndividual);
		}
	}

	void removeGroup(OWLNamedIndividual rootIndividual) {

		IRI rootIRI = rootIRIsByIndividual.remove(rootIndividual);

		if (rootIRI != null) {

			rootIndividualsByIRI.remove(rootIRI);
			removeAxioms(rootIndividual);
		}
	}

	private void removeAxioms(OWLNamedIndividual rootIndividual) {

		for (OWLAxiom axiom : axiomsByRootIndividual.remove(rootIndividual)) {

			model.removeAxiom(axiom);
		}
	}
}