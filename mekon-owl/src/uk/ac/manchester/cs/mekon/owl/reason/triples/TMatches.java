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

import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * Responsible for executing "matches" queries, testing whether
 * particular assertions are matches for specified queries.
 *
 * @author Colin Puleston
 */
public abstract class TMatches {

	private TQueryConstants constants;

	private class Renderer extends MatchingQueryRenderer {

		static private final String QUERY_FORMAT = "ASK %s";

		private TURI rootFrameNode;

		Renderer(String baseURI) {

			super(constants);

			rootFrameNode = renderURI(baseURI);
		}

		TURI getRootFrameNode() {

			return rootFrameNode;
		}

		String createQuery(String queryBody) {

			return String.format(QUERY_FORMAT, queryBody);
		}
	}

	/**
	 * Constructor
	 *
	 * @param constants Object for representing of the constants for
	 * the query
	 */
	protected TMatches(TQueryConstants constants) {

		this.constants = constants;
	}

	/**
	 * Abstract method whose implementations should execute the
	 * specified SPARQL "ask" query and directly return the result.
	 *
	 * @param query SPARQL ask-query to execute
	 * @return Result of executing ask-query
	 */
	protected abstract boolean execute(String query);

	boolean execute(ORFrame query, String baseURI) {

		return execute(new Renderer(baseURI).render(query));
	}
}
