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

import uk.ac.manchester.cs.mekon.*;

/**
 * Represents a numeric-value, with the actual value being represented
 * by a primitive Java <code>Number</code> value of some type.
 *
 * @author Colin Puleston
 */
public class INumber implements IEntity, IValue {

	/**
	 * Represents a numeric-value of plus-infinity
	 */
	static public final INumber PLUS_INFINITY = new INumber(true);

	/**
	 * Represents a numeric-value of minus-infinity
	 */
	static public final INumber MINUS_INFINITY = new INumber(false);

	/**
	 * Constructs object representing the specified numeric-value
	 * with the relevant primitive Java <code>Number</code> value.
	 *
	 * @param value Numeric-value to be represented
	 * @return Created object
	 */
	static public INumber create(Number value) {

		return getOperations(value.getClass()).toINumber(value);
	}

	/**
	 * Constructs object representing the specified numeric-value
	 * with the specified primitive Java <code>Number</code> type.
	 *
	 * @param type Required primitive Java <code>Number</code> type
	 * @param value String version of numeric-value to be represented
	 * @return Created object
	 */
	static public INumber create(Class<? extends Number> type, String value) {

		return getOperations(type).toINumber(value);
	}

	static private INumberOperations<?> getOperations(Class<? extends Number> type) {

		return getOperationsManager().get(type);
	}

	static private INumberOperationsManager getOperationsManager() {

		return INumberOperationsManager.get();
	}

	private Value value;

	private abstract class Value {

		abstract boolean infinite();

		abstract boolean indefinite();

		abstract Class<? extends Number> getNumberType();

		abstract CNumber getValueType();

		abstract String getDescription();

		abstract Number asTypeNumber();

		abstract BigDecimal asBigDecimal();

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

		abstract boolean equalTo(Value other);

		abstract boolean lessThan(Value other);

		abstract boolean moreThan(Value other);

		abstract int hashCodeValue();

		boolean lessThanOrEqualTo(Value other) {

			return !moreThan(other);
		}

		boolean moreThanOrEqualTo(Value other) {

			return !lessThan(other);
		}

		boolean undefinedMinMax(Value other) {

			if (other.indefinite()) {

				return other.undefinedMinMax(this);
			}

			return false;
		}

		abstract INumber toINumber(BigDecimal value);
	}

	private class FiniteValue<N extends Number> extends Value {

		private INumberOperations<N> operations;
		private N value;

		FiniteValue(INumberOperations<N> operations, N value) {

			this.operations = operations;
			this.value = value;
		}

		boolean infinite() {

			return false;
		}

		boolean indefinite() {

			return false;
		}

		Class<? extends Number> getNumberType() {

			return operations.getNumberType();
		}

		CNumber getValueType() {

			Class<N> numberType = operations.getNumberType();

			return new CNumber(numberType, INumber.this, INumber.this);
		}

		String getDescription() {

			return value.toString();
		}

		Number asTypeNumber() {

			return value;
		}

		BigDecimal asBigDecimal() {

			return operations.toBigDecimal(value);
		}

		INumber toINumber(BigDecimal value) {

			return operations.toINumber(value);
		}

		boolean equalTo(Value other) {

			if (other.infinite() || other.indefinite()) {

				return other.equalTo(this);
			}

			return compareTo(other) == 0;
		}

		boolean lessThan(Value other) {

			if (other.infinite() || other.indefinite()) {

				return other.moreThan(this);
			}

			return compareTo(other) == -1;
		}

		boolean moreThan(Value other) {

			if (other.infinite() || other.indefinite()) {

				return other.lessThan(this);
			}

			return compareTo(other) == 1;
		}

		int hashCodeValue() {

			return value.hashCode();
		}

		private int compareTo(Value other) {

			return asBigDecimal().compareTo(other.asBigDecimal());
		}
	}

	private abstract class InfiniteValue extends Value {

		static private final String NAME_BODY = "-INFINITY";

		boolean infinite() {

			return true;
		}

		boolean indefinite() {

			return false;
		}

		Class<? extends Number> getNumberType() {

			return Number.class;
		}

		CNumber getValueType() {

			throw createInvalidOperationException();
		}

		String getDescription() {

			return getNamePrefix() + NAME_BODY;
		}

