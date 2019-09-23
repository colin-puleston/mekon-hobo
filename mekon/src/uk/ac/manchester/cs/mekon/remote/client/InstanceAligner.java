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
class InstanceAligner {

	private IEditor iEditor;

	private IFrame updateRoot;
	private Map<IFrame, IFrame> updatesToMasters;

	private Set<IFrame> alignedMasters = new HashSet<IFrame>();

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
			IEditability updEditability = updateSlot.getEditability();

			if (!masterSlotType.getValueType().equals(updValueType)) {

				masterSlotEd.setValueType(updValueType);
			}

			if (!masterSlotType.getCardinality().equals(updCardinality)) {

				masterSlotEd.setCardinality(updCardinality);
			}

			if (!masterSlotType.getActivation().equals(updActivation)) {

				masterSlotEd.setActivation(updActivation);
			}

			if (!masterSlot.getEditability().equals(updEditability)) {

				masterSlotEd.setEditability(updEditability);
			}
		}

		private void updateSlotValues(ISlot slot) {

			new IValuesAligner(slot, getUpdateSlot(slot)).align();
		}

		private ISlot getUpdateSlot(ISlot masterSlot) {

			return update.getSlots().get(masterSlot.getType().getIdentity());
		}

		private boolean matchingSlotOn(IFrame frame, ISlot template) {

			return frame.getSlots().containsValueFor(template.getType().getIdentity());
		}
	}

	private class IValuesAligner {

		private ISlot master;
		private ISlot update;

		IValuesAligner(ISlot master, ISlot update) {

			this.master = master;
			this.update = update;
		}

		void align() {

			ISlotValues updateVals = update.getValues();

			updateFixedValues(getAlignedValues(updateVals.getFixedValues()));
			updateAssertedValues(getAlignedValues(updateVals.getAssertedValues()));
		}

		private void updateFixedValues(List<IValue> updatedValues) {

			iEditor.getSlotEditor(master).updateFixedValues(updatedValues);
		}

		private void updateAssertedValues(List<IValue> updatedValues) {

			iEditor.getSlotValuesEditor(master).update(updatedValues);
		}

		private List<IValue> getAlignedValues(List<IValue> updateValues) {

			List<IValue> alignedValues = new ArrayList<IValue>();

			for (IValue updateValue : updateValues) {

				alignedValues.add(getAlignedValue(updateValue));
			}

			return alignedValues;
		}

		private IValue getAlignedValue(IValue updateValue) {

			if (updateValue instanceof IFrame) {

				return getAlignedFrameValue((IFrame)updateValue);
			}

			return getAlignedValueDefault(updateValue);
		}

		private IValue getAlignedValueDefault(IValue updateValue) {

			List<IValue> masterVals = master.getValues().asList();
			int masterIndex = masterVals.indexOf(updateValue);

			return masterIndex != -1 ? masterVals.get(masterIndex) : updateValue;
		}

		private IValue getAlignedFrameValue(IFrame updateFrame) {

			IFrame masterFrame = updatesToMasters.get(updateFrame);

			if (masterFrame == null) {

				masterFrame = createNewMasterFrame(updateFrame);
			}

			alignFrom(masterFrame, updateFrame);

			return masterFrame;
		}

		private IFrame createNewMasterFrame(IFrame updateFrame) {

			IFrame masterFrame = updateFrame.getType().instantiate();

			initialise(masterFrame, updateFrame);

			return masterFrame;
		}
	}

	InstanceAligner(IEditor iEditor, RUpdates updates) {

		this.iEditor = iEditor;

		updateRoot = updates.getRoot();
		updatesToMasters = updates.getUpdatesToMasters();
	}

	void align(IFrame masterRoot) {

		alignFrom(masterRoot, updateRoot);
	}

	private void alignFrom(IFrame master, IFrame update) {

		if (alignedMasters.add(master)) {

			new IFrameAligner(master, update).align();
		}
	}

	private void initialise(IFrame master, IFrame update) {

		new IFrameAligner(master, update).initialise();
	}
}
