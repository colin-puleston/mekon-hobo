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

/**
 * Provides MEKON-OWL specific IRI namespaces and entity IRIs within
 * those namespaces.
 *
 * @author Colin Puleston
 */
public class O_IRINamespaces {

	/**
	 * Provides the base MEKON-OWL namespace.
	 */
	static public final String BASE = "urn:mekon-owl";

	/**
	 * Provides a namespace that extends the base MEKON-OWL namespace.
	 *
	 * @param namespaceExtn Final section of extended namespace
	 * @return Created namespace
	 */
	static public String createNamespace(String namespaceExtn) {

		return namespaceExtn.length() == 0 ? BASE : (BASE + ":" + namespaceExtn);
	}

	/**
	 * Provides an IRI for an entity within the base MEKON-OWL namespace.
	 *
	 * @param fragment IRI fragment
	 * @return Created IRI
	 */
	static public IRI createEntityIRI(String fragment) {

		return createEntityIRI("", fragment);
	}

	/**
	 * Provides an IRI for an entity within an extension of the base
	 * MEKON-OWL namespace.
	 *
	 * @param namespaceExtn Final section of extended namespace
	 * @param fragment IRI fragment
	 * @return Created IRI
	 */
	static public IRI createEntityIRI(String namespaceExtn, String fragment) {

		try {

			return IRI.create(new URI(createEntityIRIString(namespaceExtn, fragment)));
		}
		catch (URISyntaxException e) {

			return null;
		}
	}

	/**
	 * Tests whether an entity IRI is within the base MEKON-OWL namespace.
	 *
	 * @param entityIRI Entity IRI to test
	 * @return True if IRI is within base namespace
	 */
	static public boolean entityInNamespace(IRI entityIRI) {

		return entityInNamespace(entityIRI, "");
	}

	/**
	 * Tests whether an entity IRI is within a specific extension of the
	 * base MEKON-OWL namespace.
	 *
	 * @param entityIRI Entity IRI to test
	 * @param namespaceExtn Final section of extended namespace
	 * @return True if IRI is within relevant namespace
	 */
	static public boolean entityInNamespace(IRI entityIRI, String namespaceExtn) {

		return entityIRI.toString().startsWith(createEntityIRIPrefix(namespaceExtn));
	}

	static private String createEntityIRIString(String namespaceExtn, String fragment) {

		return createEntityIRIPrefix(namespaceExtn) + fragment;
	}

	static private String createEntityIRIPrefix(String namespaceExtn) {

		return createNamespace(namespaceExtn) + "#";
	}
}
