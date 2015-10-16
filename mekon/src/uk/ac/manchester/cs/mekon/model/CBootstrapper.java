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

import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * Provides the MEKON mechanisms, and those of any extensions of
 * the MEKON framework, with a means of creating a {@link CModel}
 * object, and retrieving the associated {@link CAccessor} object.
 * <p>
 * NOTE: This class is only intended for use by the MEKON and
 * MEKON-extension mechanisms and should not be accessed directly
 * by the client code.
 *
 * @author Colin Puleston
 */
public class CBootstrapper {

	/**
	 * Constructor.
	 */
	protected CBootstrapper() {
	}

	/**
	 * Creates an empty model with a default customiser.
	 *
	 * @return Accessor for model
	 */
	protected CAccessor start() {

		return start(new CCustomiserDefault());
	}

	/**
	 * Creates an empty model with the specified customiser.
	 *
	 * @param customiser Customiser for model
	 * @return Accessor for model
	 */
	protected CAccessor start(CCustomiser customiser) {

		return new CAccessorImpl(customiser);
	}

	/**
	 * Retrieves the accessor for the model.
	 *
	 * @param model Model for which accessor is required
	 * @return Accessor for model
	 */
	protected CAccessor access(CModel model) {

		return new CAccessorImpl(model);
	}
}