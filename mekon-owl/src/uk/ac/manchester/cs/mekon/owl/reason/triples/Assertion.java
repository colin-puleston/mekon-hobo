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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * @author Colin Puleston
 */
class Assertion {

	static private final String FRAME_NODE_NAME_FORMAT = "%s-F%d";

	private TFactory factory;
	private String baseURI;

	private class GraphRenderer extends InstanceRenderer {

		private TGraph graph = factory.createGraph();

		TGraph render(ORFrame instance) {

			renderFrame(instance);

			return graph;
		}

		TURI renderFrame(int index) {

			return getFrameNode(index);
		}

		TURI renderURI(String uri) {

			return factory.getURI(uri);
		}

		TNumber renderNumber(Integer number) {

			return factory.getNumber(number);
		}

		TNumber renderNumber(Long number) {

			return factory.getNumber(number);
		}

		TNumber renderNumber(Float number) {

			return factory.getNumber(number);
		}

		TNumber renderNumber(Double number) {

			return factory.getNumber(number);
		}

		TValue renderNumberMin(TNumber value) {

			throw createIndefiniteNumberException();
		}

		TValue renderNumberMax(TNumber value) {

			throw createIndefiniteNumberException();
		}

		void renderTriple(TURI subject, TURI predicate, TValue object) {

			graph.add(subject, predicate, object);
		}

		void renderUnion(TURI subject, TURI predicate, Set<TValue> objects) {

			throw createBadConstructException("frames with disjunction types");
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

	Assertion(TFactory factory, String baseURI) {

		this.factory = factory;
		this.baseURI = baseURI;
	}

	void add(ORFrame instance) {

		new GraphRenderer().render(instance).addToStore();
	}

	void remove() {

		for (int i = 0 ; removeSubGraph(getFrameNode(i)) ; i++);
	}

	private boolean removeSubGraph(TURI subject) {

		TGraph graph = factory.createFind().execute(subject);

		if (graph.isEmpty()) {

			return false;
		}

		graph.removeFromStore();

		return true;
	}

	private TURI getFrameNode(int index) {

		return factory.getURI(getFrameNodeURI(index));
	}

	private String getFrameNodeURI(int index) {

		if (index == 0) {

			return baseURI;
		}

		return String.format(FRAME_NODE_NAME_FORMAT, baseURI, index);
	}
}
