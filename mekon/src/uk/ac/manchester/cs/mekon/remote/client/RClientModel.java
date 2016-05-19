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

package uk.ac.manchester.cs.mekon.remote.client;

import java.util.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.util.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Represents a client-side version of the MEKON frames model.
 *
 * @author Colin Puleston
 */
public abstract class RClientModel {

	private CModel cModel;
	private IEditor iEditor;

	private boolean processingIFrame = false;

	private abstract class IFrameAction {

		void checkPerform(IFrame frame) {

			if (!processingIFrame) {

				processingIFrame = true;
				perform(frame);
				processingIFrame = false;
			}
		}

		abstract RUpdates performOnServer(IFrame frame);

		private void perform(IFrame frame) {

			IFrame root = findRootFrame(frame);
			RUpdates updates = performOnServer(root);

			new NetworkAligner(iEditor, updates).align(root);
		}
	}

	private class IFrameInitAction extends IFrameAction {

		RUpdates performOnServer(IFrame frame) {

			return initialiseAssertionOnServer(frame);
		}
	}

	private class IFrameUpdateAction extends IFrameAction {

		private IValuesUpdate clientUpdate;

		IFrameUpdateAction(IValuesUpdate clientUpdate) {

			this.clientUpdate = clientUpdate;
		}

		RUpdates performOnServer(IFrame frame) {

			return updateAssertionOnServer(frame, clientUpdate);
		}
	}

	private class IFrameInitialiser implements CFrameListener {

		private IFrameInitAction initAction = new IFrameInitAction();

		public void onExtended(CFrame extension) {
		}

		public void onInstantiated(IFrame instance) {

			new ISlotInitialiser(instance);

			initAction.checkPerform(instance);
		}

		IFrameInitialiser() {

			for (CFrame frame : cModel.getFrames().asList()) {

				frame.addListener(this);
			}
		}
	}

	private class ISlotInitialiser implements IFrameListener {

		public void onUpdatedInferredTypes(CIdentifieds<CFrame> updates) {
		}

		public void onUpdatedSuggestedTypes(CIdentifieds<CFrame> updates) {
		}

		public void onSlotAdded(ISlot slot) {

			new IFrameUpdater(slot);
		}

		public void onSlotRemoved(ISlot slot) {
		}

		ISlotInitialiser(IFrame container) {

			container.addListener(this);
		}
	}

	private class IFrameUpdater implements KValuesListener<IValue> {

		private ISlot slot;
		private IFrameUpdateAction postRemovalsAction;

		public void onAdded(IValue value) {

			checkUpdate(createAdditionAction(value));
		}

		public void onRemoved(IValue value) {

			checkUpdate(postRemovalsAction);
		}

		public void onCleared(List<IValue> values) {

			checkUpdate(postRemovalsAction);
		}

		IFrameUpdater(ISlot slot) {

			this.slot = slot;

			postRemovalsAction = createRemovalsAction();

			slot.getValues().addValuesListener(this);
		}

		private IFrameUpdateAction createAdditionAction(IValue addedValue) {

			return new IFrameUpdateAction(IValuesUpdate.createAddition(slot, addedValue));
		}

		private IFrameUpdateAction createRemovalsAction() {

			return new IFrameUpdateAction(IValuesUpdate.createRemovals(slot));
		}

		private void checkUpdate(IFrameUpdateAction action) {

			action.checkPerform(slot.getContainer());
		}
	}

	/**
	 * Constructor.
	 *
	 * @param hierarchy Representation of concept-level frames hierarchy
	 * present on the server
	 */
	public RClientModel(CFrameHierarchy hierarchy) {

		CBuilder cBuilder = createCBuilder(hierarchy);

		cModel = cBuilder.build();
		iEditor = cBuilder.getIEditor();

		new IFrameInitialiser();
	}

	/**
	 * Provides the client MEKON frames model.
	 *
	 * @return Client MEKON frames model
	 */
	public CModel getCModel() {

		return cModel;
	}

	/**
	 * Sends a newly-created instance-level frame to be initialised on the
	 * server.
	 *
	 * @param frame Relevant frame
	 * @return Results of initialisation process
	 */
	protected abstract RUpdates initialiseAssertionOnServer(IFrame rootFrame);

	/**
	 * Sends an instance-level frame/slot network to be automatically
	 * updated on the server after a slot-value update has occurred on the
	 * client.
	 *
	 * @param rootFrame Root-frame of frame/slot network
	 * @param clientUpdate Relevant slot-value update
	 * @return Results of update process
	 */
	protected abstract RUpdates updateAssertionOnServer(
									IFrame rootFrame,
									IValuesUpdate clientUpdate);

	private CBuilder createCBuilder(CFrameHierarchy hierarchy) {

		CBuilder cBuilder = CManager.createEmptyBuilder();

		cBuilder.setQueriesEnabled(true);
		cBuilder.addSectionBuilder(new SectionBuilder(hierarchy));

		return cBuilder;
	}

	private IFrame findRootFrame(IFrame start) {

		return new RootFrameFinder().findFrom(start);
	}
}
