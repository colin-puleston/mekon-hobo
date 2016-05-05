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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * @author Colin Puleston
 */
class Updater {

	private IEditor iEditor;

	private IFrame updateRoot;

	private Map<IFrame, IFrame> mastersToUpdates;
	private Map<IFrame, IFrame> updatesToMasters = new HashMap<IFrame, IFrame>();

	private Set<IFrame> updatedMasters = new HashSet<IFrame>();

	private class IFrameUpdater {

		private IFrame master;
		private IFrame update;

		private IFrameEditor masterEd;

		IFrameUpdater(IFrame master, IFrame update) {

			this.master = master;
			this.update = update;

			masterEd = iEditor.getFrameEditor(master);
		}

		void update() {

			removeOldSlots();
			updateCurrentSlots();
			addNewSlots();

			updateSlotValues();
		}

		void initialise() {

			for (ISlot slot : update.getSlots().asList()) {

				masterEd.addSlot(slot.getType());
			}
		}

		private void removeOldSlots() {

			for (ISlot slot : master.getSlots().asList()) {

				if (!matchingSlotOn(update, slot)) {

					masterEd.removeSlot(slot);
				}
			}
		}

		private void updateCurrentSlots() {

			for (ISlot slot : master.getSlots().asList()) {

				updateCurrentSlot(slot, getUpdateSlot(slot));
			}
		}

		private void addNewSlots() {

			for (ISlot slot : update.getSlots().asList()) {

				if (!matchingSlotOn(master, slot)) {

					masterEd.addSlot(slot.getType());
				}
			}
		}

		private void updateSlotValues() {

			for (ISlot slot : master.getSlots().asList()) {

				updateSlotValues(slot);
			}
		}

		private void updateCurrentSlot(ISlot masterSlot, ISlot updateSlot) {

			CSlot masterSlotType = masterSlot.getType();
			CSlot updateSlotType = updateSlot.getType();

			ISlotEditor masterSlotEd = iEditor.getSlotEditor(masterSlot);

			CValue<?> updValueType = updateSlotType.getValueType();
			CCardinality updCardinality = updateSlotType.getCardinality();
			CActivation updActivation = updateSlotType.getActivation();
			CEditability updEditability = updateSlotType.getEditability();

			if (!masterSlotType.getValueType().equals(updValueType)) {

				masterSlotEd.setValueType(updValueType);
			}

			if (!masterSlotType.getCardinality().equals(updCardinality)) {

				masterSlotEd.setCardinality(updCardinality);
			}

			if (!masterSlotType.getActivation().equals(updActivation)) {

				masterSlotEd.setActivation(updActivation);
			}

			if (!masterSlotType.getEditability().equals(updEditability)) {

				masterSlotEd.setEditability(updEditability);
			}
		}

		private void updateSlotValues(ISlot slot) {

			createValuesUpdater(slot, getUpdateSlot(slot)).update();
		}

		private IValuesUpdater createValuesUpdater(ISlot masterSlot, ISlot updateSlot) {

			if (masterSlot.getValueType() instanceof CFrame) {

				return new IFrameValuesUpdater(masterSlot, updateSlot);
			}

			return new IValuesUpdater(masterSlot, updateSlot);
		}

		private ISlot getUpdateSlot(ISlot masterSlot) {

			return update.getSlots().get(masterSlot.getType().getIdentity());
		}

		private boolean matchingSlotOn(IFrame frame, ISlot template) {

			return frame.getSlots().containsValueFor(template.getType().getIdentity());
		}
	}

	private class IValuesUpdater {

		final ISlot master;
		final ISlot update;

		private ISlotValuesEditor masterEd;

		IValuesUpdater(ISlot master, ISlot update) {

			this.master = master;
			this.update = update;

			masterEd = iEditor.getSlotValuesEditor(master);
		}

		void update() {

			removeOldValues();
			updateCurrentValues();
			addNewValues();
		}

		void updateCurrentValues() {
		}

		boolean matchingMasterValueFor(IValue updateValue) {

			return master.getValues().contains(updateValue);
		}

		boolean matchingUpdateValueFor(IValue masterValue) {

			return update.getValues().contains(masterValue);
		}

		IValue getNewValue(IValue updateValue) {

			return updateValue;
		}

		private void removeOldValues() {

			for (IValue value : master.getValues().asList()) {

				if (!matchingUpdateValueFor(value)) {

					masterEd.remove(value);
				}
			}
		}

		private void addNewValues() {

			for (IValue value : update.getValues().asList()) {

				if (!matchingMasterValueFor(value)) {

					masterEd.add(getNewValue(value));
				}
			}
		}
	}

	private class IFrameValuesUpdater extends IValuesUpdater {

		IFrameValuesUpdater(ISlot master, ISlot update) {

			super(master, update);
		}

		void updateCurrentValues() {

			for (IValue value : master.getValues().asList()) {

				updateCurrentValue((IFrame)value);
			}
		}

		boolean matchingMasterValueFor(IValue updateValue) {

			return matchingValueFor(master, updateValue, mastersToUpdates);
		}

		boolean matchingUpdateValueFor(IValue masterValue) {

			return matchingValueFor(update, masterValue, updatesToMasters);
		}

		IValue getNewValue(IValue updateValue) {

			return createNewMasterFrame((IFrame)updateValue);
		}

		private void updateCurrentValue(IFrame masterValue) {

			updateFrom(masterValue, mastersToUpdates.get(masterValue));
		}

		private boolean matchingValueFor(
							ISlot slot,
							IValue template,
							Map<IFrame, IFrame> toTemplates) {

			for (IValue value : master.getValues().asList()) {

				if (toTemplates.containsKey(value)) {

					return true;
				}
			}

			return false;
		}

		private IFrame createNewMasterFrame(IFrame updateFrame) {

			IFrame masterFrame = updateFrame.getType().instantiate();

			initialise(masterFrame, updateFrame);

			return masterFrame;
		}
	}

	Updater(IEditor iEditor, RUpdates updates) {

		this.iEditor = iEditor;

		updateRoot = updates.getRoot();
		mastersToUpdates = updates.getMastersToUpdates();

		for (Map.Entry<IFrame, IFrame> entry : mastersToUpdates.entrySet()) {

			updatesToMasters.put(entry.getValue(), entry.getKey());
		}
	}

	void update(IFrame masterRoot) {

		updateFrom(masterRoot, updateRoot);
	}

	private void updateFrom(IFrame master, IFrame update) {

		if (updatedMasters.add(master)) {

			new IFrameUpdater(master, update).update();
		}
	}

	private void initialise(IFrame master, IFrame update) {

		new IFrameUpdater(master, update).initialise();
	}
}
