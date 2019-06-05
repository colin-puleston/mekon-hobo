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

/**
 * @author Colin Puleston
 */
class SlotTypeResolver {

	private DModel model;
	private CFrame frameType;
	private CIdentity slotId;
	private CValue<?> valueType;
	private CCardinality cardinality;
	private CActivation activation;
	private CEditability editability;

	SlotTypeResolver(DModel model, CFrame frameType, FieldSlot fieldSlot) {

		this.model = model;
		this.frameType = frameType;

		DField<?> field = fieldSlot.getField();

		slotId = fieldSlot.getSlotId();
		valueType = field.getSlotValueType();
		cardinality = field.getCardinality();
		activation = fieldSlot.getActivation();
		editability = fieldSlot.getEditability();
	}

	CSlot resolve() {

		CSlot slotType = frameType.getSlots().getOrNull(slotId);

		if (slotType == null) {

			slotType = add();

			setAsDirect(slotType, CSource.INTERNAL);
		}
		else {

			if (slotType.getSource() == CSource.EXTERNAL) {

				setAsDirect(slotType, CSource.DUAL);
			}
		}

		absorbFieldAttributes(slotType);

		return slotType;
	}

	private CSlot add() {

		return getFrameTypeEditor().addSlot(slotId, valueType, cardinality);
	}

	private void setAsDirect(CSlot slotType, CSource source) {

		CSlotEditor ed = getSlotTypeEditor(slotType);

		ed.setSource(source);

		if (labelsFromDirectFields()) {

			ed.resetLabel(slotId.getLabel());
		}
	}

	private void absorbFieldAttributes(CSlot slotType) {

		CSlotEditor ed = getSlotTypeEditor(slotType);

		ed.absorbValueType(valueType);
		ed.absorbCardinality(cardinality);
		ed.absorbActivation(activation);
		ed.absorbEditability(editability);
	}

	private CFrameEditor getFrameTypeEditor() {

		return getCBuilder().getFrameEditor(frameType);
	}

	private CSlotEditor getSlotTypeEditor(CSlot slotType) {

		return getCBuilder().getSlotEditor(slotType);
	}

	private CBuilder getCBuilder() {

		return model.getInitialiser().getCBuilder();
	}

	private boolean labelsFromDirectFields() {

		return model.getInitialiser().getModelMap().labelsFromDirectFields();
	}
}
