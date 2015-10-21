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

package uk.ac.manchester.cs.mekon.config;

import java.util.*;

/**
 * Represents a list of parameters for a particular method, including
 * both the expected parameter-types, and the actual parameter values.
 *
 * @author Colin Puleston
 */
public class KConfigParameters {

	private List<Class<?>> types = new ArrayList<Class<?>>();
	private List<Object> values = new ArrayList<Object>();

	/**
	 * Constructor.
	 */
	public KConfigParameters() {
	}

	/**
	 * Adds a parameter, where the expected parameter-type is simply
	 * the class of the parameter value (and not a super-class).
	 *
	 * @param value Value of parameter
	 */
	public void add(Object value) {

		types.add(value.getClass());
		values.add(value);
	}

	/**
	 * Adds a list of parameters, where each of the expected parameter-types
	 * is simply the class of the parameter value (and not a super-class).
	 *
	 * @param values Values of parameters
	 */
	public void addAll(Object... values) {

		for (Object value : values) {

			add(value);
		}
	}

	/**
	 * Adds a parameter.
	 *
	 * @param <P> Generic version of type
	 * @param type Expected-type of parameter
	 * @param value Value of parameter
	 */
	public <P>void add(Class<P> type, P value) {

		types.add(type);
		values.add(value);
	}

	KConfigParameters(Object... values) {

		addAll(values);
	}

	Class<?>[] getTypes() {

		return types.toArray(new Class<?>[types.size()]);
	}

	Object[] getValues() {

		return values.toArray();
	}
}
