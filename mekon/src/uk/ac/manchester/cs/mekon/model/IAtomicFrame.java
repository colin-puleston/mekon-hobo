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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
class IAtomicFrame extends IFrame {

	static private class DynamicTypes {

		private CIdentifiedsLocal<CFrame> types = new CIdentifiedsLocal<CFrame>();

		boolean update(List<CFrame> updates) {

			if (typesMatch(updates)) {

				return false;
			}

			removeOldTypes(updates);
			addNewTypes(updates);

			return true;
		}

		CIdentifieds<CFrame> getTypes() {

			return types;
		}

		private void removeOldTypes(List<CFrame> updates) {

			for (CFrame type : types.asList()) {

				if (!updates.contains(type)) {

					types.remove(type);
				}
			}
		}

		private void addNewTypes(List<CFrame> updates) {

			for (CFrame type : updates) {

				if (!types.contains(type)) {

					types.add(type);
				}
			}
		}

		private boolean typesMatch(List<CFrame> testTypes) {

			return types.asSet().equals(new HashSet<CFrame>(testTypes));
		}
	}

	private DynamicTypes inferredTypes = new DynamicTypes();
	private DynamicTypes suggestedTypes = new DynamicTypes();

	private ISlots slots = new ISlots();

	private boolean autoUpdateEnabled = false;
	private boolean autoUpdating = false;

	private class Editor implements IFrameEditor {

		public boolean updateInferredTypes(List<CFrame> updateds) {

			if (inferredTypes.update(updateds)) {

				pollListenersForUpdatedInferredTypes();

				return true;
			}

			return false;
		}

		public boolean updateSuggestedTypes(List<CFrame> updateds) {

			if (suggestedTypes.update(updateds)) {

				pollListenersForUpdatedSuggestedTypes();

				return true;
			}

			return false;
		}

		public ISlot addSlot(CSlot slotType) {

			ISlot slot = addSlotInternal(slotType);

			pollListenersForSlotAdded(slot);

			return slot;
		}

		public ISlot addSlot(
						CIdentity identity,
						CSource source,
						CValue<?> valueType,
						CCardinality cardinality,
						CActivation activation,
						CEditability editability) {

			CSlot slotType = new CSlot(getType(), identity, valueType, cardinality);

			slotType.setSource(source);
			slotType.setActivation(activation);
			slotType.setEditability(editability);

			return addSlot(slotType);
		}

		public void removeSlot(ISlot slot) {

			slot.getValues().clearAllFixedAndAssertedValues();
			slots.remove(slot);
			pollListenersForSlotRemoved(slot);
		}

		public void setAutoUpdateEnabled(boolean enabled) {

			autoUpdateEnabled = enabled;
		}
	}

	private class AutoUpdater implements KValuesListener<IValue> {

		private ISlotValues slotValues;

		public void onAdded(IValue value) {

			checkAutoUpdates();
		}

		public void onRemoved(IValue value) {

			checkAutoUpdates();
		}

		public void onCleared(List<IValue> values) {

			checkAutoUpdates();
		}

		AutoUpdater(ISlot slot) {

			slotValues = slot.getValues();
			slotValues.addValuesListener(this);
		}

		private void checkAutoUpdates() {

			if (autoUpdateEnabled && !autoUpdating) {

				autoUpdating = true;
				autoUpdate(new HashSet<IFrame>());
				autoUpdating = false;
			}
		}
	}

	public Set<IUpdateOp> update() {

		return getIUpdating().update(this);
	}

	public Set<IUpdateOp> update(IUpdateOp... ops) {

		return update(new HashSet<IUpdateOp>(Arrays.asList(ops)));
	}

	public Set<IUpdateOp> update(Set<IUpdateOp> ops) {

		return getIUpdating().update(this, ops);
	}

	public Set<IUpdateOp> checkManualUpdate() {

		return getIUpdating().checkManualUpdate(this);
	}

