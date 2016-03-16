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
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.hobo.model.motor.*;

/**
 * @author Colin Puleston
 */
class FieldSlotInitialiser {

	private DModel model;
	private CFrame frameType;
	private IFrame frame;
	private String slotId;
	private String slotLabel;
	private CValue<?> valueType;
	private CCardinality cardinality;
	private CEditability editability;

	FieldSlotInitialiser(DModel model, IFrame frame, FieldSlot fieldSlot) {

		this.model = model;
		this.frame = frame;

		DField<?> field = fieldSlot.getField();
		String fieldName = fieldSlot.getFieldName();
		DBinding binding = fieldSlot.getBinding();

		frameType = binding.getFrame();
		slotId = binding.getSlotId(fieldName);
		slotLabel = fieldSlot.getSlotLabel();
		valueType = field.getSlotValueType();
		cardinality = field.getCardinality();
		editability = fieldSlot.getEditability();
	}

	ISlot initialiseSlot() {

		CSlot slotType = resolveSlotType();
		ISlot slot = frame.getSlots().getOrNull(slotId);

		return slot != null ? slot : addSlot(slotType);
	}

	private CSlot resolveSlotType() {

		CSlot slotType = frameType.getSlots().getOrNull(slotId);

		if (slotType == null) {

			slotType = addSlotType();

			initAsDirectSlotType(slotType, CSource.INTERNAL);
		}
		else {

			if (slotType.getSource() == CSource.EXTERNAL) {

				initAsDirectSlotType(slotType, CSource.DUAL);
			}
		}

		updateSlotType(slotType);

		return slotType;
	}

	private void initAsDirectSlotType(CSlot slotType, CSource source) {

		CSlotEditor slotTypeEd = getSlotTypeEditor(slotType);

		slotTypeEd.setSource(source);

		if (getModelMap().labelsFromDirectFields()) {

			slotTypeEd.resetLabel(slotLabel);
		}
	}

	private void updateSlotType(CSlot slotType) {

		CSlotEditor slotTypeEd = getSlotTypeEditor(slotType);

		slotTypeEd.absorbValueType(valueType);
		slotTypeEd.absorbCardinality(cardinality);
		slotTypeEd.absorbEditability(editability);
	}

	private CSlot addSlotType() {

		CIdentity id = new CIdentity(slotId, slotLabel);

		return getFrameTypeEditor().addSlot(id, valueType, cardinality);
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
