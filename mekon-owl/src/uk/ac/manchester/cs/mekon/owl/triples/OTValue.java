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
 * Represents a value used in a triple, either a URI, a number, or
 * a string.
 *
 * @author Colin Puleston
 */
public abstract class OTValue {

	private Object value;

	/**
	 * Objects are equal if other object is of same type and represents
	 * the same value.
	 *
	 * @param other Other object to test against
	 * @return True if objects are equal
	 */
	public boolean equals(Object other) {

		if (other instanceof OTValue) {

			return value.equals(((OTValue)other).value);
		}

		return false;
	}

	/**
	 */
	public int hashCode() {

		return value.hashCode();
	}

	/**
	 * Provides a rendering of the value as a string.
	 *
	 * @return String rendering of value
	 */
	public String toString() {

		return value.toString();
	}

	/**
	 * Provides the class object for the value.
	 *
	 * @return Class object for value
	 */
	public Class<?> getValueType() {

		return value.getClass();
	}

	OTValue(Object value) {

		this.value = value;
	}

	abstract void accept(OTValueVisitor visitor);

	boolean isValueType(Class<?> type) {

		return value.getClass().equals(type);
	}

	<V>V getValueAsType(Class<V> type) {

		return type.cast(value);
	}

	String getQueryRendering(OTQueryConstants constants) {

		return constants.getVariableRendering(this);
	}
}
