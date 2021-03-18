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

package uk.ac.manchester.cs.mekon.model.motor;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;

/**
 * Represents a numeric value-type, defined via a primitive Java
 * <code>Number</code> type and specific numeric range.
 *
 * @author Colin Puleston
 */
public class CNumberFactory {

	static private Map<Class<? extends Number>, CNumber> unconstraineds
							= new HashMap<Class<? extends Number>, CNumber>();

	/**
	 * Represents an unconstrained integer value-type.
	 */
	static public final CNumber INTEGER = create(Integer.class);

	/**
	 * Represents an unconstrained long value-type.
	 */
	static public final CNumber LONG = create(Long.class);

	/**
	 * Represents an unconstrained float value-type.
	 */
	static public final CNumber FLOAT = create(Float.class);

	/**
	 * Represents an unconstrained double value-type.
	 */
	static public final CNumber DOUBLE = create(Double.class);

	/**
	 * Provides an unconstrained numeric value-type.
	 *
	 * @param numberType Relevant number value-type
	 * @return Unconstrained numeric value-type
	 */
	static public CNumber unconstrained(Class<? extends Number> numberType) {

		return unconstraineds.get(numberType);
	}

	/**
	 * Creates an integer value-type with the specified limits.
	 *
	 * @param min Minimum value for integer value-type
	 * @param max Maximum value for integer value-type
	 * @return Created integer value-type
	 */
	static public CNumber range(Integer min, Integer max) {

		return range(Integer.class, min, max);
	}

	/**
	 * Creates a long value-type with the specified limits.
	 *
	 * @param min Minimum value for long value-type
	 * @param max Maximum value for long value-type
	 * @return Created long value-type
	 */
	static public CNumber range(Long min, Long max) {

		return range(Long.class, min, max);
	}

	/**
	 * Creates a float value-type with the specified limits.
	 *
	 * @param min Minimum value for float value-type
	 * @param max Maximum value for float value-type
	 * @return Created float value-type
	 */
	static public CNumber range(Float min, Float max) {

		return range(Float.class, min, max);
	}

	/**
	 * Creates a double value-type with the specified limits.
	 *
	 * @param min Minimum value for double value-type
	 * @param max Maximum value for double value-type
	 * @return Created double value-type
	 */
	static public CNumber range(Double min, Double max) {

		return range(Double.class, min, max);
	}

	/**
	 * Creates a numeric value-type with the specified limits. The
	 * number-type will be derived from the specified limit-values,
	 * which must have identical number-types, or one of which must
	 * represent an infinite value, and hence will not have a specific
	 * number-type. If both limits represent infinite values then an
	 * exception will be thrown.
	 *
	 * @param min Minimum value for value-type
	 * @param max Maximum value for value-type
	 * @return Created value-type
	 * @throws KModelException if minimum value is greater than
	 * maximum  value, or if minimum and maximum values have
	 * incompatible number-types
	 */
	static public CNumber range(INumber min, INumber max) {

		return create(getRangeNumberType(min, max), min, max);
	}

	/**
	 * Creates a numeric value-type with the specified limits.
	 *
	 * @param numberType Relevant number-type
	 * @param min Minimum value for value-type
	 * @param max Maximum value for value-type
	 * @return Created value-type
	 * @throws KModelException if minimum value is greater than maximum
	 * value, or if minimum or maximum value does not have specified
	 * number-type
	 */
	static public CNumber range(
							Class<? extends Number> numberType,
							INumber min,
							INumber max) {

		checkValidLimit(numberType, min);
		checkValidLimit(numberType, max);

		return create(numberType, min, max);
	}

	/**
	 * Creates an integer value-type with the specified minimum value.
	 *
	 * @param min Minimum value for integer value-type
	 * @return Created integer value-type
	 */
	static public CNumber min(Integer min) {

		return range(min, null);
	}

	/**
	 * Creates a long value-type with the specified minimum value.
	 *
	 * @param min Minimum value for long value-type
	 * @return Created long value-type
	 */
	static public CNumber min(Long min) {

		return range(min, null);
	}

	/**
	 * Creates a float value-type with the specified minimum value.
	 *
	 * @param min Minimum value for float value-type
	 * @return Created float value-type
	 */
	static public CNumber min(Float min) {

		return range(min, null);
	}

