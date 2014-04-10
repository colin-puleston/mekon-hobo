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
 * Represents a view of a field in the Object Model (OM).
 * Provides methods for accessing the current state of the
 * field, but not for editing it.
 *
 * @author Colin Puleston
 */
public class DFieldViewer<V, F extends DField<V>> implements DFieldView<V> {

	private F field;

	/**
	 * {@inheritDoc}
	 */
	public void addUpdateListener(KUpdateListener listener) {

		field.addUpdateListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addConcreteOnlyUpdateListener(KUpdateListener listener) {

		field.addConcreteOnlyUpdateListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeUpdateListener(KUpdateListener listener) {

		field.removeUpdateListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addValuesListener(KValuesListener<V> listener) {

		field.addValuesListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addConcreteOnlyValuesListener(KValuesListener<V> listener) {

		field.addConcreteOnlyValuesListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeValuesListener(KValuesListener<V> listener) {

		field.removeValuesListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasValue(V value) {

		return field.hasValue(value);
	}

	/**
	 * {@inheritDoc}
	 */
	public ISlot getSlot() {

		return field.getSlot();
	}

	DFieldViewer(F field) {

		this.field = field;
	}

	F getField() {

		return field;
	}
}
