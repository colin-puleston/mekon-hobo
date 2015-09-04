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

package uk.ac.manchester.cs.mekon.owl.jena;

import java.net.*;
import java.util.*;

import org.apache.jena.rdf.model.*;

import uk.ac.manchester.cs.mekon.owl.triples.*;

/**
 * @author Colin Puleston
 */
class OJenaGraph implements OTGraph {

	private Model model;
	private List<Statement> statements = new ArrayList<Statement>();

	public void add(OT_URI subject, OT_URI predicate, OTValue object) {

		OJenaValue s = (OJenaValue)subject;
		OJenaValue p = (OJenaValue)predicate;
		OJenaValue o = (OJenaValue)object;

		add(s.extractResource(), p.extractResource(), o.extractNode());
	}

	public void addToStore() {

		model.add(statements);
	}

	public void removeFromStore() {

		model.remove(statements);
	}

	public boolean isEmpty() {

		return statements.isEmpty();
	}

	OJenaGraph(Model model) {

		this.model = model;
	}

	private void add(Resource subject, Resource predicate, RDFNode object) {

		statements.add(model.createStatement(subject, asProperty(predicate), object));
	}

	private Property asProperty(Resource resource) {

		String uri = resource.getURI();
		String fragment = URI.create(uri).getFragment();
		String namespace = uri.substring(0, uri.length() - fragment.length());

		return model.createProperty(namespace, fragment);
	}
}
