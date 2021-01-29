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

/**
 * Represents the activation status of a slot.
 *
 * @author Colin Puleston
 */
public enum CActivation {

	/**
	 * Slot is active and exposed to end-users.
	 */
	ACTIVE_EXPOSED,

	/**
	 * Slot is active but hidden from end-users.
	 */
	ACTIVE_HIDDEN,

	/**
	 * Slot is inactive.
	 */
	INACTIVE;

	/**
	 * Specifies whether this is either the {@link #ACTIVE_EXPOSED}
	 * or {@link #ACTIVE_HIDDEN} value.
	 *
	 * @return True if this is an active value, either exposed or
	 * hidden
	 */
	public boolean active() {

		return !inactive();
	}

	/**
	 * Specifies whether this is the {@link #ACTIVE_EXPOSED} value.
	 *
	 * @return True if this is an active value
	 */
	public boolean activeExposed() {

		return this == ACTIVE_EXPOSED;
	}

	/**
	 * Specifies whether this is the {@link #ACTIVE_HIDDEN} value.
	 *
	 * @return True if this is an active value
	 */
	public boolean activeHidden() {

		return this == ACTIVE_HIDDEN;
	}

	/**
	 * Specifies whether this is the {@link #INACTIVE} value.
	 *
	 * @return True if this is the inactive value
	 */
	public boolean inactive() {

		return this == INACTIVE;
	}

	/**
	 * Provides the strongest activation between this and the other
	 * specified activation, with {@link #ACTIVE_EXPOSED} value being
	 * the strongest, followed by {@link #ACTIVE_HIDDEN}, then {@link
	 * #INACTIVE}.
	 *
	 * @param other Other activation to test against
	 * @return True if this is the strongest of the two
	 */
	public CActivation getStrongest(CActivation other) {

		return ordinal() < other.ordinal() ? this : other;
	}

	/**
	 * Provides the weakest activation between this and the other
	 * specified activation, with {@link #INACTIVE} value being the
	 * weakest, followed by {@link #ACTIVE_HIDDEN}, then {@link
	 * #ACTIVE_EXPOSED}.
	 *
	 * @param other Other activation to test against
	 * @return True if this is the weakest of the two
	 */
	public CActivation getWeakest(CActivation other) {

		return ordinal() > other.ordinal() ? this : other;
	}
}
