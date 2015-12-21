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

package uk.ac.manchester.cs.mekon.model.motor;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents the results of an (@link IFrame}-classification as
 * enacted via an {@link IClassifier} object.
 *
 * @author Colin Puleston
 */
public class IClassification {

	private List<CIdentity> inferredTypes;
	private List<CIdentity> suggestedTypes;

	/**
	 * Constructor.
	 *
	 * @param inferredTypes Identities of all relevant frames (see
	 * {@link #getInferredTypes}
	 * @param suggestedTypes Identities of all relevant frames (see
	 * {@link #getSuggestedTypes}
	 */
	public IClassification(
			List<CIdentity> inferredTypes,
			List<CIdentity> suggestedTypes) {

		this.inferredTypes = inferredTypes;
		this.suggestedTypes = suggestedTypes;
	}

	/**
	 * Provides the identities of all concept-level frames of which
	 * the instance-level frame is a direct instance.
	 *
	 * @return Identities of all relevant frames
	 */
	public List<CIdentity> getInferredTypes() {

		return inferredTypes;
	}

	/**
	 * Provides the identities of all concept-level frames that are
	 * direct children of a concept-level version of the instance-level
	 * frame.
	 *
	 * @return Identities of all relevant frames
	 */
	public List<CIdentity> getSuggestedTypes() {

		return suggestedTypes;
	}
}
