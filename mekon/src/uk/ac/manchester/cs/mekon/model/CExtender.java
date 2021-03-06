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

import uk.ac.manchester.cs.mekon_util.*;

/**
 * Responsible for the definition of and subsequent creation of an
 * extension-frame (see {@link CFrameCategory#extension}) that extends
 * a particular atomic-frame (see {@link CFrameCategory#atomic}).
 *
 * @author Colin Puleston
 */
public class CExtender {

	private CAtomicFrame baseFrame;
	private String label;
	private CSlotValues slotValues = new CSlotValues();
	private boolean concrete = false;

	/**
	 * Constructor for defining an extension-frame with a default
	 * label that provides a description of the extension.
	 *
	 * @param baseFrame Atomic-frame to be extended
	 * @throws KAccessException if specified base-frame is not an
	 * atomic-frame
	 */
	public CExtender(CFrame baseFrame) {

		this(baseFrame, null);
	}

	/**
	 * Constructor for defining an extension-frame with the specified
	 * label.
	 *
	 * @param baseFrame Atomic-frame to be extended
	 * @param label Label for extension-frame
	 * @throws KAccessException if specified base-frame is not an
	 * atomic-frame
	 */
	public CExtender(CFrame baseFrame, String label) {

		this.baseFrame = baseFrame.asAtomicFrame();
		this.label = label;
	}

	/**
	 * Used to specify whether the extension should be "concrete",
	 * rather than abstract (see {@link CFrame}). By default the
	 * extension will be abstract.
	 *
	 * @param concrete True if extension is to be concrete
	 */
	public void setConcreteExtension(boolean concrete) {

		this.concrete = concrete;
	}

	/**
	 * Adds a fixed slot-value for the extension-frame.
	 *
	 * @param slotId Identity of relevant slot
	 * @param value Relevant value
	 */
	public void addSlotValue(CIdentity slotId, CValue<?> value) {

		slotValues.add(slotId, value);
	}

	/**
	 * Creates the required extension-frame.
	 *
	 * @return Created extension-frame, or just the base-frame if no
	 * slot-values have been specified
	 */
	public CFrame extend() {

		return slotValues.valuesDefined()
				? baseFrame.extend(label, slotValues, concrete)
				: baseFrame;
	}
}
