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

package uk.ac.manchester.cs.mekon.model.motor;

/**
 * Specifies the type(s) of classification operations required.
 *
 * @author Colin Puleston
 */
public class IClassifierOps {

	private boolean inferreds;
	private boolean suggesteds;

	/**
	 * True if a classification operation to retrieve the
	 * inferred-types is required.
	 *
	 * @return True if inferred-typres required
	 */
	public boolean inferreds() {

		return inferreds;
	}

	/**
	 * True if a classification operation to retrieve the
	 * suggested-types is required.
	 *
	 * @return True if suggested-typres required
	 */
	public boolean suggesteds() {

		return suggesteds;
	}

	IClassifierOps(boolean inferreds, boolean suggesteds) {

		this.inferreds = inferreds;
		this.suggesteds = suggesteds;
	}
}
