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

package uk.ac.manchester.cs.mekon.serial;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.mechanism.core.*;
import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Abstract base-class for parsers for the standard XML
 * serialisation of {@link IFrame} objects.
 *
 * @author Colin Puleston
 */
public abstract class IFrameParserAbstract extends ISerialiser {

	static private Set<Class<? extends Number>> numberTypes
						= new HashSet<Class<? extends Number>>();

	static {

		numberTypes.add(Integer.class);
		numberTypes.add(Long.class);
		numberTypes.add(Float.class);
		numberTypes.add(Double.class);
	}

	private CModel model;
	private IEditor iEditor;
	private IFrameCategory frameCategory;

	private class OneTimeParser {

		private XNode containerNode;

		private List<IFrame> frames = new ArrayList<IFrame>();

		private KListMap<IFrame, SlotSpec<?>> frameSlots
							= new KListMap<IFrame, SlotSpec<?>>();

		private Map<Integer, IFrame> frameRefs = new HashMap<Integer, IFrame>();

		private abstract class SlotSpec<V> {

			private IFrame frame;
			private CIdentity slotId;

			private List<V> valueSpecs = new ArrayList<V>();

			SlotSpec(IFrame frame, XNode slotNode) {

				this.frame = frame;

				slotId = parseIdentity(slotNode.getChild(CSLOT_ID));

				frameSlots.add(frame, this);
			}

			void addValueSpecs(XNode valuesNode) {

				for (XNode valueNode : valuesNode.getChildren(getValueId())) {

					valueSpecs.add(resolveValueSpec(valueNode));
				}
			}

			boolean process() {

				ISlot slot = checkResolveSlot(frame, slotId);

				if (slot != null) {

					setValidValues(slot);
					frameSlots.remove(frame, this);

					return true;
				}

				return false;
			}

			abstract String getValueId();

			abstract ISlot checkResolveSlot(IFrame frame, CIdentity slotId);

			abstract V resolveValueSpec(XNode valueNode);

			abstract IValue getValue(ISlot slot, V valueSpec);

			private void setValidValues(ISlot slot) {

				List<IValue> values = getValidValues(slot);

				if (!values.isEmpty()) {

					setValues(slot, values);
				}
			}

			private void setValues(ISlot slot, List<IValue> values) {

				iEditor.getSlotEditor(slot).setAssertedValues(values);
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

			IFrameSlotSpec(IFrame frame, XNode slotNode) {

				super(frame, slotNode);
			}

			String getValueId() {

				return IFRAME_ID;
			}

			ISlot checkResolveSlot(IFrame frame, CIdentity slotId) {

				return checkResolveIFrameSlot(frame, slotId);
			}

			IValue resolveValueSpec(XNode valueNode) {

				return parseIFrame(valueNode);
			}

			IValue getValue(ISlot slot, IValue valueSpec) {

				return valueSpec;
			}
		}

		private class CFrameSlotSpec extends SlotSpec<IValue> {

			CFrameSlotSpec(IFrame frame, XNode slotNode) {

				super(frame, slotNode);
			}

			String getValueId() {

				return CFRAME_ID;
			}

			ISlot checkResolveSlot(IFrame frame, CIdentity slotId) {

				return checkResolveCFrameSlot(frame, slotId);
			}

			IValue resolveValueSpec(XNode valueNode) {

				return parseCFrame(valueNode);
			}

			IValue getValue(ISlot slot, IValue valueSpec) {

				return valueSpec;
			}
		}

		private class INumberSlotSpec extends SlotSpec<XNode> {

			private Class<? extends Number> numberType;

			INumberSlotSpec(IFrame frame, XNode slotNode) {

				super(frame, slotNode);

				numberType = getNumberTypeOrNull(slotNode);
			}

			String getValueId() {

				return INUMBER_ID;
			}

			ISlot checkResolveSlot(IFrame frame, CIdentity slotId) {

				return checkResolveINumberSlot(frame, slotId, numberType);
			}

			XNode resolveValueSpec(XNode valueNode) {

				return valueNode;
			}

			IValue getValue(ISlot slot, XNode valueSpec) {

				return parseINumber(getValueType(slot), valueSpec);
			}

			private Class<? extends Number> getNumberTypeOrNull(XNode slotNode) {

				XNode typeNode = slotNode.getChildOrNull(CNUMBER_ID);

				return typeNode != null ? getNumberType(typeNode) : null;
			}

