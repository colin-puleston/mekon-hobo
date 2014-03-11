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
public abstract class CNumberDef {

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

		if (min.moreThan(max)) {

			throw new KModelException(
						"Illegal numeric limits: "
						+ "(minimum = " + min
						+ ", maximum = " + max + ")");
		}
	}
}
