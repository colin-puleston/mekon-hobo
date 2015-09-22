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

import java.io.*;
import java.util.*;

import org.coode.owlapi.rdf.rdfxml.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.apibinding.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * Responsible for consolidating into a single ontology all
 * constructs from the relevant source ontologies that are
 * relevant to the type of reasoning that a matcher is required
 * to perform. The available reasoning-type options are specified
 * via the {@link ORReasoningType} enum.
 *
 * @author Colin Puleston
 */
public class ORMatcherModel {

	static private final String TEMP_FILE_PREFIX_FORMAT = "ORMatcher-%s-";
	static private final String TEMP_FILE_SUFFIX = ".owl";

	private OModel sourceModel;

	private OWLOntologyManager manager;
	private IRI ontologyIRI;
	private OWLOntology ontology;

	/**
	 * Constructor.
	 *
	 * @param sourceModel Model from which ontology will be derived
	 * @param reasoningType Type of reasoning that the matcher is to
	 * perform
	 */
	public ORMatcherModel(OModel sourceModel, ORReasoningType reasoningType) {

		this.sourceModel = sourceModel;

		manager = OWLManager.createOWLOntologyManager();
		ontologyIRI = getOntologyIRI(sourceModel.getMainOntology());
		ontology = create(sourceModel.getAllOntologies());

		if (reasoningType != ORReasoningType.DL) {

			assertInferredHierarchy();
		}

		purge(reasoningType);
	}

	/**
	 * Provides the ontology that is to be used by the matcher.
	 *
	 * @return Ontology for matcher
	 */
	public OWLOntology getOntology() {

		return ontology;
	}

	/**
	 * Provides a MEKON-OWL model representing the ontology that is
	 * to be used by the matcher.
	 *
	 * @return Model representing matcher ontology
	 */
	public OModel getModel() {

		OWLReasonerFactory reasonerFactory = sourceModel.getReasonerFactory();
		OModel model = new OModel(manager, ontology, reasonerFactory, true);

		OWLDataProperty numProp = sourceModel.getIndirectNumericProperty();

		if (numProp != null) {

			model.setIndirectNumericProperty(numProp.getIRI());
		}

		return model;
	}

	/**
	 * Renders the ontology that is to be used by the matcher to
	 * file in OWL-RDF format.
	 *
	 * @return File containing OWL-RDF rendering of ontology for
	 * matcher
	 */
	public File createTempOWLFile() {

		try {

			File file = createTempFile();

			renderToFile(file);

			return file;
		}
		catch (IOException e) {

			throw new KSystemConfigException(e);
		}
	}

	private OWLOntology create(Set<OWLOntology> sourceOntologies) {

		try {

			return manager.createOntology(ontologyIRI, sourceOntologies, false);
		}
		catch (OWLOntologyCreationException e) {

			throw new KModelException(e);
		}
	}

	private void assertInferredHierarchy() {

		for (OWLClass concept : sourceModel.getConcepts().getAll()) {

			assertInferredSubConcepts(concept);
		}
	}

	private void assertInferredSubConcepts(OWLClass concept) {

		Set<OWLClassExpression> assSubs = sourceModel.getAssertedSubs(concept);

		for (OWLClass infSub : sourceModel.getInferredSubs(concept, true)) {

			if (!assSubs.contains(infSub)) {

				assertSubConcept(concept, infSub);
			}
		}
	}

	private void assertSubConcept(OWLClass concept, OWLClass subConcept) {

		addAxiom(getSubClassAxiom(concept, subConcept));
	}

	private void purge(ORReasoningType reasoningType) {

		for (OWLAxiom axiom : ontology.getAxioms()) {

			if (!reasoningType.requiredAxiom(axiom)) {

				manager.removeAxiom(ontology, axiom);
			}
		}
	}

	private void renderToFile(File file) throws IOException {

		FileWriter writer = new FileWriter(file);

		try {

			new RDFXMLRenderer(ontology, writer).render();
		}
		finally {

			writer.close();
		}
	}

	private File createTempFile() throws IOException {

		String prefix = getTempFilePrefix();

		return File.createTempFile(prefix, TEMP_FILE_SUFFIX);
	}

	private String getTempFilePrefix() {

		String ontName = getSimpleOntologyName();

		return String.format(TEMP_FILE_PREFIX_FORMAT, ontName);
	}

	private String getSimpleOntologyName() {

		return ontologyIRI != null ? ontologyIRI.getFragment() : "UNNAMED";
	}

	private IRI getOntologyIRI(OWLOntology sourceOntology) {

		OWLOntologyID id = sourceOntology.getOntologyID();
		IRI iri = id.getOntologyIRI();

		return iri != null ? iri : id.getDefaultDocumentIRI();
	}

	private void addAxiom(OWLAxiom axiom) {

		manager.addAxiom(ontology, axiom);
	}

	private OWLAxiom getSubClassAxiom(OWLClass sup, OWLClass sub) {

		return manager.getOWLDataFactory().getOWLSubClassOfAxiom(sub, sup);
	}
}
