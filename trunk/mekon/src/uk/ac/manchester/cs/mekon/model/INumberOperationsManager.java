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
import java.util.*;

import uk.ac.manchester.cs.mekon.*;

/**
 * @author Colin Puleston
 */
class INumberOperationsManager {

	static INumberOperationsManager get() {

		return singleton;
	}

	static private final INumberOperationsManager singleton = new INumberOperationsManager();

	final INumberOperations<Integer> integerOperations;
	final INumberOperations<Long> longOperations;
	final INumberOperations<Float> floatOperations;
	final INumberOperations<Double> doubleOperations;

	private Map<Class<? extends Number>, INumberOperations<?>> finder
				= new HashMap<Class<? extends Number>, INumberOperations<?>>();

	private class IntegerOperations extends INumberOperations<Integer> {

		Class<Integer> getNumberType() {

			return Integer.class;
		}

		BigDecimal toBigDecimal(Integer value) {

			return new BigDecimal(value);
		}

		INumber toINumber(Number typeValue) {

			return new INumber((Integer)typeValue);
		}

		INumber toINumber(BigDecimal value) {

			return new INumber(value.intValue());
		}

		INumber toINumber(String value) {

			return new INumber(Integer.parseInt(value));
		}
	}

	private class LongOperations extends INumberOperations<Long> {

		Class<Long> getNumberType() {

			return Long.class;
		}

		BigDecimal toBigDecimal(Long value) {

			return new BigDecimal(value);
		}

		INumber toINumber(Number typeValue) {

			return new INumber((Long)typeValue);
		}

		INumber toINumber(BigDecimal value) {

			return new INumber(value.longValue());
		}

		INumber toINumber(String value) {

			return new INumber(Long.parseLong(value));
		}
	}

	private class FloatOperations extends INumberOperations<Float> {

		Class<Float> getNumberType() {

			return Float.class;
		}

		BigDecimal toBigDecimal(Float value) {

			return new BigDecimal(value);
		}

		INumber toINumber(Number typeValue) {

			return new INumber((Float)typeValue);
		}

		INumber toINumber(BigDecimal value) {

			return new INumber(value.floatValue());
		}

		INumber toINumber(String value) {

			return new INumber(Float.parseFloat(value));
		}
	}

	private class DoubleOperations extends INumberOperations<Double> {

		Class<Double> getNumberType() {

			return Double.class;
		}

		BigDecimal toBigDecimal(Double typeValue) {

			return new BigDecimal(typeValue);
		}

		INumber toINumber(Number typeValue) {

			return new INumber((Double)typeValue);
		}

		INumber toINumber(BigDecimal value) {

			return new INumber(value.doubleValue());
		}

		INumber toINumber(String value) {

			return new INumber(Double.parseDouble(value));
		}
	}

	INumberOperations<?> get(Class<? extends Number> numberType) {

		INumberOperations<?> operations = finder.get(numberType);

		if (operations == null) {

			throw new KAccessException(
						"Cannot create INumber for value-type: "
						+ numberType);
		}

		return operations;
	}

	private INumberOperationsManager() {

		integerOperations = add(new IntegerOperations());
		longOperations = add(new LongOperations());
		floatOperations = add(new FloatOperations());
		doubleOperations = add(new DoubleOperations());
	}

	private <N extends Number>INumberOperations<N> add(INumberOperations<N> operations) {

		finder.put(operations.getNumberType(), operations);

		return operations;
	}
}
