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

package uk.ac.manchester.cs.hobo.model;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Common interface for both {@link DField} and {@link DFieldViewer}.
 *
 * @author Colin Puleston
 */
public interface DFieldView<V> {

	/**
	 * Adds a general-update listener to the field.
	 *
	 * @param listener Listener to add
	 */
	public void addUpdateListener(KUpdateListener listener);

	/**
	 * Removes a general-update listener to the field.
	 *
	 * @param listener Listener to remove
	 */
	public void removeUpdateListener(KUpdateListener listener);

	/**
	 * Adds a values-update listener to the field.If specified
	 * listener is not a currenly registered listener then does
	 * nothing.
	 *
	 * @param listener Listener to add
	 */
	public void addValuesListener(KValuesListener<V> listener);

	/**
	 * Removes a values-update listener to the field. If specified
	 * listener is not a currenly registered listener then does
	 * nothing.
	 *
	 * @param listener Listener to remove
	 */
	public void removeValuesListener(KValuesListener<V> listener);

	/**
	 * Tests whether field currently has the specified value.
	 *
	 * @param value Value to test for
	 * @return true if field has value
	 */
	public boolean hasValue(V value);

	/**
	 * Provides the slot to which the field is bound.
	 *
	 * @return Slot to which field is bound
	 */
	public ISlot getSlot();
}
