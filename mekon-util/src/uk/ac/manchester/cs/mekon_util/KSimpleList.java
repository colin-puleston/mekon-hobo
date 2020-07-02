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

package uk.ac.manchester.cs.mekon_util;

import java.util.*;

/**
 * Simple concrete extension of {@link KList}.
 *
 * @author Colin Puleston
 */
public class KSimpleList<V> extends KList<V> {

	/**
	 * Constructor.
	 */
	public KSimpleList() {
	}

	/**
	 * Constructor.
	 *
	 * @param values Values to be added to list
	 */
	public KSimpleList(Collection<V> values) {

		super(values);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addValue(V value) {

		return super.addValue(value);
	}


	/**
	 * {@inheritDoc}
	 */
	public int insertValue(V value, int index) {

		return super.insertValue(value, index);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<V> addAllValues(Collection<V> values) {

		return super.addAllValues(values);
	}

	/**
	 * {@inheritDoc}
	 */
	public int removeValue(V value) {

		return super.removeValue(value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeValue(int index) {

		super.removeValue(index);
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearValues() {

		super.clearValues();
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateValues(List<V> latestValues) {

		super.updateValues(latestValues);
	}
}
