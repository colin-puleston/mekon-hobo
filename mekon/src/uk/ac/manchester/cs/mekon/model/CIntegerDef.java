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
 * Defines a {@link CNumber} object that is to be created to define
 * <code>Integer</code> type values.
 *
 * @author Colin Puleston
 */
public class CIntegerDef extends CNumberDef {

	/**
	 * Represents an unconstrained integer-type definition.
	 */
	static public final CIntegerDef UNCONSTRAINED = new CIntegerDef();

	/**
	 * Creates an integer-type definition with the specified limits.
	 *
	 * @param min Minimnum value for integer-type
	 * @param max Maximnum value for integer-type
	 * @return Created definition
	 */
	static public CIntegerDef range(Integer min, Integer max) {

		return new CIntegerDef(resolveMin(min), resolveMax(max));
	}

	/**
	 * Creates an integer-type definition with the specified minimum
	 * value.
	 *
	 * @param min Minimnum value for integer-type
	 * @return Created definition
	 */
	static public CIntegerDef min(Integer min) {

		return range(min, null);
	}

	/**
	 * Creates an integer-type definition with the specified maximum
	 * value.
	 *
	 * @param max Maximnum value for integer-type
	 * @return Created definition
	 */
	static public CIntegerDef max(Integer max) {

		return range(null, max);
	}

	/**
	 * Creates an integer-type definition with the specified exact
	 * value.
	 *
	 * @param exact Exact value for integer-type
	 * @return Created definition
	 */
	static public CIntegerDef exact(Integer exact) {

		return range(exact, exact);
	}

	static private INumber resolveMin(Integer min) {

		return min != null ? new INumber(min) : INumber.MINUS_INFINITY;
	}

	static private INumber resolveMax(Integer max) {

		return max != null ? new INumber(max) : INumber.PLUS_INFINITY;
	}

	private CIntegerDef() {

		super(Integer.class, INumber.MINUS_INFINITY, INumber.PLUS_INFINITY);
	}

	private CIntegerDef(INumber min, INumber max) {

		super(Integer.class, min, max);
	}
}
