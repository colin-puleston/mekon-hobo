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

import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
class IDefiniteNumberCreator {

	static private final IDefiniteNumberCreator singleton = new IDefiniteNumberCreator();

	static IDefiniteNumberCreator get() {

		return singleton;
	}

	private Map<Class<? extends Number>, TypeCreator<?>> typeCreators
				= new HashMap<Class<? extends Number>, TypeCreator<?>>();

	private abstract class TypeCreator<N extends Number> {

		TypeCreator(Class<N> numberType) {

			typeCreators.put(numberType, this);
		}

		IDefiniteNumber<?> create(String value) {

			return create(parseValue(value));
		}

		abstract IDefiniteNumber<?> create(Number value);

		abstract N parseValue(String value);
	}

	private class IntegerCreator extends TypeCreator<Integer> {

		IntegerCreator() {

			super(Integer.class);
		}

		IDefiniteNumber<?> create(Number value) {

			return new IInteger((Integer)value);
		}

		Integer parseValue(String value) {

			return Integer.parseInt(value);
		}
	}

	private class LongCreator extends TypeCreator<Long> {

		LongCreator() {

			super(Long.class);
		}

		IDefiniteNumber<?> create(Number value) {

			return new ILong((Long)value);
		}

		Long parseValue(String value) {

			return Long.parseLong(value);
		}
	}

	private class FloatCreator extends TypeCreator<Float> {

		FloatCreator() {

			super(Float.class);
		}

		IDefiniteNumber<?> create(Number value) {

			return new IFloat((Float)value);
		}

		Float parseValue(String value) {

			return Float.parseFloat(value);
		}
	}

	private class DoubleCreator extends TypeCreator<Double> {

		DoubleCreator() {

			super(Double.class);
		}

		IDefiniteNumber<?> create(Number value) {

			return new IDouble((Double)value);
		}

		Double parseValue(String value) {

			return Double.parseDouble(value);
		}
	}

	IDefiniteNumber<?> create(Number value) {

		return getTypeCreator(value.getClass()).create(value);
	}

	IDefiniteNumber<?> create(Class<? extends Number> type, String value) {

		return getTypeCreator(type).create(value);
	}

	private IDefiniteNumberCreator() {

		new IntegerCreator();
		new LongCreator();
		new FloatCreator();
		new DoubleCreator();
	}

	private TypeCreator<?> getTypeCreator(Class<? extends Number> numberType) {

		TypeCreator<?> creator = typeCreators.get(numberType);

		if (creator == null) {

			throw new KAccessException(
						"Cannot create INumber for number-type: "
						+ numberType);
		}

		return creator;
	}
}
