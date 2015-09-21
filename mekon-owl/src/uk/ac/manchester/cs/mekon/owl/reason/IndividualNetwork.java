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
import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class IndividualNetwork extends InstanceConstruct {

	private OModel model;

	private OInstanceIRIs iris;
	private IndividualsRenderer renderer;
	private OWLNamedIndividual rootIndividual;

	IndividualNetwork(
		OModel model,
		ORFrame frame,
		IRI rootIRI,
		IndividualsRenderer renderer) {

		this.model = model;
		this.renderer = renderer;

		rootIndividual = renderer.render(frame, rootIRI);
	}

	boolean matches(ConceptExpression queryExpression) {

		return model.hasType(rootIndividual, queryExpression.getOWLConstruct());
	}

	void cleanUp() {

		renderer.removeGroup(rootIndividual);
	}

	boolean suggestsTypes() {

		return false;
	}

	OWLNamedIndividual getOWLConstruct() {

		return rootIndividual;
	}

	Set<OWLClass> getInferredTypes() {

		return getReasoner()
				.getTypes(rootIndividual, true)
				.getFlattened();
	}

	Set<OWLClass> getSuggestedTypes() {

		throw new Error("Method should never be invoked!");
	}

	private OWLDataFactory getDataFactory() {

		return model.getDataFactory();
	}

	private OWLReasoner getReasoner() {

		return model.getReasoner();
	}
}
