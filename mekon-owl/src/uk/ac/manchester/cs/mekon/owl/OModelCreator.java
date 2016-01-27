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

import java.io.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.apibinding.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Responsible for creating an {@link OModel} object
 * representing either a set of ontologies loaded directly
 * from disk, or the contents of an existing model.
 *
 * @author Colin Puleston
 */
public abstract class OModelCreator {

	static private final IRI DEFAULT_INSTANCES_IRI
				= IRI.create(OInstanceIRIs.BASE_NAMESPACE);

	static OWLOntologyManager createManager() {

		return OWLManager.createOWLOntologyManager();
	}

	static IRI getOntologyIRI(OWLOntology ontology) {

		OWLOntologyID id = ontology.getOntologyID();
		IRI iri = id.getOntologyIRI();

		return iri != null ? iri : id.getDefaultDocumentIRI();
	}

	static private OWLReasonerFactory
						createReasonerFactory(
							Class<? extends OWLReasonerFactory> type) {

		return new KConfigObjectConstructor
						<OWLReasonerFactory>(type)
						.construct();
	}

	private OWLReasonerFactory reasoner;
	private OReasoningType reasoningType = OReasoningType.DL;
	private IRI instancesIRI = DEFAULT_INSTANCES_IRI;

	/**
	 * Sets the factory to be used for creating the required reasoner.
	 *
	 * @param reasoner Factory to be used for creating required
	 * reasoner
	 */
	public void setReasoner(OWLReasonerFactory reasoner) {

		this.reasoner = reasoner;
	}

	/**
	 * Sets the type of factory to be used for creating the required
	 * reasoner.
	 *
	 * @param reasoner Type of factory to be used for creating required
	 * reasoner
	 */
	public void setReasoner(Class<? extends OWLReasonerFactory> reasoner) {

		this.reasoner = createReasonerFactory(reasoner);
	}

	/**
	 * Sets the type of reasoning that is to be performed on the
	 * model.
	 *
	 * @param reasoningType Relevant reasoning-type
	 */
	public void setReasoningType(OReasoningType reasoningType) {

		this.reasoningType = reasoningType;
	}

	/**
	 * Sets the IRI for the instance-ontology.
	 *
	 * @param instancesIRI Relevant IRI
	 */
	public void setInstanceOntologyIRI(IRI instancesIRI) {

		if (instancesIRI != null) {

			this.instancesIRI = instancesIRI;
		}
	}

	/**
	 * Creates and configures the required model. If the start-reasoner
	 * option is not selected then the {@link OModel#startReasoner}
	 * method should be invoked manually prior to use.
	 *
	 * @param startReasoner True if initial classification of the
	 * ontology and subsequent initialisation of cached-data are to be
	 * invoked after model creation
	 * @return Created model
	 */
	public OModel create(boolean startReasoner) {

		OModel model = construct();

		configure(model);

		if (startReasoner) {

			model.startReasoner();
		}

		return model;
	}

	OModelCreator() {
	}

	OModelCreator(Class<? extends OWLReasonerFactory> reasoner) {

		this(createReasonerFactory(reasoner));
	}

	OModelCreator(OWLReasonerFactory reasoner) {

		this.reasoner = reasoner;
	}

	abstract OWLOntology createModelOntology(OWLOntologyManager manager);

	abstract void assertExternallyInferableHierarchy(OModel model);

	abstract File getMainSourceFile();

	abstract IRI getIndirectNumericProperty();

	private OModel construct() {

		File file = getMainSourceFile();
		OWLOntologyManager man = createManager();
		OWLOntology modOnt = createModelOntology(man);
		OWLOntology instOnt = createInstanceOntology(man);

		addImport(man, instOnt, getOntologyIRI(modOnt));

		return new OModel(file, man, modOnt, instOnt, reasoner, reasoningType);
	}

	private void configure(OModel model) {

		model.purgeForReasoningType();
		assertExternallyInferableHierarchy(model);
		checkSetIndirectNumericProperty(model);
	}

	private OWLOntology createInstanceOntology(OWLOntologyManager manager) {

		try {

			return manager.createOntology(instancesIRI);
		}
		catch (OWLOntologyCreationException e) {

			throw new KModelException(e);
		}
	}

	private void addImport(
					OWLOntologyManager manager,
					OWLOntology ontology,
					IRI importIRI) {

		OWLImportsDeclaration dec = getImportsDec(manager, importIRI);

		manager.applyChange(new AddImport(ontology, dec));
	}

	private OWLImportsDeclaration getImportsDec(
									OWLOntologyManager manager,
									IRI importIRI) {

		OWLDataFactory dataFactory = manager.getOWLDataFactory();

		return dataFactory.getOWLImportsDeclaration(importIRI);
	}

	private void checkSetIndirectNumericProperty(OModel model) {

		IRI prop = getIndirectNumericProperty();

		if (prop != null) {

			model.setIndirectNumericProperty(prop);
		}
	}
}
