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

/**
 * @author Colin Puleston
 */
class PathSearchOntologyIRIMapper implements OWLOntologyIRIMapper {

	static private final String URL_SEPARATOR = "/";
	static private final String URN_SEPARATOR = ":";
	static private final String OWL_EXTENSION = ".owl";

	private File leafDirectory;

	public IRI getDocumentIRI(IRI iri) {

		File file = lookForFile(leafDirectory, guessFileName(iri));

		return file != null ? IRI.create(file) : iri;
	}

	PathSearchOntologyIRIMapper(File leafDirectory) {

		this.leafDirectory = leafDirectory;
	}

	private String guessFileName(IRI iri) {

		String path = iri.toString();
		int lastDiv = path.lastIndexOf(URL_SEPARATOR);

		if (lastDiv == -1) {

			lastDiv = path.lastIndexOf(URN_SEPARATOR);
		}

		path = path.substring(lastDiv + 1);

		if (!path.endsWith(OWL_EXTENSION)) {

			path += OWL_EXTENSION;
		}

		return path;
	}

	private File lookForFile(File dir, String fileName) {

		File path = new File(dir, fileName);

		if (path.exists()) {

			return path;
		}

		File parentDir = dir = dir.getParentFile();

		return parentDir != null ? lookForFile(parentDir, fileName) : null;
	}
}