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
import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.config.*;

/**
 * Responsible for creating an {@link OModel} object representing a
 * set of ontologies loaded from disk, together with an associated
 * reasoner. The input ontologies will consist of a main
 * entry-point ontology plus the set of ontologies constituting its
 * imports-closure. The OWL files containing the imports should all
 * be located in the same directory as the main OWL file, or a
 * descendant directory.
 *
 * @author Colin Puleston
 */
public class OModelBuilder extends OModelCreator {

	private File mainOWLFile;
	private IRI indirectNumericProperty = null;

	/**
	 * Creates builder for model loaded from disk, together with
	 * reasoner created by factory of specified type.
	 *
	 * @param mainOWLFile File containing main entry-point ontology
	 * @param reasoner Type of factory for creating required reasoner
	 */
	public OModelBuilder(
			File mainOWLFile,
			Class<? extends OWLReasonerFactory> reasoner) {

		super(reasoner);

		this.mainOWLFile = mainOWLFile;
	}

	/**
	 * Creates builder for model loaded from disk, together with
	 * reasoner created by specified factory.
	 *
	 * @param mainOWLFile File containing main entry-point ontology
	 * @param reasoner Factory for creating required reasoner
	 */
	public OModelBuilder(File mainOWLFile, OWLReasonerFactory reasoner) {

		super(reasoner);

		this.mainOWLFile = mainOWLFile;
	}

	/**
	 * Creates builder for model defined via the appropriately-tagged
	 * child of the specified parent-configuration-node.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * model
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration information
	 */
	public OModelBuilder(KConfigNode parentConfigNode) {

		this(parentConfigNode, null);
	}

	/**
	 * Creates builder for model defined via the appropriately-tagged
	 * child of the specified parent-configuration-node, assuming that
	 * the path of the main ontology file specified therein is relative
	 * to the specified base-directory.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * model
	 * @param baseDirectory Base-directory for main ontology file
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration information
	 */
	public OModelBuilder(KConfigNode parentConfigNode, File baseDirectory) {

		new OModelConfig(parentConfigNode).configure(this, baseDirectory);
	}

	/**
	 * Sets the OWL file containing the main ontology, possibly
	 * overriding a value obtained via a configuration node.
	 *
	 * @param mainOWLFile File containing main entry-point ontology
	 */
	public void setMainOWLFile(File file) {

		mainOWLFile = file;
	}

	/**
	 * Sets the "indirect-numeric-property" for the model, possibly
	 * overriding a value obtained via a configuration node.
	 *
	 * @param iri IRI of indirect-numeric-property for model, or null
	 * if not defined
	 */
	public void setIndirectNumericProperty(IRI iri) {

		indirectNumericProperty = iri;
	}

	OWLOntology createModelOntology(OWLOntologyManager manager) {

		OMonitor.pollForPreOntologyLoad(mainOWLFile);

		OWLOntologyManager sourceManager = createSourceManager();
		OWLOntology mainInput = loadInputOntologies(sourceManager);
		Set<OWLOntology> allInputs = sourceManager.getOntologies();

		IRI ontIRI = getOntologyIRI(mainInput);
		OWLOntology ontology = createModelOntology(manager, allInputs, ontIRI);

		OMonitor.pollForOntologyLoaded();

		return ontology;
	}

	void assertExternallyInferableHierarchy(OModel model) {
	}

	IRI getIndirectNumericProperty() {

		return indirectNumericProperty;
	}

	private OWLOntology createModelOntology(
							OWLOntologyManager manager,
							Set<OWLOntology> inputs,
							IRI ontologyIRI) {

		try {

			return manager.createOntology(ontologyIRI, inputs, false);
		}
		catch (OWLOntologyCreationException e) {

			throw new KModelException(e);
		}
	}

	private OWLOntology loadInputOntologies(OWLOntologyManager manager) {

		try {

			return manager.loadOntologyFromOntologyDocument(mainOWLFile);
		}
		catch (OWLOntologyCreationException e) {

			throw new KModelException(e);
		}
	}

	private OWLOntologyManager createSourceManager() {

		OWLOntologyManager manager = createManager();

		manager.addIRIMapper(createIRIMapper());

		return manager;
	}

	private OWLOntologyIRIMapper createIRIMapper() {

		return new PathSearchOntologyIRIMapper(mainOWLFile.getParentFile());
	}
}
