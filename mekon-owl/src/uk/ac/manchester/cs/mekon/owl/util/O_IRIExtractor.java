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

import java.io.*;
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
	 * absolute IRI.
	 *
	 * @param identity Identity from which IRI is to be extracted
	 * @return IRI extracted from identity, or null if relevant
	 * identifier is not a valid absolute IRI
	 */
	static public IRI extractIRI(CIdentity identity) {

		return extractIRI(identity.getIdentifier());
	}

	/**
	 * Extracts an IRI from the specified <code>CIdentity</code>
	 * object or, if the entity's identifier is not a valid absolute
	 * IRI, uses the identifier in combination with the supplied
	 * default-namespace to provide a suitable IRI.
	 *
	 * @param identity Identity from which IRI is to be extracted
	 * @param defaultNamespace Default namespace for IRI, if required
	 * @return IRI extracted directly from identity, or created from
	 * identity plus default-namespace
	 */
	static public IRI extractIRI(CIdentity identity, String defaultNamespace) {

		String identifier = identity.getIdentifier();
		IRI iri = extractIRI(identifier);

		return iri != null ? iri : createIRI(defaultNamespace, identifier);
	}

	static private IRI extractIRI(String identifier) {

		try {

			URI uri = new URI(identifier);

			if (uri.isAbsolute()) {

				return IRI.create(uri);
			}
		}
		catch (URISyntaxException e) {
		}

		return null;
	}

	static private IRI createIRI(String namespace, String identifier) {

		return IRI.create(namespace + "#" + toIRIFragment(identifier));
	}

	static private String toIRIFragment(String identifier) {

		try {

			return URLEncoder.encode(identifier, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {

			throw new Error(e);
		}
	}
}
