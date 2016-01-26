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

import uk.ac.manchester.cs.mekon.*;

/**
 * Responsible for copying the model represented by an {@link OModel}
 * object, possibly with a different type of reasoner and/or updated
 * reasoning-type. Copied models will contain only copies of the
 * contents of the model ontology. Any contents of the instance
 * ontology will not be copied.
 *
 * @author Colin Puleston
 */
public class OModelCopier extends OModelCreator {

	private OModel sourceModel;

	/**
	 * Constructor.
	 *
	 * @param sourceModel Model to be copied from
	 */
	public OModelCopier(OModel sourceModel) {

		super(sourceModel.getReasonerFactory().getClass());

		this.sourceModel = sourceModel;
	}

	OWLOntology createModelOntology(OWLOntologyManager manager) {

		OWLOntology modelSource = sourceModel.getModelOntology();
		IRI ontIRI = getOntologyIRI(modelSource);
		Set<OWLOntology> modelSources = ontologyAsSet(modelSource);

		return createOntology(manager, ontIRI, modelSources);
	}

	void assertExternallyInferableHierarchy(OModel model) {

		if (soureInferableHierarchy(model)) {

			for (OWLClass concept : model.getConcepts().getAll()) {

				assertInferableSubs(model, concept);
			}
		}
	}

	IRI getIndirectNumericProperty() {

		if (sourceModel.indirectNumericPropertyDefined()) {

			return sourceModel.getIndirectNumericProperty().getIRI();
		}

		return null;
	}

	private OWLOntology createOntology(
							OWLOntologyManager manager,
							IRI ontologyIRI,
							Set<OWLOntology> sources) {

		try {

			return manager.createOntology(ontologyIRI, sources, false);
		}
		catch (OWLOntologyCreationException e) {

			throw new KModelException(e);
		}
	}

	private Set<OWLOntology> ontologyAsSet(OWLOntology ontology) {

		return Collections.<OWLOntology>singleton(ontology);
	}

	private boolean soureInferableHierarchy(OModel model) {

		OReasoningType srcReasonType = sourceModel.getReasoningType();
		OReasoningType copyReasonType = model.getReasoningType();

		return srcReasonType.morePowerfullThan(copyReasonType);
	}

	private void assertInferableSubs(OModel model, OWLClass concept) {

		Set<OWLClassExpression> assSubs = model.getAssertedSubs(concept);

		for (OWLClass infSub : getInferredSubsFromSourceModel(concept)) {

			if (!assSubs.contains(infSub)) {

				model.assertSubConcept(concept, infSub);
			}
		}
	}

	private Set<OWLClass> getInferredSubsFromSourceModel(OWLClass concept) {

		IRI iri = concept.getIRI();
		OWLClass sourceConcept = sourceModel.getConcepts().get(iri);

		return sourceModel.getInferredSubs(sourceConcept, true);
	}
}
