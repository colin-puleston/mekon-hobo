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

package uk.ac.manchester.cs.hobo.model;

import uk.ac.manchester.cs.hobo.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * Represents a numeric range.
 *
 * @author Colin Puleston
 */
public class DNumberRange<N extends Number> {

	/**
	 * Represents an unconstrained integer-range.
	 */
	static public final DNumberRange<Integer> INTEGER
							= createInteger(CNumberFactory.INTEGER);

	/**
	 * Represents an unconstrained long-range.
	 */
	static public final DNumberRange<Long> LONG
							= createLong(CNumberFactory.LONG);

	/**
	 * Represents an unconstrained float-range.
	 */
	static public final DNumberRange<Float> FLOAT
							= createFloat(CNumberFactory.FLOAT);

	/**
	 * Represents an unconstrained double-range.
	 */
	static public final DNumberRange<Double> DOUBLE
							= createDouble(CNumberFactory.DOUBLE);

	/**
	 * Creates a integer-range with the specified limits.
	 *
	 * @param min Minimum value for range
	 * @param max Maximum value for range
	 * @return Created range
	 */
	static public DNumberRange<Integer> range(Integer min, Integer max) {

		return createInteger(CNumberFactory.range(min, max));
	}

	/**
	 * Creates a long-range with the specified limits.
	 *
	 * @param min Minimum value for range
	 * @param max Maximum value for range
	 * @return Created range
	 */
	static public DNumberRange<Long> range(Long min, Long max) {

		return createLong(CNumberFactory.range(min, max));
	}

	/**
	 * Creates a float-range with the specified limits.
	 *
	 * @param min Minimum value for range
	 * @param max Maximum value for range
	 * @return Created range
	 */
	static public DNumberRange<Float> range(Float min, Float max) {

		return createFloat(CNumberFactory.range(min, max));
	}

	/**
	 * Creates a double-range with the specified limits.
	 *
	 * @param min Minimum value for range
	 * @param max Maximum value for range
	 * @return Created range
	 */
	static public DNumberRange<Double> range(Double min, Double max) {

		return createDouble(CNumberFactory.range(min, max));
	}

	/**
	 * Creates a integer-range with the specified minimum value.
	 *
	 * @param min Minimum value for range
	 * @return Created range
	 */
	static public DNumberRange<Integer> min(Integer min) {

		return createInteger(CNumberFactory.min(min));
	}

	/**
	 * Creates a long-range with the specified minimum value.
	 *
	 * @param min Minimum value for range
	 * @return Created range
	 */
	static public DNumberRange<Long> min(Long min) {

		return createLong(CNumberFactory.min(min));
	}


	/**
	 * Creates a float-range with the specified minimum value.
	 *
	 * @param min Minimum value for range
	 * @return Created range
	 */
	static public DNumberRange<Float> min(Float min) {

		return createFloat(CNumberFactory.min(min));
	}


	/**
	 * Creates a double-range with the specified minimum value.
	 *
	 * @param min Minimum value for range
	 * @return Created range
	 */
	static public DNumberRange<Double> min(Double min) {

		return createDouble(CNumberFactory.min(min));
	}

	/**
	 * Creates a integer-range with the specified maximum value.
	 *
	 * @param max Maximum value for range
	 * @return Created range
	 */
	static public DNumberRange<Integer> max(Integer max) {

		return createInteger(CNumberFactory.max(max));
	}

	/**
	 * Creates a long-range with the specified maximum value.
	 *
	 * @param max Maximum value for range
	 * @return Created range
	 */
	static public DNumberRange<Long> max(Long max) {

		return createLong(CNumberFactory.max(max));
	}

	/**
	 * Creates a float-range with the specified maximum value.
	 *
	 * @param max Maximum value for range
	 * @return Created range
	 */
	static public DNumberRange<Float> max(Float max) {

		return createFloat(CNumberFactory.max(max));
	}

	/**
	 * Creates a double-range with the specified maximum value.
	 *
	 * @param max Maximum value for range
	 * @return Created range
	 */
	static public DNumberRange<Double> max(Double max) {

		return createDouble(CNumberFactory.max(max));
	}

	/**
	 * Creates a integer-range with the specified exact value.
	 *
	 * @param exact Exact value for range
	 * @return Created range
	 */
	static public DNumberRange<Integer> exact(Integer exact) {

		return createInteger(CNumberFactory.exact(exact));
	}

	/**
	 * Creates a long-range with the specified exact value.
	 *
	 * @param exact Exact value for range
	 * @return Created range
	 */
	static public DNumberRange<Long> exact(Long exact) {

		return createLong(CNumberFactory.exact(exact));
	}

