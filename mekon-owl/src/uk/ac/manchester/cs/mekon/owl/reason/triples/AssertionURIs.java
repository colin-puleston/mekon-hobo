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

/**
 * @author Colin Puleston
 */
class AssertionURIs {

	static private final String NAMESPACE = "urn:mekon-stardog";

	static private final String BASE_FRAGMENT_PREFIX = "G";
	static private final String BASE_FRAGMENT_FORMAT = BASE_FRAGMENT_PREFIX + "%d";
	static private final int BASE_COMMON_LENGTH = getBaseURICommonLength();

	static String getBaseURI(int assertionIndex) {

		return getURI(getBaseURIFragment(assertionIndex));
	}

	static int extractAssertionIndex(String baseURI) {

		return Integer.parseInt(baseURI.substring(BASE_COMMON_LENGTH));
	}

	static private String getBaseURIFragment(int assertionIndex) {

		return String.format(BASE_FRAGMENT_FORMAT, assertionIndex);
	}

	static private int getBaseURICommonLength() {

		return getURI(BASE_FRAGMENT_PREFIX).length();
	}

	static private String getURI(String fragment) {

		return NAMESPACE + "#" + fragment;
	}
}
