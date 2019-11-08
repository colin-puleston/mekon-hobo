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

import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class ConceptExpression extends InstanceConstruct {

	private OModel model;
	private OWLClassExpression expression;

	ConceptExpression(ReasoningModel reasoningModel, NNode node) {

		this(
			reasoningModel,
			new ExpressionRenderer(reasoningModel),
			node);
	}

	ConceptExpression(
		ReasoningModel reasoningModel,
		StringValueProxies stringValueProxies,
		NNode node) {

		this(
			reasoningModel,
			new ExpressionRenderer(reasoningModel, stringValueProxies),
			node);
	}

	boolean subsumes(ConceptExpression testSubsumed) {

		return model.isSubsumption(expression, testSubsumed.getOWLConstruct());
	}

	List<IRI> getMatchingConcepts() {

		return getSortedIRIs(inferEquivalentsAndAllSubs());
	}

	List<IRI> getMatchingIndividuals() {

		return getSortedIRIs(inferIndividuals());
	}

	void cleanUp() {
	}

	boolean suggestsTypes() {

		return true;
	}

	OWLClassExpression getOWLConstruct() {

		return expression;
	}

	Set<OWLClass> getInferredTypes() {

		return expression instanceof OWLClass
				? Collections.<OWLClass>emptySet()
				: inferEquivalentsOrDirectSupers();
	}

	Set<OWLClass> getSuggestedTypes() {

		return model.getInferredSubs(expression, true);
	}

	private ConceptExpression(
				ReasoningModel reasoningModel,
				ExpressionRenderer renderer,
				NNode node) {

		model = reasoningModel.getModel();
		expression = renderer.render(node);
	}

	private Set<OWLClass> inferEquivalentsOrDirectSupers() {

		Set<OWLClass> results = model.getInferredEquivalents(expression);

		return results.isEmpty() ? model.getInferredSupers(expression, true) : results;
	}

	private Set<OWLClass> inferEquivalentsAndAllSubs() {

		Set<OWLClass> results = new HashSet<OWLClass>();

		results.addAll(model.getInferredEquivalents(expression));
		results.addAll(model.getInferredSubs(expression, false));

		return results;
	}

	private Set<OWLNamedIndividual> inferIndividuals() {

		return model.getInferredIndividuals(expression, false);
	}

	private List<IRI> getSortedIRIs(Set<? extends OWLEntity> entities) {

		SortedSet<IRI> iris = new TreeSet<IRI>();

		for (OWLEntity entity : entities) {

			iris.add(entity.getIRI());
		}

		return new ArrayList<IRI>(iris);
	}
}
