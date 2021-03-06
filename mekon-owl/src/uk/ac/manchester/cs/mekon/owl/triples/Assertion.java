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

import java.net.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
class Assertion {

	private OTFactory factory;
	private String baseURI;

	private class GraphRenderer extends InstanceRenderer<OT_URI> {

		private OTGraphAdder adder = createGraphAdder();

		void render(NNode instance) {

			renderFromRoot(instance);

			adder.addGraphToStore();
		}

		OT_URI renderDynamicNode(int index) {

			return new OT_URI(TriplesURIs.getNodeURI(baseURI, index));
		}

		OT_URI renderInstanceRefNode(URI refURI) {

			return new OT_URI(refURI);
		}

		OTValue renderNumberMin(OTNumber value) {

			throw createIndefiniteNumberException();
		}

		OTValue renderNumberMax(OTNumber value) {

			throw createIndefiniteNumberException();
		}

		void renderTriple(OT_URI subject, OT_URI predicate, OTValue object) {

			adder.addToGraph(subject, predicate, object);
		}

		void renderUnion(OT_URI subject, OT_URI predicate, Set<OTValue> objects) {

			throw createBadConstructException("nodes with disjunction types");
		}

		boolean typeRenderingRequired(NNode node) {

			return true;
		}

		private KAccessException createIndefiniteNumberException() {

			return createBadConstructException("indefinite number-values");
		}

		private KAccessException createBadConstructException(String construct) {

			return new KAccessException(
							"Cannot store instances containing "
							+ construct);
		}
	}

	Assertion(OTFactory factory, String baseURI) {

		this.factory = factory;
		this.baseURI = baseURI;
	}

	void add(NNode instance) {

		new GraphRenderer().render(instance);
	}

	void remove() {

		createGraphRemover().removeGraphFromStore();
	}

	private OTGraphAdder createGraphAdder() {

		return factory.createGraphAdder(getGraphURI());
	}

	private OTGraphRemover createGraphRemover() {

		return factory.createGraphRemover(getGraphURI());
	}

	private String getGraphURI() {

		return TriplesURIs.getGraphURI(baseURI);
	}
}
