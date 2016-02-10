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
 * Specifies the policy for frame-valued slot creation.
 *
 * @author Colin Puleston
 */
public enum OBFrameSlotsPolicy {

	/**
	 * No policy has been defined.
	 */
	UNSPECIFIED,

	/**
	 * All frame-valued slots are to {@link IFrame}-valued.
	 */
	IFRAME_VALUED_ONLY,

	/**
	 * All frame-valued slots are to {@link CFrame}-valued.
	 */
	CFRAME_VALUED_ONLY,

	/**
	 * Frame-valued slots are to {@link CFrame}-valued when
	 * there are no slots attached any of the frames within the
	 * section of hierarchy that defines the possible slot
	 * values. Otherwise they are to be {@link IFrame}-valued.
	 */
	CFRAME_VALUED_IF_NO_STRUCTURE;
}
