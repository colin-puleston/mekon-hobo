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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * @author Colin Puleston
 */
class MatchQuery {

	private OTQuery selectQuery;

	private class Renderer extends MatchingQueryRenderer {

		static private final String ROOT_FRAME_VARIABLE = "?f";
		static private final String QUERY_FORMAT = "SELECT %s\nWHERE %s";

		Renderer() {

			super(selectQuery.getConstants());
		}

		OT_URI getRootFrameNode() {

			return new QueryValue(ROOT_FRAME_VARIABLE);
		}

		String createQuery(String queryBody) {

			return String.format(QUERY_FORMAT, ROOT_FRAME_VARIABLE, queryBody);
		}
	}

	MatchQuery(OTFactory factory) {

		selectQuery = factory.createQuery();
	}

	List<CIdentity> execute(Store store, ORFrame query) {

		List<CIdentity> ids = new ArrayList<CIdentity>();

		for (List<OTValue> bindings : execute(query)) {

			OT_URI baseURI = (OT_URI)bindings.get(0);

			ids.add(store.baseURIToId(baseURI.getURI()));
		}

		return ids;
	}

	private List<List<OTValue>> execute(ORFrame query) {

		return selectQuery.executeSelect(new Renderer().render(query));
	}
}
