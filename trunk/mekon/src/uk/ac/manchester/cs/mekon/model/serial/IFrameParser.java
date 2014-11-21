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
public class IFrameParser extends ISerialiser {

	private CModel model;

	private List<SlotValueSpec> slotValueSpecs = new ArrayList<SlotValueSpec>();

	private abstract class SlotValueSpec {

		private ISlots slots;
		private CIdentity slotId;

		SlotValueSpec(IFrame frame, CIdentity slotId) {

			slots = frame.getSlots();
			this.slotId = slotId;

			slotValueSpecs.add(this);
		}

		boolean process() {

			if (slots.containsValueFor(slotId)) {

				ISlot slot = slots.get(slotId);

				if (slot.editable()) {

					slot.getValuesEditor().add(getValue(slot));
					slotValueSpecs.remove(this);
				}

				return true;
			}

			return false;
		}

		abstract IValue getValue(ISlot slot);
	}

	private class FrameSlotValueSpec extends SlotValueSpec {

		private IValue value;

		FrameSlotValueSpec(IFrame frame, CIdentity slotId, IValue value) {

			super(frame, slotId);

			this.value = value;
		}

		IValue getValue(ISlot slot) {

			return value;
		}
	}

	private class INumberSlotValueSpec extends SlotValueSpec {

		private XNode valueNode;

		INumberSlotValueSpec(IFrame frame, CIdentity slotId, XNode valueNode) {

			super(frame, slotId);

			this.valueNode = valueNode;
		}

		IValue getValue(ISlot slot) {

			return parseINumber(getCNumberValueType(slot), valueNode);
		}

		private CNumber getCNumberValueType(ISlot slot) {

			CValue<?> valueType = slot.getValueType();

			if (valueType instanceof CNumber) {

				return (CNumber)valueType;
			}

			throw new XDocumentException(
						"Unexpected value-type for slot: " + slot
						+ ": Expected: " + CNumber.class
						+ ", Found: " + valueType.getClass());
		}
	}

 	/**
	 */
 	public IFrameParser(CModel model) {

		this.model = model;
	}

	/**
	 */
 	public IFrame parse(XDocument document) {

		return parseIFrame(document.getRootNode());
	}

	/**
	 */
 	public IFrame parse(XNode node) {

		IFrame frame = parseIFrame(node);

		addAllSlotValues();

		return frame;
	}

	private IFrame parseIFrame(XNode node) {

		CFrame frameType = parseCFrame(node.getChild(CFRAME_ID));
		IFrame frame = frameType.instantiate();

		for (XNode slotNode : node.getChildren(ISLOT_ID)) {

			parseISlot(frame, slotNode);
		}

		return frame;
	}

	private CFrame parseCFrame(XNode node) {

		return node.hasAttribute(IDENTITY_ATTR)
				? parseModelCFrame(node)
				: parseDisjunctionCFrame(node);
	}

	private CFrame parseDisjunctionCFrame(XNode node) {

		List<CFrame> disjuncts = new ArrayList<CFrame>();

		for (XNode disjunctNode : node.getChildren(CFRAME_ID)) {

			disjuncts.add(parseModelCFrame(disjunctNode));
		}

		return CFrame.resolveDisjunction(disjuncts);
	}

	private CFrame parseModelCFrame(XNode node) {

		return getCFrame(parseIdentity(node));
	}

	private INumber parseINumber(CNumber valueType, XNode node) {

		return node.hasAttribute(NUMBER_VALUE_ATTR)
				? parseDefiniteINumber(valueType, node, NUMBER_VALUE_ATTR)
				: parseIndefiniteINumber(valueType, node);
	}

	private INumber parseIndefiniteINumber(CNumber valueType, XNode node) {

		INumber min = INumber.MINUS_INFINITY;
		INumber max = INumber.PLUS_INFINITY;

		if (node.hasAttribute(NUMBER_MIN_ATTR)) {

			min = parseDefiniteINumber(valueType, node, NUMBER_MIN_ATTR);
		}

		if (node.hasAttribute(NUMBER_MAX_ATTR)) {

			max = parseDefiniteINumber(valueType, node, NUMBER_MAX_ATTR);
		}

		return CNumberDef.range(min, max).createNumber().asINumber();
	}

	private INumber parseDefiniteINumber(CNumber valueType, XNode node, String attrName) {

		return INumber.create(valueType.getNumberType(), node.getString(attrName));
	}

	private CNumber parseCNumber(XNode node) {

		return parseCNumberDef(node).createNumber();
	}

	private CNumberDef parseCNumberDef(XNode node) {

		String className = node.getString(NUMBER_TYPE_ATTR);

		if (isClassName(Integer.class, className)) {

			return CIntegerDef.UNCONSTRAINED;
		}

		if (isClassName(Long.class, className)) {

			return CLongDef.UNCONSTRAINED;
		}

		if (isClassName(Float.class, className)) {

			return CFloatDef.UNCONSTRAINED;
		}

		if (isClassName(Double.class, className)) {

			return CDoubleDef.UNCONSTRAINED;
		}

		throw new XDocumentException("Unrecognised class: " + className);
	}

	private void parseISlot(IFrame frame, XNode node) {

		CIdentity id = parseIdentity(node.getChild(CSLOT_ID));
		XNode valuesNode = node.getChildOrNull(ISLOT_VALUES_ID);

		for (XNode valueNode : valuesNode.getChildren(IFRAME_ID)) {

			new FrameSlotValueSpec(frame, id, parseIFrame(valueNode));
		}

		for (XNode valueNode : valuesNode.getChildren(INUMBER_ID)) {

			new INumberSlotValueSpec(frame, id, valueNode);
		}

		for (XNode valueNode : valuesNode.getChildren(CFRAME_ID)) {

			new FrameSlotValueSpec(frame, id, parseCFrame(valueNode));
		}
	}

	private void addAllSlotValues() {

		while (true) {

			boolean anyProcessed = false;

			for (SlotValueSpec spec : new ArrayList<SlotValueSpec>(slotValueSpecs)) {

				anyProcessed |= spec.process();
			}

			if (!anyProcessed) {

				break;
			}
		}
	}

	private CFrame getCFrame(CIdentity id) {

		return model.getFrames().get(id);
	}

	private CProperty getProperty(CIdentity id) {

		return model.getProperties().get(id);
	}

	private boolean isClassName(Class<?> testClass, String testName) {

		return testClass.getSimpleName().equals(testName);
	}
}