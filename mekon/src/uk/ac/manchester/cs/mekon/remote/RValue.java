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

import uk.ac.manchester.cs.mekon.*;

/**
 * Represents a value for a slot in the remote frames-based
 * representation.
 *
 * @author Colin Puleston
 */
public class RValue {

	private Object value;

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>RValue</code>
	 * object representing the same value
	 */
	public boolean equals(Object other) {

		if (other instanceof RValue) {

			return value.equals(((RValue)other).value);
		}

		return false;
	}

	/**
	 * Provides hash-code based on represented value.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return value.hashCode();
	}

	/**
	 * Provides the class of the represented value.
	 *
	 * @return Class of represented value
	 */
	public Class<?> getValueClass() {

		return value.getClass();
	}

	/**
	 * Provides represented value as concept, if applicable.
	 *
	 * @return Value as concept
	 * @throws KAccessException if not concept value
	 */
	public RConcept asConcept() {

		return as(RConcept.class);
	}

	/**
	 * Provides represented value as frame, if applicable.
	 *
	 * @return Value as frame
	 * @throws KAccessException if not frame value
	 */
	public RFrame asFrame() {

		return as(RFrame.class);
	}

	/**
	 * Provides represented value as frame-disjunction, if applicable.
	 *
	 * @return Value as frame-disjunction
	 * @throws KAccessException if not frame-disjunction value
	 */
	public RFrameDisjunction asFrameDisjunction() {

		return as(RFrameDisjunction.class);
	}

	/**
	 * Provides represented value as number, if applicable.
	 *
	 * @return Value as number
	 * @throws KAccessException if not number value
	 */
	public Number asNumber() {

		return as(Number.class);
	}

	/**
	 * Provides represented value as number-range, if applicable.
	 *
	 * @return Value as number-range
	 * @throws KAccessException if not number-range value
	 */
	public RNumberRange asNumberRange() {

		return as(RNumberRange.class);
	}

	/**
	 * Provides represented value as string, if applicable.
	 *
	 * @return Value as string
	 * @throws KAccessException if not string value
	 */
	public String asString() {

		return as(String.class);
	}

	RValue(Object value) {

		this.value = value;
	}

	private <T>T as(Class<T> type) {

		if (type == value.getClass()) {

			return type.cast(value);
		}

		throw new KAccessException(
					"Value not of required type: "
					+ "required type: " + type
					+ ", actual type: " + value.getClass());
	}
}
