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

package uk.ac.manchester.cs.mekon.owl.reason;

/**
 * Specifies the type of semantics to be embodied by the OWL constructs
 * that will be constructed to represent property value-sets.
 *
 * @author Colin Puleston
 */
public enum ORSemanticWorld {

	/**
	 * Specifies open-world semantics, meaning that the set of values
	 * for each property will give rise to a set of existential
	 * restrictions only.
	 */
	OPEN,

	/**
	 * Specifies closed-world semantics, meaning that the set of values
	 * for each property will give rise to a set of existential
	 * restrictions,  plus a universal restriction whose filler is a
	 * disjunction of all values for the slot.
	 */
	CLOSED;

	/**
	 * Specifies whether this value equals {@link #OPEN}.
	 *
	 * @return true if open-world value
	 */
	public boolean open() {

		return this == OPEN;
	}

	/**
	 * Specifies whether this value equals {@link #CLOSED}.
	 *
	 * @return true if closed-world value
	 */
	public boolean closed() {

		return this == CLOSED;
	}

	ORSemanticWorld getOpposite() {

		return this == OPEN ? CLOSED : OPEN;
	}
}
