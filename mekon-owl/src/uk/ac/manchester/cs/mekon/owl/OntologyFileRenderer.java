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

import org.coode.owlapi.rdf.rdfxml.*;
import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.config.*;

/**
 * @author Colin Puleston
 */
class OntologyFileRenderer {

	static private final String TEMP_PREFIX_FORMAT = "MEKON-OWL-%s-";
	static private final String TEMP_SUFFIX = ".owl";
	static private final String ANON_ONTOLOGY_NAME = "UNNAMED";

	private OWLOntology ontology;

	OntologyFileRenderer(OWLOntology ontology) {

		this.ontology = ontology;
	}

	File renderToTemp() {

		File file = createTempFile();

		renderTo(file);

		return file;
	}

	void renderTo(File file) {

		try {

			tryRender(file);
		}
		catch (IOException e) {

			throw new KSystemConfigException(e);
		}
	}

	private void tryRender(File file) throws IOException {

		FileWriter writer = new FileWriter(file);

		try {

			new RDFXMLRenderer(ontology, writer).render();
		}
		finally {

			writer.close();
		}
	}

	private File createTempFile() {

		String prefix = getTempFilePrefix();

		try {

			return File.createTempFile(prefix, TEMP_SUFFIX);
		}
		catch (IOException e) {

			throw new KSystemConfigException(e);
		}
	}

	private String getTempFilePrefix() {

		String ontName = getSimpleOntologyName();

		return String.format(TEMP_PREFIX_FORMAT, ontName);
	}

	private String getSimpleOntologyName() {

		IRI iri = ontology.getOntologyID().getOntologyIRI();

		return iri != null ? iri.toURI().getFragment() : ANON_ONTOLOGY_NAME;
	}
}
