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
class IndividualsRenderer {

	static private final String ANON_NAME_PREFIX = "ANON-";

	private OModel model;
	private OWLDataFactory dataFactory;
	private IndividualCategory individualCategory;

	private Map<String, OWLNamedIndividual> rootIndividualsByName
							= new HashMap<String, OWLNamedIndividual>();

	private Map<OWLNamedIndividual, String> rootNamesByIndividual
							= new HashMap<OWLNamedIndividual, String>();

	private Map<OWLNamedIndividual, Set<OWLAxiom>> axiomsByRootIndividual
							= new HashMap<OWLNamedIndividual, Set<OWLAxiom>>();

	private long anonGroupCount = 0;

	private class GroupRenderer extends Renderer<OWLNamedIndividual> {

		private ORFrame rootFrame;
		private String rootName;

		private IndividualIRIGenerator iriGenerator;

		private Map<ORFrame, OWLNamedIndividual> individuals
						= new HashMap<ORFrame, OWLNamedIndividual>();
		private Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		private class FrameToIndividualRenderer extends FrameRenderer {

			private ORFrame frame;
			private OWLNamedIndividual individual;

			FrameToIndividualRenderer(ORFrame frame) {

				super(frame);

				this.frame = frame;
			}

			OWLNamedIndividual render(OWLClassExpression type) {

				individual = individuals.get(frame);

				if (individual == null) {

					individual = addIndividual();

					individuals.put(frame, individual);

					addTypeAssignment(type);
					renderSlots();
				}

				return individual;
			}

			void addHasValueForExpr(OWLObjectProperty property, OWLClassExpression expr) {

				OWLIndividual indValue = toIndividualValue(expr);

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
								expr));
				}
			}

			void addOnlyValuesForExpr(OWLObjectProperty property, OWLClassExpression expr) {

				addTypeAssignment(
					dataFactory
						.getOWLObjectAllValuesFrom(
							property,
							expr));
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

				return dataFactory.getOWLNamedIndividual(generateIRI());
			}

			private void addTypeAssignment(OWLClassExpression type) {

				addAxiom(dataFactory.getOWLClassAssertionAxiom(type, individual));
			}

			private OWLIndividual toIndividualValue(OWLClassExpression expr) {

				if (expr instanceof OWLObjectOneOf) {

					return toIndividualValue((OWLObjectOneOf)expr);
				}

				if (expr instanceof OWLDataHasValue) {

					return toIndividualValue((OWLDataHasValue)expr);
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

			private IRI generateIRI() {

				return iriGenerator.generateFor(frame);
			}
		}

		GroupRenderer(ORFrame rootFrame, String rootName) {

			super(model);

			this.rootFrame = rootFrame;
			this.rootName = rootName;

			iriGenerator = createIRIGenerator();
		}

		OWLNamedIndividual render() {

			OWLNamedIndividual rootIndividual = renderFrame(rootFrame);

			rootIndividualsByName.put(rootName, rootIndividual);
			rootNamesByIndividual.put(rootIndividual, rootName);
			axiomsByRootIndividual.put(rootIndividual, axioms);

			return rootIndividual;
		}

		FrameRenderer createFrameRenderer(ORFrame frame) {

			return new FrameToIndividualRenderer(frame);
		}

		private IndividualIRIGenerator createIRIGenerator() {

			return new IndividualIRIGenerator(
							rootFrame,
							rootName,
							individualCategory);
		}
	}

	IndividualsRenderer(OModel model, IndividualCategory individualCategory) {

		this.model = model;
		this.individualCategory = individualCategory;

		dataFactory = model.getDataFactory();
	}

	boolean rendered(String rootName) {

		return rootIndividualsByName.containsKey(rootName);
	}

	OWLNamedIndividual render(ORFrame frame) {

		return render(frame, getNextAnnonGroupName());
	}

	OWLNamedIndividual render(ORFrame frame, String rootName) {

		return new GroupRenderer(frame, rootName).render();
	}

	boolean removeGroup(String rootName) {

		OWLNamedIndividual rootIndividual = rootIndividualsByName.remove(rootName);

		if (rootIndividual != null) {

			rootNamesByIndividual.remove(rootIndividual);
			removeAxioms(rootIndividual);

			return true;
		}

		return false;
	}

	boolean removeGroup(OWLNamedIndividual rootIndividual) {

		String rootName = rootNamesByIndividual.remove(rootIndividual);

		if (rootName != null) {

			rootIndividualsByName.remove(rootName);
			removeAxioms(rootIndividual);

			return true;
		}

		return false;
	}

	private void removeAxioms(OWLNamedIndividual rootIndividual) {

		for (OWLAxiom axiom : axiomsByRootIndividual.remove(rootIndividual)) {

			model.removeAxiom(axiom);
		}
	}

	private synchronized String getNextAnnonGroupName() {

		return ANON_NAME_PREFIX + anonGroupCount++;
	}
}