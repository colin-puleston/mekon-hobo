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

package uk.ac.manchester.cs.mekon.owl.jena;

import java.io.*;

import org.apache.jena.rdf.model.*;
import org.apache.jena.ontology.*;

import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.triples.*;
import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * <i>Jena</i>-specific extension of {@link OTMatcher}.
 *
 * @author Colin Puleston
 */
public class OJenaMatcher extends OTMatcher {

	/**
	 * Constructs matcher for specified model.
	 *
	 * @param model Model over which matcher is to operate
	 */
	public OJenaMatcher(OModel model) {

		super(model);

		initialise(new OJenaFactory(createJenaModel()));
	}

	/**
	 * Constructs matcher for specified model and configuration.
	 *
	 * @param model Model over which matcher is to operate
	 * @param config Configuration for matcher
	 */
	public OJenaMatcher(OModel model, KConfigNode parentConfigNode) {

		this(model);
	}

	/**
	 * Does nothing since no clear-ups are required for this type
	 * of store.
	 */
	public void stop() {
	}

	private OntModel createJenaModel() {

		OntModel jenaModel = createEmptyJenaModel();

		loadMatchingOntology(jenaModel);

		return jenaModel;
	}

	private OntModel createEmptyJenaModel() {

		return ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
	}

	private void loadMatchingOntology(OntModel jenaModel) {

		File owlFile = getModel().renderModelToTempFile();

		jenaModel.read(owlFile.toURI().toString(), null);

		owlFile.delete();
	}
}
