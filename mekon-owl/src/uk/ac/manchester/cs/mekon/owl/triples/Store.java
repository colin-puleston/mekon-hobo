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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.util.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * @author Colin Puleston
 */
class Store {

	private OTFactory factory;

	private AssertionIndexes assertionIndexes = new AssertionIndexes();

	private class AssertionIndexes extends KIndexes<String> {

		protected KRuntimeException createException(String message) {

			return new KSystemConfigException(message);
		}
	}

	Store(OTFactory factory) {

		this.factory = factory;
	}

	synchronized void add(ORFrame instance, CIdentity identity) {

		add(instance, assertionIndexes.assignIndex(identity.getIdentifier()));
	}

	synchronized void remove(CIdentity identity) {

		remove(assertionIndexes.getIndex(identity.getIdentifier()));
	}

	boolean present(CIdentity identity) {

		return assertionIndexes.hasIndex(identity.getIdentifier());
	}

	List<CIdentity> match(ORFrame query) {

		List<CIdentity> ids = new ArrayList<CIdentity>();

		for (OT_URI uri : executeMatch(query)) {

			ids.add(baseURIToId(extractBaseURI(uri.asURI())));
		}

		return ids;
	}

	boolean matches(ORFrame query, ORFrame instance) {

		int assertionIndex = addTemp(instance);
		String baseURI = getBaseURI(assertionIndex);

		boolean result = executeMatches(query, baseURI);

		removeTemp(assertionIndex);

		return result;
	}

	private synchronized int addTemp(ORFrame instance) {

		int index = assertionIndexes.assignIndex();

		add(instance, index);

		return index;
	}

	private synchronized void removeTemp(int index) {

		assertionIndexes.freeIndex(index);

		remove(index);
	}

	private void add(ORFrame instance, int index) {

		getAssertion(index).add(instance);
	}

	private void remove(int index) {

		getAssertion(index).remove();
	}

	private List<OT_URI> executeMatch(ORFrame query) {

		return new MatchQuery(factory).execute(query);
	}

	private boolean executeMatches(ORFrame query, String baseURI) {

		return new MatchesQuery(factory).execute(query, baseURI);
	}

	private Assertion getAssertion(int index) {

		return new Assertion(factory, getBaseURI(index));
	}

	private CIdentity baseURIToId(String baseURI) {

		int assertionIndex = extractAssertionIndex(baseURI);

		return new CIdentity(assertionIndexes.getElement(assertionIndex));
	}

	private String getBaseURI(int assertionIndex) {

		return AssertionURIs.getBaseURI(assertionIndex);
	}

	private String extractBaseURI(String uri) {

		return AssertionURIs.extractBaseURI(uri);
	}

	private int extractAssertionIndex(String baseURI) {

		return AssertionURIs.extractAssertionIndex(baseURI);
	}
}
