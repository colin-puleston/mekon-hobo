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
	private IFrameCategory frameCategory;

	private class OneTimeParser {

		private XNode containerNode;

		private Set<IFrame> iFrames = new HashSet<IFrame>();

		private Map<Integer, IFrame> iFrameRefs
						= new HashMap<Integer, IFrame>();

		private List<SlotValuesSpec<?>> slotValuesSpecs
						= new ArrayList<SlotValuesSpec<?>>();

		private abstract class SlotValuesSpec<V> {

			private IFrame frame;
			private CIdentity slotId;

			private List<V> valueSpecs = new ArrayList<V>();

			SlotValuesSpec(IFrame frame, CIdentity slotId, XNode parentNode) {

				this.frame = frame;
				this.slotId = slotId;

				for (XNode valueNode : parentNode.getChildren(getValueId())) {

					valueSpecs.add(getValueSpec(valueNode));
				}

				slotValuesSpecs.add(this);
			}

			boolean process() {

				ISlots slots = frame.getSlots();

				if (slots.containsValueFor(slotId)) {

					ISlot slot = slots.get(slotId);

					if (slot.getEditability().editable()) {

						setValues(slot);
					}

					slotValuesSpecs.remove(this);

					return true;
				}

				return false;
			}

			abstract String getValueId();

			abstract V getValueSpec(XNode valueNode);

			abstract IValue getValue(ISlot slot, V valueSpec);

			private void setValues(ISlot slot) {

				IFrameEditor frameEd = iEditor.getFrameEditor(frame);
				ISlotValuesEditor valuesEd = slot.getValuesEditor();

				frameEd.setAutoUpdateEnabled(false);
				valuesEd.addAll(getValidValues(slot));
				frameEd.setAutoUpdateEnabled(true);
			}

			private List<IValue> getValidValues(ISlot slot) {

				List<IValue> values = new ArrayList<IValue>();
				CValue<?> valueType = slot.getValueType();

				for (V valueSpec : valueSpecs) {

					IValue value = getValue(slot, valueSpec);

					if (valueType.validValue(value)) {

						values.add(value);
					}
				}

				return values;
			}
		}

		private class IFrameSlotValuesSpec extends SlotValuesSpec<IValue> {

			IFrameSlotValuesSpec(IFrame frame, CIdentity slotId, XNode parentNode) {

				super(frame, slotId, parentNode);
			}

			String getValueId() {

				return IFRAME_ID;
			}

			IValue getValueSpec(XNode valueNode) {

				return parseIFrame(valueNode);
			}

			IValue getValue(ISlot slot, IValue valueSpec) {

				if (slot.getValueType() instanceof MFrame) {

					return ((IFrame)valueSpec).getType();
				}

				return valueSpec;
			}
		}

		private class CFrameSlotValuesSpec extends SlotValuesSpec<IValue> {

			CFrameSlotValuesSpec(IFrame frame, CIdentity slotId, XNode parentNode) {

				super(frame, slotId, parentNode);
			}

			String getValueId() {

				return CFRAME_ID;
			}

			IValue getValueSpec(XNode valueNode) {

				return parseCFrame(valueNode);
			}

			IValue getValue(ISlot slot, IValue valueSpec) {

				return valueSpec;
			}
		}

		private class INumberSlotValuesSpec extends SlotValuesSpec<XNode> {

			INumberSlotValuesSpec(IFrame frame, CIdentity slotId, XNode parentNode) {

				super(frame, slotId, parentNode);
			}

			String getValueId() {

				return INUMBER_ID;
			}

			XNode getValueSpec(XNode valueNode) {

				return valueNode;
			}

			IValue getValue(ISlot slot, XNode valueSpec) {

				return parseINumber(getValueType(slot), valueSpec);
			}

			private CNumber getValueType(ISlot slot) {

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

		OneTimeParser(XNode containerNode) {

			this.containerNode = containerNode;
		}

		IFrame parse() {

			IFrame frame = parseIFrame(getTopLevelFrameNode());

			addAllSlotValues();

			return frame;
		}

		private IFrame parseIFrame(XNode node) {

			int refIndex = node.getInteger(IFRAME_REF_INDEX_ATTR, 0);

			return refIndex == 0
					? parseIFrameDirect(node)
					: resolveIFrameIndirect(refIndex);
		}

		private IFrame parseIFrameDirect(XNode node) {

			CFrame frameType = parseCFrame(node.getChild(CFRAME_ID));
			IFrame frame = createFrame(frameType);

			for (XNode slotNode : node.getChildren(ISLOT_ID)) {

				parseISlot(frame, slotNode);
			}

			return frame;
		}

		private IFrame resolveIFrameIndirect(Integer refIndex) {

			IFrame frame = iFrameRefs.get(refIndex);

			if (frame == null) {

				frame = parseIFrameIndirect(refIndex);

				iFrameRefs.put(refIndex, frame);
			}

			return frame;
		}

		private IFrame parseIFrameIndirect(int refIndex) {

			return parseIFrameDirect(getReferencedFrame(refIndex));
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

			if (node.hasChild(IVALUES_ID)) {

				parseISlotValues(frame, id, node.getChild(IVALUES_ID));
			}
		}

		private void parseISlotValues(IFrame frame, CIdentity id, XNode node) {

			if (node.hasChild(IFRAME_ID)) {

				new IFrameSlotValuesSpec(frame, id, node);
			}
			else if (node.hasChild(INUMBER_ID)) {

				new INumberSlotValuesSpec(frame, id, node);
			}
			else if (node.hasChild(CFRAME_ID)) {

				new CFrameSlotValuesSpec(frame, id, node);
			}
		}

		private IFrame createFrame(CFrame frameType) {

			IFrame frame = frameType.instantiate(frameCategory);

			iFrames.add(frame);

			return frame;
		}

		private void addAllSlotValues() {

			while (true) {

				boolean anyProcessed = false;

				for (SlotValuesSpec<?> spec : copySlotValueSpecs()) {

					anyProcessed |= spec.process();
				}

				if (!anyProcessed || slotValuesSpecs.isEmpty()) {

					break;
				}

				checkUpdateSlotsOnFrames();
			}
		}

		private void checkUpdateSlotsOnFrames() {

			for (IFrame iFrame : iFrames) {

				iFrame.update();
			}
		}

		private XNode getTopLevelFrameNode() {

			if (containerNode.getId().equals(ITREE_ID)) {

				return containerNode.getChild(IFRAME_ID);
			}

			if (containerNode.getId().equals(IGRAPH_ID)) {

				return getTopLevelGraphFrameNode();
			}

			throw createContainerNodeException();
		}

		private XNode getTopLevelGraphFrameNode() {

			List<XNode> frameNodes = containerNode.getChildren(IFRAME_ID);

			if (!frameNodes.isEmpty()) {

				return frameNodes.get(0);
			}

			throw new XDocumentException(
						"Cannot find any "
						+ "\"" + IFRAME_ID + "\""
						+ " nodes on "
						+ "\"" + IGRAPH_ID + "\""
						+ " node");
		}

		private XNode getReferencedFrame(int refIndex) {

			List<XNode> frames = containerNode.getChildren(IFRAME_ID);

			if (frames.size() >= refIndex) {

				return frames.get(refIndex);
			}

			throw new XDocumentException(
						"Invalid index for "
						+ "\"" + IFRAME_ID + "\""
						+ " node on "
						+ "\"" + IGRAPH_ID + "\""
						+ " node: " + refIndex);
		}

		private List<SlotValuesSpec<?>> copySlotValueSpecs() {

			return new ArrayList<SlotValuesSpec<?>>(slotValuesSpecs);
		}
	}

	/**
	 */
	public IFrameParser(
				CModel model,
				IEditor iEditor,
				IFrameCategory frameCategory) {

		this.model = model;
		this.iEditor = iEditor;
		this.frameCategory = frameCategory;
	}

	/**
	 */
	public IFrame parse(XDocument document) {

		return doParse(document.getRootNode());
	}

	/**
	 */
	public IFrame parse(XNode parentNode) {

		return doParse(getContainerNode(parentNode));
	}

	private IFrame doParse(XNode containerNode) {

		return new OneTimeParser(containerNode).parse();
	}

	private XNode getContainerNode(XNode parentNode) {

		XNode node = parentNode.getChildOrNull(IGRAPH_ID);

		if (node != null) {

			return node;
		}

		node = parentNode.getChildOrNull(ITREE_ID);

		if (node != null) {

			return node;
		}

		throw createContainerNodeException();
	}

	private XDocumentException createContainerNodeException() {

		throw new XDocumentException(
					"Cannot find either "
					+ "\"" + IGRAPH_ID + "\""
					+ " or "
					+ "\"" + ITREE_ID + "\""
					+ " node at top-level");
	}

	private CFrame getCFrame(CIdentity id) {

		return model.getFrames().get(id);
	}

	private boolean isClassName(Class<?> testClass, String testName) {

		return testClass.getSimpleName().equals(testName);
	}
}