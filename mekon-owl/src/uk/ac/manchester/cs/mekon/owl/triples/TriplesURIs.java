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

/**
 * @author Colin Puleston
 */
class TriplesURIs {

	static private final String GRAPH_SUFFIX = "-GRAPH";
	static private final String NODE_SUFFIX_PREFIX = "-N";
	static private final String NODE_SUFFIX_FORMAT = NODE_SUFFIX_PREFIX + "%d";

	static String getGraphURI(String baseURI) {

		return baseURI + GRAPH_SUFFIX;
	}

	static String getRootNodeURI(String baseURI) {

		return getNodeURI(baseURI, 0);
	}

	static String getNodeURI(String baseURI, int index) {

		if (index == 0) {

			return baseURI;
		}

		return baseURI + getNodeURISuffix(index);
	}

	static String extractBaseURI(String nodeURI) {

		String uriSlice = removeFinalDigits(nodeURI);

		if (uriSlice.endsWith(NODE_SUFFIX_PREFIX)) {

			int l = uriSlice.length() - NODE_SUFFIX_PREFIX.length();

			return uriSlice.substring(0, l);
		}

		return nodeURI;
	}

	static private String getNodeURISuffix(int index) {

		return String.format(NODE_SUFFIX_FORMAT, index);
	}

	static private String removeFinalDigits(String uri) {

		int i = uri.length();

		for ( ; i > 0 && Character.isDigit(uri.charAt(i - 1)) ; i--);

		return uri.substring(0, i);
	}
}