	/**
	 * Creates a float-range with the specified exact value.
	 *
	 * @param exact Exact value for range
	 * @return Created range
	 */
	static public DNumberRange<Float> exact(Float exact) {

		return createFloat(CNumberFactory.exact(exact));
	}

	/**
	 * Creates a double-range with the specified exact value.
	 *
	 * @param exact Exact value for range
	 * @return Created range
	 */
	static public DNumberRange<Double> exact(Double exact) {

		return createDouble(CNumberFactory.exact(exact));
	}

	static private DNumberRange<Integer> createInteger(CNumber cNumber) {

		return new DNumberRange<Integer>(Integer.class, cNumber);
	}

	static private DNumberRange<Long> createLong(CNumber cNumber) {

		return new DNumberRange<Long>(Long.class, cNumber);
	}

	static private DNumberRange<Float> createFloat(CNumber cNumber) {

		return new DNumberRange<Float>(Float.class, cNumber);
	}

	static private DNumberRange<Double> createDouble(CNumber cNumber) {

		return new DNumberRange<Double>(Double.class, cNumber);
	}

	private Class<N> numberType;
	private CNumber cNumber;

	/**
	 * Constructs range from the Frames Model (FM) representation.
	 *
	 * @param numberType Primitive Java <code>Number</code> type for
	 * range
	 * @param cNumber FM representation of range
	 * @throws HAccessException if specified number-type and number-type
	 * from specified FM range not equal
	 */
	public DNumberRange(Class<N> numberType, CNumber cNumber) {

		this.numberType = numberType;
		this.cNumber = cNumber;

		if (cNumber.getNumberType() != numberType) {

			throw new HAccessException(
						"CNumber number-type does not equal "
						+ "specified number-type");
		}
	}

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>DNumberRange</code>
	 * with same limit-values, including value-type, as this one
	 */
	public boolean equals(Object other) {

		if (other == this) {

			return true;
		}

		if (other instanceof DNumberRange) {

			return cNumber.equals(((DNumberRange)other).cNumber);
		}

		return false;
	}

	/**
	 * Provides hash-code based on number-type and limit-values.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return cNumber.hashCode();
	}

	/**
	 * Provides the primitive Java <code>Number</code> type for the
	 * range.
	 *
	 * @return Relevant <code>Number</code> type
	 */
	public Class<N> getNumberType() {

		return numberType;
	}

	/**
	 * Provides a description of the range, intended to provide
	 * information to the software developer, and not suitable for
	 * displaying to an end-user.
	 *
	 * @return Development-level description of range
	 */
	public String toString() {

		return DNumberRange.class.getSimpleName() + "(" + cNumber.getDisplayLabel() + ")";
	}

	/**
	 * Provides a label for the range, suitable for displaying to
	 * an end-user.
	 *
	 * @return Display-label for range
	 */
	public String getDisplayLabel() {

		return cNumber.getDisplayLabel();
	}

	/**
	 * Specifies whether object represents an exact value, with equal
	 * min and max values.
	 *
	 * @return True if exact value
	 */
	public boolean exactValue() {

		return cNumber.exactValue();
	}

	/**
	 * Specifies whether a minimum value is defined.
	 *
	 * @return True if min value defined
	 */
	public boolean hasMin() {

		return cNumber.hasMin();
	}

	/**
	 * Specifies whether a maximum value is defined.
	 *
	 * @return True if max value defined
	 */
	public boolean hasMax() {

		return cNumber.hasMax();
	}

	/**
	 * Provides the exact value defined by the object, if applicable.
	 *
	 * @return Exact value
	 * @throws HAccessException if not an exact value
	 */
	public N getExactValue() {

		if (exactValue()) {

			return getMin();
		}

		throw new HAccessException("Range-value not an exact value: " + this);
	}

	/**
	 * Provides the minimum value, if defined.
	 *
	 * @return Minimum value, or null in not defined
	 */
	public N getMin() {

		return toLimitValue(cNumber.getMin());
	}

	/**
	 * Provides the maximum value, if defined.
	 *
	 * @return Maximum value, or null in not defined
	 */
	public N getMax() {

		return toLimitValue(cNumber.getMax());
	}

	/**
	 * Provides the Frames Model (FM) representation of the range.
	 *
	 * @return FM representation of range
	 */
	public CNumber asCNumber() {

		return cNumber;
	}

	private N toLimitValue(INumber iValue) {

		return iValue.infinite() ? null : asTypeNumber(iValue);
	}

	private N asTypeNumber(INumber iValue) {

		return numberType.cast(iValue.asTypeNumber());
	}
}
