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
 * Represents a value used in a triple, either a URI or a number.
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
	 * Tests whether value is a URI.
	 *
	 * @return True if value is URI
	 */
	public boolean isURI() {

		return isValueType(String.class);
	}

	/**
	 * Tests whether value is a integer.
	 *
	 * @return True if value is integer
	 */
	public boolean isInteger() {

		return isValueType(Integer.class);
	}

	/**
	 * Tests whether value is a long.
	 *
	 * @return True if value is long
	 */
	public boolean isLong() {

		return isValueType(Long.class);
	}

	/**
	 * Tests whether value is a float.
	 *
	 * @return True if value is float
	 */
	public boolean isFloat() {

		return isValueType(Float.class);
	}

	/**
	 * Tests whether value is a double.
	 *
	 * @return True if value is double
	 */
	public boolean isDouble() {

		return isValueType(Double.class);
	}

	/**
	 * Provides string representation of URI value.
	 *
	 * @return String representation of URI value
	 * @throws ClassCastException if not a URI value
	 */
	public String asURI() {

		return (String)value;
	}

	/**
	 * Provides integer value.
	 *
	 * @return Integer value
	 * @throws ClassCastException if not a integer value
	 */
	public Integer asInteger() {

		return (Integer)value;
	}

	/**
	 * Provides long value.
	 *
	 * @return Long value
	 * @throws ClassCastException if not a long value
	 */
	public Long asLong() {

		return (Long)value;
	}

	/**
	 * Provides float value.
	 *
	 * @return Float value
	 * @throws ClassCastException if not a float value
	 */
	public Float asFloat() {

		return (Float)value;
	}

	/**
	 * Provides double value.
	 *
	 * @return Double value
	 * @throws ClassCastException if not a double value
	 */
	public Double asDouble() {

		return (Double)value;
	}

	OTValue(Object value) {

		this.value = value;
	}

	String getQueryRendering(OTQueryConstants constants) {

		return constants.getVariableRendering(this);
	}

	private boolean isValueType(Class<?> type) {

		return value.getClass().equals(type);
	}
}
