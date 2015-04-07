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
import java.net.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.mekon.config.*;

/**
 * @author Colin Puleston
 */
class OModelConfig implements OModelConfigVocab {

	private KConfigNode configNode;

	OModelConfig(KConfigNode parentConfigNode) {

		configNode = parentConfigNode.getChild(ROOT_ID);
	}

	void configure(OModelBuilder modelBuilder, File baseDirectory) {

		File mainOWLFile = getMainOWLFile(baseDirectory);

		modelBuilder.initialise(mainOWLFile, getReasonerFactoryClass());
		modelBuilder.setIndirectNumericProperty(getIndirectNumericPropertyIRI());
	}

	private File getMainOWLFile(File baseDir) {

		return configNode.getResource(OWL_FILE_ATTR, getOWLFileFinder(baseDir));
	}

	private Class<? extends OWLReasonerFactory> getReasonerFactoryClass() {

		return configNode.getClass(REASONER_FACTORY_CLASS_ATTR, OWLReasonerFactory.class);
	}

	private IRI getIndirectNumericPropertyIRI() {

		URI uri = configNode.getURI(INDIRECT_NUMERIC_PROPERTY_URI_ATTR, null);

		return uri != null ? IRI.create(uri) : null;
	}

	private KConfigResourceFinder getOWLFileFinder(File baseDir) {

		return baseDir == null
				? KConfigResourceFinder.FILES
				: new KConfigResourceFinder(baseDir, false);
	}
}
