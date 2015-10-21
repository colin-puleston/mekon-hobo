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
 * Builder for creating a {@link OModel} object.
 *
 * @author Colin Puleston
 */
public class OModelBuilder {

	private File mainOWLFile;
	private Class<? extends OWLReasonerFactory> reasonerFactory;
	private IRI indirectNumericProperty = null;

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
	 * Sets the OWL file containing the main ontology, overriding the
	 * value obtained via the configuration node.
	 *
	 * @param file OWL file containing main ontology
	 */
	public void setMainOWLFile(File file) {

		mainOWLFile = file;
	}

	/**
	 * Sets the class of factory that is to be used for creating required
	 * reasoner, overriding the value obtained via the configuration node.
	 *
	 * @param type Class of factory to be used for creating required reasoner
	 */
	public void setReasonerFactory(Class<? extends OWLReasonerFactory> type) {

		reasonerFactory = type;
	}

	/**
	 * Sets the "indirect-numeric-property" for the model, overriding the
	 * value obtained via the configuration node, if applicable.
	 *
	 * @param iri IRI of indirect-numeric-property for model, or null
	 * if not defined
	 */
	public void setIndirectNumericProperty(IRI iri) {

		indirectNumericProperty = iri;
	}

	/**
	 * Creates and then initialises the {@link OModel}.
	 *
	 * @param startReasoner True if initial classification of the ontology
	 * and subsequent initialisation of cached-data are to be invoked
	 * (otherwise {@link OModel#startReasoner} method should be invoked
	 * prior to use)
	 * @return Created model
	 */
	public OModel create(boolean startReasoner) {

		OModel model = new OModel(mainOWLFile, reasonerFactory, startReasoner);

		if (indirectNumericProperty != null) {

			model.setIndirectNumericProperty(indirectNumericProperty);
		}

		return model;
	}
}
