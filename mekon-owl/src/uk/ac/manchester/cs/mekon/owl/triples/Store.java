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

package uk.ac.manchester.cs.mekon.owl.triples;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class Store {

	private OTFactory factory;

	private ODynamicInstanceIRIs dynamicInstanceIRIs = new ODynamicInstanceIRIs();

	Store(OTFactory factory) {

		this.factory = factory;
	}

	void add(NNode instance, IRI iri) {

		getAssertion(iri).add(instance);
	}

	void remove(IRI iri) {

		getAssertion(iri).remove();
	}

	List<IRI> match(NNode query) {

		List<IRI> matches = new ArrayList<IRI>();

		for (OT_URI uri : executeMatch(query)) {

			matches.add(IRI.create(extractBaseURI(uri.asURI())));
		}

		return matches;
	}

	boolean matches(NNode query, NNode instance) {

		IRI dynamicIRI = addDynamic(instance);
		boolean result = executeMatches(query, dynamicIRI.toString());

		removeDynamic(dynamicIRI);

		return result;
	}

	private IRI addDynamic(NNode instance) {

		IRI iri = dynamicInstanceIRIs.assign();

		add(instance, iri);

		return iri;
	}

	private void removeDynamic(IRI iri) {

		dynamicInstanceIRIs.free(iri);

		remove(iri);
	}

	private List<OT_URI> executeMatch(NNode query) {

		return new MatchQuery(factory).execute(query);
	}

	private boolean executeMatches(NNode query, String baseURI) {

		return new MatchesQuery(factory).execute(query, baseURI);
	}

	private Assertion getAssertion(IRI iri) {

		return new Assertion(factory, iri.toString());
	}

	private String extractBaseURI(String uri) {

		return TriplesURIs.extractBaseURI(uri);
	}
}
