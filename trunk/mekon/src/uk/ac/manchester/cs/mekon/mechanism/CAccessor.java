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

package uk.ac.manchester.cs.mekon.mechanism;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Provides privileged access to the MEKON model for extensions
 * of the MEKON framework.
 *
 * @author Colin Puleston
 */
public interface CAccessor {

	/**
	 * Maps an object to an instance-level frame.
	 *
	 * @param frame Frame to which object is to be mapped
	 * @param mappedObject Object to be mapped
	 */
	public void setIFrameMappedObject(IFrame frame, Object mappedObject);

	/**
	 * Retrieves a mapped object from an instance-level frame.
	 *
	 * @param frame Frame from which mapped object is to be retrieved.
	 * @param expectedType Expected type of mapped object
	 * @return Mapped object, or null if no mapped object
	 * @throws KAccessException if mapped object is not of expected type
	 */
	public <T>T getIFrameMappedObject(IFrame frame, Class<T> expectedType);

	/**
	 * Provides the instantiation editor for the model.
	 *
	 * @return Instantiation editor
	 */
	public IEditor getIEditor();
}
