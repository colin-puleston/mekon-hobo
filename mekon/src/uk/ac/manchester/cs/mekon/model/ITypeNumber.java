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

import java.math.*;

/**
 * @author Colin Puleston
 */
abstract class ITypeNumber {

	void setINumber(INumber iNumber) {
	}

	abstract int hashCodeValue();

	abstract boolean infinite();

	abstract boolean indefinite();

	abstract Class<? extends Number> getNumberType();

	abstract CNumber getValueType();

	abstract String getDescription();

	abstract Number asTypeNumber();

	abstract Integer asInteger();

	abstract Long asLong();

	abstract Float asFloat();

	abstract Double asDouble();

	abstract boolean equalTo(ITypeNumber other);

	abstract boolean lessThan(ITypeNumber other);

	abstract boolean moreThan(ITypeNumber other);

	boolean lessThanOrEqualTo(ITypeNumber other) {

		return !moreThan(other);
	}

	boolean moreThanOrEqualTo(ITypeNumber other) {

		return !lessThan(other);
	}

	boolean undefinedMinMax(ITypeNumber other) {

		if (other.indefinite()) {

			return other.undefinedMinMax(this);
		}

		return false;
	}

	abstract ITypeNumber add(ITypeNumber other);

	abstract ITypeNumber subtract(ITypeNumber other);

	abstract ITypeNumber multiplyBy(ITypeNumber other);

	abstract ITypeNumber divideBy(ITypeNumber other);
}
