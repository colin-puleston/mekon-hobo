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

/**
 * Represents a particular SPARQL query.
 *
 * @author Colin Puleston
 */
public interface OTQuery {

	/**
	 * Provides the object that is responsible for managing the
	 * constants for the query.
	 *
	 * @return Manager for queries constants
	 */
	public OTQueryConstants getConstants();

	/**
	 * Executes an "ask" query.
	 *
	 * @param query String representation of SPARQL query to execute
	 * @return Result of query execution
	 */
	public boolean executeAsk(String query);

	/**
	 * Executes a "select" query and returns a list containing each
	 * resulting set of bindings, with each set being represented via
	 * a list of bound values, ordered according to the position of
	 * the corresponding variables in the query-string.
	 *
	 * @param query String representation of SPARQL query to execute
	 * @return Result of query execution
	 */
	public List<List<OTValue>> executeSelect(String query);
}
