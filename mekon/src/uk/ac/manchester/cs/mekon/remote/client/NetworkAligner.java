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
class NetworkAligner {

	private IEditor iEditor;

	private IFrame updateRoot;

	private Map<IFrame, IFrame> mastersToUpdates;
	private Map<IFrame, IFrame> updatesToMasters = new HashMap<IFrame, IFrame>();

	private Set<IFrame> updatedMasters = new HashSet<IFrame>();

	private class IFrameAligner {

		private IFrame master;
		private IFrame update;

		private IFrameEditor masterEd;

		IFrameAligner(IFrame master, IFrame update) {

			this.master = master;
			this.update = update;

			masterEd = iEditor.getFrameEditor(master);
		}

		void align() {

			removeOldSlots();
			updateCurrentSlots();
			addNewSlots();
			updateSlotValues();
		}

		void initialise() {

			addNewSlots();
			updateSlotValues();
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

			createValuesAligner(slot, getUpdateSlot(slot)).align();
		}

		private IValuesAligner createValuesAligner(ISlot masterSlot, ISlot updateSlot) {

			if (masterSlot.getValueType() instanceof CFrame) {

				return new IFrameValuesAligner(masterSlot, updateSlot);
			}

			return new IValuesAligner(masterSlot, updateSlot);
		}

		private ISlot getUpdateSlot(ISlot masterSlot) {

			return update.getSlots().get(masterSlot.getType().getIdentity());
		}

		private boolean matchingSlotOn(IFrame frame, ISlot template) {

			return frame.getSlots().containsValueFor(template.getType().getIdentity());
		}
	}

	private class IValuesAligner {

		final ISlot master;
		final ISlot update;

		private ISlotValuesEditor masterEd;

		IValuesAligner(ISlot master, ISlot update) {

			this.master = master;
			this.update = update;

			masterEd = iEditor.getSlotValuesEditor(master);
		}

		void align() {

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

	private class IFrameValuesAligner extends IValuesAligner {

		IFrameValuesAligner(ISlot master, ISlot update) {

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

			alignFrom(masterValue, mastersToUpdates.get(masterValue));
		}

		private boolean matchingValueFor(
							ISlot slot,
							IValue template,
							Map<IFrame, IFrame> toTemplates) {

			for (IValue value : slot.getValues().asList()) {

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

	NetworkAligner(IEditor iEditor, RUpdates updates) {

		this.iEditor = iEditor;

		updateRoot = updates.getRoot();
		mastersToUpdates = updates.getMastersToUpdates();

		for (Map.Entry<IFrame, IFrame> entry : mastersToUpdates.entrySet()) {

			updatesToMasters.put(entry.getValue(), entry.getKey());
		}
	}

	void align(IFrame masterRoot) {

		alignFrom(masterRoot, updateRoot);
	}

	private void alignFrom(IFrame master, IFrame update) {

		if (updatedMasters.add(master)) {

			new IFrameAligner(master, update).align();
		}
	}

	private void initialise(IFrame master, IFrame update) {

		new IFrameAligner(master, update).initialise();
	}
}
