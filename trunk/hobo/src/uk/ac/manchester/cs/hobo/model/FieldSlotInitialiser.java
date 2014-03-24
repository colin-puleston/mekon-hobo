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
	private boolean editable;
	private boolean boundField;

	FieldSlotInitialiser(
		DModel model,
		DBinding binding,
		IFrame frame,
		DField<?> field,
		String fieldName,
		String slotLabel,
		boolean editable) {

		this.model = model;
		this.frame = frame;
		this.slotLabel = slotLabel;
		this.editable = editable;

		frameType = binding.getFrame();
		slotId = binding.getSlotId(fieldName);
		boundField = binding.isBoundField(fieldName);
		cardinality = field.getCardinality();
		valueType = field.getSlotValueType();
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

		CProperty property = resolveProperty();
		CFrameEditor frameTypeEd = getFrameTypeEditor();
		CSlot slotType = frameTypeEd.addSlot(property, cardinality, valueType);
		CSlotEditor slotTypeEd = getSlotTypeEditor(slotType);

		slotTypeEd.setSource(CSource.DIRECT);
		slotTypeEd.absorbEditable(editable);

		return slotType;
	}

	private void updateSlotType(CSlot slotType) {

		CProperty property = slotType.getProperty();
		CSlotEditor slotTypeEd = getSlotTypeEditor(slotType);

		if (model.getModelMap().labelsFromDirectFields()) {

			getPropertyEditor(property).resetLabel(slotLabel);
		}

		slotTypeEd.setSource(CSource.DUAL);
		slotTypeEd.absorbCardinality(cardinality);
		slotTypeEd.absorbValueType(valueType);
		slotTypeEd.absorbEditable(editable);
	}

	private ISlot addSlot(CSlot slotType) {

		return getFrameEditor(frame).addSlot(slotType);
	}

	private CProperty resolveProperty() {

		return getCBuilder().resolveProperty(new CIdentity(slotId, slotLabel));
	}

	private CFrameEditor getFrameTypeEditor() {

		return getCBuilder().getFrameEditor(frameType);
	}

	private CPropertyEditor getPropertyEditor(CProperty property) {

		return getCBuilder().getPropertyEditor(property);
	}

	private CSlotEditor getSlotTypeEditor(CSlot slotType) {

		return getCBuilder().getSlotEditor(slotType);
	}

	private IFrameEditor getFrameEditor(IFrame frame) {

		return model.getIEditor().getFrameEditor(frame);
	}

	private CBuilder getCBuilder() {

		return model.getCBuilder();
	}
}
