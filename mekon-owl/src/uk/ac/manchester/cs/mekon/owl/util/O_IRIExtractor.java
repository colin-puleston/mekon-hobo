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

package uk.ac.manchester.cs.mekon.owl.util;

import java.net.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Responsible for extracting IRIs from <code>CIdentity</code>
 * objects that were constructed with IRIs as their identifiers.
 *
 * @author Colin Puleston
 */
public class O_IRIExtractor {

	/**
	 * Extracts an IRI from the specified <code>CIdentity</code>
	 * object, assuming that the entity's identifier is a valid
	 * absolute URI.
	 *
	 * @param identity Identity from which IRI is to be extracted
	 * @return IRI extracted from model-entity, or null if entity's
	 * identifier is not a valid absolute URI
	 */
	static public IRI extractIRI(CIdentity identity) {

		try {

			URI uri = new URI(identity.getIdentifier());

			if (uri.isAbsolute()) {

				return IRI.create(uri);
			}
		}
		catch (URISyntaxException e) {
		}

		return null;
	}

	/**
	 * Extracts an IRI from the <code>CIdentity</code> associated
	 * with the specified model-entity, assuming that the entity's
	 * identifier is a valid IRI.
	 *
	 * @param identified Frames-based model-entity with associated
	 * identity from which IRI is to be extracted
	 * @return IRI extracted from model-entity, or null if entity's
	 * identifier is not a valid IRI
	 */
	static public IRI extractIRI(CIdentified identified) {

		return extractIRI(identified.getIdentity());
	}
}