		Number asTypeNumber() {

			throw createInvalidOperationException();
		}

		BigDecimal asBigDecimal() {

			throw createInvalidOperationException();
		}

		boolean equalTo(Value other) {

			return this == other;
		}

		int hashCodeValue() {

			return asDouble().hashCode();
		}

		INumber toINumber(BigDecimal value) {

			throw createInvalidOperationException();
		}

		abstract String getNamePrefix();

		private KAccessException createInvalidOperationException() {

			return new KAccessException(
						"Cannot perform operation on infinite value: "
						+ INumber.this);
		}
	}

	private class PositiveInfiniteValue extends InfiniteValue {

		static private final String NAME_PREFIX = "PLUS";

		Integer asInteger() {

			return Integer.MAX_VALUE;
		}

		Long asLong() {

			return Long.MAX_VALUE;
		}

		Float asFloat() {

			return Float.MAX_VALUE;
		}

		Double asDouble() {

			return Double.MAX_VALUE;
		}

		boolean lessThan(Value other) {

			return false;
		}

		boolean moreThan(Value other) {

			return this != other;
		}

		String getNamePrefix() {

			return NAME_PREFIX;
		}
	}

	private class NegativeInfiniteValue extends InfiniteValue {

		static private final String NAME_PREFIX = "MINUS";

		Integer asInteger() {

			return Integer.MIN_VALUE;
		}

		Long asLong() {

			return Long.MIN_VALUE;
		}

		Float asFloat() {

			return Float.MIN_VALUE;
		}

		Double asDouble() {

			return Double.MIN_VALUE;
		}

		boolean lessThan(Value other) {

			return this != other;
		}

		boolean moreThan(Value other) {

			return false;
		}

		String getNamePrefix() {

			return NAME_PREFIX;
		}
	}

	private class IndefiniteValue extends Value {

		private CNumber valueType;


		IndefiniteValue(CNumber valueType) {

			this.valueType = valueType;
		}

		boolean infinite() {

			return false;
		}

		boolean indefinite() {

			return true;
		}

		Class<? extends Number> getNumberType() {

			return valueType.getNumberType();
		}

		CNumber getValueType() {

			return valueType;
		}

		String getDescription() {

			return valueType.getLimitsString();
		}

		Number asTypeNumber() {

			throw createInvalidOperationException();
		}

		BigDecimal asBigDecimal() {

			throw createInvalidOperationException();
		}

		INumber toINumber(BigDecimal value) {

			throw createInvalidOperationException();
		}

		boolean equalTo(Value other) {

			return valueType.equals(other.getValueType());
		}

		boolean lessThan(Value other) {

			return strictOrder(this, other);
		}

		boolean moreThan(Value other) {

			return strictOrder(other, this);
		}

		boolean lessThanOrEqualTo(Value other) {

			return lessThan(other) || maxMinEqual(this, other);
		}

		boolean moreThanOrEqualTo(Value other) {

			return moreThan(other) || maxMinEqual(other, this);
		}

		boolean undefinedMinMax(Value other) {

			return !equalTo(other) && !lessThan(other) && !moreThan(other);
		}

		int hashCodeValue() {

			return valueType.hashCode();
		}

		private boolean strictOrder(Value one, Value two) {

			return getMax(one).lessThan(getMin(two));
		}

		private boolean maxMinEqual(Value one, Value two) {

			return getMax(one).equalTo(getMin(two));
		}

		private INumber getMin(Value value) {

			return value.getValueType().getMin();
		}

		private INumber getMax(Value value) {

			return value.getValueType().getMax();
		}

		private KAccessException createInvalidOperationException() {

			return new KAccessException(
						"Cannot perform operation on indefinite value: "
						+ INumber.this);
		}
	}

	/**
	 * Constructs object representing the specified integer-value.
	 *
	 * @param value Integer-value to be represented
	 */
	public INumber(Integer value) {

		this(getOperationsManager().integerOperations, value);
	}

	/**
	 * Constructs object representing the specified long-value.
	 *
	 * @param value Long-value to be represented
	 */
	public INumber(Long value) {

		this(getOperationsManager().longOperations, value);
	}

