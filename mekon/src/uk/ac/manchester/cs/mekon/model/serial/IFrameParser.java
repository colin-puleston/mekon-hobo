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
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;
import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Parser for the standard XML serialisation of {@link IFrame}/{@link ISlot}
 * networks.
 *
 * @author Colin Puleston
 */
public class IFrameParser extends ISerialiser {

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
	private IFrameFunction frameFunction;

	private IFrameParseMechanisms mechanisms;

	private class OneTimeParser {

		private XNode containerNode;

		private List<IFrame> frames = new ArrayList<IFrame>();

		private Map<String, IFrame> framesByXDocId;
		private Map<String, XNode> frameNodesByXDocId = new HashMap<String, XNode>();

		private KListMap<IFrame, SlotSpec<?>> frameSlots
							= new KListMap<IFrame, SlotSpec<?>>();

		private abstract class SlotSpec<V> extends IFrameParserSlotSpec {

			private IFrame container;
			private XNode slotNode;
			private XNode slotTypeNode;

			private List<V> valueSpecs = new ArrayList<V>();

			SlotSpec(IFrame container, XNode slotNode) {

				this.container = container;
				this.slotNode = slotNode;

				slotTypeNode = slotNode.getChild(CSLOT_ID);

				frameSlots.add(container, this);
			}

			void addValueSpecs(XNode valuesNode) {

				for (XNode valueNode : valuesNode.getChildren(getValueId())) {

					valueSpecs.add(resolveValueSpec(valueNode));
				}
			}

			boolean process() {

				ISlot slot = mechanisms.checkResolveSlot(this);

				if (slot != null) {

					setValidValues(slot);
					frameSlots.remove(container, this);

					return true;
				}

				return false;
			}

			IFrame getContainer() {

				return container;
			}

			CIdentity getSlotId() {

				return parseIdentity(slotTypeNode);
			}

			CValue<?> getValueType() {

				return getValueType(slotNode.getChild(getValueTypeId()));
			}

			CValue<?> getDefaultValueType() {

				return getDefaultValueType(slotNode);
			}

			CCardinality getCardinality() {

				return slotTypeNode.getEnum(CARDINALITY_ATTR, CCardinality.class);
			}

			IEditability getEditability() {

				return slotNode.getEnum(EDITABILITY_ATTR, IEditability.class);
			}

			abstract String getValueTypeId();

			abstract String getValueId();

			abstract CValue<?> getValueType(XNode valueTypeNode);

			abstract CValue<?> getDefaultValueType(XNode slotNode);

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

		private class CFrameSlotSpec extends SlotSpec<IValue> {

			CFrameSlotSpec(IFrame container, XNode slotNode) {

				super(container, slotNode);
			}

			String getValueTypeId() {

				return MFRAME_ID;
			}

			String getValueId() {

				return CFRAME_ID;
			}

			CValue<?> getValueType(XNode valueTypeNode) {

				return getCFrame(parseIdentity(valueTypeNode)).getType();
			}

			CValue<?> getDefaultValueType(XNode slotNode) {

				return getRootCFrame().getType();
			}

			IValue resolveValueSpec(XNode valueNode) {

				return parseCFrame(valueNode);
			}

			IValue getValue(ISlot slot, IValue valueSpec) {

				return valueSpec;
			}
		}

		private class IFrameSlotSpec extends SlotSpec<IValue> {

			IFrameSlotSpec(IFrame container, XNode slotNode) {

				super(container, slotNode);
			}

			String getValueTypeId() {

				return CFRAME_ID;
			}

			String getValueId() {

				return IFRAME_ID;
			}

			CValue<?> getValueType(XNode valueTypeNode) {

				return getCFrame(parseIdentity(valueTypeNode));
			}

			CValue<?> getDefaultValueType(XNode slotNode) {

				return getRootCFrame();
			}

			IValue resolveValueSpec(XNode valueNode) {

				return resolveIFrame(valueNode);
			}

			IValue getValue(ISlot slot, IValue valueSpec) {

				return valueSpec;
			}
		}

		private class INumberSlotSpec extends SlotSpec<XNode> {

			INumberSlotSpec(IFrame container, XNode slotNode) {

				super(container, slotNode);
			}

			String getValueTypeId() {

				return CNUMBER_ID;
			}

			String getValueId() {

				return INUMBER_ID;
			}

			CValue<?> getValueType(XNode valueTypeNode) {

				return parseCNumber(getNumberType(valueTypeNode), valueTypeNode);
			}

			CValue<?> getDefaultValueType(XNode slotNode) {

				XNode valueTypeNode = slotNode.getChild(CNUMBER_ID);

				return CNumber.unconstrained(getNumberType(valueTypeNode));
			}

			XNode resolveValueSpec(XNode valueNode) {

				return valueNode;
			}

