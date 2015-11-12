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

import java.util.*;

import uk.ac.manchester.cs.mekon.*;

/**
 * Represents a numeric-type, defined via a primitive Java
 * <code>Number</code> type and specific numeric range.
 *
 * @author Colin Puleston
 */
public class CNumber extends CValue<INumber> implements CEntity {

	/**
	 * Represents an unconstrained integer-type.
	 */
	static public final CNumber INTEGER = new CNumber(Integer.class);

	/**
	 * Represents an unconstrained long-type.
	 */
	static public final CNumber LONG = new CNumber(Long.class);

	/**
	 * Represents an unconstrained float-type.
	 */
	static public final CNumber FLOAT = new CNumber(Float.class);

	/**
	 * Represents an unconstrained double-type.
	 */
	static public final CNumber DOUBLE = new CNumber(Double.class);

	/**
	 * Creates an integer-type with the specified limits.
	 *
	 * @param min Minimnum value for integer-type
	 * @param max Maximnum value for integer-type
	 * @return Created integer-type
	 */
	static public CNumber range(Integer min, Integer max) {

		return range(Integer.class, min, max);
	}

	/**
	 * Creates a long-type with the specified limits.
	 *
	 * @param min Minimnum value for long-type
	 * @param max Maximnum value for long-type
	 * @return Created long-type
	 */
	static public CNumber range(Long min, Long max) {

		return range(Long.class, min, max);
	}

	/**
	 * Creates a float-type with the specified limits.
	 *
	 * @param min Minimnum value for float-type
	 * @param max Maximnum value for float-type
	 * @return Created float-type
	 */
	static public CNumber range(Float min, Float max) {

		return range(Float.class, min, max);
	}

	/**
	 * Creates a double-type with the specified limits.
	 *
	 * @param min Minimnum value for double-type
	 * @param max Maximnum value for double-type
	 * @return Created double-type
	 */
	static public CNumber range(Double min, Double max) {

		return range(Double.class, min, max);
	}

	/**
	 * Creates a number-type with the specified limits.
	 *
	 * @param min Minimnum value for number-type
	 * @param max Maximnum value for number-type
	 * @return Created
	 * @throws KModelException if minimnum value is greater than
	 * maximnum value, or if minimnum and maximnum values have
	 * incompatible number-types
	 */
	static public CNumber range(INumber min, INumber max) {

		return new CNumber(getRangeNumberType(min, max), min, max);
	}

	/**
	 * Creates an integer-type with the specified minimum value.
	 *
	 * @param min Minimnum value for integer-type
	 * @return Created integer-type
	 */
	static public CNumber min(Integer min) {

		return range(min, null);
	}

	/**
	 * Creates a long-type with the specified minimum value.
	 *
	 * @param min Minimnum value for long-type
	 * @return Created long-type
	 */
	static public CNumber min(Long min) {

		return range(min, null);
	}

	/**
	 * Creates a float-type with the specified minimum value.
	 *
	 * @param min Minimnum value for float-type
	 * @return Created float-type
	 */
	static public CNumber min(Float min) {

		return range(min, null);
	}

	/**
	 * Creates a double-type with the specified minimum value.
	 *
	 * @param min Minimnum value for double-type
	 * @return Created double-type
	 */
	static public CNumber min(Double min) {

		return range(min, null);
	}

	/**
	 * Creates a number-type with the specified minimum value.
	 *
	 * @param min Minimnum value for number-type
	 * @return Created number-type
	 */
	static public CNumber min(INumber min) {

		return new CNumber(min.getNumberType(), min, INumber.PLUS_INFINITY);
	}

	/**
	 * Creates an integer-type with the specified maximum value.
	 *
	 * @param max Maximnum value for integer-type
	 * @return Created integer-type
	 */
	static public CNumber max(Integer max) {

		return range(null, max);
	}

	/**
	 * Creates a long-type with the specified maximum value.
	 *
	 * @param max Maximnum value for long-type
	 * @return Created long-type
	 */
	static public CNumber max(Long max) {

		return range(null, max);
	}

	/**
	 * Creates a float-type with the specified maximum value.
	 *
	 * @param max Maximnum value for float-type
	 * @return Created float-type
	 */
	static public CNumber max(Float max) {

		return range(null, max);
	}

