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

package uk.ac.manchester.cs.mekon.remote;

/**
 * Responsible for creating and serialisation of {@link RNumberRange}
 * objects. The parameterless constructor and relevant sets of "get"
 * and "set" methods are designed to enable JSON serialisation.
 *
 * @author Colin Puleston
 */
public class RNumberRangeSpec {

	private Class<? extends Number> numberType;
	private Number min;
	private Number max;

	/**
	 * Constructor.
	 */
	public RNumberRangeSpec() {
	}

	/**
	 * Sets value of type.
	 *
	 * @param type Value to set
	 */
	public void setNumberType(Class<? extends Number> numberType) {

		this.numberType = numberType;
	}

	/**
	 * Sets value of min.
	 *
	 * @param min Value to set
	 */
	public void setMin(Number min) {

		this.min = min;
	}

	/**
	 * Sets value of max.
	 *
	 * @param max Value to set
	 */
	public void setMax(Number max) {

		this.max = max;
	}

	/**
	 * Gets value of type.
	 *
	 * @return Relevant value
	 */
	public Class<? extends Number> getNumberType() {

		return numberType;
	}

	/**
	 * Gets value of min.
	 *
	 * @return Relevant value
	 */
	public Number getMin() {

		return min;
	}

	/**
	 * Gets value of max.
	 *
	 * @return Relevant value
	 */
	public Number getMax() {

		return max;
	}

	RNumberRange create() {

		return new RNumberRange(numberType, min, max);
	}
}

