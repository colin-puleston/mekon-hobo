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
 * of concept-level or instance-level model-slots.
 * <p>
 * See ancestor classes for details of list-operations and
 * associated listening mechanisms.
 *
 * @author Colin Puleston
 */
public abstract class FSlots<S> extends FIdentifiables<S> {

	/**
	 * Tests whether list contains slot for the specified
	 * property.
	 *
	 * @param property Property to look for
	 * @return True if list contains slot for specified property
	 */
	public boolean containsSlotFor(CProperty property) {

		return containsValueFor(property.getIdentity());
	}

	/**
	 * Retrieves the slot for the specified property.
	 *
	 * @param property Property for which slot is required
	 * @return Required slot
	 * @throws KAccessException If list does not contain slot for
	 * specified property
	 */
	public S getSlotFor(CProperty property) {

		return get(property.getIdentity());
	}

	/**
	 * Provides the identity for the property associated with the
	 * specified slot.
	 *
	 * @param slot Slot for which identity is required
	 * @return Identity for property associated with specified slot
	 */
	protected CIdentity getIdentity(S slot) {

		return getCSlot(slot).getProperty().getIdentity();
	}

	void add(S slot) {

		addValue(slot);
	}

	void addAll(Collection<S> slots) {

		addAllValues(slots);
	}

	void remove(S slot) {

		removeValue(slot);
	}

	abstract CSlot getCSlot(S slot);
}
