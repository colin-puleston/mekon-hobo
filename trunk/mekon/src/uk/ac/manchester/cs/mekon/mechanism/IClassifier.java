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

		private boolean doInferreds;
		private boolean doSuggesteds;
		private boolean doSlots;
		private boolean doSlotValues;

		private IClassifierOps classifierOps;

		Updater(IEditor iEditor, IFrame frame, Set<IUpdateOp> updateOps) {

			this.iEditor = iEditor;
			this.frame = frame;

			doInferreds = updateOps.contains(IUpdateOp.INFERRED_TYPES);
			doSlots = updateOps.contains(IUpdateOp.SLOTS);
			doSlotValues = updateOps.contains(IUpdateOp.SLOT_VALUES);

			classifierOps = getClassifierOps(updateOps);
		}

		void update() {

			IClassification classification = classify(frame, classifierOps);

			if (classifierOps.inferreds()) {

				updateFromInferreds(toCFrames(classification.getInferredTypes()));
			}

			if (classifierOps.suggesteds()) {

				updateSuggesteds(toCFrames(classification.getSuggestedTypes()));
			}
		}

		private IClassifierOps getClassifierOps(Set<IUpdateOp> updateOps) {

			boolean inferreds = doInferreds || doSlots || doSlotValues;
			boolean suggesteds = updateOps.contains(IUpdateOp.SUGGESTED_TYPES);

			return new IClassifierOps(inferreds, suggesteds);
		}

		private void updateFromInferreds(List<CFrame> inferreds) {

			if (!doInferreds || updateInferreds(inferreds)) {

				updateSlotsAndValues(inferreds);
			}
		}

		private boolean updateInferreds(List<CFrame> updates) {

			return getFrameEditor().updateInferredTypes(updates);
		}

		private void updateSuggesteds(List<CFrame> updates) {

			getFrameEditor().updateSuggestedTypes(updates);
		}

		private void updateSlotsAndValues(List<CFrame> inferredsUpdates) {

			if (doSlots || doSlotValues) {

				ISlotSpecs specs = createSlotSpecs(inferredsUpdates);

				if (doSlots) {

					specs.updateSlots(frame);
				}

				if (doSlotValues) {

					specs.updateSlotValues(frame);
				}
			}
		}

		private ISlotSpecs createSlotSpecs(List<CFrame> inferredsUpdates) {

			ISlotSpecs specs = new ISlotSpecs(iEditor);

			specs.absorb(frame.getType(), true);
			specs.absorbAll(inferredsUpdates, true);

			return specs;
		}

		private IFrameEditor getFrameEditor() {

			return iEditor.getFrameEditor(frame);
		}

		private List<CFrame> toCFrames(List<CIdentity> ids) {

			return getCModel().getFrames().getForIdentities(ids);
		}

		private CModel getCModel() {

			return frame.getType().getModel();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialiseFrame(IEditor iEditor, IFrame frame) {

		ISlotSpecs specs = new ISlotSpecs(iEditor);

		specs.absorb(frame.getType(), true);
		specs.initialiseSlots(frame);
		specs.updateSlotValues(frame);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateFrame(IEditor iEditor, IFrame frame, Set<IUpdateOp> ops) {

		new Updater(iEditor, frame, ops).update();
	}

	/**
	 * Method whose implementations will classify the specified
	 * instance-level frame.
	 *
	 * @param frame Instance-level frame to classify
	 * @param ops Types of classification operations to be performed
	 * @return Results of classification operations
	 */
	protected abstract IClassification classify(IFrame frame, IClassifierOps ops);
}