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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Provides mechanisms for initialising the sets of slots
 * for specific instance-level frames, and for dynamically
 * updating those sets based on the current states of the
 * frames.
 * <p>
 * Updates to the slot-sets can be of the following types:
 * <ul>
 *   <li>Addition of new slots
 *   <li>Removal of existing slots
 *   <li>Updates to slot value-types
 *   <li>Updates to slot default-value types
 * </ul>
 *
 * @author Colin Puleston
 */
public interface IReasoner {

	/**
	 * Adds initial set of slots for specified instance-level frame.
	 *
	 * @param iEditor Model-instantiation editor
	 * @param frame Frame to be initialised
	 */
	public void initialiseFrame(IEditor iEditor, IFrame frame);

	/**
	 * Performs selected update operations on specified instance-level
	 * frame.
	 *
	 * @param iEditor Model-instantiation editor
	 * @param frame Frame to be updated
	 * @param ops Types of update operations to be performed
	 * @return Subset of specified update operations that actually
	 * produced updates
	 */
	public Set<IUpdateOp> updateFrame(IEditor iEditor, IFrame frame, Set<IUpdateOp> ops);
}