	/**
	 * Creates a double-type with the specified maximum value.
	 *
	 * @param max Maximnum value for double-type
	 * @return Created double-type
	 */
	static public CNumber max(Double max) {

		return range(null, max);
	}

	/**
	 * Creates a number-type with the specified maximum value.
	 *
	 * @param max Maximnum value for number-type
	 * @return Created number-type
	 */
	static public CNumber max(INumber max) {

		return new CNumber(max.getNumberType(), INumber.MINUS_INFINITY, max);
	}

	/**
	 * Creates an integer-type with the specified exact value.
	 *
	 * @param exact Exact value for integer-type
	 * @return Created integer-type
	 */
	static public CNumber exact(Integer exact) {

		return range(exact, exact);
	}

	/**
	 * Creates a long-type with the specified exact value.
	 *
	 * @param exact Exact value for long-type
	 * @return Created long-type
	 */
	static public CNumber exact(Long exact) {

		return range(exact, exact);
	}

	/**
	 * Creates a float-type with the specified exact value.
	 *
	 * @param exact Exact value for float-type
	 * @return Created float-type
	 */
	static public CNumber exact(Float exact) {

		return range(exact, exact);
	}

	/**
	 * Creates a double-type with the specified exact value.
	 *
	 * @param exact Exact value for double-type
	 * @return Created double-type
	 */
	static public CNumber exact(Double exact) {

		return range(exact, exact);
	}

	/**
	 * Creates a number-type with the specified exact value.
	 *
	 * @param exact Exact value for number-type
	 * @return Created number-type
	 */
	static public CNumber exact(INumber exact) {

		return new CNumber(exact.getNumberType(), exact, exact);
	}

	static private CNumber range(
							Class<? extends Number> numberType,
							Number min,
							Number max) {

		return new CNumber(numberType, resolveMin(min), resolveMax(max));
	}

	static private INumber resolveMin(Number min) {

		return resolveLimit(min, INumber.MINUS_INFINITY);
	}

	static private INumber resolveMax(Number max) {

		return resolveLimit(max, INumber.PLUS_INFINITY);
	}