	/**
	 * Creates a double value-type with the specified minimum value.
	 *
	 * @param min Minimum value for double value-type
	 * @return Created double value-type
	 */
	static public CNumber min(Double min) {

		return range(min, null);
	}

	/**
	 * Creates a numeric value-type with the specified minimum value.
	 *
	 * @param min Minimum value for value-type
	 * @return Created value-type
	 */
	static public CNumber min(INumber min) {

		return create(min.getNumberType(), min, INumber.PLUS_INFINITY);
	}

	/**
	 * Creates an integer value-type with the specified maximum value.
	 *
	 * @param max Maximum value for integer value-type
	 * @return Created integer value-type
	 */
	static public CNumber max(Integer max) {

		return range(null, max);
	}

	/**
	 * Creates a long value-type with the specified maximum value.
	 *
	 * @param max Maximum value for long value-type
	 * @return Created long value-type
	 */
	static public CNumber max(Long max) {

		return range(null, max);
	}

	/**
	 * Creates a float value-type with the specified maximum value.
	 *
	 * @param max Maximum value for float value-type
	 * @return Created float value-type
	 */
	static public CNumber max(Float max) {

		return range(null, max);
	}

	/**
	 * Creates a double value-type with the specified maximum value.
	 *
	 * @param max Maximum value for double value-type
	 * @return Created double value-type
	 */
	static public CNumber max(Double max) {

		return range(null, max);
	}

	/**
	 * Creates a numeric value-type with the specified maximum value.
	 *
	 * @param max Maximum value for value-type
	 * @return Created value-type
	 */
	static public CNumber max(INumber max) {

		return create(max.getNumberType(), INumber.MINUS_INFINITY, max);
	}

	/**
	 * Creates an integer value-type with the specified exact value.
	 *
	 * @param exact Exact value for integer value-type
	 * @return Created integer value-type
	 */
	static public CNumber exact(Integer exact) {

		return range(exact, exact);
	}

	/**
	 * Creates a long value-type with the specified exact value.
	 *
	 * @param exact Exact value for long value-type
	 * @return Created long value-type
	 */
	static public CNumber exact(Long exact) {

		return range(exact, exact);
	}

	/**
	 * Creates a float value-type with the specified exact value.
	 *
	 * @param exact Exact value for float value-type
	 * @return Created float value-type
	 */
	static public CNumber exact(Float exact) {

		return range(exact, exact);
	}

	/**
	 * Creates a double value-type with the specified exact value.
	 *
	 * @param exact Exact value for double value-type
	 * @return Created double value-type
	 */
	static public CNumber exact(Double exact) {

		return range(exact, exact);
	}

	/**
	 * Creates a numeric value-type with the specified exact value.
	 *
	 * @param exact Exact value for value-type
	 * @return Created value-type
	 */
	static public CNumber exact(INumber exact) {

		return create(exact.getNumberType(), exact, exact);
	}

	static private CNumber range(
							Class<? extends Number> numberType,
							Number min,
							Number max) {

		return create(numberType, resolveMin(min), resolveMax(max));
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

		if (minType == maxType) {

			if (min.infinite()) {

				throw new KModelException(
							"Cannot create CNumber with "
							+ "both limits specified as "
							+ "infinite-valued INumber objects");
			}

			return minType;
		}

		if (min.infinite()) {

			return maxType;
		}

		if (max.infinite()) {

			return minType;
		}

		throw new KModelException(
					"Incompatible limit number-types: "
					+ "minimum = " + minType
					+ ", maximum = " + maxType);
	}

	static private void checkValidLimit(Class<? extends Number> numberType, INumber limit) {

		if (!limit.infinite() && limit.getNumberType() != numberType) {

			throw new KModelException(
						"Limit not compatible with number-type: "
						+ "number-type = " + numberType
						+ ", limit = " + limit);
		}
	}

	static private CNumber create(Class<? extends Number> numberType) {

		CNumber number = create(numberType, INumber.MINUS_INFINITY, INumber.PLUS_INFINITY);

		unconstraineds.put(numberType, number);

		return number;
	}

	static private CNumber create(Class<? extends Number> numberType, INumber min, INumber max) {

		return ZCModelAccessor.get().createCNumber(numberType, min, max);
	}
}
