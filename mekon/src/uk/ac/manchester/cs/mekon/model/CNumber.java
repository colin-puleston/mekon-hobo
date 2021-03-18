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

import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * Represents a numeric value-type, defined via a primitive Java
 * <code>Number</code> type and specific numeric range.
 *
 * @author Colin Puleston
 */
public class CNumber extends CDataValue<INumber> {

	private Class<? extends Number> numberType;

	private INumber min;
	private INumber max;

	/**
	 * Tests for equality between this and other specified object,
	 * which will hold if and only if the other object is another
	 * <code>CNumber</code> with same numeric value-type and limit-values
	 * as this one.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if objects are equal
	 */
	public boolean equals(Object other) {

		if (other instanceof CNumber) {

			CNumber n = (CNumber)other;

			return numberType.equals(n.numberType)
					&& min.equalTo(n.min)
					&& max.equalTo(n.max);
		}

		return false;
	}

	/**
	 * Provides hash-code based on number-type and limit-values.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return numberType.hashCode() + min.hashCode() + max.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<INumber> getValueType() {

		return INumber.class;
	}

	/**
	 * Stipulates that this numeric value-type defines specific
	 * constraints on the number values that it defines if and only
	 * if it has any defined limits.
	 *
	 * @return True if defined limits
	 */
	public boolean constrained() {

		return hasMin() || hasMax();
	}

	/**
	 * Provides the unconstrained version of this numeric value-type,
	 * which will be a numeric value-type with the same number-type but no
	 * minimum or maximum values.
	 *
	 * @return Unconstrained version of this numeric value-type
	 */
	public CNumber toUnconstrained() {

		return CNumberFactory.unconstrained(numberType);
	}

	/**
	 * Stipulates that this numeric value-type does define a default
	 * number value, which will be the value that is provided by the
	 * {@link #asINumber} method.
	 *
	 * @return True always
	 */
	public boolean hasDefaultValue() {

		return true;
	}

	/**
	 * Stipulates that this numeric value-type defines only a single
	 * possible value if and only if it represents an exact value
	 * (see {@link #exactValue}). If so then that exact value will
	 * be the single possible value.
	 *
	 * @return True if numeric value-type represents an exact value
	 */
	public boolean onePossibleValue() {

		return exactValue();
	}

	/**
	 * Provides the primitive Java <code>Number</code> type for the
	 * numeric value-type.
	 *
	 * @return Relevant <code>Number</code> type
	 */
	public Class<? extends Number> getNumberType() {

		return numberType;
	}

	/**
	 * Tests whether the primitive Java <code>Number</code> type
	 * of the numeric value-type is equal to a specified type.
	 *
	 * @param testNumberType <code>Number</code> type to test for
	 * @return True if numeric value-type is of specified type
	 */
	public boolean hasNumberType(Class<? extends Number> testNumberType) {

		return testNumberType == numberType;
	}

	/**
	 * Specifies whether this numeric value-type represents an exact value,
	 * which will be the case if the minimum and maximum values are
	 * equals.
	 *
	 * @return True if exact value
	 */
	public boolean exactValue() {

		return min.equals(max);
	}

	/**
	 * Specifies whether a minimum value has been defined for the
	 * numeric value-type.
	 *
	 * @return True if min value defined
	 */
	public boolean hasMin() {

		return !min.equals(INumber.MINUS_INFINITY);
	}

	/**
	 * Specifies whether a maximum value has been defined for the
	 * numeric value-type.
	 *
	 * @return True if max value defined
	 */
	public boolean hasMax() {

		return !max.equals(INumber.PLUS_INFINITY);
	}

	/**
	 * Provides the minimum value for the numeric value-type (if no
	 * minimum value has been defined this will be
	 * {@link INumber#MINUS_INFINITY}).
	 *
	 * @return Minimum value for the numeric value-type
	 */
	public INumber getMin() {

		return min;
	}

	/**
	 * Provides the maximum value for the numeric value-type (if no
	 * maximum value has been defined this will be
	 * {@link INumber#PLUS_INFINITY}).
	 *
	 * @return Maximum value for the numeric value-type
	 */
	public INumber getMax() {

		return max;
	}

