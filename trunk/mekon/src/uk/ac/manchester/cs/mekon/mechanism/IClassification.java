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

package uk.ac.manchester.cs.mekon.mechanism;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents the results of an (@link IFrame}-classification process
 * as enacted by {@link IClassifier}.
 *
 * @author Colin Puleston
 */
public class IClassification {

	private List<CIdentity> inferredTypes;
	private List<CIdentity> suggestedTypes;

	/**
	 * Constructor.
	 *
	 * @param inferredTypes Identities of all concept-level frames
	 * of which the instance-level frame is a direct instance
	 * @param suggestedTypes Identities of all concept-level frames
	 * that are direct children of a concept-level version of the
	 * instance-level frame
	 */
	public IClassification(
			List<CIdentity> inferredTypes,
			List<CIdentity> suggestedTypes) {

		this.inferredTypes = inferredTypes;
		this.suggestedTypes = suggestedTypes;
	}

	List<CFrame> getInferredTypes(CModel model) {

		return toCFrames(model, inferredTypes);
	}

	List<CFrame> getSuggestedTypes(CModel model) {

		return toCFrames(model, suggestedTypes);
	}

	private List<CFrame> toCFrames(CModel model, List<CIdentity> ids) {

		List<CFrame> cFrames = new ArrayList<CFrame>();
		CIdentifieds<CFrame> modelFrames = model.getFrames();

		for (CIdentity id : ids) {

			if (modelFrames.containsValueFor(id)) {

				cFrames.add(modelFrames.get(id));
			}
		}

		return cFrames;
	}
}
