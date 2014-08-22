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

package uk.ac.manchester.cs.mekon.model;

import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class IStoreParser implements IStoreSerialiser {

	private CModel model;
	private XFile file;

	private class SlotValuesParser extends CValueVisitor {

		private ISlotValues values;
		private XNode slotNode;

		protected void visit(CFrame value) {

			for (XNode valueNode : slotNode.getChildren(FRAME_ID)) {

				values.add(parseIFrame(valueNode));
			}
		}

		protected void visit(CNumber value) {

			values.add(parseNumber(value, slotNode));
		}

		protected void visit(MFrame value) {

			for (XNode valueNode : slotNode.getChildren(FRAME_ID)) {

				values.add(parseCFrame(valueNode));
			}
		}

		SlotValuesParser(ISlot slot, XNode slotNode) {

			values = slot.getValues();

			this.slotNode = slotNode;

			visit(slot.getValueType());
		}
	}

 	IStoreParser(CModel model) {

		this(model, DEFAULT_FILE_NAME);
	}

 	IStoreParser(CModel model, String fileName) {

		this.model = model;

		file = new XFile(fileName);
	}

 	void parse(IStore store) {

		XNode rootNode = file.getRootNode();

		for (XNode instNode : rootNode.getChildren(INSTANCE_ID)) {

			XNode frameNode = instNode.getChild(FRAME_ID);

			store.addInternal(parseIFrame(frameNode), parseIdentity(instNode));
		}
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

		IFrameEditor frameEd = frame.createEditor();
		CProperty property = getProperty(id);
		CValue<?> valueType = parseValueType(slotNode);

		return frameEd.addSlot(
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

		throw new XFileException("Unrecognised class: " + name);
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

		throw new XFileException("Unrecognised class: " + name);
	}

	private INumber parseNumber(CNumber valueType, XNode node) {

		String value = node.getString(NUMBER_VALUE_ATTR);

		return INumber.create(valueType.getNumberType(), value);
	}

	private CIdentity parseIdentity(XNode node) {

		String id = node.getString(IDENTITY_ATTR);
		String label = node.getString(LABEL_ATTR);

		return new CIdentity(id, label);
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

	private boolean isClassName(Class<?> testClass, String testName) {

		return testClass.getSimpleName().equals(testName);
	}
}