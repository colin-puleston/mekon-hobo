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

import uk.ac.manchester.cs.mekon.network.*;

/**
 * @author Colin Puleston
 */
class MatchesQuery extends SpecificQuery {

	private class QueryBodyRenderer extends MatchingQueryBodyRenderer {

		private String rootNodeURI;

		QueryBodyRenderer(String baseURI) {

			super(getConstants());

			rootNodeURI = TripleNodeURIs.getRootNodeURI(baseURI);
		}

		QueryVariable getRootTripleNode() {

			return new QueryVariable(getRootNodeRendering());
		}

		private String getRootNodeRendering() {

			return getConstants().getVariableRendering(renderURI(rootNodeURI));
		}
	}

	MatchesQuery(OTFactory factory) {

		super(factory);
	}

	boolean execute(NNode query, String baseURI) {

		return executeAsk(renderQueryBody(query, baseURI));
	}

	private String renderQueryBody(NNode query, String baseURI) {

		return new QueryBodyRenderer(baseURI).render(query);
	}
}