			private Class<? extends Number> getNumberType(XNode typeNode) {

				return getNumberType(typeNode.getString(NUMBER_TYPE_ATTR));
			}

			private Class<? extends Number> getNumberType(String className) {

				for (Class<? extends Number> numberType : numberTypes) {

					if (numberType.getSimpleName().equals(className)) {

						return numberType;
					}
				}

				throw new XDocumentException("Unrecognised number class: " + className);
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

			IFrame rootFrame = parseIFrame(getTopLevelFrameNode());

			addSlotValues();
			onParseCompletion(rootFrame, frames);

			return rootFrame;
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

		private void parseISlot(IFrame frame, XNode slotNode) {

			XNode valuesNode = slotNode.getChildOrNull(IVALUES_ID);

			if (valuesNode == null || !parseISlot(frame, slotNode, valuesNode)) {

				parseISlotWithNoValues(frame, slotNode);
			}
		}

		private boolean parseISlot(IFrame frame, XNode slotNode, XNode valuesNode) {

			SlotSpec<?> spec = null;

			if (valuesNode.hasChild(IFRAME_ID)) {

				spec = new IFrameSlotSpec(frame, slotNode);
			}
			else if (valuesNode.hasChild(INUMBER_ID)) {

				spec = new INumberSlotSpec(frame, slotNode);
			}
			else if (valuesNode.hasChild(CFRAME_ID)) {

				spec = new CFrameSlotSpec(frame, slotNode);
			}
			else {

				return false;
			}

			spec.addValueSpecs(valuesNode);

			return true;
		}

		private void parseISlotWithNoValues(IFrame frame, XNode slotNode) {

			if (slotNode.hasChild(CFRAME_ID)) {

				new IFrameSlotSpec(frame, slotNode);
			}
			else if (slotNode.hasChild(CNUMBER_ID)) {

				new INumberSlotSpec(frame, slotNode);
			}
			else if (slotNode.hasChild(MFRAME_ID)) {

				new CFrameSlotSpec(frame, slotNode);
			}
		}

		private IFrame createAndRegisterFrame(CFrame type) {

			IFrame frame = instantiateFrame(type, frameCategory);

			frames.add(frame);

			return frame;
		}

		private void addSlotValues() {

			while (!frameSlots.isEmpty() && addValuesForAvailableSlots()) {

				checkUpdateFrameSlotSets(frames);
			}
		}

		private boolean addValuesForAvailableSlots() {

			boolean anyAdded = false;

			for (IFrame frame : frameSlots.keySet()) {

				for (SlotSpec<?> spec : frameSlots.getList(frame)) {

					anyAdded |= spec.process();
				}
			}

			return anyAdded;
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
	 * Parses serialised frame from top-level element of specified
	 * document.
	 *
	 * @param document Document containing serialised frame
	 * @return Generated frame
	 */
	public IFrame parse(XDocument document) {

		return parseFromContainerNode(document.getRootNode());
	}

	/**
	 * Parses serialised frame from relevant child of specified
	 * parent. This will be a node with either a "ITree" or "IGraph"
	 * tag, depending on the format in which the frame is serialised.
	 *
	 * @param parentNode Parent of relevant node
	 * @return Generated frame
	 */
	public IFrame parse(XNode parentNode) {

		return parseFromContainerNode(getContainerNode(parentNode));
	}

	IFrameParserAbstract(CModel model, IFrameCategory frameCategory) {

		this.model = model;
 		this.frameCategory = frameCategory;

		iEditor = ZCModelAccessor.get().getIEditor(model);
	}

	abstract IFrame instantiateFrame(CFrame type, IFrameCategory category);

	abstract ISlot checkResolveIFrameSlot(IFrame frame, CIdentity slotId);

	abstract ISlot checkResolveCFrameSlot(IFrame frame, CIdentity slotId);

	abstract ISlot checkResolveINumberSlot(
						IFrame frame,
						CIdentity slotId,
						Class<? extends Number> numberType);

	abstract void checkUpdateFrameSlotSets(List<IFrame> frames);

	abstract void onParseCompletion(IFrame rootFrame, List<IFrame> frames);

	private IFrame parseFromContainerNode(XNode containerNode) {

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
}
