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
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.serial.*;

/**
 * @author Colin Puleston
 */
public class IFrameParser extends ISerialiser {

	private CModel model;
	private IEditor iEditor;

	private class SlotValuesParser extends CValueVisitor {

		private XNode valuesNode;
		private ISlotValuesEditor valuesEditor;

		protected void visit(CFrame value) {

			for (XNode valueNode : valuesNode.getChildren(IFRAME_ID)) {

				valuesEditor.add(parseIFrame(valueNode));
			}
		}

		protected void visit(CNumber value) {

			valuesEditor.add(parseINumber(value, valuesNode.getChild(INUMBER_ID)));
		}

		protected void visit(MFrame value) {

			for (XNode valueNode : valuesNode.getChildren(CFRAME_ID)) {

				valuesEditor.add(parseCFrame(valueNode));
			}
		}

		SlotValuesParser(ISlot slot, XNode valuesNode) {

			this.valuesNode = valuesNode;

			valuesEditor = slot.getValuesEditor();

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
 	public IFrame parse(XNode node) {

		return parseIFrame(node);
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

	private ISlot parseISlot(IFrame frame, XNode node) {

		ISlot slot = resolveISlot(frame, node);
		XNode valuesNode = node.getChildOrNull(ISLOT_VALUES_ID);

		if (valuesNode != null) {

			new SlotValuesParser(slot, valuesNode);
		}

		return slot;
	}

	private ISlot resolveISlot(IFrame frame, XNode node) {

		CIdentity id = parseIdentity(node.getChild(CSLOT_ID));
		ISlots slots = frame.getSlots();

		if (slots.containsValueFor(id)) {

			return slots.get(id);
		}

		return getNewISlot(frame, id, node);
	}

	private ISlot getNewISlot(IFrame frame, CIdentity id, XNode node) {

		return getIFrameEditor(frame)
				.addSlot(
					getProperty(id),
					CSource.UNSPECIFIED,
					CCardinality.FREE,
					parseISlotValueType(id, node));
	}

	private CValue<?> parseISlotValueType(CIdentity id, XNode slotNode) {

		if (slotNode.hasChild(MFRAME_ID)) {

			return getRootMFrame();
		}

		if (slotNode.hasChild(CFRAME_ID)) {

			return getRootCFrame();
		}

		if (slotNode.hasChild(CNUMBER_ID)) {

			return parseCNumber(slotNode.getChild(CNUMBER_ID));
		}

		throw new XDocumentException(
					"Cannot find value-type element for slot: "
					+ id.getIdentifier());
	}

	private MFrame getRootMFrame() {

		return getRootCFrame().getType();
	}

	private CFrame getRootCFrame() {

		return model.getRootFrame();
	}

	private CFrame getCFrame(CIdentity id) {

		return model.getFrames().get(id);
	}

	private CProperty getProperty(CIdentity id) {

		return model.getProperties().get(id);
	}

	private IFrameEditor getIFrameEditor(IFrame frame) {

		return iEditor.getFrameEditor(frame);
	}

	private boolean isClassName(Class<?> testClass, String testName) {

		return testClass.getSimpleName().equals(testName);
	}
}