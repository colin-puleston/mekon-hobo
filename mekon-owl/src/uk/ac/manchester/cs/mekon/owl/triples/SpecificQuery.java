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
 * @author Colin Puleston
 */
abstract class SpecificQuery {

	static private final String ASK_FORMAT = "ASK {%s}";
	static private final String SELECT_FORMAT = "SELECT %s {%s}";

	static private final String NAMED_GRAPH_WRAPPER_FORMAT = "GRAPH ?g {%s}";

	private OTQuery query;
	private OTQueryConstants constants = new OTQueryConstants();

	SpecificQuery(OTFactory factory) {

		query = factory.createQuery();
	}

	OTQueryConstants getConstants() {

		return constants;
	}

	boolean executeAsk(String queryBody) {

		return query.executeAsk(renderAsk(queryBody), constants);
	}

	Set<OT_URI> executeSelect(String variable, String queryBody) {

		return query.executeSelect(renderSelect(variable, queryBody), constants);
	}

	private String renderAsk(String queryBody) {

		return String.format(ASK_FORMAT, resolveBody(queryBody));
	}

	private String renderSelect(String variable, String queryBody) {

		return String.format(SELECT_FORMAT, variable, resolveBody(queryBody));
	}

	private String resolveBody(String queryBody) {

		return query.namedGraphs() ? wrapForNamedGraphs(queryBody) : queryBody;
	}

	private String wrapForNamedGraphs(String queryBody) {

		return String.format(NAMED_GRAPH_WRAPPER_FORMAT, queryBody);
	}
}
