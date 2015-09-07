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

import org.semanticweb.owlapi.vocab.*;

/**
 * @author Colin Puleston
 */
class FindQuery {

	static private final String QUERY_FORMAT = "SELECT ?p ?o WHERE {%s ?p ?o}";

	private OTFactory factory;
	private OTQuery selectQuery;

	FindQuery(OTFactory factory) {

		this.factory = factory;

		selectQuery = factory.createQuery();
	}

	OTGraph execute(OT_URI subject) {

		return execute(renderQuery(subject), subject);
	}

	private OTGraph execute(String query, OT_URI subject) {

		OTGraph graph = factory.createGraph();

		for (List<OTValue> bindings : selectQuery.executeSelect(query)) {

			OTValue object = bindings.get(1);

			if (!isOWLThing(object)) {

				OT_URI predicate = (OT_URI)bindings.get(0);

				graph.add(subject, predicate, object);
			}
		}

		return graph;
	}

	private String renderQuery(OT_URI subject) {

		return String.format(QUERY_FORMAT, renderSubject(subject));
	}

	private String renderSubject(OT_URI subject) {

		return selectQuery.getConstants().renderURI(subject.getURI());
	}

	private boolean isOWLThing(OTValue value) {

		if (value instanceof OT_URI) {

			OT_URI uri = (OT_URI)value;

			return uri.getURI().equals(OWLRDFVocabulary.OWL_THING);
		}

		return false;
	}
}
