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
 * the MEKON framework, with a means of creating and retrieving a
 * {@link CModel} object together with associated {@link CBuilder}
 * and {@link CAccessor} objects.
 * <p>
 * NOTE: This class is only intended for use by the MEKON and
 * MEKON-extension mechanisms and should not be accessed directly
 * by the client code.
 *
 * @author Colin Puleston
 */
public class CBootstrapper {

	private CModel model;
	private CBuilder builder;

	/**
	 * Constructor that creates a model with a default customiser.
	 */
	protected CBootstrapper() {

		this(new CCustomiserDefault());
	}

	/**
	 * Constructor that creates a model with the specified customiser.
	 *
	 * @param customiser Customiser for model
	 */
	protected CBootstrapper(CCustomiser customiser) {

		model = new CModel(customiser);
		builder = new CBuilderImpl(model);
	}

	/**
	 * Retrieves the created model object.
	 *
	 * @return Created model object
	 */
	protected CModel getModel() {

		return model;
	}

	/**
	 * Retrieves the builder for the created model object.
	 *
	 * @return Builder for created model object
	 */
	protected CBuilder getBuilder() {

		return builder;
	}

	/**
	 * Retrieves the accessor for the created model.
	 *
	 * @return Accessor for created model
	 */
	protected CAccessor getAccessor() {

		return model.getAccessor();
	}

	private CBootstrapper(CModel model) {

		this.model = model;

		builder = new CBuilderImpl(model);
	}
}