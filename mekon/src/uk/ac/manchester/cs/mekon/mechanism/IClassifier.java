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
 * Provides classification-based versions of the reasoning
 * mechanisms defined by {@link IReasoner}. The actual
 * classification mechanisms are provided by derived classes.
 *
 * @author Colin Puleston
 */
public abstract class IClassifier implements IReasoner {

	private class Updater {

		private IEditor iEditor;
		private IFrame frame;

		Updater(IEditor iEditor, IFrame frame) {

			this.iEditor = iEditor;
			this.frame = frame;
		}

		boolean update() {

			CModel model = frame.getType().getModel();
			IClassification classification = classify(frame);

			updateSuggesteds(classification.getSuggestedTypes(model));

			List<CFrame> inferreds = classification.getInferredTypes(model);

			return updateInferreds(inferreds) && updateSlots(inferreds);
		}

		private boolean updateInferreds(List<CFrame> updates) {

			return getFrameEditor().updateInferredTypes(updates);
		}

		private boolean updateSuggesteds(List<CFrame> updates) {

			return getFrameEditor().updateSuggestedTypes(updates);
		}

		private boolean updateSlots(List<CFrame> inferredTypes) {

			ISlotSpecs specs = new ISlotSpecs(iEditor);

			specs.absorb(frame.getType(), true);
			specs.absorbAll(inferredTypes, true);

			return specs.updateSlots(frame);
		}

		private IFrameEditor getFrameEditor() {

			return iEditor.getFrameEditor(frame);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialiseFrame(IEditor iEditor, IFrame frame) {

		ISlotSpecs specs = new ISlotSpecs(iEditor);

		specs.absorb(frame.getType(), true);
		specs.initialiseSlots(frame);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean updateFrame(IEditor iEditor, IFrame frame) {

		return new Updater(iEditor, frame).update();
	}

	/**
	 * Method whose implementations will classify the specified
	 * instance-level frame.
	 *
	 * @param frame Instance-level frame to classify
	 * @return Results of classification operation
	 */
	protected abstract IClassification classify(IFrame frame);
}
