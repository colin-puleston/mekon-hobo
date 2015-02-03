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

package uk.ac.manchester.cs.mekon.owl.build;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents a set of attributes that will determine the details
 * of the entities generated in the Frames Model (FM) for a
 * particular OWL property.
 *
 * @author Colin Puleston
 */
public class OBPropertyAttributes {

	private boolean frameSource = false;
	private boolean dependent = false;
	private boolean abstractAssertable = false;

	/**
	 * Sets a value for the attribute that determines whether the
	 * property, in addition to any slots that it will be used to
	 * generate, will also be used to generate a frame in the
	 * frames-model.
	 *
	 * @param value Value for attribute
	 */
	public void setFrameSource(boolean value) {

		frameSource = value;
	}

	/**
	 * Sets a value for the attribute that determines whether the
	 * property will be used to generate frames-model slots that
	 * are {@link CSlot#dependent}.
	 *
	 * @param value Value for attribute
	 */
	public void setDependent(boolean value) {

		dependent = value;
	}

	/**
	 * Sets a value for the attribute that determines whether the
	 * property will be used to generate frames-model slots that
	 * are {@link CSlot#abstractAssertable}.
	 *
	 * @param value Value for attribute
	 */
	public void setAbstractAssertable(boolean value) {

		abstractAssertable = value;
	}

	boolean frameSource() {

		return frameSource;
	}

	boolean dependent() {

		return dependent;
	}

	boolean abstractAssertable() {

		return abstractAssertable;
	}
}
