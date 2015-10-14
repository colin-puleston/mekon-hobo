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
import uk.ac.manchester.cs.mekon.mechanism.network.*;

/**
 * Provides classification-based versions of the reasoning mechanisms
 * defined by {@link IReasoner}. This is an abstract class that leaves
 * the implementatation of the actual classification mechanisms to the
 * derived class.
 * <p>
 * The instance-level frames that are passed into the top-level
 * classification method, are converted into the intermediate network
 * representations that the abstract methods implemented by the derived
 * classes operate on.
 * <p>
 * The classification process can be customised by adding one or more
 * pre-processors to modify the networks that will be passed to the
 * methods on the derived class (see {@link #addPreProcessor}) .
 *
 * @author Colin Puleston
 */
public abstract class IClassifier extends DefaultIReasoner {

	private NNetworkManager networkManager = new NNetworkManager();

	private class Updater {

		private IEditor iEditor;
		private IFrame frame;

		private boolean doInferreds;
		private boolean doSuggesteds;
		private boolean doSlots;
		private boolean doSlotValues;

		private IClassifierOps classifierOps;
		private Set<IUpdateOp> enactedUpdateOps = new HashSet<IUpdateOp>();

		Updater(IEditor iEditor, IFrame frame, Set<IUpdateOp> updateOps) {

			this.iEditor = iEditor;
			this.frame = frame;

			doInferreds = updateOps.contains(IUpdateOp.INFERRED_TYPES);
			doSlots = updateOps.contains(IUpdateOp.SLOTS);
			doSlotValues = updateOps.contains(IUpdateOp.SLOT_VALUES);

			classifierOps = getClassifierOps(updateOps);
		}

		Set<IUpdateOp> update() {

			IClassification classification = classify(frame, classifierOps);

			if (classifierOps.inferreds()) {

				updateForInferreds(toCFrames(classification.getInferredTypes()));
			}

			if (classifierOps.suggesteds()) {

				updateForSuggesteds(toCFrames(classification.getSuggestedTypes()));
			}

			return enactedUpdateOps;
		}

		private IClassifierOps getClassifierOps(Set<IUpdateOp> updateOps) {

			boolean inferreds = doInferreds || doSlots || doSlotValues;
			boolean suggesteds = updateOps.contains(IUpdateOp.SUGGESTED_TYPES);

			return new IClassifierOps(inferreds, suggesteds);
		}

		private void updateForInferreds(List<CFrame> inferreds) {

			if (!doInferreds || updateInferreds(inferreds)) {

				if (doInferreds) {

					enactedUpdateOps.add(IUpdateOp.INFERRED_TYPES);
				}

				updateSlotsAndValues(inferreds);
			}
		}

		private void updateForSuggesteds(List<CFrame> suggesteds) {

			if (updateSuggesteds(suggesteds)) {

				enactedUpdateOps.add(IUpdateOp.INFERRED_TYPES);
			}
		}

		private boolean updateInferreds(List<CFrame> updates) {

			return getFrameEditor().updateInferredTypes(updates);
		}

		private boolean updateSuggesteds(List<CFrame> updates) {

			return getFrameEditor().updateSuggestedTypes(updates);
		}

		private void updateSlotsAndValues(List<CFrame> inferredsUpdates) {

			if (doSlots || doSlotValues) {

				ISlotSpecs specs = createSlotSpecs(inferredsUpdates);
				ISlotOps ops = ISlotOps.get(doSlots, doSlotValues);
				ISlotOps enactedOps = specs.update(frame, ops);

				enactedUpdateOps.addAll(enactedOps.asUpdateOps());
			}
		}

		private ISlotSpecs createSlotSpecs(List<CFrame> inferredsUpdates) {

			ISlotSpecs specs = new ISlotSpecs(iEditor, frame.getType());

			specs.absorbAll(inferredsUpdates);

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
	 * Registers a pre-processor to perform certain required
	 * modifications to appropriate representations of instances that
	 * are about to be classified.
	 *
	 * @param preProcessor Pre-processor for instances about to be
	 * classified
	 */
	public void addPreProcessor(NNetworkProcessor preProcessor) {

		networkManager.addPreProcessor(preProcessor);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IUpdateOp> updateFrame(IEditor iEditor, IFrame frame, Set<IUpdateOp> ops) {

		return new Updater(iEditor, frame, ops).update();
	}

	/**
	 * Handles the required classification by first converting
	 * the frame-based instance representation into the network-based
	 * version, then running any registered pre-processors over the
	 * resulting network, then finally invoking the
	 * {@link #classify(NNode, IClassifierOps)} method to perform the
	 * actual classification..
	 *
	 * @param instance Instance to classify
	 * @param ops Types of classification operations to be performed
	 * @return Results of classification operations
	 */
	protected IClassification classify(IFrame instance, IClassifierOps ops) {

		return classify(networkManager.createNetwork(instance), ops);
	}

	/**
	 * Method whose implementations will classify the specified
	 * network representation of an instance.
	 *
	 * @param instance Instance to classify
	 * @param ops Types of classification operations to be performed
	 * @return Results of classification operations
	 */
	protected abstract IClassification classify(NNode instance, IClassifierOps ops);
}
