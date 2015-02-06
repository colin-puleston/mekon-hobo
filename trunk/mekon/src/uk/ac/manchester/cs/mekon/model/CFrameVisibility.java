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
 * Represents the "visibility" status requirements for the
 * selection of collections of {@link CFrame} objects.
 *
 * @author Colin Puleston
 */
public enum CFrameVisibility {

	/**
	 * All frames of whatever visibility status.
	 */
	ALL,

	/**
	 * Only frames with "exposed" visibility status.
	 */
	EXPOSED,

	/**
	 * Only frames with "hidden" visibility status.
	 */
	HIDDEN;

	/**
	 * Specifies whether this visibility status covers "exposed".
	 *
	 * @return true if visibility status covers "exposed"
	 */
	public boolean coversExposed() {

		return coversVisibility(EXPOSED);
	}

	/**
	 * Specifies whether this visibility status covers "hidden".
	 *
	 * @return true if visibility status covers "hidden"
	 */
	public boolean coversHidden() {

		return coversVisibility(HIDDEN);
	}

	/**
	 * Specifies whether this visibility status covers the specified
	 * visibility status.
	 *
	 * @param visibility Visibility status to test for
	 * @return true if visibility status covers specified visibility
	 * status
	 */
	public boolean coversVisibility(CFrameVisibility visibility) {

		return this == ALL || this == visibility;
	}

	/**
	 * Specifies whether this visibility status covers the specified
	 * "hidden" status.
	 *
	 * @param hidden Hidden status to test for
	 * @return true if visibility status covers specified "hidden"
	 * status
	 */
	public boolean coversHiddenStatus(boolean hidden) {

		return hidden ? coversHidden() : coversExposed();
	}
}
