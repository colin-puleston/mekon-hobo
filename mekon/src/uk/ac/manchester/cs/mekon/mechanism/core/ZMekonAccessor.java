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

package uk.ac.manchester.cs.mekon.mechanism.core;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * Provides the MEKON mechanisms, and the mechanisms of any
 * extensions of the MEKON framework, with privileged access to
 * the MEKON model.
 * <p>
 * THIS CLASS SHOULD NOT BE ACCESSED DIRECTLY BY EITHER THE CLIENT
 * OR THE PLUGIN CODE.
 *
 * @author Colin Puleston
 */
public interface ZMekonAccessor {

	/**
	 * Provides the model object.
	 *
	 * @return Model object
	 */
	public CModel getModel();

	/**
	 * Creates a builder for the model.
	 *
	 * @return Created builder
	 */
	public CBuilder createBuilder();

	/**
	 * Provides the instantiation editor for the model.
	 *
	 * @return Instantiation editor
	 */
	public IEditor getIEditor();

	/**
	 * Provides the free-instantiator object for the model.
	 *
	 * @return Free-instantiator object
	 */
	public ZFreeInstantiator getFreeInstantiator();

	/**
	 * Provides the object-model mapper for the model.
	 *
	 * @return Object-model mapper
	 */
	public ZObjectModelMapper getObjectModelMapper();
}
