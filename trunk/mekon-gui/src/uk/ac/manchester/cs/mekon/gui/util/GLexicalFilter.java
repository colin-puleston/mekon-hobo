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

package uk.ac.manchester.cs.mekon.gui.util;

import java.util.regex.*;

/**
 * @author Colin Puleston
 */
public class GLexicalFilter {

	static private final String WILD_CARD_SEQUENCE = ".*";

	private Pattern filter;

	public GLexicalFilter(String pattern, boolean matchStartOnly) {

		try {

			filter = Pattern.compile(resolvePattern(pattern, matchStartOnly));
		}
		catch (PatternSyntaxException e) {

			filter = null;
		}
	}

	boolean pass(String label) {

		return filter == null || filter.matcher(normaliseCase(label)).matches();
	}

	private String resolvePattern(String pattern, boolean matchStartOnly) {

		pattern = normaliseCase(pattern);

		if (!matchStartOnly) {

			pattern = WILD_CARD_SEQUENCE + pattern;
		}

		return pattern + WILD_CARD_SEQUENCE;
	}

	private String normaliseCase(String s) {

		return s.toLowerCase();
	}
}
