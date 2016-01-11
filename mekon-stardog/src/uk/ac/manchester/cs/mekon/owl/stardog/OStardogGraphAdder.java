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

package uk.ac.manchester.cs.mekon.owl.stardog;

import java.util.*;

import org.openrdf.model.*;
import org.openrdf.model.impl.*;

import com.complexible.stardog.*;
import com.complexible.stardog.api.*;

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.triples.*;

/**
 * @author Colin Puleston
 */
class OStardogGraphAdder implements OTGraphAdder {

	static private final ValueFactory valueFactory = SimpleValueFactory.getInstance();

	private Connection connection;
	private IRI context;

	private List<Triple> triples = new ArrayList<Triple>();

	private class Triple {

		private IRI subject;
		private IRI predicate;
		private Value object;

		Triple(OT_URI subj, OT_URI pred, OTValue obj) {

			subject = convertURI(subj);
			predicate = convertURI(pred);
			object = ValueConverter.convert(obj);

			triples.add(this);
		}

		void add() throws StardogException {

			connection.add().statement(subject, predicate, object, context);
		}

		private IRI convertURI(OT_URI uri) {

			return valueFactory.createIRI(uri.asURI());
		}
	}

	public void addGraphToStore() {

		try {

			connection.begin();
			addTriples();
			connection.commit();
		}
		catch (StardogException e) {

			throw new KSystemConfigException(e);
		}
	}

	public void addToGraph(OT_URI subject, OT_URI predicate, OTValue object) {

		new Triple(subject, predicate, object);
	}

	OStardogGraphAdder(Connection connection, String contextURI) {

		this.connection = connection;

		context = valueFactory.createIRI(contextURI);
	}

	private void addTriples() throws StardogException {

		for (Triple triple : triples) {

			triple.add();
		}
	}
}
