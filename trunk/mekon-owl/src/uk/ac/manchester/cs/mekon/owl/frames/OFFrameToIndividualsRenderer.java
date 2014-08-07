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

package uk.ac.manchester.cs.mekon.owl.frames;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * Renders the {@link OFFrame}/{@link OSSlot} networks emanating
 * from specified frames, as networks of OWL individuals.
 * <p>
 * The rendering of the main frame in the network will be assigned
 * a "root-name". The other frames will be assigned a name
 * automatically derived from the root-name.
 *
 * @author Colin Puleston
 */
public class OFFrameToIndividualsRenderer
				extends
					OFFrameRenderer<OWLNamedIndividual> {

	private OModel model;
	private OWLDataFactory dataFactory;

	private Map<OFFrame, OWLNamedIndividual> individuals
					= new HashMap<OFFrame, OWLNamedIndividual>();
	private Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

	private IndividualIRIGenerator iriGenerator = new IndividualIRIGenerator();

	private class FrameToIndividualsRenderer extends FrameRenderer {

		private OFFrame frame;
		private OWLNamedIndividual individual;

		FrameToIndividualsRenderer(OFFrame frame) {

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

		void addValueExpression(OWLClassExpression expr) {

			addTypeAssignment(expr);
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

		private IRI generateIRI() {

			return iriGenerator.generateFor(frame);
		}
	}

	/**
	 * Constructor
	 *
	 * @param model Relevant model
	 */
	public OFFrameToIndividualsRenderer(OModel model) {

		super(model);

		this.model = model;

		dataFactory = model.getDataFactory();
	}

	/**
	 * Sets the namespace for the individuals that will be generated.
	 * If this method has not been invoked then a default namespace
	 * will be used.
	 *
	 * @param namespace Required namespace
	 */
	public void setNamespace(String namespace) {

		iriGenerator.setNamespace(namespace);
	}

	/**
	 * Provides the OWL rendering of the frame/slot network emanating
	 * from the specified frame, using the default root-name that
	 * will determine the names of all individuals created (see
	 * {link #OFFrameToIndividualsRenderer})
	 *
	 * @param frame Main frame in the network to be rendered
	 */
	public OWLNamedIndividual render(OFFrame frame) {

		iriGenerator.start(frame);

		return renderFrame(frame);
	}

	/**
	 * Provides the OWL rendering of the frame/slot network emanating
	 * from the specified frame.
	 *
	 * @param frame Main frame in the network to be rendered
	 * @param rootName Root-name that will determine the names of all
	 * individuals created (see {link #OFFrameToIndividualsRenderer})
	 */
	public OWLNamedIndividual render(OFFrame frame, String rootName) {

		iriGenerator.start(frame, rootName);

		return renderFrame(frame);
	}

	/**
	 * Removes all individuals that have been rendered by this object.
	 */
	public void removeAllRendered() {

		for (OWLAxiom axiom : axioms) {

			model.removeAxiom(axiom);
		}
	}

	FrameRenderer createFrameRenderer(OFFrame frame) {

		return new FrameToIndividualsRenderer(frame);
	}

	private void addAxiom(OWLAxiom axiom) {

		model.addAxiom(axiom);
		axioms.add(axiom);
	}
}