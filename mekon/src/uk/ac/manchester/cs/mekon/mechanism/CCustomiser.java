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
 * Allows extensions of the MEKON system to intervene in, and
 * modify, the default MEKON behaviour.
 *
 * @author Colin Puleston
 */
public interface CCustomiser {

	/**
	 * Method invoked when a frame is added to the model.
	 *
	 * @param frame Added frame
	 */
	public void onFrameAdded(CFrame frame);

	/**
	 * Method invoked when a frame is removed to from model.
	 *
	 * @param frame Removed frame
	 */
	public void onFrameRemoved(CFrame frame);

	/**
	 * Method invoked when a slot is removed from a model-frame.
	 *
	 * @param slot Removed slot
	 */
	public void onSlotRemoved(CSlot slot);

	/**
	 * Checks whether a frame is mapped to some object that cannot
	 * be instantiated, which will mean that the frame itself
	 * cannot be instantiated.
	 *
	 * @param frame Frame to check
	 * @return True if frame is mapped to a non-instantiable object
	 */
	public boolean mappedToNonInstantiableObject(CFrame frame);
}
