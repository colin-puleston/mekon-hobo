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

/**
 * Provides mechanisms for editing a specific {@link CAnnotations}
 * object.
 *
 * @author Colin Puleston
 */
public interface CAnnotationsEditor {

	/**
	 * Adds an annotation-value for a particular key.
	 *
	 * @param key Key for annotation-value to be added
	 * @param value Annotation-value to be added
	 */
	public void add(Object key, Object value);

	/**
	 * Adds a set of annotation-values for a particular key.
	 *
	 * @param key Key for annotation-values to be added
	 * @param values Annotation-values to be added
	 */
	public void addAll(Object key, Collection<?> values);

	/**
	 * Removes an annotation-value for a particular key.
	 *
	 * @param key Key for annotation-value to be removed
	 * @param value Annotation-value to be removed
	 */
	public void remove(Object key, Object value);

	/**
	 * Removes a set of annotation-values for a particular key.
	 *
	 * @param key Key for annotation-values to be removed
	 * @param values Annotation-values to be removed
	 */
	public void removeAll(Object key, Collection<?> values);

	/**
	 * Removes all annotation-values for a particular key.
	 *
	 * @param key Key for annotation-values to be removed
	 */
	public void removeAll(Object key);

	/**
	 * Removes all annotation-values for all keys.
	 */
	public void clear();
}
