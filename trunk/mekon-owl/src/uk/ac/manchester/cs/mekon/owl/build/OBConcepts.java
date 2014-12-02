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

package uk.ac.manchester.cs.mekon.owl.build;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.mekon.owl.*;

/**
 * Represents the set of OWL classes that will be used in generating
 * frames in the Frames Model (FM).
 *
 * @author Colin Puleston
 */
public class OBConcepts extends OBEntities<OWLClass, OBConceptInclusions> {

	private Set<OWLClass> hiddenConcepts = new HashSet<OWLClass>();

	/**
	 * Registers a concept as one that will be used to generate a
	 * "hidden" frame. If the concept is not also registered via
	 * the {@link OBEntities#add} method then this method will have
	 * no effect.
	 *
	 * @param concept Relevant concept
	 */
	public void setHidden(OWLClass concept) {

		hiddenConcepts.add(concept);
	}

	/**
	 * Adds a collection of concepts to the set.
	 *
	 * @param concepts Concepts to add
	 */
	public void addAll(Collection<OWLClass> concepts) {

		for (OWLClass concept : concepts) {

			add(concept);
		}
	}

	OBConcepts(OModel model) {

		super(model);
	}

	void addGroupEntity(
			OBConceptInclusions group,
			OWLClass concept,
			boolean isRoot) {

		add(concept);

		if (isHidden(group, concept, isRoot)) {

			setHidden(concept);
		}
	}

	boolean isHidden(OWLClass concept) {

		return hiddenConcepts.contains(concept);
	}

	String getTypeName() {

		return "class";
	}

	boolean isValidEntity(IRI iri) {

		return getMainOntology().containsClassInSignature(iri, true);
	}

	OWLClass get(IRI iri) {

		return getDataFactory().getOWLClass(iri);
	}

	Set<OWLClass> getAllInModel() {

		return getModel().getConcepts().getAll();
	}

	Set<OWLClass> getDescendants(OWLClass entity) {

		return getModel().getInferredSubs(entity, false);
	}

	Set<OWLClass> extractAll(OWLClassExpression expression) {

		return expression.getClassesInSignature();
	}

	private boolean isHidden(
						OBConceptInclusions group,
						OWLClass concept,
						boolean isRoot) {

		return group.getConceptHiding().isHidden(getModel(), concept, isRoot);
	}
}
