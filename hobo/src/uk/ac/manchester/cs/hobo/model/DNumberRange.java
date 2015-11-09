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

import uk.ac.manchester.cs.mekon.model.*;

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
							= createInteger(CIntegerDef.UNCONSTRAINED);

	/**
	 * Represents an unconstrained long-range.
	 */
	static public final DNumberRange<Long> LONG
							= createLong(CLongDef.UNCONSTRAINED);

	/**
	 * Represents an unconstrained float-range.
	 */
	static public final DNumberRange<Float> FLOAT
							= createFloat(CFloatDef.UNCONSTRAINED);

	/**
	 * Represents an unconstrained double-range.
	 */
	static public final DNumberRange<Double> DOUBLE
							= createDouble(CDoubleDef.UNCONSTRAINED);

	/**
	 * Creates a integer-range with the specified limits.
	 *
	 * @param min Minimnum value for range
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public DNumberRange<Integer> range(Integer min, Integer max) {

		return createInteger(CIntegerDef.range(min, max));
	}

	/**
	 * Creates a long-range with the specified limits.
	 *
	 * @param min Minimnum value for range
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public DNumberRange<Long> range(Long min, Long max) {

		return createLong(CLongDef.range(min, max));
	}

	/**
	 * Creates a float-range with the specified limits.
	 *
	 * @param min Minimnum value for range
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public DNumberRange<Float> range(Float min, Float max) {

		return createFloat(CFloatDef.range(min, max));
	}

	/**
	 * Creates a double-range with the specified limits.
	 *
	 * @param min Minimnum value for range
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public DNumberRange<Double> range(Double min, Double max) {

		return createDouble(CDoubleDef.range(min, max));
	}

	/**
	 * Creates a integer-range with the specified minimum value.
	 *
	 * @param min Minimnum value for range
	 * @return Created range
	 */
	static public DNumberRange<Integer> min(Integer min) {

		return createInteger(CIntegerDef.min(min));
	}

	/**
	 * Creates a long-range with the specified minimum value.
	 *
	 * @param min Minimnum value for range
	 * @return Created range
	 */
	static public DNumberRange<Long> min(Long min) {

		return createLong(CLongDef.min(min));
	}


	/**
	 * Creates a float-range with the specified minimum value.
	 *
	 * @param min Minimnum value for range
	 * @return Created range
	 */
	static public DNumberRange<Float> min(Float min) {

		return createFloat(CFloatDef.min(min));
	}


	/**
	 * Creates a double-range with the specified minimum value.
	 *
	 * @param min Minimnum value for range
	 * @return Created range
	 */
	static public DNumberRange<Double> min(Double min) {

		return createDouble(CDoubleDef.min(min));
	}

	/**
	 * Creates a integer-range with the specified maximum value.
	 *
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public DNumberRange<Integer> max(Integer max) {

		return createInteger(CIntegerDef.max(max));
	}

	/**
	 * Creates a long-range with the specified maximum value.
	 *
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public DNumberRange<Long> max(Long max) {

		return createLong(CLongDef.max(max));
	}

	/**
	 * Creates a float-range with the specified maximum value.
	 *
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public DNumberRange<Float> max(Float max) {

		return createFloat(CFloatDef.max(max));
	}

	/**
	 * Creates a double-range with the specified maximum value.
	 *
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public DNumberRange<Double> max(Double max) {

		return createDouble(CDoubleDef.max(max));
	}

	/**
	 * Creates a integer-range with the specified exact value.
	 *
	 * @param exact Exact value for range
	 * @return Created range
	 */
	static public DNumberRange<Integer> exact(Integer exact) {

		return createInteger(CIntegerDef.exact(exact));
	}

	/**
	 * Creates a long-range with the specified exact value.
	 *
	 * @param exact Exact value for range
	 * @return Created range
	 */
	static public DNumberRange<Long> exact(Long exact) {

		return createLong(CLongDef.exact(exact));
	}

	/**
	 * Creates a float-range with the specified exact value.
	 *
	 * @param exact Exact value for range
	 * @return Created range
	 */
	static public DNumberRange<Float> exact(Float exact) {

		return createFloat(CFloatDef.exact(exact));
	}

	/**
	 * Creates a double-range with the specified exact value.
	 *
	 * @param exact Exact value for range
	 * @return Created range
	 */
	static public DNumberRange<Double> exact(Double exact) {

		return createDouble(CDoubleDef.exact(exact));
	}

	static private DNumberRange<Integer> createInteger(CIntegerDef cDefinition) {

		return new DNumberRange<Integer>(Integer.class, cDefinition);
	}

	static private DNumberRange<Long> createLong(CLongDef cDefinition) {

		return new DNumberRange<Long>(Long.class, cDefinition);
	}

	static private DNumberRange<Float> createFloat(CFloatDef cDefinition) {

		return new DNumberRange<Float>(Float.class, cDefinition);
	}

	static private DNumberRange<Double> createDouble(CDoubleDef cDefinition) {

		return new DNumberRange<Double>(Double.class, cDefinition);
	}

	private Class<N> numberType;
	private CNumber cNumber;

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>DNumberRange</code>
	 * with same limit-values, including value-type, as this one
	 */
	public boolean equals(Object other) {

		if (other instanceof DNumberRange) {

			return cNumber.equals(((DNumberRange)other).cNumber);
		}

		return false;
	}

	/**
	 * Provides hash-code based on limit-values, including value-type.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return cNumber.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return getClass().getSimpleName() + cNumber.getDisplayLabel();
	}

	/**
	 * Specifies whether a minimnum value is defined.
	 *
	 * @return True if min value defined
	 */
	public boolean hasMin() {

		return cNumber.hasMin();
	}

	/**
	 * Specifies whether a maximnum value is defined.
	 *
	 * @return True if max value defined
	 */
	public boolean hasMax() {

		return cNumber.hasMax();
	}

	/**
	 * Provides the minimnum value, if defined.
	 *
	 * @return Minimnum value, or null in not defined
	 */
	public N getMin() {

		return toLimitValue(cNumber.getMin());
	}

	/**
	 * Provides the maximnum value, if defined.
	 *
	 * @return Maximnum value, or null in not defined
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

	DNumberRange(Class<N> numberType, CNumber cNumber) {

		this.numberType = numberType;
		this.cNumber = cNumber;
	}

	private DNumberRange(Class<N> numberType, CNumberDef cDefinition) {

		this(numberType, cDefinition.createNumber());
	}

	private N toLimitValue(INumber iValue) {

		return iValue.infinite() ? null : asTypeNumber(iValue);
	}

	private N asTypeNumber(INumber iValue) {

		return numberType.cast(iValue.asTypeNumber());
	}
}