	public Set<IUpdateOp> checkManualUpdate(Set<IUpdateOp> ops) {

		return getIUpdating().checkManualUpdate(this, ops);
	}

	public String getDisplayLabel() {

		return getType().getDisplayLabel();
	}

	public IFrameCategory getCategory() {

		return IFrameCategory.ATOMIC;
	}

	public boolean autoUpdateEnabled() {

		return autoUpdateEnabled;
	}

	public CIdentifieds<CFrame> getInferredTypes() {

		return inferredTypes.getTypes();
	}

	public CIdentifieds<CFrame> getSuggestedTypes() {

		return suggestedTypes.getTypes();
	}

	public ISlots getSlots() {

		return slots;
	}

	IAtomicFrame(CFrame type, IFrameFunction function, boolean freeInstance) {

		super(type, function, freeInstance);
	}

	void completeInitialInstantiation() {

		getIUpdating().initialise(this);

		super.completeInitialInstantiation();

		autoUpdateEnabled = !freeInstance();
	}

	Set<IUpdateOp> completeReinstantiation(boolean possibleModelUpdates) {

		Set<IUpdateOp> updates = checkReinitialise(possibleModelUpdates);

		super.completeReinstantiation(possibleModelUpdates);

		autoUpdateEnabled = !freeInstance();

		return updates;
	}

	IFrameEditor createEditor() {

		return new Editor();
	}

	ISlot addSlotInternal(CSlot slotType) {

		return addSlotInternal(new ISlot(slotType.copy(), this));
	}

	ISlot addSlotInternal(ISlot slot) {

		slots.add(slot);
		IFrameSlotValueUpdateProcessor.checkAddTo(slot);

		new AutoUpdater(slot);

		return slot;
	}

	IFrame copyEmpty(IFrameFunction copyFunction, boolean freeInstance) {

		return new IAtomicFrame(getType(), copyFunction, freeInstance);
	}

	void autoUpdate(Set<IFrame> visited) {

		if (visited.add(this)) {

			autoUpdateThis();
			autoUpdateReferencingFrames(visited);
		}
	}

	boolean updateInferredTypes(List<CFrame> updateds) {

		return inferredTypes.update(updateds);
	}

	String describeLocally() {

		return FEntityDescriber.entityToString(this, getType());
	}

	private Set<IUpdateOp> checkReinitialise(boolean possibleModelUpdates) {

		if (possibleModelUpdates && !freeInstance()) {

			return getIUpdating().reinitialise(this);
		}

		return Collections.<IUpdateOp>emptySet();
	}

	private void autoUpdateThis() {

		IUpdating updating = getIUpdating();

		while (updating.checkAutoUpdate(this).contains(IUpdateOp.SLOT_VALUES));
	}

	private void checkAutoUpdateContainer(ISlot refSlot, Set<IFrame> visited) {

		IFrame container = refSlot.getContainer();

		if (container instanceof IAtomicFrame) {

			((IAtomicFrame)container).autoUpdate(visited);
		}
	}

	private void pollListenersForSlotAdded(ISlot slot) {

		for (IFrameListener listener : copyListeners()) {

			listener.onSlotAdded(slot);
		}
	}

	private void pollListenersForSlotRemoved(ISlot slot) {

		for (IFrameListener listener : copyListeners()) {

			listener.onSlotRemoved(slot);
		}
	}

	private void pollListenersForUpdatedInferredTypes() {

		CIdentifieds<CFrame> updates = inferredTypes.getTypes();

		for (IFrameListener listener : copyListeners()) {

			listener.onUpdatedInferredTypes(updates);
		}
	}

	private void pollListenersForUpdatedSuggestedTypes() {

		CIdentifieds<CFrame> updates = suggestedTypes.getTypes();

		for (IFrameListener listener : copyListeners()) {

			listener.onUpdatedSuggestedTypes(updates);
		}
	}

	private IUpdating getIUpdating() {

		return getModel().getIUpdating();
	}

	private CModel getModel() {

		return getType().getModel();
	}
}
