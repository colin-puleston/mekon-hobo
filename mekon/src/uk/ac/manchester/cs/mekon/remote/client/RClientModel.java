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
import uk.ac.manchester.cs.mekon_util.*;

/**
 * Represents a client-side version of the MEKON frames model.
 *
 * @author Colin Puleston
 */
public abstract class RClientModel {

	private CModel cModel;
	private IEditor iEditor;

	private Deque<IFrame> actionSubjectStack = new ArrayDeque<IFrame>();

	private abstract class InstanceAction {

		void checkPerform(IFrame frame) {

			if (!actionSubjectStack.contains(frame)) {

				actionSubjectStack.push(frame);
				perform(frame);
				actionSubjectStack.pop();
			}
		}

		abstract RUpdates performOnServer(IFrame frame);

		private void perform(IFrame frame) {

			IFrame root = findRootFrame(frame);
			RUpdates updates = performOnServer(root);

			new InstanceAligner(iEditor, updates).align(root);
		}
	}

	private class InstanceCreateInitAction extends InstanceAction {

		RUpdates performOnServer(IFrame frame) {

			return initialiseOnServer(frame);
		}
	}

	private class InstanceReloadInitAction extends InstanceAction {

		RUpdates performOnServer(IFrame frame) {

			return updateOnServer(frame);
		}
	}

	private class InstanceUpdateAction extends InstanceAction {

		private IValuesUpdate clientUpdate;

		InstanceUpdateAction(IValuesUpdate clientUpdate) {

			this.clientUpdate = clientUpdate;
		}

		RUpdates performOnServer(IFrame frame) {

			return updateOnServer(frame, clientUpdate);
		}
	}

	private class IFrameUpdater implements KValuesListener<IValue> {

		private ISlot slot;
		private InstanceUpdateAction removalsAction;

		public void onAdded(IValue value) {

			checkUpdate(createAdditionAction(value));
		}

		public void onRemoved(IValue value) {

			checkUpdate(removalsAction);
		}

		public void onCleared(List<IValue> values) {

			checkUpdate(removalsAction);
		}

		IFrameUpdater(ISlot slot) {

			this.slot = slot;

			removalsAction = createRemovalsAction();

			slot.getValues().addValuesListener(this);
		}

		private InstanceUpdateAction createAdditionAction(IValue addedValue) {

			return new InstanceUpdateAction(IValuesUpdate.createAddition(slot, addedValue));
		}

		private InstanceUpdateAction createRemovalsAction() {

			return new InstanceUpdateAction(IValuesUpdate.createRemovals(slot));
		}

		private void checkUpdate(InstanceUpdateAction action) {

			action.checkPerform(slot.getContainer());
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

		ISlotInitialiser(IFrame container, boolean reloaded) {

			if (reloaded) {

				initialseReloadedStructure(container);
			}

			container.addListener(this);
		}

		private void initialseReloadedStructure(IFrame container) {

			for (ISlot slot : container.getSlots().asList()) {

				new IFrameUpdater(slot);

				if (slot.getValueType() instanceof CFrame) {

					for (IValue value : slot.getValues().asList()) {

						new ISlotInitialiser((IFrame)value, true);
					}
				}
			}
		}
	}

	private class IFrameInitialisingReasoner implements IReasoner {

		private InstanceCreateInitAction createInitAction = new InstanceCreateInitAction();
		private InstanceReloadInitAction reloadInitAction = new InstanceReloadInitAction();

		public void initialise(IFrame frame, IEditor iEditor, boolean initSlotValues) {

			initialise(frame, createInitAction);
		}

		public Set<IUpdateOp> reinitialise(IFrame frame, IEditor iEditor, Set<IUpdateOp> ops) {

			initialise(frame, reloadInitAction);

			return Collections.<IUpdateOp>emptySet();
		}

		public Set<IUpdateOp> update(IFrame frame, IEditor iEditor, Set<IUpdateOp> ops) {

			return Collections.<IUpdateOp>emptySet();
		}

		private void initialise(IFrame frame, InstanceAction initAction) {

			initAction.checkPerform(frame);

			new ISlotInitialiser(frame, false);
		}
	}

	/**
	 * Constructor.
	 *
	 * @param hierarchy Representation of concept-level frames hierarchy
	 * present on the server
	 */
	public RClientModel(CHierarchy hierarchy) {

		CBuilder cBuilder = createCBuilder(hierarchy);

		cModel = cBuilder.build();
		iEditor = cBuilder.getIEditor();
	}

	/**
	 * Performs required initialisations on instance-level frame/slot
	 * network retrieved from store.
	 *
	 * @param rootFrame Root-frame of frame/slot network
	 */
	public void initialiseReloadedInstance(IFrame rootFrame) {

		new ISlotInitialiser(rootFrame, true);
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
	 * Sends a newly-created instance-level frame/slot network to be
	 * initialised on the server.
	 *
	 * @param rootFrame Root-frame of frame/slot network
	 * @return Results of initialisation process
	 */
	protected abstract RUpdates initialiseOnServer(IFrame rootFrame);

	/**
	 * Sends an instance-level frame/slot network that has been reloaded
	 * from a client-side store to be automatically updated on the server.
	 *
	 * @param rootFrame Root-frame of frame/slot network
	 * @return Results of update process
	 */
	protected abstract RUpdates updateOnServer(IFrame rootFrame);

	/**
	 * Sends an instance-level frame/slot network to be automatically
	 * updated on the server after a slot-value update has occurred on the
	 * client.
	 *
	 * @param rootFrame Root-frame of frame/slot network
	 * @param clientUpdate Relevant slot-value update
	 * @return Results of update process
	 */
	protected abstract RUpdates updateOnServer(IFrame rootFrame, IValuesUpdate clientUpdate);

	private CBuilder createCBuilder(CHierarchy hierarchy) {

		CBuilder cBuilder = CManager.createEmptyBuilder();

		cBuilder.setQueriesEnabled(true);
		cBuilder.addSectionBuilder(createSectionBuilder(hierarchy));

		return cBuilder;
	}

	private RClientSectionBuilder createSectionBuilder(CHierarchy hierarchy) {

		return new RClientSectionBuilder(hierarchy, new IFrameInitialisingReasoner());
	}

	private IFrame findRootFrame(IFrame start) {

		return new RootFrameFinder().findFrom(start);
	}
}
