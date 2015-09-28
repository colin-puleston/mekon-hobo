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
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
public class IFrameParser extends ISerialiser {

	private CModel model;
	private IEditor iEditor;
	private IFrameCategory frameCategory;

	private class OneTimeParser {

		private XNode containerNode;

		private List<IFrame> frames = new ArrayList<IFrame>();

		private KSetMap<IFrame, SlotSpec<?>> frameSlots
							= new KSetMap<IFrame, SlotSpec<?>>();

		private Map<Integer, IFrame> frameRefs = new HashMap<Integer, IFrame>();

		private abstract class SlotSpec<V> {

			private IFrame frame;
			private CIdentity slotId;

			private List<V> valueSpecs = new ArrayList<V>();

			SlotSpec(IFrame frame, CIdentity slotId, XNode slotNode) {

				this.frame = frame;
				this.slotId = slotId;

				resolveValueSpecs(slotNode.getChild(IVALUES_ID));

				frameSlots.add(frame, this);
			}

			boolean process() {

				ISlots slots = frame.getSlots();

				if (slots.containsValueFor(slotId)) {

					setValues(slots.get(slotId));
					frameSlots.remove(frame, this);

					return true;
				}

				return false;
			}

			abstract String getValueId();

			abstract V resolveValueSpec(XNode valueNode);

			abstract IValue getValue(ISlot slot, V valueSpec);

			private void resolveValueSpecs(XNode valuesNode) {

				for (XNode valueNode : valuesNode.getChildren(getValueId())) {

					valueSpecs.add(resolveValueSpec(valueNode));
				}
			}

			private void setValues(ISlot slot) {

				iEditor.getSlotValuesEditor(slot).update(getValidValues(slot));
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

		private class IFrameSlotSpec extends SlotSpec<IValue> {

			IFrameSlotSpec(IFrame frame, CIdentity slotId, XNode parentNode) {

				super(frame, slotId, parentNode);
			}

			String getValueId() {

				return IFRAME_ID;
			}

			IValue resolveValueSpec(XNode valueNode) {

				return parseIFrame(valueNode);
			}

			IValue getValue(ISlot slot, IValue valueSpec) {

				if (slot.getValueType() instanceof MFrame) {

					return ((IFrame)valueSpec).getType();
				}

				return valueSpec;
			}
		}

		private class CFrameSlotSpec extends SlotSpec<IValue> {

			CFrameSlotSpec(IFrame frame, CIdentity slotId, XNode parentNode) {

				super(frame, slotId, parentNode);
			}

			String getValueId() {

				return CFRAME_ID;
			}

			IValue resolveValueSpec(XNode valueNode) {

				return parseCFrame(valueNode);
			}

			IValue getValue(ISlot slot, IValue valueSpec) {

				return valueSpec;
			}
		}

		private class INumberSlotSpec extends SlotSpec<XNode> {

			INumberSlotSpec(IFrame frame, CIdentity slotId, XNode parentNode) {

				super(frame, slotId, parentNode);
			}

			String getValueId() {

				return INUMBER_ID;
			}

			XNode resolveValueSpec(XNode valueNode) {

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

			addSlotValues();
			setAutoUpdateEnabledForAllFrames(true);

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
			IFrame frame = createAndRegisterFrame(frameType);

			for (XNode slotNode : node.getChildren(ISLOT_ID)) {

				parseISlot(frame, slotNode);
			}

			return frame;
		}

		private IFrame resolveIFrameIndirect(Integer refIndex) {

			IFrame frame = frameRefs.get(refIndex);

			if (frame == null) {

				frame = parseIFrameIndirect(refIndex);

				frameRefs.put(refIndex, frame);
			}

			return frame;
		}

		private IFrame parseIFrameIndirect(int refIndex) {

			return parseIFrameDirect(getReferencedFrame(refIndex));
		}

		private CFrame parseMFrame(XNode node) {

			return parseCFrame(node, MFRAME_ID);
		}

		private CFrame parseCFrame(XNode node) {

			return parseCFrame(node, CFRAME_ID);
		}

		private CFrame parseCFrame(XNode node, String tag) {

			return node.hasAttribute(IDENTITY_ATTR)
					? parseAtomicCFrame(node)
					: parseDisjunctionCFrame(node, tag);
		}

		private CFrame parseDisjunctionCFrame(XNode node, String tag) {

			List<CFrame> disjuncts = new ArrayList<CFrame>();

			for (XNode disjunctNode : node.getChildren(tag)) {

				disjuncts.add(parseAtomicCFrame(disjunctNode));
			}

			return CFrame.resolveDisjunction(disjuncts);
		}

		private CFrame parseAtomicCFrame(XNode node) {

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

		private void parseISlot(IFrame frame, XNode node) {

			CIdentity id = parseIdentity(node.getChild(CSLOT_ID));

			if (node.hasChild(IVALUES_ID)) {

				if (node.hasChild(IFRAME_ID)) {

					new IFrameSlotSpec(frame, id, node);
				}
				else if (node.hasChild(INUMBER_ID)) {

					new INumberSlotSpec(frame, id, node);
				}
				else if (node.hasChild(CFRAME_ID)) {

					new CFrameSlotSpec(frame, id, node);
				}
			}
		}

		private IFrame createAndRegisterFrame(CFrame frameType) {

			IFrame frame = createFrameNoAutoUpdate(frameType);

			frames.add(frame);

			return frame;
		}

		private IFrame createFrameNoAutoUpdate(CFrame frameType) {

			return iEditor.instantiateNoAutoUpdate(frameType, frameCategory);
		}

		private void addSlotValues() {

			while (addValuesForAvailableSlots() && !frameSlots.isEmpty()) {

				checkUpdateFrameSlotSets();
			}
		}

		private boolean addValuesForAvailableSlots() {

			boolean anyAdded = false;

			for (IFrame frame : frameSlots.keySet()) {

				for (SlotSpec<?> spec : frameSlots.getSet(frame)) {

					anyAdded |= spec.process();
				}
			}

			return anyAdded;
		}

		private void checkUpdateFrameSlotSets() {

			setAutoUpdateEnabledForAllFrames(true);

			for (IFrame frame : frames) {

				frame.update();
			}

			setAutoUpdateEnabledForAllFrames(false);
		}

		private void setAutoUpdateEnabledForAllFrames(boolean enabled) {

			for (IFrame frame : frames) {

				setAutoUpdateEnabled(frame, enabled);
			}
		}

		private void setAutoUpdateEnabled(IFrame frame, boolean enabled) {

			iEditor.getFrameEditor(frame).setAutoUpdateEnabled(enabled);
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