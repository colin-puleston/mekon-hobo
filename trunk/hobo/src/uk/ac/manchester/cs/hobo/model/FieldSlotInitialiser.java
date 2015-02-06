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

package uk.ac.manchester.cs.hobo.model;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.hobo.mechanism.*;

/**
 * @author Colin Puleston
 */
class FieldSlotInitialiser {

	private DModel model;
	private CFrame frameType;
	private IFrame frame;
	private String slotId;
	private String slotLabel;
	private CCardinality cardinality;
	private CValue<?> valueType;
	private CEditability editability;
	private boolean boundField;

	FieldSlotInitialiser(DModel model, IFrame frame, FieldSlot fieldSlot) {

		this.model = model;
		this.frame = frame;

		DField<?> field = fieldSlot.getField();
		String fieldName = fieldSlot.getFieldName();
		DBinding binding = fieldSlot.getBinding();

		frameType = binding.getFrame();
		slotId = binding.getSlotId(fieldName);
		slotLabel = fieldSlot.getSlotLabel();
		cardinality = field.getCardinality();
		valueType = field.getSlotValueType();
		editability = fieldSlot.getEditability();
		boundField = binding.isBoundField(fieldName);
	}

	ISlot initialiseSlot() {

		CSlot slotType = resolveSlotType();
		ISlot slot = frame.getSlots().getOrNull(slotId);

		updateSlotType(slotType);

		return slot != null ? slot : addSlot(slotType);
	}

	private CSlot resolveSlotType() {

		if (boundField) {

			return frameType.getSlots().get(slotId);
		}

		CSlot slotType = frameType.getSlots().getOrNull(slotId);

		return slotType != null ? slotType : createSlotType();
	}

	private CSlot createSlotType() {

		CSlot slotType = addSlotType();
		CSlotEditor slotTypeEd = getSlotTypeEditor(slotType);

		slotTypeEd.setSource(CSource.INTERNAL);
		slotTypeEd.absorbEditability(editability);

		return slotType;
	}

	private CSlot addSlotType() {

		CIdentity id = new CIdentity(slotId, slotLabel);

		return getFrameTypeEditor().addSlot(id, cardinality, valueType);
	}

	private void updateSlotType(CSlot slotType) {

		CSlotEditor slotTypeEd = getSlotTypeEditor(slotType);

		if (getModelMap().labelsFromDirectFields()) {

			slotTypeEd.resetLabel(slotLabel);
		}

		if (boundField) {

			slotTypeEd.setSource(CSource.DUAL);
		}

		slotTypeEd.absorbCardinality(cardinality);
		slotTypeEd.absorbValueType(valueType);
		slotTypeEd.absorbEditability(editability);
	}

	private ISlot addSlot(CSlot slotType) {

		return getFrameEditor(frame).addSlot(slotType);
	}

	private CFrameEditor getFrameTypeEditor() {

		return getCBuilder().getFrameEditor(frameType);
	}

	private CSlotEditor getSlotTypeEditor(CSlot slotType) {

		return getCBuilder().getSlotEditor(slotType);
	}

	private IFrameEditor getFrameEditor(IFrame frame) {

		return model.getIEditor().getFrameEditor(frame);
	}

	private CBuilder getCBuilder() {

		return model.getInitialiser().getCBuilder();
	}

	private DModelMap getModelMap() {

		return model.getInitialiser().getModelMap();
	}
}
