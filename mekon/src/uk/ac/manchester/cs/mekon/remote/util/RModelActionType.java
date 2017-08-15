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

package uk.ac.manchester.cs.mekon.remote.util;

/**
 * Specifies a particular action over the remote frames model.
 *
 * @author Colin Puleston
 */
public enum RModelActionType {

	/**
	 * Specifies a complete frames-hierarchy retrieval action
	 */
	GET_FRAME_HIERARCHY,

	/**
	 * Specifies an assertion-instance initialisation action
	 */
	INITIALISE_ASSERTION,

	/**
	 * Specifies an query-instance initialisation action
	 */
	INITIALISE_QUERY,

	/**
	 * Specifies an assertion-instance update action
	 */
	UPDATE_ASSERTION,

	/**
	 * Specifies an query-instance update action
	 */
	UPDATE_QUERY
}
