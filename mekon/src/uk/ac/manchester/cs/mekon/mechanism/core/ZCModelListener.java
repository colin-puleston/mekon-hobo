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

/**
 * THIS CLASS SHOULD NOT BE ACCESSED DIRECTLY BY EITHER THE CLIENT
 * OR THE PLUGIN CODE.
 * <p>
 * Listener for events concerned with the creation and modification
 * of a {@link CModel}.
 *
 * @author Colin Puleston
 */
public abstract class ZCModelListener {

	/**
	 * Removes the listener from the static register for the model.
	 *
	 * @param model Relevant model
	 */
	public void remove(CModel model) {

		ZCModelAccessor.get().removeListener(model, this);
	}

	/**
	 * Method invoked after a frame has been added to a model.
	 *
	 * @param frame Added frame
	 */
	public abstract void onFrameAdded(CFrame frame);

	/**
	 * Method invoked after a frame has been removed to from a model.
	 *
	 * @param frame Removed frame
	 */
	public abstract void onFrameRemoved(CFrame frame);

	/**
	 * Method invoked after a slot has been removed from an
	 * atomic-frame.
	 *
	 * @param slot Removed slot
	 */
	public abstract void onSlotRemoved(CSlot slot);

	/**
	 * Method invoked after the model-build process has completed.
	 */
	public abstract void onBuildComplete();

	/**
	 * Constructor that adds the listener to the static register
	 * for the model.
	 *
	 * @param model Relevant model
	 */
	protected ZCModelListener(CModel model) {

		ZCModelAccessor.get().addListener(model, this);
	}
}
