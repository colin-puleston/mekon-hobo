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

/**
 * Abstract class whose extensions represent ordered lists
 * of {@link CIdentified} objects.
 * <p>
 * See ancestor classes for details of list-operations and
 * associated listening mechanisms.
 *
 * @author Colin Puleston
 */
public abstract class CIdentifieds<V extends CIdentified> extends FIdentifiables<V> {

	/**
	 * Constructor.
	 */
	protected CIdentifieds() {
	}

	/**
	 * Constructor.
	 *
	 * @param values Values to be added to list
	 */
	protected CIdentifieds(Collection<V> values) {

		addAllValues(values);
	}

	/**
	 * Provides the identity for the specified value.
	 *
	 * @param value Value whose identity is required
	 * @return Identity for specified value
	 */
	protected CIdentity getIdentity(V value) {

		return value.getIdentity();
	}
}
