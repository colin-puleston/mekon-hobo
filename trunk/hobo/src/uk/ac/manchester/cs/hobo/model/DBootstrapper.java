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

package uk.ac.manchester.cs.hobo.model;

import uk.ac.manchester.cs.hobo.mechanism.*;

/**
 * Provides the HOBO mechanisms with a means of creating and
 * retrieving a {@link DModel} object together with an associated
 * {@link DBuilder} object.
 * <p>
 * NOTE: This class is only intended for use by the HOBO mechanisms
 * and should not be accessed directly by the client code.
 *
 * @author Colin Puleston
 */
public class DBootstrapper {

	private DModel model;
	private DBuilder builder;

	/**
	 * Constructor.
	 */
	protected DBootstrapper() {

		model = new DModel();
		builder = new DBuilderImpl(model);
	}

	/**
	 * Retrieves the created model object.
	 *
	 * @return Created model object
	 */
	protected DModel getModel() {

		return model;
	}

	/**
	 * Retrieves the builder for the created model object.
	 *
	 * @return Builder for created model object
	 */
	protected DBuilder getBuilder() {

		return builder;
	}
}