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

package uk.ac.manchester.cs.mekon.model.util;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Builder for a {@link CFrameHierarchy} object.
 *
 * @author Colin Puleston
 */
public class CFrameHierarchyBuilder {

	private CFrameHierarchy hierarchy;

	/**
	 * Constructor.
	 *
	 * @param frameId Identity of root-frame for hierarchy
	 */
	public CFrameHierarchyBuilder(CIdentity rootFrameId) {

		hierarchy = new CFrameHierarchy(rootFrameId);
	}

	/**
	 * Adds a new sub-frame relationship for a frame that has been previously
	 * added, either as the root-frame or, via this method, as a sub-frame.
	 * The specified sub-frame may or may not have been previously added.
	 *
	 * @param frameId Identity of existing frame
	 * @param subFrameId Identity of sub-frame of existing frame
	 * @return True if sub-frame has not been previously added
	 * @throws KAccessException if frame to which sub-frame is to added not
	 * present in hierarchy
	 */
	public boolean addSub(CIdentity frameId, CIdentity subFrameId) {

		return hierarchy.addSub(frameId, subFrameId);
	}

	/**
	 * Adds a value for the specified annotation on the specified frame.
	 *
	 * @param frameId Identity of relevant frame
	 * @param key Key for relevant annotation
	 * @return Value for specified annotation
	 * @throws KAccessException if specified frame not present in hierarchy
	 */
	public void addAnnotation(CIdentity frameId, Object key, Object value) {

		hierarchy.addAnnotation(frameId, key, value);
	}

	/**
	 * Provides the object that has-been/is-being built
	 *
	 * @return Object that has-been/is-being built
	 */
	public CFrameHierarchy getHierarchy() {

		return hierarchy;
	}
}
