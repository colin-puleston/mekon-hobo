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

package uk.ac.manchester.cs.mekon.util;

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

		label.append(name.charAt(0));

		for (int i = 1 ; i < name.length() ; i++) {

			char c = name.charAt(i);

			if (Character.isUpperCase(c)) {

				if (nonUpperCase(name, i - 1) || nonUpperCaseOrDigit(name, i + 1)) {

					label.append(' ');

					if (!isUpperCase(name, i + 1)) {

						c = Character.toLowerCase(c);
					}
				}

				label.append(c);
			}
			else if (Character.isDigit(c)) {

				if (nonDigit(name, i - 1)) {

					label.append(' ');
				}

				label.append(c);

				if (nonDigit(name, i + 1) && nonUpperCase(name, i + 1)) {

					label.append(' ');
				}
			}
			else {

				label.append(c);
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

	static private boolean isUpperCase(String name, int i) {

		return i < name.length() && Character.isUpperCase(name.charAt(i));
	}

	static private boolean nonUpperCaseOrDigit(String name, int i) {

		return nonUpperCase(name, i) && nonDigit(name, i);
	}

	static private boolean nonUpperCase(String name, int i) {

		return i < name.length() && !Character.isUpperCase(name.charAt(i));
	}

	static private boolean nonDigit(String name, int i) {

		return i < name.length() && !Character.isDigit(name.charAt(i));
	}
}
