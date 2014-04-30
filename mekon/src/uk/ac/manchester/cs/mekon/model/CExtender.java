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
 * Responsible for the definition of and subsequent creation of for
 * defining an extension-frame (see {@link CFrame#extension}) that
 * extends a particular model-frame (see {@link CFrame#modelFrame}).
 *
 * @author Colin Puleston
 */
public class CExtender {

	private CModelFrame baseFrame;
	private String label;
	private CSlotValues slotValues = new CSlotValues();

	/**
	 * Constructor for defining an extension-frame with a default
	 * label that provides a description of the extension.
	 *
	 * @param baseFrame Model-frame to be extended
	 * @return throws KAccessException if specified base-frame is not
	 * a model-frame
	 */
	public CExtender(CFrame baseFrame) {

		this(baseFrame, null);
	}

	/**
	 * Constructor for defining an extension-frame with the specified
	 * label.
	 *
	 * @param baseFrame Model-frame to be extended
	 * @param label Label for extension-frame
	 * @return throws KAccessException if specified base-frame is not
	 * a model-frame
	 */
	public CExtender(CFrame baseFrame, String label) {

		this.baseFrame = baseFrame.asModelFrame();
		this.label = label;
	}

	/**
	 * Adds a fixed slot-value for the extension-frame.
	 *
	 * @param property Property associated with relevant slot
	 * @param value Relevant value
	 */
	public void addSlotValue(CProperty property, CValue<?> value) {

		slotValues.add(property, value);
	}

	/**
	 * Creates the required extension-frame.
	 *
	 * @return Created extension-frame, or just the base-frame if no
	 * slot-values have been specified
	 */
	public CFrame extend() {

		return slotValues.valuesDefined()
				? baseFrame.extend(label, slotValues)
				: baseFrame;
	}
}
