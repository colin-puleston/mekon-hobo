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

import uk.ac.manchester.cs.mekon_util.*;

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
	FREE {

		public CString getStandardValueType() {

			return CString.FREE;
		}
	},

	/**
	 * Instantiations must represent valid URI values.
	 */
	URI_VALUE {

		public CString getStandardValueType() {

			return CString.URI_VALUE;
		}
	},

	/**
	 * Instantiations must represent valid URL values.
	 */
	URL_VALUE {

		public CString getStandardValueType() {

			return CString.URL_VALUE;
		}
	},

	/**
	 * Instantiations must represent specific types of string
	 * values, with value validity check being defined via some
	 * unspecified custom mechanism.
	 */
	CUSTOM {

		public CString getStandardValueType() {

			throw new KAccessException("Cannot invoke method for custom format");
		}
	};

	/**
	 * Gives the singleton object provided by {@link CString}
	 * representing the standard string value-type that has this
	 * format.
	 *
	 * @return Singleton object representing relevant value-type
	 * @throws KAccessException if this value is {@link #CUSTOM}
	 */
	public abstract CString getStandardValueType();
}
