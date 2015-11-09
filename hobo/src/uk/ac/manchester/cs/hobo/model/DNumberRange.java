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

	private Class<N> numberType;
	private CNumber cValue;

	/**
	 * Specifies whether a minimnum value is defined.
	 *
	 * @return True if min value defined
	 */
	public boolean hasMin() {

		return cValue.hasMin();
	}

	/**
	 * Specifies whether a maximnum value is defined.
	 *
	 * @return True if max value defined
	 */
	public boolean hasMax() {

		return cValue.hasMax();
	}

	/**
	 * Provides the minimnum value for the numeric-type, if defined.
	 *
	 * @return Minimnum value for the numeric-type, or null in not
	 * defined
	 */
	public N getMin() {

		return toLimitValue(cValue.getMin());
	}

	/**
	 * Provides the maximnum value for the numeric-type, if defined.
	 *
	 * @return Maximnum value for the numeric-type, or null in not
	 * defined
	 */
	public N getMax() {

		return toLimitValue(cValue.getMax());
	}

	/**
	 * Provides the Frames Model (FM) representation of the range.
	 *
	 * @return FM representation of range
	 */
	public CNumber asCNumber() {

		return cValue;
	}

	DNumberRange(Class<N> numberType, CNumber cValue) {

		this.numberType = numberType;
		this.cValue = cValue;
	}

	private N toLimitValue(INumber iValue) {

		return iValue.infinite() ? null : asTypeNumber(iValue);
	}

	private N asTypeNumber(INumber iValue) {

		return numberType.cast(iValue.asTypeNumber());
	}
}
