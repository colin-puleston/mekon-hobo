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
 * Represents a number used as an object in a triple.
 *
 * @author Colin Puleston
 */
public class OTNumber extends OTValue {

	/**
	 * Constructor.
	 *
	 * @param number Number value
	 */
	public OTNumber(Number number) {

		super(number);
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
	 * Provides integer value.
	 *
	 * @return Integer value
	 * @throws ClassCastException if not a integer value
	 */
	public Integer asInteger() {

		return getValueAsType(Integer.class);
	}

	/**
	 * Provides long value.
	 *
	 * @return Long value
	 * @throws ClassCastException if not a long value
	 */
	public Long asLong() {

		return getValueAsType(Long.class);
	}

	/**
	 * Provides float value.
	 *
	 * @return Float value
	 * @throws ClassCastException if not a float value
	 */
	public Float asFloat() {

		return getValueAsType(Float.class);
	}

	/**
	 * Provides double value.
	 *
	 * @return Double value
	 * @throws ClassCastException if not a double value
	 */
	public Double asDouble() {

		return getValueAsType(Double.class);
	}

	void accept(OTValueVisitor visitor) {

		visitor.visit(this);
	}
}
