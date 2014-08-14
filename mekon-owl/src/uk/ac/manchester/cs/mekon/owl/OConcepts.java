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

package uk.ac.manchester.cs.mekon.owl;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

class OConcepts extends OEntities<OWLClass> {

	OConcepts(OModel model) {

		super(model);
	}

	Set<OWLClassExpression> getAssertedSupers(OWLClass concept) {

		return concept.getSuperClasses(getAllOntologies());
	}

	Set<OWLClassExpression> getAssertedSubs(OWLClass concept) {

		return concept.getSubClasses(getAllOntologies());
	}

	Set<OWLIndividual> getAssertedIndividuals(OWLClass concept) {

		return concept.getIndividuals(getAllOntologies());
	}

	Set<OWLClass> getInferredSupers(
						OWLClassExpression expression,
						boolean directOnly) {

		return normaliseNonSubs(
					expression,
					getReasoner()
						.getSuperClasses(
							expression,
							directOnly));
	}

	Set<OWLClass> getInferredSubs(
						OWLClassExpression expression,
						boolean directOnly) {

		return normaliseSubs(getReasoner().getSubClasses(expression, directOnly));
	}

	Set<OWLClass> getInferredEquivalents(OWLClassExpression expression) {

		return normaliseNonSubs(
					expression,
					getReasoner()
						.getEquivalentClasses(expression)
							.getEntities());
	}

	Set<OWLNamedIndividual> getInferredIndividuals(
								OWLClassExpression expression,
								boolean directOnly) {

		return getReasoner().getInstances(expression, directOnly).getFlattened();
	}

	String getEntityTypeName() {

		return "class";
	}

	Set<OWLClass> findAll() {

		return getMainOntology().getClassesInSignature(true);
	}

	OWLClass getTop() {

		return getDataFactory().getOWLThing();
	}

	OWLClass getBottom() {

		return getDataFactory().getOWLNothing();
	}

	private Set<OWLClass> normaliseSubs(NodeSet<OWLClass> concepts) {

		return normalise(concepts.getFlattened());
	}

	private Set<OWLClass> normaliseNonSubs(
							OWLClassExpression expression,
							NodeSet<OWLClass> concepts) {

		return normaliseNonSubs(expression, concepts.getFlattened());
	}

	private Set<OWLClass> normaliseNonSubs(
							OWLClassExpression expression,
							Set<OWLClass> concepts) {

		if (concepts.contains(getDataFactory().getOWLNothing())) {

			throw new KModelException(
						"Inconsistent class-expression: "
						+ render(expression));
		}

		return normalise(concepts);
	}

	private String render(OWLClassExpression expression) {

		return new OLabelRenderer(getModel()).render(expression);
	}
}
