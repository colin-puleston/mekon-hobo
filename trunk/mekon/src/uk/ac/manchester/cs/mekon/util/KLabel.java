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
 * versions that are hopefully suitable for being displayed to
 * an end-user. Will produce either "concept-format" labels
 * (begining with upper-case) or "property-format" (begining
 * with lower-case).
 *
 * @author Colin Puleston
 */
public class KLabel {

	/**
	 * Creates an end-user format label from a class-name.
	 *
	 * @param sourceClass Class from whose name the label is
	 * to be generated
	 * @return generated label
	 */
	static public String create(Class<?> sourceClass) {

		return create(sourceClass.getSimpleName(), true);
	}

	/**
	 * Creates an end-user format label.
	 *
	 * @param name Entity-name from which label is to be generated
	 * @param conceptFormat True if label is to be in concept-format
	 * rather than property-format.
	 * @return generated label
	 */
	static public String create(String name, boolean conceptFormat) {

		return new KLabel(conceptFormat).create(name);
	}

	private boolean conceptFormat;

	private KLabel(boolean conceptFormat) {

		this.conceptFormat = conceptFormat;
	}

	private String create(String name) {

		String label = "";
		Character lastChar = null;

		for (int i = 0 ; i < name.length() ; i++) {

			char c = name.charAt(i);

			label += toLabelChars(c, lastChar);
			lastChar = c;
		}

		return label;
	}

	private String toLabelChars(char nameChar, Character lastNameChar) {

		String prefix = "";
		char labelChar = nameChar;

		if (lastNameChar == null) {

			if (conceptFormat) {

				labelChar = Character.toUpperCase(nameChar);
			}
		}
		else {

			if (Character.isUpperCase(nameChar)
				&& Character.isLowerCase(lastNameChar)) {

				prefix = "-";
				labelChar = Character.toLowerCase(nameChar);
			}
		}

		return prefix + labelChar;
	}
}