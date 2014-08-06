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

package uk.ac.manchester.cs.mekon.owl.frames;

/**
 * Specifies the type of semantics to be embodied by the OWL expressions
 * that will represent the incoming frames-based instances.
 *
 * @author Colin Puleston
 */
public enum OFSemantics {

	/**
	 * Specifies open-world semantics, meaning that the set of values
	 * for each slot will give rise to a set of existential restrictions
	 * only.
	 */
	OPEN_WORLD,

	/**
	 * Specifies closed-world semantics, meaning that the set of values
	 * for each slot will give rise to a set of existential restrictions,
	 * plus a universal restriction whose filler is a disjunction of all
	 * values for the slot.
	 */
	CLOSED_WORLD;

	OFSemantics getOpposite() {

		return this == OPEN_WORLD ? CLOSED_WORLD : OPEN_WORLD;
	}
}