			IValue getValue(ISlot slot, XNode valueSpec) {

				return parseINumber(getValueType(slot), valueSpec);
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

		private class IStringSlotSpec extends SlotSpec<IValue> {

			IStringSlotSpec(IFrame container, XNode slotNode) {

				super(container, slotNode);
			}

			String getValueTypeId() {

				return CSTRING_ID;
			}

			String getValueId() {

				return ISTRING_ID;
			}

			CValue<?> getValueType(XNode valueTypeNode) {

				return CString.SINGLETON;
			}

			CValue<?> getDefaultValueType(XNode slotNode) {

				return CString.SINGLETON;
			}

			IValue resolveValueSpec(XNode valueNode) {

				return parseIString(valueNode);
			}

			IValue getValue(ISlot slot, IValue valueSpec) {

				return valueSpec;
			}
		}

		OneTimeParser(XNode containerNode, Map<String, IFrame> framesByXDocId) {

			this.containerNode = containerNode;
			this.framesByXDocId = framesByXDocId;
		}

		IFrame parse() {

			IFrame rootFrame = resolveIFrame(getTopLevelFrameNode());

			addSlotValues();
			mechanisms.onParseCompletion(rootFrame, frames);

			return rootFrame;
		}

		private IFrame resolveIFrame(XNode node) {

			String xid = node.getString(IFRAME_XDOC_ID_REF_ATTR, null);
			boolean tree = xid == null;

			if (tree) {

				xid = node.getString(IFRAME_XDOC_ID_ATTR);
			}

			IFrame frame = framesByXDocId.get(xid);

			if (frame == null) {

				frame = parseIFrame(tree ? node : getGraphFrameNode(xid));
				framesByXDocId.put(xid, frame);
			}

			return frame;
		}

		private IFrame parseIFrame(XNode node) {

			return node.hasChild(CFRAME_ID)
					? parseAtomicIFrame(node)
					: parseDisjunctionIFrame(node);
		}

		private IFrame parseAtomicIFrame(XNode node) {

			CFrame frameType = parseCFrame(node.getChild(CFRAME_ID));
			IFrame frame = createAndRegisterFrame(frameType);

			for (XNode slotNode : node.getChildren(ISLOT_ID)) {

				parseISlot(frame, slotNode);
			}

			return frame;
		}

		private IFrame parseDisjunctionIFrame(XNode node) {

			List<IFrame> disjuncts = new ArrayList<IFrame>();

			for (XNode disjunctNode : node.getChildren(IFRAME_ID)) {

				disjuncts.add(resolveIFrame(disjunctNode));
			}

			return IFrame.createDisjunction(disjuncts);
		}

		private INumber parseINumber(CNumber valueType, XNode node) {

			Class<? extends Number> numberType = valueType.getNumberType();

			return node.hasAttribute(NUMBER_VALUE_ATTR)
					? parseDefiniteINumber(numberType, node, NUMBER_VALUE_ATTR)
					: parseCNumber(numberType, node).asINumber();
		}

		private INumber parseDefiniteINumber(
							Class<? extends Number> numberType,
							XNode node,
							String attrName) {

			return new INumber(numberType, node.getString(attrName));
		}

		private IString parseIString(XNode node) {

			return new IString(node.getString(STRING_VALUE_ATTR));
		}

		private CFrame parseCFrame(XNode node) {

			return node.hasAttribute(IDENTITY_ATTR)
					? parseAtomicCFrame(node)
					: parseDisjunctionCFrame(node);
		}

		private CFrame parseDisjunctionCFrame(XNode node) {

			List<CFrame> disjuncts = new ArrayList<CFrame>();

			for (XNode disjunctNode : node.getChildren(CFRAME_ID)) {

				disjuncts.add(parseAtomicCFrame(disjunctNode));
			}

			return CFrame.resolveDisjunction(disjuncts);
		}

		private CFrame parseAtomicCFrame(XNode node) {

			return getCFrame(parseIdentity(node));
		}

		private CNumber parseCNumber(Class<? extends Number> numberType, XNode node) {

			INumber min = INumber.MINUS_INFINITY;
			INumber max = INumber.PLUS_INFINITY;

			if (node.hasAttribute(NUMBER_MIN_ATTR)) {

				min = parseDefiniteINumber(numberType, node, NUMBER_MIN_ATTR);
			}

			if (node.hasAttribute(NUMBER_MAX_ATTR)) {

				max = parseDefiniteINumber(numberType, node, NUMBER_MAX_ATTR);
			}

			return CNumber.range(min, max);
		}

		private void parseISlot(IFrame container, XNode slotNode) {

			XNode valuesNode = slotNode.getChildOrNull(IVALUES_ID);
			SlotSpec<?> spec = checkCreateSlotSpec(container, slotNode, valuesNode);

			if (spec != null && valuesNode != null) {

				spec.addValueSpecs(valuesNode);
			}
		}

		private SlotSpec<?> checkCreateSlotSpec(
								IFrame container,
								XNode slotNode,
								XNode valuesNode) {

			if (slotNode.hasChild(MFRAME_ID)) {

				return new CFrameSlotSpec(container, slotNode);
			}

			if (slotNode.hasChild(CFRAME_ID)) {

				return new IFrameSlotSpec(container, slotNode);
			}

			if (slotNode.hasChild(CNUMBER_ID)) {

				return new INumberSlotSpec(container, slotNode);
			}

			if (slotNode.hasChild(CSTRING_ID)) {

				return new IStringSlotSpec(container, slotNode);
			}

			if (valuesNode != null) {

				if (valuesNode.hasChild(CFRAME_ID)) {

					return new CFrameSlotSpec(container, slotNode);
				}

				if (valuesNode.hasChild(IFRAME_ID)) {

					return new IFrameSlotSpec(container, slotNode);
				}

				if (valuesNode.hasChild(INUMBER_ID)) {

					return new INumberSlotSpec(container, slotNode);
				}

				if (valuesNode.hasChild(ISTRING_ID)) {

					return new IStringSlotSpec(container, slotNode);
				}
			}

			return null;
		}

		private IFrame createAndRegisterFrame(CFrame type) {

			IFrame frame = mechanisms.instantiateFrame(type, frameFunction);

			frames.add(frame);

			return frame;
		}

		private void addSlotValues() {

			while (!frameSlots.isEmpty() && addValuesForAvailableSlots()) {

				mechanisms.checkUpdateFrameSlotSets(frames);
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

		private XNode getGraphFrameNode(String xid) {

			XNode frameNode = getFrameNodesByXDocId().get(xid);

			if (frameNode != null) {

				return frameNode;
			}

			throw new XDocumentException(
						"Invalid reference for "
						+ "\"" + IFRAME_ID + "\""
						+ " node on "
						+ "\"" + IGRAPH_ID + "\""
						+ " node: " + xid);
		}

		private Map<String, XNode> getFrameNodesByXDocId() {

			if (frameNodesByXDocId.isEmpty()) {

				for (XNode frameNode : containerNode.getChildren(IFRAME_ID)) {

					String xid = frameNode.getString(IFRAME_XDOC_ID_ATTR);

					frameNodesByXDocId.put(xid, frameNode);
				}
			}

			return frameNodesByXDocId;
		}
	}

	/**
	 * Constructor
	 *
	 * @param model Relevant model
	 * @param frameFunction Function of frames to be parsed
	 */
	public IFrameParser(CModel model, IFrameFunction frameFunction) {

		this.model = model;
 		this.frameFunction = frameFunction;

		iEditor = ZCModelAccessor.get().getIEditor(model);
		mechanisms = new IFrameDynamicParseMechanisms(model);
	}

	/**
	 * Sets the manner in which schema information is to be derived
	 * when parsing. Defaults to {@link ISchemaParse#DYNAMIC}.
	 *
	 * @param schemaParse Required schema-level
	 */
	public void setSchemaParse(ISchemaParse schemaParse) {

		if (mechanisms.getSchemaParse() != schemaParse) {

			mechanisms = schemaParse.getMechanisms(model);
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
	 * Parses serialised frame from top-level element of specified
	 * document.
	 *
	 * @param document Document containing serialised frame
	 * @param framesByXDocId Map into which to write the document-specific
	 * frame-identifiers corresponding to the generated component frames
	 *
	 * @return Generated frame
	 */
	public IFrame parse(XDocument document, Map<String, IFrame> framesByXDocId) {

		return parseFromContainerNode(document.getRootNode(), framesByXDocId);
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

	/**
	 * Parses serialised frame from relevant child of specified
	 * parent. This will be a node with either a "ITree" or "IGraph"
	 * tag, depending on the format in which the frame is serialised.
	 *
	 * @param parentNode Parent of relevant node
	 * @param framesByXDocId Map into which to write the document-specific
	 * frame-identifiers corresponding to the generated component frames
	 *
	 * @return Generated frame
	 */
	public IFrame parse(XNode parentNode, Map<String, IFrame> framesByXDocId) {

		return parseFromContainerNode(getContainerNode(parentNode), framesByXDocId);
	}

	private IFrame parseFromContainerNode(XNode containerNode) {

		return parseFromContainerNode(containerNode, new HashMap<String, IFrame>());
	}

	private IFrame parseFromContainerNode(
						XNode containerNode,
						Map<String, IFrame> framesByXDocId) {

		return new OneTimeParser(containerNode, framesByXDocId).parse();
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

	private CFrame getRootCFrame() {

		return model.getRootFrame();
	}

	private CFrame getCFrame(CIdentity id) {

		return model.getFrames().get(id);
	}
}