	static private INumber resolveLimit(Number limit, INumber infinity) {

		return limit != null ? new INumber(limit) : infinity;
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

	private CAnnotations annotations = new CAnnotations(this);

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>CNumber</code>
	 * with same numeric-type, limit-values and annotations as this
	 * one
	 */
	public boolean equals(Object other) {

		if (other instanceof CNumber) {

			CNumber n = (CNumber)other;

			return numberType.equals(n.numberType)
					&& min.equalTo(n.min)
					&& max.equalTo(n.max)
					&& annotations.equals(n.annotations);
		}

		return false;
	}

	/**
	 * Provides hash-code based on number-type, limit-values and
	 * annotations.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return numberType.hashCode()
				+ min.hashCode()
				+ max.hashCode()
				+ annotations.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return FEntityDescriber.entityToString(this, getDescription());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayLabel() {

		return getDescription();
	}

	/**
	 * Stipulates that this numeric-type defines specific
	 * constraints on the value-entities that it defines if and only
	 * if it has any defined limits.
	 *
	 * @return True if defined limits
	 */
	public boolean constrained() {

		return hasMin() || hasMax();
	}

	/**
	 * Stipulates that this numeric-type can provide a default
	 * value-entity if and only if it represents an exact value (see
	 * {@link #exactValue}). If so then that exact value will be the
	 * default-value.
	 *
	 * @return True if numeric-type represents an exact value
	 */
	public boolean hasDefaultValue() {

		return exactValue();
	}

	/**
	 * Stipulates that this numeric-type defines only a single
	 * possible value if and only if it represents an exact value
	 * (see {@link #exactValue}). If so then that exact value will
	 * be the single possible value.
	 *
	 * @return True if numeric-type represents an exact value
	 */
	public boolean onePossibleValue() {

		return exactValue();
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
	 * Tests whether the primitive Java <code>Number</code> type
	 * of the numeric-type is equal to a specified type.
	 *
	 * @param testNumberType <code>Number</code> type to test for
	 * @return True if numeric-type is of specified type
	 */
	public boolean hasNumberType(Class<? extends Number> testNumberType) {

		return testNumberType == numberType;
	}

	/**
	 * Specifies whether this numeric-type represents an exact value,
	 * which will be the case if the minimum and maximum values are
	 * equals.
	 *
	 * @return True if exact value
	 */
	public boolean exactValue() {

		return min.equals(max);
	}

	/**
	 * Specifies whether a minimnum value has been defined for the
	 * numeric-type.
	 *
	 * @return True if min value defined
	 */
	public boolean hasMin() {

		return !min.equals(INumber.MINUS_INFINITY);
	}

	/**
	 * Specifies whether a maximnum value has been defined for the
	 * numeric-type.
	 *
	 * @return True if max value defined
	 */
	public boolean hasMax() {

		return !max.equals(INumber.PLUS_INFINITY);
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
	 * {@inheritDoc}
	 */
	public boolean subsumes(CValue<?> other) {

		if (other instanceof CNumber) {

			CNumber n = other.castAs(CNumber.class);

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
	 * Tests whether the range of the other specified numeric-type
	 * is fully contained within the range of this one.
	 *
	 * @param other Other numeric-type to test
	 * @return True if required range containment
	 */
	public boolean contains(CNumber other) {

		return other.min.moreThanOrEqualTo(min)
				&& other.max.lessThanOrEqualTo(max);
	}

	/**
	 * Tests whether the range of the other specified numeric-type
	 * intersects with the range of this one.
	 *
	 * @param other Other numeric-type to test
	 * @return True if required range intersection
	 */
	public boolean intersectsWith(CNumber other) {

		return getMaxMin(other).lessThanOrEqualTo(getMinMax(other));
	}

	/**
	 * Produces a new numeric-type with the same primitive Java
	 * <code>Number</code> type as this one, whose range is the
	 * intersection of the ranges of this and the other specified
	 * numeric-type, and which includes all annotations from both
	 * sources.
	 *
	 * @param other Other numeric-type to whose range is to be
	 * intersected
	 * @return Resulting numeric-type
	 */
	public CNumber getIntersection(CNumber other) {

		CNumber intersect = createCNumber(getMaxMin(other), getMinMax(other));
		CAnnotations intersectAnnos = intersect.getAnnotations();

		intersectAnnos.addAll(annotations);
		intersectAnnos.addAll(other.annotations);

		return intersect;
	}

	/**
	 * Provides an instance-level representation of this numeric-type.
	 * If the numeric-type represents an exact value (see {@link
	 * #exactValue}) then the returned object will represent that
	 * particular value, otherwise it will represent the appropriate
	 * indefinite value (see {@link INumber#indefinite}).
	 *
	 * @return Instance-level representation of this numeric-type
	 */
	public INumber asINumber() {

		return exactValue() ? min : new INumber(this);
	}

	/**
	 * Provides any annotations on the numeric-type.
	 *
	 * @return Annotations on numeric-type
	 */
	public CAnnotations getAnnotations() {

		return annotations;
	}

	CNumber(Class<? extends Number> numberType) {

		this(numberType, INumber.MINUS_INFINITY, INumber.PLUS_INFINITY);
	}

	CNumber(Class<? extends Number> numberType, INumber min, INumber max) {

		super(INumber.class);

		this.numberType = numberType;
		this.min = min;
		this.max = max;
	}

	void acceptVisitor(CValueVisitor visitor) throws Exception {

		visitor.visit(this);
	}

	CValue<?> mergeWith(CValue<?> other) {

		return other instanceof CNumber
				? getIntersection((CNumber)other)
				: null;
	}

	INumber getDefaultValueOrNull() {

		return exactValue() ? min : null;
	}

	boolean validTypeValue(INumber value) {

		return contains(value.getType());
	}

	String getLimitsString() {

		String limits = minToString();

		if (!exactValue()) {

			limits += ("-" + maxToString());
		}

		return limits;
	}

	private CNumber createCNumber(INumber min, INumber max) {

		return new CNumber(numberType, min, max);
	}

	private INumber getMaxMin(CNumber other) {

		return min.max(other.min);
	}

	private INumber getMinMax(CNumber other) {

		return max.min(other.max);
	}

	private String getDescription() {

		return numberType.getSimpleName() + "[" + getLimitsString() +"]";
	}

	private String minToString() {

		return limitToString(min, INumber.MINUS_INFINITY);
	}

	private String maxToString() {

		return limitToString(max, INumber.PLUS_INFINITY);
	}

	private String limitToString(INumber limit, INumber absLimit) {

		return limit != absLimit ? limit.getDisplayLabel() : "?";
	}
}
