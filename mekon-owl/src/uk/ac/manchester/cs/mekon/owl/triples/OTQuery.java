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

/**
 * Represents a particular SPARQL query.
 *
 * @author Colin Puleston
 */
public interface OTQuery {

	/**
	 * Stipulates whether the triple-store uses named-graphs, and hence
	 * that the query-strings should be constructed to include a construct
	 * specifying that queries should operate over all named graphs.
	 *
	 * @return True if triple-store uses named-graphs
	 */
	public boolean namedGraphs();

	/**
	 * Executes an ASK query.
	 *
	 * @param query String representation of SPARQL query to execute
	 * @param constants Constants for query.
	 * @return Result of query execution
	 */
	public boolean executeAsk(String query, OTQueryConstants constants);

	/**
	 * Executes a SELECT query whose result will be a list of bindings
	 * for a single URI-valued variable.
	 *
	 * @param query String representation of SPARQL query to execute
	 * @param constants Constants for query.
	 * @return Result of query execution
	 */
	public Set<OT_URI> executeSelect(String query, OTQueryConstants constants);
}
