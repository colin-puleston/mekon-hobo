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

/**
 * Listener for dynamic operations on a {@link IFrame} object.
 *
 * @author Colin Puleston
 */
public interface IFrameListener {

	/**
	 * Method invoked after the set of inferred-types for the
	 * frame has been updated.
	 *
	 * @param updates New inferred-types for frame
	 */
	public void onUpdatedInferredTypes(CIdentifieds<CFrame> updates);

	/**
	 * Method invoked after the set of suggested-types for the
	 * frame has been updated.
	 *
	 * @param updates New suggested-types for frame
	 */
	public void onUpdatedSuggestedTypes(CIdentifieds<CFrame> updates);

	/**
	 * Method invoked after a slot has been added to the frame.
	 *
	 * @param slot Added slot
	 */
	public void onSlotAdded(ISlot slot);

	/**
	 * Method invoked after a slot has been removed from the frame.
	 *
	 * @param slot Removed slot
	 */
	public void onSlotRemoved(ISlot slot);
}
