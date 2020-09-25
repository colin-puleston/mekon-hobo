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

package uk.ac.manchester.cs.mekon.model;

import java.net.*;

/**
 * Represents the required format for the content of instantiations
 * of a specific string value-type, as represented by a {@link CString}
 * object.
 *
 * @author Colin Puleston
 */
public enum CStringFormat {

	/**
	 * Instantiations may contain any string values.
	 */
	FREE,

	/**
	 * Instantiations must represent valid URI values.
	 */
	URI_VALUE,

	/**
	 * Instantiations must represent valid URL values.
	 */
	URL_VALUE,

	/**
	 * Instantiations must represent specific types of string
	 * values, with value validity check being defined via some
	 * unspecified custom mechanism.
	 */
	CUSTOM;

	/**
	 * Combines this with another specified format. If neither
	 * format is {@link #CUSTOM} then returns the one with the
	 * greatest ordinal value, since the non-{@link #CUSTOM}
	 * format-values are arranged so that earlier values subsume
	 * later values. If only one of the formats is {@link #CUSTOM},
	 * then returns {@link #CUSTOM}, which is always assumed to
	 * take precedence over other formats. If both formats are
	 * {@link #CUSTOM} then returns null to indicate that any
	 * resolution needs to be sorted out elsewhere.
	 *
	 * @param other Format with which to combine this one
	 * @return Combined format, or null if both values are
	 * {@link #CUSTOM}
	 */
	public CStringFormat combineWith(CStringFormat other) {

		if (this == CUSTOM && other == CUSTOM) {

			return null;
		}

		return ordinal() < other.ordinal() ? other : this;
	}
}
