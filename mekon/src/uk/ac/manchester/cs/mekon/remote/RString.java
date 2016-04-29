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

package uk.ac.manchester.cs.mekon.remote;

/**
 * Represents a string-value.
 *
 * @author Colin Puleston
 */
public class RString extends RValue {

	private String value;

	/**
	 * Constructor.
	 *
	 * @param value String-value to be represented
	 */
	public RString(String value) {

		this.value = value;
	}

	/**
	 * Tests for equality between this and other specified object,
	 * which will hold if and only if the other object is another
	 * <code>RString</code> object representing the same string-value
	 * as this one.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if objects are equal
	 */
	public boolean equals(Object other) {

		return other instanceof RString && value.equals(((RString)other).value);
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
	 * Provides the represented string-value.
	 *
	 * @return Represented string-value
	 */
	public String get() {

		return value;
	}

	void acceptVisitor(RValueVisitor visitor) {

		visitor.visit(this);
	}

	RStringSpec toSpec() {

		RStringSpec spec = new RStringSpec();

		spec.setValue(value);

		return spec;
	}
}
