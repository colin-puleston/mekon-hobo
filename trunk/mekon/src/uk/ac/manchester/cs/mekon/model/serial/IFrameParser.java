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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.serial.*;

/**
 * @author Colin Puleston
 */
public class IFrameParser extends ISerialiser {

	private CModel model;
	private IEditor iEditor;

	private class SlotValuesParser extends CValueVisitor {

		private ISlotValuesEditor valuesEditor;
		private XNode slotNode;

		protected void visit(CFrame value) {

			for (XNode valueNode : slotNode.getChildren(FRAME_ID)) {

				valuesEditor.add(parseIFrame(valueNode));
			}
		}

		protected void visit(CNumber value) {

			valuesEditor.add(parseNumber(value, slotNode));
		}

		protected void visit(MFrame value) {

			for (XNode valueNode : slotNode.getChildren(FRAME_ID)) {

				valuesEditor.add(parseCFrame(valueNode));
			}
		}

		SlotValuesParser(ISlot slot, XNode slotNode) {

			valuesEditor = slot.getValuesEditor();

			this.slotNode = slotNode;

			visit(slot.getValueType());
		}
	}

 	/**
	 */
 	public IFrameParser(CModel model, IEditor iEditor) {

		this.model = model;
		this.iEditor = iEditor;
	}

	/**
	 */
 	public IFrame parse(XDocument document) {

		return parseIFrame(document.getRootNode());
	}

	/**
	 */
 	public IFrame parse(XNode frameNode) {

		return parseIFrame(frameNode);
	}

	private IFrame parseIFrame(XNode frameNode) {

		IFrame frame = parseCFrame(frameNode).instantiate();

		for (XNode slotNode : frameNode.getChildren(SLOT_ID)) {

			ISlot slot = parseSlot(frame, slotNode);

			new SlotValuesParser(slot, slotNode);
		}

		return frame;
	}

	private CFrame parseCFrame(XNode frameNode) {

		return getCFrame(parseIdentity(frameNode));
	}

	private ISlot parseSlot(IFrame frame, XNode slotNode) {

		CIdentity id = parseIdentity(slotNode);
		ISlots slots = frame.getSlots();

		if (slots.containsValueFor(id)) {

			return slots.get(id);
		}

		return parseNewSlot(frame, id, slotNode);
	}

	private ISlot parseNewSlot(IFrame frame, CIdentity id, XNode slotNode) {

		CProperty property = getProperty(id);
		CValue<?> valueType = parseValueType(slotNode);

		return getIFrameEditor(frame)
				.addSlot(
					property,
					CSource.UNSPECIFIED,
					CCardinality.FREE,
					valueType);
	}

	private CValue<?> parseValueType(XNode slotNode) {

		String name = slotNode.getString(VALUE_TYPE_ATTR);

		if (isClassName(MFrame.class, name)) {

			return getRootMFrame();
		}

		if (isClassName(CFrame.class, name)) {

			return getRootCFrame();
		}

		if (isClassName(CNumber.class, name)) {

			return parseNumberValueType(slotNode).createNumber();
		}

		throw new XDocumentException("Unrecognised class: " + name);
	}

	private CNumberDef parseNumberValueType(XNode slotNode) {

		String name = slotNode.getString(NUMBER_TYPE_ATTR);

		if (isClassName(Integer.class, name)) {

			return CIntegerDef.UNCONSTRAINED;
		}

		if (isClassName(Long.class, name)) {

			return CLongDef.UNCONSTRAINED;
		}

		if (isClassName(Float.class, name)) {

			return CFloatDef.UNCONSTRAINED;
		}

		if (isClassName(Double.class, name)) {

			return CDoubleDef.UNCONSTRAINED;
		}

		throw new XDocumentException("Unrecognised class: " + name);
	}

	private INumber parseNumber(CNumber valueType, XNode node) {

		String value = node.getString(NUMBER_VALUE_ATTR);

		return INumber.create(valueType.getNumberType(), value);
	}

	private CFrame getCFrame(CIdentity id) {

		return model.getFrames().get(id);
	}

	private CProperty getProperty(CIdentity id) {

		return model.getProperties().get(id);
	}

	private MFrame getRootMFrame() {

		return getRootCFrame().getType();
	}

	private CFrame getRootCFrame() {

		return model.getRootFrame();
	}

	private IFrameEditor getIFrameEditor(IFrame frame) {

		return iEditor.getFrameEditor(frame);
	}

	private boolean isClassName(Class<?> testClass, String testName) {

		return testClass.getSimpleName().equals(testName);
	}
}