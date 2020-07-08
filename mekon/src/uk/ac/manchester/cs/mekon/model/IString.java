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

import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * Represents a string-value, with the actual value being represented
 * by a primitive Java <code>String</code> value.
 *
 * @author Colin Puleston
 */
public class IString extends IDataValue {

	/**
	 * Represents the Empty string.
	 */
	static public final IString EMPTY_STRING = new IString("");

	private String value;

	/**
	 * Tests for equality between this and other specified object,
	 * which will hold if and only if the other object is another
	 * <code>IString</code> object representing the same string-value
	 * as this one.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if objects are equal
	 */
	public boolean equals(Object other) {

		return other instanceof IString && equalsString((IString)other);
	}

	/**
	 * Provides hash-code based on the string-value.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return value.hashCode();
	}

	/**
	 * Provides the type of the string value, which will always be the
	 * {@link CString#UNCONSTRAINED} object (though this will not always
	 * be the type from which the value was instantiated).
	 *
	 * @return Type of string value
	 */
	public CString getType() {

		return CStringFactory.FREE;
	}

	/**
	 * Stipulates that this value-entity never abstract.
	 *
	 * @return Always false
	 */
	public boolean abstractValue() {

		return false;
	}

	/**
	 * Tests whether this value-entity subsumes another specified
	 * value-entity, if and only if the other value-entity is another
	 * <code>IString</code> object, as determined via the {@link #equals}
	 * method.
	 *
	 * @param other Other value-entity to test for subsumption
	 * @return True if this value-entity subsumes other value-entity
	 */
	public boolean subsumes(IValue other) {

		return equals(other);
	}

	/**
	 * Provides the represented string-value.
	 *
	 * @return Represented string-value
	 */
	public String get() {

		return value;
	}

	IString(String value) {

		this.value = value;
	}

	String getDataValueDescription() {

		return value;
	}

	private boolean equalsString(IString other) {

		return value.equals(other.value);
	}
}
