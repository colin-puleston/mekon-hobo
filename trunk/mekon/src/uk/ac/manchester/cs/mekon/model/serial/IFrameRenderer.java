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
import java.lang.reflect.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.serial.*;

/**
 * @author Colin Puleston
 */
public class IFrameRenderer extends ISerialiser {

	private class SlotValuesRenderer extends ISlotValuesVisitor {

		private XNode slotNode;

		protected void visit(CFrame valueType, List<IFrame> values) {

			for (IFrame value : values) {

				renderIFrame(value, slotNode);
			}
		}

		protected void visit(CNumber valueType, List<INumber> values) {

			renderNumber(values.get(0), slotNode);
		}

		protected void visit(MFrame valueType, List<CFrame> values) {

			for (CFrame value : values) {

				renderCFrame(value, slotNode);
			}
		}

		SlotValuesRenderer(ISlot slot, XNode slotNode) {

			this.slotNode = slotNode;

			visit(slot);
		}
	}

	/**
	 */
 	public XDocument render(IFrame frame) {

		XDocument document = new XDocument(FRAME_ID);

		renderIFrameDetails(frame, document.getRootNode());

		return document;
	}

	/**
	 */
 	public void render(IFrame frame, XNode parentNode) {

		renderIFrame(frame, parentNode);
	}

 	private void renderIFrame(IFrame frame, XNode parentNode) {

		renderIFrameDetails(frame, parentNode.addChild(FRAME_ID));
	}

 	private void renderIFrameDetails(IFrame frame, XNode frameNode) {

		renderIdentity(frame.getType(), frameNode);

		for (ISlot slot : frame.getSlots().asList()) {

			if (slotToBeRendered(slot)) {

				renderSlot(slot, frameNode);
			}
		}
	}

	private XNode renderCFrame(CFrame frameType, XNode parentNode) {

		XNode frameNode = parentNode.addChild(FRAME_ID);

		renderIdentity(frameType, frameNode);

		return frameNode;
	}

	private void renderSlot(ISlot slot, XNode parentNode) {

		XNode slotNode = parentNode.addChild(SLOT_ID);

		renderIdentity(slot.getType().getProperty(), slotNode);
		renderClassName(slot.getValueType(), slotNode, VALUE_TYPE_ATTR);

		new SlotValuesRenderer(slot, slotNode);
	}

	private void renderNumber(INumber number, XNode node) {

		Number value = number.asTypeNumber();

		renderClassName(value, node, NUMBER_TYPE_ATTR);
		node.addValue(NUMBER_VALUE_ATTR, value);
	}

	private void renderIdentity(CIdentified id, XNode node) {

		renderIdentity(id.getIdentity(), node);
	}

	private void renderClassName(Object value, XNode node, String attr) {

		Class<?> publicClass = findPublicClass(value.getClass());

		node.addValue(attr, publicClass.getSimpleName());
	}

	private boolean slotToBeRendered(ISlot slot) {

		return !slot.dependent() && !slot.getValues().isEmpty();
	}

	private Class<?> findPublicClass(Class<?> testClass) {

		return isPublicClass(testClass)
					? testClass
					: findPublicClass(testClass.getSuperclass());
	}

	private boolean isPublicClass(Class<?> testClass) {

		return Modifier.isPublic(testClass.getModifiers());
	}
}
