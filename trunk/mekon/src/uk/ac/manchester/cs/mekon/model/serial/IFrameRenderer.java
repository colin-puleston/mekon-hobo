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

package uk.ac.manchester.cs.mekon.model.serial;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.serial.*;

/**
 * @author Colin Puleston
 */
public class IFrameRenderer extends ISerialiser {

	private boolean renderNonEditableSlots = false;

	private class ISlotDetailsRenderer extends ISlotValuesVisitor {

		private XNode slotNode;

		protected void visit(CFrame valueType, List<IFrame> values) {

			renderCFrame(valueType, slotNode);

			for (IFrame value : values) {

				renderIFrame(value, slotNode);
			}
		}

		protected void visit(CNumber valueType, List<INumber> values) {

			renderCNumber(valueType, slotNode);
			renderINumber(values.get(0), slotNode);
		}

		protected void visit(MFrame valueType, List<CFrame> values) {

			renderMFrame(valueType, slotNode);

			for (CFrame value : values) {

				renderCFrame(value, slotNode);
			}
		}

		ISlotDetailsRenderer(ISlot slot, XNode slotNode) {

			this.slotNode = slotNode;

			visit(slot);
		}
	}

	/**
	 */
 	public void setRenderNonEditableSlots(boolean value) {

		renderNonEditableSlots = value;
	}

	/**
	 */
 	public XDocument render(IFrame frame) {

		XDocument document = new XDocument(IFRAME_ID);

		renderIFrameDetails(frame, document.getRootNode());

		return document;
	}

	/**
	 */
 	public void render(IFrame frame, XNode parentNode) {

		renderIFrame(frame, parentNode);
	}

 	private void renderIFrame(IFrame frame, XNode parentNode) {

		renderIFrameDetails(frame, parentNode.addChild(IFRAME_ID));
	}

 	private void renderIFrameDetails(IFrame frame, XNode node) {

		renderCFrame(frame.getType(), node);

		for (ISlot slot : frame.getSlots().asList()) {

			if (slotToBeRendered(slot)) {

				renderISlot(slot, node);
			}
		}
	}

	private void renderCFrame(CFrame frame, XNode parentNode) {

		renderCFrame(frame, parentNode, CFRAME_ID);
	}

	private void renderCFrame(CFrame frame, XNode parentNode, String tag) {

		if (frame.getCategory().disjunction()) {

			XNode disjunctsNode = parentNode.addChild(tag);

			for (CFrame disjunct : frame.getSubs()) {

				renderIdentity(disjunct, disjunctsNode.addChild(tag));
			}
		}
		else {

			renderIdentity(frame, parentNode.addChild(tag));
		}
	}

	private void renderMFrame(MFrame frame, XNode parentNode) {

		renderCFrame(frame.getRootCFrame(), parentNode, MFRAME_ID);
	}

	private void renderINumber(INumber number, XNode parentNode) {

		XNode node = parentNode.addChild(INUMBER_ID);

		if (number.indefinite()) {

			node.addValue(NUMBER_VALUE_ATTR, number.asTypeNumber());
		}
		else {

			renderCNumberRange(number.getType(), node);
		}
	}

	private void renderCNumber(CNumber number, XNode parentNode) {

		XNode node = parentNode.addChild(CNUMBER_ID);

		renderClassId(number.getNumberType(), node, NUMBER_TYPE_ATTR);
		renderCNumberRange(number, node);
	}

	private void renderCNumberRange(CNumber number, XNode node) {

		if (number.hasMin()) {

			node.addValue(NUMBER_MIN_ATTR, number.getMin().asTypeNumber());
		}

		if (number.hasMax()) {

			node.addValue(NUMBER_MAX_ATTR, number.getMax().asTypeNumber());
		}
	}

	private void renderISlot(ISlot slot, XNode parentNode) {

		XNode node = parentNode.addChild(ISLOT_ID);

		node.addValue(EDITABLE_ATTR, slot.editable());

		renderCSlot(slot.getType(), node);
		new ISlotDetailsRenderer(slot, node);
	}

	private void renderCSlot(CSlot slot, XNode parentNode) {

		XNode node = parentNode.addChild(CSLOT_ID);

		renderIdentity(slot.getProperty(), node);
		node.addValue(CARDINALITY_ATTR, slot.getCardinality());
	}

	private boolean slotToBeRendered(ISlot slot) {

		if (slot.getValues().isEmpty()) {

			return false;
		}

		return renderNonEditableSlots || slot.editable();
	}
}
