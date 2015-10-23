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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Responsible for finding the most-general members of a specified
 * set of concept-level frames.
 *
 * @author Colin Puleston
 */
public class MostGeneralCFrames extends MostExtremeCFrames {

	/**
	 * Constructor.
	 */
	public MostGeneralCFrames() {
	}

	/**
	 * Constructor.
	 *
	 * @param frames Frames with which to initialise the set of
	 * most-general frames.
	 */
	public MostGeneralCFrames(Collection<CFrame> frames) {

		update(frames);
	}

	boolean firstIsMoreExtreme(CFrame first, CFrame second) {

		return first.subsumes(second);
	}

	List<CFrame> getNextMoreExtremes(CFrame current) {

		return current.getSupers();
	}
}