	/**
	 * Constructs object representing the specified float-value.
	 *
	 * @param value Float-value to be represented
	 */
	public INumber(Float value) {

		this(getOperationsManager().floatOperations, value);
	}

	/**
	 * Constructs object representing the specified double-value.
	 *
	 * @param value Double-value to be represented
	 */
	public INumber(Double value) {

		this(getOperationsManager().doubleOperations, value);
	}

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>INumber</code>
	 * with the same primitive Java <code>Number</code> type as this
	 * one, and representing the same numeric-value.
	 */
	public boolean equals(Object other) {

		if (other instanceof INumber) {

			INumber n = (INumber)other;

			return getNumberType().equals(n.getNumberType()) && equalTo(n);
		}

		return false;
	}

	/**
	 * Provides hash-code based on numeric-value.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return value.hashCodeValue();
	}

	/**
	 * Specifies whether this object represents an infinite value
	 * (will be true if and only if the object equals either
	 * {@link #PLUS_INFINITY} or {@link #MINUS_INFINITY}).
	 *
	 * @return True if object represents infinite value
	 */
	public boolean infinite() {

		return value.infinite();
	}

	/**
	 * Specifies whether this object represents an indefinite
	 * value, for which only the <code>Number</code>-type and
	 * range are specified.
	 *
	 * @return True if object represents indefinite value
	 */
	public boolean indefinite() {

		return value.indefinite();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return FEntityDescriber.entityToString(this, value.getDescription());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayLabel() {

		return value.getDescription();
	}

	/**
	 * Provides the most specific numeric value-type of which
	 * this numeric-value is an instance, which will be one
	 * where both the minimum and maximum values are equal to
	 * the value represented by this numeric-value.
	 *
	 * @return Relevant numeric value-type
	 */
	public CNumber getType() {

		return value.getValueType();
	}

	/**
	 * Stipulates that this frame is abstract if and only if it is
	 * an {@link #indefinite} number.
	 *
	 * @return True if indefinite number
	 */
	public boolean abstractValue() {

		return indefinite();
	}

	/**
	 * Provides the primitive Java <code>Number</code> type of the
	 * numeric-value.
	 *
	 * @return Relevant <code>Number</code> type
	 */
	public Class<? extends Number> getNumberType() {

		return value.getNumberType();
	}

	/**
	 * Tests whether the primitive Java <code>Number</code> type
	 * of the numeric-value is equal to a specified type.
	 *
	 * @param testNumberType <code>Number</code> type to test for
	 * @return True if numeric-value is of specified type
	 */
	public boolean hasNumberType(Class<? extends Number> testNumberType) {

		return testNumberType == getNumberType();
	}

	/**
	 * Tests for equality between the numeric-values represented
	 * by this and another specified <code>INumber</code> object
	 * (regardless of the primitive Java <code>Number</code> type
	 * of the represented value).
	 *
	 * @param other Object to test for value-equality with this one
	 * @return true if represented numeric-values are equal
	 */
	public boolean equalTo(INumber other) {

		return value.equalTo(other.value);
	}

	/**
	 * Tests whether this numeric-value is strictly less-than the
	 * other specified numeric-value.
	 *
	 * @param other Other numeric-value to compare to this one
	 * @return true if other numeric-value is strictly less than
	 * this one
	 */
	public boolean lessThan(INumber other) {

		return value.lessThan(other.value);
	}

	/**
	 * Tests whether this numeric-value is less-than-or-equal-to
	 * the other specified numeric-value.
	 *
	 * @param other Other numeric-value to compare to this one
	 * @return true if other numeric-value is less-than-or-equal-to
	 * this one
	 */
	public boolean lessThanOrEqualTo(INumber other) {

		return value.lessThanOrEqualTo(other.value);
	}

	/**
	 * Tests whether this numeric-value is strictly greater-than the
	 * other specified numeric-value.
	 *
	 * @param other Other numeric-value to compare to this one
	 * @return true if other numeric-value is strictly greater than
	 * this one
	 */
	public boolean moreThan(INumber other) {

		return value.moreThan(other.value);
	}

	/**
	 * Tests whether this numeric-value is greater-than-or-equal-to
	 * the other specified numeric-value.
	 *
	 * @param other Other numeric-value to compare to this one
	 * @return true if other numeric-value is greater-than-or-equal-to
	 * this one
	 */
	public boolean moreThanOrEqualTo(INumber other) {

		return value.moreThanOrEqualTo(other.value);
	}

	/**
	 * Provides the minimum between this and another numeric-value.
	 *
	 * @param other Other numeric-value to compare to this one
	 * @return Minimum of the two numeric-values, or null if there is no
	 * defined minimum (which can happen if either of the values is
	 * {@link #indefinite}
	 */
	public INumber min(INumber other) {

		if (undefinedMinMax(other)) {

			return null;
		}

		return lessThan(other) ? this : other;
	}

	/**
	 * Provides the maximum between this and another numeric-value.
	 *
	 * @param other Other numeric-value to compare to this one
	 * @return Maximum of the two numeric-values, or null if there is no
	 * defined maximum (which can happen if either of the values is
	 * {@link #indefinite}
	 */
	public INumber max(INumber other) {

		if (undefinedMinMax(other)) {

			return null;
		}

		return moreThan(other) ? this : other;
	}

	/**
	 * Adds the other specified numeric-value to this one.
	 *
	 * @param other Other numeric-value to add to this one
	 * @return Result of addition operation
	 */
	public INumber add(INumber other) {

		return toINumber(asBigDecimal().add(other.asBigDecimal()));
	}

	/**
	 * Subtracts the other specified numeric-value from this one.
	 *
	 * @param other Other numeric-value to subtract from this one
	 * @return Result of subtraction operation
	 */
	public INumber subtract(INumber other) {

		return toINumber(asBigDecimal().subtract(other.asBigDecimal()));
	}

	/**
	 * Multiplies this value by the other specified numeric-value.
	 *
	 * @param other Other numeric-value by which to multiply this one
	 * @return Result of multiplication operation
	 */
	public INumber multiplyBy(INumber other) {

		return toINumber(asBigDecimal().multiply(other.asBigDecimal()));
	}

	/**
	 * Divides this value by the other specified numeric-value.
	 *
	 * @param other Other numeric-value by which to divide this one
	 * @return Result of division operation
	 */
	public INumber divideBy(INumber other) {

		return toINumber(asBigDecimal().divide(other.asBigDecimal()));
	}

	/**
	 * Provides numeric-value in the form of the primitive Java
	 * <code>Number</code> type that was used in constructing this
	 * object.
	 *
	 * @return Numeric-value as relevant type
	 */
	public Number asTypeNumber() {

		return value.asTypeNumber();
	}

	/**
	 * Provides numeric-value in the form of an <code>Integer</code>
	 * type object, performing any necessary rounding.
	 *
	 * @return Numeric-value as integer
	 */
	public Integer asInteger() {

		return value.asInteger();
	}

	/**
	 * Provides numeric-value in the form of a <code>Long</code>
	 * type object, performing any necessary rounding.
	 *
	 * @return Numeric-value as long
	 */
	public Long asLong() {

		return value.asLong();
	}

	/**
	 * Provides numeric-value in the form of a <code>Float</code>
	 * type object.
	 *
	 * @return Numeric-value as float
	 */
	public Float asFloat() {

		return value.asFloat();
	}

	/**
	 * Provides numeric-value in the form of a <code>Double</code>
	 * type object.
	 *
	 * @return Numeric-value as float
	 */
	public Double asDouble() {

		return value.asDouble();
	}

	INumber(CNumber valueType) {

		value = new IndefiniteValue(valueType);
	}

	CNumber toExactType() {

		return new CNumber(getNumberType(), this, this);
	}

	private <N extends Number>INumber(
								INumberOperations<N> operations,
								N numberValue) {

		value = new FiniteValue<N>(operations, numberValue);
	}

	private INumber(boolean positiveInfinity) {

		value = createInfiniteValue(positiveInfinity);
	}

	private InfiniteValue createInfiniteValue(boolean positive) {

		return positive
				? new PositiveInfiniteValue()
				: new NegativeInfiniteValue();
	}

	private boolean undefinedMinMax(INumber other) {

		return value.undefinedMinMax(other.value);
	}

	private BigDecimal asBigDecimal() {

		return value.asBigDecimal();
	}

	private INumber toINumber(BigDecimal value) {

		return this.value.toINumber(value);
	}
}
