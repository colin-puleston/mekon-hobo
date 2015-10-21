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
 * Utility class that takes a {@link Collection} of {@link CValue}
 * objects of a specific known type and produces either a {@link List}
 * or {@link Set} of the relevant type.
 *
 * @author Colin Puleston
 */
public class CValues<T extends CValue<?>> {

	private List<T> list = new ArrayList<T>();

	/**
	 * Constructor.
	 *
	 * @param values value-type-entities of specific type
	 * @param valueType Type of value-type-entities
	 * @throws KAccessException if any value-type-entities are not of
	 * required type
	 */
	public CValues(Collection<? extends CValue<?>> values, Class<T> valueType) {

		for (CValue<?> value : values) {

			list.add(value.castAs(valueType));
		}
	}

	/**
	 * Provides the value-type-entities as a list.
	 *
	 * @return List of value-type-entities
	 */
	public List<T> asList() {

		return list;
	}

	/**
	 * Provides the value-type-entities as a set.
	 *
	 * @return Set of value-type-entities
	 */
	public Set<T> asSet() {

		return new HashSet<T>(list);
	}
}
