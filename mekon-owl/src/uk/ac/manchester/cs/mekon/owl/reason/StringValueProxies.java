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

package uk.ac.manchester.cs.mekon.owl.reason;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
class StringValueProxies {

	static private final String PROPERTIES_NAMESPACE_EXTN = "proxy-properties";
	static private final String PROPERTY_FRAGMENT_FORMAT = "proxy-%s-%s";

	private OModel model;
	private OWLDataFactory dataFactory;

	private ProxyIndexes<OWLDataProperty> proxyPropertyIndexes
								= new ProxyIndexes<OWLDataProperty>();

	private Map<OWLDataProperty, OWLDataProperty> proxyProperties
					= new HashMap<OWLDataProperty, OWLDataProperty>();

	private ProxyIndexes<String> proxyValues = new ProxyIndexes<String>();

	private class ProxyIndexes<E> extends KIndexes<E> {

		protected KRuntimeException createException(String message) {

			throw new Error("Method should never be invoked!");
		}
	}

	StringValueProxies(OModel model) {

		this.model = model;

		dataFactory = model.getDataFactory();
	}

	OWLDataProperty toProxyProperty(OWLDataProperty property) {

		OWLDataProperty proxy = proxyProperties.get(property);

		if (proxy == null) {

			proxy = addProxyProperty(property);
			proxyProperties.put(property, proxy);
		}

		return proxy;
	}

	INumber toProxyValue(String value) {

		return new INumber(proxyValues.ensureIndex(value));
	}

	private OWLDataProperty addProxyProperty(OWLDataProperty property) {

		IRI proxyIri = createProxyPropertyIRI(property);
		OWLDataProperty proxy = dataFactory.getOWLDataProperty(proxyIri);

		model.addInstanceAxiom(dataFactory.getOWLDeclarationAxiom(proxy));

		return proxy;
	}

	private IRI createProxyPropertyIRI(OWLDataProperty property) {

		String frag = property.getIRI().toURI().getFragment();
		int proxyIdx = proxyPropertyIndexes.ensureIndex(property);
		String proxyFrag = String.format(PROPERTY_FRAGMENT_FORMAT, proxyIdx, frag);

		return O_IRINamespaces.createEntityIRI(PROPERTIES_NAMESPACE_EXTN, proxyFrag);
	}
}
