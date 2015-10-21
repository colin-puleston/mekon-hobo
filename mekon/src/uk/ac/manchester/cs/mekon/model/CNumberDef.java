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

import uk.ac.manchester.cs.mekon.*;

/**
 * Abstract base-class for classes that define {@link CNumber}
 * objects that are to be created for specific <code>Number</code>
 * types.
 *
 * @author Colin Puleston
 */
public class CNumberDef {

	/**
	 * Creates a number-type definition with the specified limits.
	 *
	 * @param min Minimnum value for number-type
	 * @param max Maximnum value for number-type
	 * @return Created definition
	 * @throws KModelException if minimnum value is greater than
	 * maximnum value, or if minimnum and maximnum values have
	 * incompatible number-types
	 */
	static public CNumberDef range(INumber min, INumber max) {

		return new CNumberDef(getRangeNumberType(min, max), min, max);
	}

	/**
	 * Creates a number-type definition with the specified minimum
	 * value.
	 *
	 * @param min Minimnum value for number-type
	 * @return Created definition
	 */
	static public CNumberDef min(INumber min) {

		return new CNumberDef(min.getNumberType(), min, INumber.PLUS_INFINITY);
	}

	/**
	 * Creates a number-type definition with the specified maximum
	 * value.
	 *
	 * @param max Maximnum value for number-type
	 * @return Created definition
	 */
	static public CNumberDef max(INumber max) {

		return new CNumberDef(max.getNumberType(), INumber.MINUS_INFINITY, max);
	}

	static private Class<? extends Number> getRangeNumberType(INumber min, INumber max) {

		Class<? extends Number> minType = min.getNumberType();
		Class<? extends Number> maxType = max.getNumberType();

		if (minType == Number.class) {

			return maxType;
		}

		if (maxType == Number.class || minType == maxType) {

			return minType;
		}

		throw new KModelException(
					"Incompatible numeric limit types: "
					+ "minimum-type = " + minType
					+ ", maximum-type = " + maxType);
	}

	private Class<? extends Number> numberType;

	private INumber min;
	private INumber max;

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return getClass().getSimpleName() + "(min = " + min + ", max = " + max + ")";
	}

	/**
	 * Provides the primitive Java <code>Number</code> type for the
	 * numeric-type.
	 *
	 * @return Relevant <code>Number</code> type
	 */
	public Class<? extends Number> getNumberType() {

		return numberType;
	}

	/**
	 * Provides the minimnum value for the numeric-type (if no minimnum
	 * value has been defined this will be {@link INumber#MINUS_INFINITY}).
	 *
	 * @return Minimnum value for the numeric-type
	 */
	public INumber getMin() {

		return min;
	}

	/**
	 * Provides the maximnum value for the numeric-type (if no maximnum
	 * value has been defined this will be {@link INumber#PLUS_INFINITY}).
	 *
	 * @return Maximnum value for the numeric-type
	 */
	public INumber getMax() {

		return max;
	}

	/**
	 * Creates the numeric-type defined by this object.
	 *
	 * @return Defined numeric-type
	 */
	public CNumber createNumber() {

		return new CNumber(numberType, min, max);
	}

	CNumberDef(Class<? extends Number> numberType, INumber min, INumber max) {

		this.numberType = numberType;

		this.min = min;
		this.max = max;

		validateRange();

		checkDefiniteLimit(min, "Minimum");
		checkDefiniteLimit(max, "Maximum");
	}

	private void validateRange() {

		if (min.moreThan(max)) {

			throw new KModelException(
						"Illegal numeric limits: "
						+ "minimum = " + min
						+ ", maximum = " + max);
		}
	}

	private void checkDefiniteLimit(INumber limit, String limitName) {

		if (limit.indefinite()) {

			throw new KModelException(
						limitName
						+ "-value cannot be indefinite value: "
						+ limit);
		}
	}
}
