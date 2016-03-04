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
 * Represents the activation, or otherwise, of a slot.
 *
 * @author Colin Puleston
 */
public enum CActivation {

	/**
	 * Slot is active.
	 */
	ACTIVE,

	/**
	 * Slot is inactive.
	 */
	INACTIVE;

	/**
	 * Specifies whether this is the {@link #ACTIVE} value.
	 *
	 * @return True if this is active value
	 */
	public boolean active() {

		return this == ACTIVE;
	}

	/**
	 * Specifies whether this is the {@link #INACTIVE} value.
	 *
	 * @return True if this is inactive value
	 */
	public boolean inactive() {

		return this == INACTIVE;
	}

	/**
	 * Provides the weakest activation between this and the other
	 * specified activation.
	 *
	 * @param other Other activation to test against
	 * @return True if this is the weakest of the two
	 */
	public CActivation getWeakest(CActivation other) {

		return this == INACTIVE || other == INACTIVE ? INACTIVE : ACTIVE;
	}

	/**
	 * Provides the strongest activation between this and the other
	 * specified activation.
	 *
	 * @param other Other activation to test against
	 * @return True if this is the strongest of the two
	 */
	public CActivation getStrongest(CActivation other) {

		return this == ACTIVE || other == ACTIVE ? ACTIVE : INACTIVE;
	}
}