	/**
	 * Tests whether this value-type-entity subsumes another
	 * specified value-type-entity, which will be the case if and
	 * only if the other value-type-entity is another
	 * <code>CNumber</code> object, with the primitive Java
	 * <code>Number</code> type as this one, and whose range is
	 * fully contained within the range of this one, as determined
	 * via the {@link #contains} method.
	 *
	 * @param other Other value-type-entity to test for subsumption
	 * @return True if this value-type-entity subsumes other
	 * value-type-entity
	 */
	public boolean subsumes(CValue<?> other) {

		if (other instanceof CNumber) {

			CNumber n = (CNumber)other;

			return numberType.equals(n.numberType) && contains(n);
		}

		return false;
	}

	/**
	 * Tests whether the provided string represents a valid number
	 * of the relevant type.
	 *
	 * @param value String to test
	 * @return True if valid number of relevant type
	 */
	public boolean validNumberValue(String value) {

		try {

			return validTypeValue(new INumber(numberType, value));
		}
		catch (NumberFormatException e) {

			return false;
		}
	}

	/**
	 * Tests whether the range of the other specified numeric value-type
	 * is fully contained within the range of this one.
	 *
	 * @param other Other numeric value-type to test
	 * @return True if required range containment
	 */
	public boolean contains(CNumber other) {

		return other.min.moreThanOrEqualTo(min)
				&& other.max.lessThanOrEqualTo(max);
	}

	/**
	 * Tests whether the range of the other specified numeric value-type
	 * intersects with the range of this one.
	 *
	 * @param other Other numeric value-type to test
	 * @return True if required range intersection
	 */
	public boolean intersectsWith(CNumber other) {

		return getMaxMin(other).lessThanOrEqualTo(getMinMax(other));
	}

	/**
	 * Produces a new numeric value-type with the same primitive Java
	 * <code>Number</code> type as this one, whose range is the
	 * intersection of the ranges of this and the other specified
	 * numeric value-type.
	 *
	 * @param other Other numeric value-type to whose range is to be
	 * intersected
	 * @return Resulting numeric value-type
	 */
	public CNumber getIntersection(CNumber other) {

		return new CNumber(numberType, getMaxMin(other), getMinMax(other));
	}

	/**
	 * Provides an instance-level representation of this numeric value-type.
	 * If the numeric value-type represents an exact value (see {@link
	 * #exactValue}) then the returned object will represent that
	 * particular value, otherwise it will represent the appropriate
	 * indefinite value (see {@link INumber#indefinite}).
	 *
	 * @return Instance-level representation of this numeric value-type
	 */
	public INumber asINumber() {

		return exactValue() ? min : INumber.indefiniteRange(this);
	}

	CNumber(Class<? extends Number> numberType, INumber min, INumber max) {

		this.numberType = numberType;
		this.min = min;
		this.max = max;
	}

	CValue<?> update(CValue<?> other) {

		return other instanceof CNumber
				? getIntersection((CNumber)other)
				: null;
	}

	void acceptVisitor(CValueVisitor visitor) throws Exception {

		visitor.visit(this);
	}

	INumber getDefaultValueOrNull() {

		return asINumber();
	}

	boolean validTypeValue(INumber value) {

		return subsumes(value.normaliseValueTypeTo(this));
	}

	String getDataValueDescription() {

		return numberType.getSimpleName() + "[" + getLimitsString() +"]";
	}

	String getLimitsString() {

		String limits = minToString();

		if (!exactValue()) {

			limits += ("-" + maxToString());
		}

		return limits;
	}

	private INumber getMaxMin(CNumber other) {

		return min.max(other.min);
	}

	private INumber getMinMax(CNumber other) {

		return max.min(other.max);
	}

	private String minToString() {

		return limitToString(min, INumber.MINUS_INFINITY);
	}

	private String maxToString() {

		return limitToString(max, INumber.PLUS_INFINITY);
	}

	private String limitToString(INumber limit, INumber absLimit) {

		return limit.equals(absLimit) ? "?" : limit.getDisplayLabel();
	}
}
