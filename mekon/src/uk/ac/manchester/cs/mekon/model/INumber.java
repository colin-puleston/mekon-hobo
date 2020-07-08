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

/**
 * Represents a numeric-value, with the actual value being represented
 * by a primitive Java <code>Number</code> value of some type.
 *
 * @author Colin Puleston
 */
public class INumber extends IDataValue {

	/**
	 * Represents a numeric-value of plus-infinity
	 */
	static public final INumber PLUS_INFINITY = new INumber(new IPlusInfinity());

	/**
	 * Represents a numeric-value of minus-infinity
	 */
	static public final INumber MINUS_INFINITY = new INumber(new IMinusInfinity());

	/**
	 * Constructs object representing the specified numeric-range
	 * with the specified primitive Java <code>Number</code> type.
	 * Unless the minimum and maximum values are equal then the
	 * constructed object will represent an {@link INumber#indefinite}
	 * value.
	 *
	 * @param numberType Required primitive Java <code>Number</code> type
	 * @param min Minimnum range value
	 * @param max Maximnum range value
	 */
	static public INumber range(
							Class<? extends Number> numberType,
							INumber min,
							INumber max) {

		return new CNumber(numberType, min, max).asINumber();
	}

	static INumber indefiniteRange(CNumber range) {

		return new INumber(new IIndefiniteNumber(range));
	}

	private ITypeNumber typeNumber;

	/**
	 * Constructs object representing the specified numeric-value
	 * with the relevant primitive Java <code>Number</code> value.
	 *
	 * @param value Numeric-value to be represented
	 */
	public INumber(Number value) {

		this(IDefiniteNumberCreator.get().create(value));
	}

	/**
	 * Constructs object representing the specified numeric-value
	 * with the specified primitive Java <code>Number</code> type.
	 *
	 * @param numberType Required primitive Java <code>Number</code> type
	 * @param value String version of numeric-value to be represented
	 */
	public INumber(Class<? extends Number> numberType, String value) {

		this(IDefiniteNumberCreator.get().create(numberType, value));
	}

	/**
	 * Tests for equality between this and other specified object,
	 * which will hold if and only if the other object is another
	 * <code>INumber</code> with the same primitive Java
	 * <code>Number</code> type as this one, and representing the
	 * same numeric-value.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if objects are equal
	 */
	public boolean equals(Object other) {

		return other instanceof INumber && equalsNumber((INumber)other);
	}

	/**
	 * Provides hash-code based on the numeric-value.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return typeNumber.hashCodeValue();
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

		return typeNumber.getValueType();
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
	 * Tests whether this value-entity subsumes another specified
	 * value-entity, which will be the case if and only if the other
	 * value-entity is another <code>INumber</code> object, whose
	 * type object (see {@link #getType}) is subsumed by that of this
	 * one. If this is a definite number then subsumption is equivalent
	 * to equality, whereas if it is an indefinite number subsumption
	 * holds if the number represented by the other object (definite or
	 * indefinite) falls within the relevant range.
	 *
	 * @param other Other value-entity to test for subsumption
	 * @return True if this value-entity subsumes other value-entity
	 */
	public boolean subsumes(IValue other) {

		return other instanceof INumber && subsumesNumber((INumber)other);
	}

	/**
	 * Specifies whether this object represents an infinite value
	 * (will be true if and only if the object equals either
	 * {@link #PLUS_INFINITY} or {@link #MINUS_INFINITY}).
	 *
	 * @return True if object represents infinite value
	 */
	public boolean infinite() {

		return typeNumber.infinite();
	}

	/**
	 * Specifies whether this object represents an indefinite
	 * value, for which only the <code>Number</code>-type and
	 * range are specified.
	 *
	 * @return True if object represents indefinite value
	 */
	public boolean indefinite() {

		return typeNumber.indefinite();
	}

	/**
	 * Specifies whether this object represents a definite
	 * value.
	 *
	 * @return True if object represents definite value
	 */
	public boolean definite() {

		return !infinite() && !indefinite();
	}

	/**
	 * Provides the primitive Java <code>Number</code> type of the
	 * numeric-value.
	 *
	 * @return Relevant <code>Number</code> type
	 */
	public Class<? extends Number> getNumberType() {

		return typeNumber.getNumberType();
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

		return typeNumber.equalTo(other.typeNumber);
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

		return typeNumber.lessThan(other.typeNumber);
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

		return typeNumber.lessThanOrEqualTo(other.typeNumber);
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

		return typeNumber.moreThan(other.typeNumber);
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

		return typeNumber.moreThanOrEqualTo(other.typeNumber);
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

		return new INumber(typeNumber.add(other.typeNumber));
	}

	/**
	 * Subtracts the other specified numeric-value from this one.
	 *
	 * @param other Other numeric-value to subtract from this one
	 * @return Result of subtraction operation
	 */
	public INumber subtract(INumber other) {

		return new INumber(typeNumber.subtract(other.typeNumber));
	}

	/**
	 * Multiplies this value by the other specified numeric-value.
	 *
	 * @param other Other numeric-value by which to multiply this one
	 * @return Result of multiplication operation
	 */
	public INumber multiplyBy(INumber other) {

		return new INumber(typeNumber.multiplyBy(other.typeNumber));
	}

	/**
	 * Divides this value by the other specified numeric-value.
	 *
	 * @param other Other numeric-value by which to divide this one
	 * @return Result of division operation
	 */
	public INumber divideBy(INumber other) {

		return new INumber(typeNumber.divideBy(other.typeNumber));
	}

	/**
	 * Provides numeric-value in the form of the primitive Java
	 * <code>Number</code> type that was used in constructing this
	 * object.
	 *
	 * @return Numeric-value as relevant type
	 */
	public Number asTypeNumber() {

		return typeNumber.asTypeNumber();
	}

	/**
	 * Provides numeric-value in the form of an <code>Integer</code>
	 * type object, performing any necessary rounding.
	 *
	 * @return Numeric-value as integer
	 */
	public Integer asInteger() {

		return typeNumber.asInteger();
	}

	/**
	 * Provides numeric-value in the form of a <code>Long</code>
	 * type object, performing any necessary rounding.
	 *
	 * @return Numeric-value as long
	 */
	public Long asLong() {

		return typeNumber.asLong();
	}

	/**
	 * Provides numeric-value in the form of a <code>Float</code>
	 * type object.
	 *
	 * @return Numeric-value as float
	 */
	public Float asFloat() {

		return typeNumber.asFloat();
	}

	/**
	 * Provides numeric-value in the form of a <code>Double</code>
	 * type object.
	 *
	 * @return Numeric-value as float
	 */
	public Double asDouble() {

		return typeNumber.asDouble();
	}

	CNumber normaliseValueTypeTo(CNumber type) {

		return typeNumber.normaliseValueTypeTo(type);
	}

	String getDataValueDescription() {

		return typeNumber.getDescription();
	}

	private INumber(ITypeNumber typeNumber) {

		this.typeNumber = typeNumber;

		typeNumber.setINumber(this);
	}

	private boolean equalsNumber(INumber other) {

		return getNumberType().equals(other.getNumberType()) && equalTo(other);
	}

	private boolean subsumesNumber(INumber other) {

		return getType().subsumes(other.getType());
	}

	private boolean undefinedMinMax(INumber other) {

		return typeNumber.undefinedMinMax(other.typeNumber);
	}
}
