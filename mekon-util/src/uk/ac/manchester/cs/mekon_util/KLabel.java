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

package uk.ac.manchester.cs.mekon_util;

/**
 * Utility class that heuristically converts concept-names or
 * property-names, which may be in "camel-back" notation, into
 * versions intended to be suitable for being displayed to an
 * end-user.
 *
 * @author Colin Puleston
 */
public class KLabel {

	/**
	 * Creates an end-user format label from a class-name.
	 *
	 * @param sourceClass Class from whose name the label is to be
	 * generated
	 * @return Generated label
	 */
	static public String create(Class<?> sourceClass) {

		return create(sourceClass.getSimpleName());
	}

	/**
	 * Creates an end-user format label.
	 *
	 * @param name Entity-name from which label is to be generated
	 * @return Generated label
	 */
	static public String create(String name) {

		StringBuilder label = new StringBuilder();

		for (int i = 0 ; i < name.length() ; i++) {

			Character c = name.charAt(i);
			Character p = getPreviousOrNull(name, i);
			Character n = getNextOrNull(name, i);

			if (lowerCase(c) || dash(c) || dash(p) || underscore(p)) {

				label.append(c);
			}
			else if (underscore(c)) {

				label.append(' ');
			}
			else if (lowerCase(p)) {

				label.append(' ');

				if (lowerCase(n)) {

					c = Character.toLowerCase(c);
				}

				label.append(c);
			}
			else {

				label.append(c);

				if (lowerCase(n) && (upperCase(p) || digit(p))) {

					label.append(' ');
				}
			}
		}

		return label.toString();
	}

	/**
	 * Attempts to recreate the entity-name from which end-user format
	 * label was generated.
	 *
	 * @param label Entity-name from which label is to be generated
	 * @return Regenerated entity-name
	 */
	static public String recreateName(String label) {

		StringBuilder name = new StringBuilder();

		for (String word : label.split(" ")) {

			if (!word.isEmpty()) {

				name.append(Character.toUpperCase(word.charAt(0)));
				name.append(word.substring(1));
			}
		}

		return name.toString();
	}

	static private Character getPreviousOrNull(String name, int i) {

		return i > 0 ? name.charAt(i - 1) : null;
	}

	static private Character getNextOrNull(String name, int i) {

		return i < name.length() - 1 ? name.charAt(i + 1) : null;
	}

	static private boolean lowerCase(Character c) {

		return c != null && Character.isLowerCase(c);
	}

	static private boolean upperCase(Character c) {

		return c != null && Character.isUpperCase(c);
	}

	static private boolean digit(Character c) {

		return c != null && Character.isDigit(c);
	}

	static private boolean dash(Character c) {

		return c != null && c == '-';
	}

	static private boolean underscore(Character c) {

		return c != null && c == '_';
	}
}
