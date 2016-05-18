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

package uk.ac.manchester.cs.mekon.model.serial;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents the input-data for a specific {@link IFrame}/{@link ISlot}
 * network rendering operation to be performed by a {@link IFrameRenderer}.
 *
 * @author Colin Puleston
 */
public class IFrameRenderInput {

	private IFrame topLevelFrame;
	private IUpdate update = IUpdate.NO_UPDATE;

	private Map<IFrame, String> xDocIds = null;

	/**
	 * Constructor.
	 *
	 * @param topLevelFrame Top-level frame of network to be rendered
	 */
	public IFrameRenderInput(IFrame topLevelFrame) {

		this.topLevelFrame = topLevelFrame;
	}

	/**
	 * Registers a slot-value-addition operation as the latest update that
	 * was made to the network being rendered.
	 * <p>
	 * This information should be provided when sending information to the
	 * server for automatic-update.
	 *
	 * @param slot Slot to which value was added
	 * @param addedValue Value that was added to slot
	 */
	public void registerAddition(ISlot slot, IValue addedValue) {

		update = IUpdate.createAddition(slot, addedValue);
	}

	/**
	 * Registers a slot-value(s)-removal operation as the latest update that
	 * was made to the network being rendered.
	 * <p>
	 * This information should be provided when sending information to the
	 * server for automatic-update.
	 *
	 * @param slot Slot from which value(s) were removed
	 */
	public void registerRemovals(ISlot slot) {

		update = IUpdate.createRemovals(slot);
	}

	/**
	 * Sets the map into which the document-specific frame-identifiers that
	 * are generated whilst rendering will be written. This map can also be
	 * pre-populated with any identifiers that are to be used in the rendering.
	 *
	 * @param xDocIds Map for document-specific frame-identifiers (possibly
	 * pre-populated)
	 */
	public void setXDocIds(Map<IFrame, String> xDocIds) {

		this.xDocIds = xDocIds;
	}

	IFrame getTopLevelFrame() {

		return topLevelFrame;
	}

	IUpdate getUpdate() {

		return update;
	}

	IFrameXDocIds getXDocIds() {

		return xDocIds != null ? new IFrameXDocIds(xDocIds) : new IFrameXDocIds();
	}
}
