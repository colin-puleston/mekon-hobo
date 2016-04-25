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

	private abstract class Operations {

		abstract boolean equalToOther();

		abstract boolean lessThanOther();

		abstract boolean moreThanOther();

		abstract ITypeNumber addOther();

		abstract ITypeNumber subtractOther();

		abstract ITypeNumber multiplyByOther();

		abstract ITypeNumber divideByOther();
	}

	private class DefiniteOperations extends Operations {

		private IDefiniteNumber<?> other;

		DefiniteOperations(IDefiniteNumber<?> other) {

			this.other = other;
		}

		boolean equalToOther() {

			return comparesAs(0);
		}

		boolean lessThanOther() {

			return comparesAs(-1);
		}

		boolean moreThanOther() {

			return comparesAs(1);
		}

		ITypeNumber addOther() {

			return toDefiniteNumber(asBigDecimal().add(other.asBigDecimal()));
		}

		ITypeNumber subtractOther() {

			return toDefiniteNumber(asBigDecimal().subtract(other.asBigDecimal()));
		}

		ITypeNumber multiplyByOther() {

			return toDefiniteNumber(asBigDecimal().multiply(other.asBigDecimal()));
		}

		ITypeNumber divideByOther() {

			return toDefiniteNumber(asBigDecimal().divide(other.asBigDecimal()));
		}

		private boolean comparesAs(int requiredCompValue) {

			return asBigDecimal().compareTo(other.asBigDecimal()) == requiredCompValue;
		}
	}

	private class NonDefiniteOperations extends Operations {

		private ITypeNumber other;

		NonDefiniteOperations(ITypeNumber other) {

			this.other = other;
		}

		boolean equalToOther() {

			return other.equalTo(IDefiniteNumber.this);
		}

		boolean lessThanOther() {

			return other.moreThan(IDefiniteNumber.this);
		}

		boolean moreThanOther() {

			return other.lessThan(IDefiniteNumber.this);
		}

		ITypeNumber addOther() {

			return other.add(IDefiniteNumber.this);
		}

		ITypeNumber subtractOther() {

			return other.subtract(IDefiniteNumber.this);
		}

		ITypeNumber multiplyByOther() {

			return other.multiplyBy(IDefiniteNumber.this);
		}

		ITypeNumber divideByOther() {

			return other.divideBy(IDefiniteNumber.this);
		}
	}

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

	Integer asInteger() {

		return asBigDecimal().intValue();
	}

	Long asLong() {

		return asBigDecimal().longValue();
	}

	Float asFloat() {

		return asBigDecimal().floatValue();
	}

	Double asDouble() {

		return asBigDecimal().doubleValue();
	}

	abstract BigDecimal toBigDecimal(N value);

	boolean equalTo(ITypeNumber other) {

		return getOperations(other).equalToOther();
	}

	boolean lessThan(ITypeNumber other) {

		return getOperations(other).lessThanOther();
	}

	boolean moreThan(ITypeNumber other) {

		return getOperations(other).moreThanOther();
	}

	ITypeNumber add(ITypeNumber other) {

		return getOperations(other).addOther();
	}

	ITypeNumber subtract(ITypeNumber other) {

		return getOperations(other).subtractOther();
	}

	ITypeNumber multiplyBy(ITypeNumber other) {

		return getOperations(other).multiplyByOther();
	}

	ITypeNumber divideBy(ITypeNumber other) {

		return getOperations(other).divideByOther();
	}

	abstract IDefiniteNumber<N> toDefiniteNumber(BigDecimal value);

	private Operations getOperations(ITypeNumber other) {

		return other instanceof IDefiniteNumber
				? new DefiniteOperations((IDefiniteNumber<?>)other)
				: new NonDefiniteOperations(other);
	}

	private BigDecimal asBigDecimal() {

		return toBigDecimal(value);
	}
}
