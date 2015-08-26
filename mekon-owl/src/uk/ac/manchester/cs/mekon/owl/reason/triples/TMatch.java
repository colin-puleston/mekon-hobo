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

package uk.ac.manchester.cs.mekon.owl.reason.triples;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * Responsible for executing "match" queries, finding all
 * assertions that are matches for specified queries.
 *
 * @author Colin Puleston
 */
public abstract class TMatch {

	private TQueryConstants constants;

	private class Renderer extends MatchingQueryRenderer {

		static private final String ROOT_FRAME_VARIABLE = "?f";
		static private final String QUERY_FORMAT = "SELECT %s\nWHERE %s";

		Renderer() {

			super(constants);
		}

		TURI getRootFrameNode() {

			return new QueryValue(ROOT_FRAME_VARIABLE);
		}

		String createQuery(String queryBody) {

			return String.format(QUERY_FORMAT, ROOT_FRAME_VARIABLE, queryBody);
		}
	}

	/**
	 * Constructor
	 *
	 * @param constants Object for representing of the constants for
	 * the query
	 */
	protected TMatch(TQueryConstants constants) {

		this.constants = constants;
	}

	/**
	 * Abstract method whose implementations should execute the
	 * specified SPARQL "select" query, and return the required set
	 * of matches as a set of URIs retrieved from the resulting
	 * collection of binding-sets, each of which will contain a
	 * single binding representing the base-URI for a particular
	 * assertion.
	 *
	 * @param query SPARQL select-query to execute
	 * @return Set of base-URIs of all assertions from store that
	 * match the relevant query
	 */
	protected abstract List<TURI> execute(String query);

	List<CIdentity> execute(Store store, ORFrame query) {

		List<CIdentity> ids = new ArrayList<CIdentity>();

		for (TURI baseURI : execute(query)) {

			ids.add(store.baseURIToId(baseURI.getURI()));
		}

		return ids;
	}

	private List<TURI> execute(ORFrame query) {

		return execute(new Renderer().render(query));
	}
}
