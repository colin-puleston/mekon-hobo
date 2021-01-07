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
 * Represents the general category of a {@link IFrame}.
 *
 * @author Colin Puleston
 */
public enum IFrameCategory {

	/**
	 * Frame is an atomic-frame.
	 */
	ATOMIC,

	/**
	 * Frame is a disjunction-frame.
	 */
	DISJUNCTION,

	/**
	 * Frame is a reference-frame.
	 */
	REFERENCE;

	/**
	 * States whether frame is of category {@link #ATOMIC}.
	 *
	 * @return True if atomic.
	 */
	public boolean atomic() {

		return this == ATOMIC;
	}

	/**
	 * States whether frame is of category {@link #DISJUNCTION}.
	 *
	 * @return True if disjunction.
	 */
	public boolean disjunction() {

		return this == DISJUNCTION;
	}

	/**
	 * States whether frame is of category {@link #REFERENCE}.
	 *
	 * @return True if reference.
	 */
	public boolean reference() {

		return this == REFERENCE;
	}
}
