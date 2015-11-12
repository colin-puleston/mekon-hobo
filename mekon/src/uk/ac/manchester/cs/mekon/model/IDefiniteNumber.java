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
abstract class IDefiniteNumber<N extends Number> extends ITypeNumber {

	private N value;
	private INumber iNumber = null;

	IDefiniteNumber(N value) {

		this.value = value;
	}

	void setINumber(INumber iNumber) {

		this.iNumber = iNumber;
	}

	int hashCodeValue() {

		return value.hashCode();
	}

	boolean infinite() {

		return false;
	}

	boolean indefinite() {

		return false;
	}

	CNumber getValueType() {

		return new CNumber(getNumberType(), iNumber, iNumber);
	}

	String getDescription() {

		return value.toString();
	}

	Number asTypeNumber() {

		return value;
	}

	BigDecimal asBigDecimal() {

		return toBigDecimal(value);
	}

	abstract BigDecimal toBigDecimal(N value);

	boolean equalTo(ITypeNumber other) {

		if (other.infinite() || other.indefinite()) {

			return other.equalTo(this);
		}

		return compareTo(other) == 0;
	}

	boolean lessThan(ITypeNumber other) {

		if (other.infinite() || other.indefinite()) {

			return other.moreThan(this);
		}

		return compareTo(other) == -1;
	}

	boolean moreThan(ITypeNumber other) {

		if (other.infinite() || other.indefinite()) {

			return other.lessThan(this);
		}

		return compareTo(other) == 1;
	}

	private int compareTo(ITypeNumber other) {

		return asBigDecimal().compareTo(other.asBigDecimal());
	}
}
