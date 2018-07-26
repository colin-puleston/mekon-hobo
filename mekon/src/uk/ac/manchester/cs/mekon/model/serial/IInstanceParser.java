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
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * Parser for the standard XML serialisation of MEKON instances as
 * represented via {@link IFrame}/{@link ISlot} networks.
 *
 * @author Colin Puleston
 */
public class IInstanceParser extends ISerialiser {

	static private final String IDENTIFIER_ATTR = CIdentitySerialiser.IDENTIFIER_ATTR;

	static private IRelaxedInstantiator instantiator = IRelaxedInstantiator.get();

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
	private boolean freeInstances = false;

	private class OneTimeParser {

		private XNode containerNode;

		private Map<String, IFrame> framesByXDocId;
		private Map<String, XNode> frameNodesByXDocId = new HashMap<String, XNode>();

		private List<SlotSpec<?>> slotSpecs = new ArrayList<SlotSpec<?>>();
		private ValuesUpdate valuesUpdate = new ValuesUpdate();

		private Set<CIdentity> invalidCFrameIds = new HashSet<CIdentity>();

		private class ValuesUpdate {

			private ISlot slot = null;
			private IValue addedValue = null;

			void setSlot(ISlot slot) {

				this.slot = slot;
			}

			void setAddedValue(IValue addedValue) {

				this.addedValue = addedValue;
			}

			void checkApply() {

				if (slot != null) {

					if (addedValue != null) {

						slot.getValuesEditor().add(addedValue);
					}
					else {

						updateForRemovals(slot, new HashSet<IFrame>());
					}
				}
			}

			private void updateForRemovals(ISlot currentSlot, Set<IFrame> visitedFrames) {

				IFrame currentFrame = currentSlot.getContainer();

				if (visitedFrames.add(currentFrame)) {

					currentFrame.update();

					for (ISlot refSlot : currentFrame.getReferencingSlots().asList()) {

						updateForRemovals(refSlot, visitedFrames);
					}
				}
			}
		}

		private abstract class SlotSpec<V> {

			private IFrame container;
			private ISlot slot;

			private XNode slotNode;
			private XNode slotTypeNode;

			private List<V> fixedValueSpecs = new ArrayList<V>();
			private List<V> assertedValueSpecs = new ArrayList<V>();

			private int addedValueIndex = -1;

			SlotSpec(IFrame container, XNode slotNode) {

				this.container = container;
				this.slotNode = slotNode;

				slotTypeNode = slotNode.getChild(CSLOT_ID);
				slot = addSlot();

				if (!freeInstances) {

					checkInitForValuesUpdate();
				}

				slotSpecs.add(this);
			}

			void addValueSpecs(XNode valuesNode) {

				for (XNode valueNode : valuesNode.getChildren(getValueId())) {

					getValueSpecsList(valueNode).add(resolveValueSpec(valueNode));
				}
			}

			void processValueSpecs() {

				if (!fixedValueSpecs.isEmpty()) {

					processFixedValueSpecs();
				}

				if (!assertedValueSpecs.isEmpty()) {

					processAssertedValueSpecs();
				}
			}

			abstract String getValueTypeId();

			abstract String getValueId();

			abstract CValue<?> getValueType(XNode valueTypeNode);

			abstract CValue<?> getDefaultValueType(XNode slotNode);

			abstract V resolveValueSpec(XNode valueNode);

			abstract IValue getValue(ISlot slot, V valueSpec);

			private ISlot addSlot() {

				return freeInstances ? addFreeSlot() : addConstrainedSlot();
			}

			private ISlot addFreeSlot() {

				return instantiator.addFreeSlot(container, getSlotId(), getDefaultValueType());
			}

			private ISlot addConstrainedSlot() {

				return instantiator
						.addSlot(
							container,
							getSlotId(),
							getValueType(),
							getCardinality(),
							getActivation(),
							getEditability());
			}

			private void checkInitForValuesUpdate() {

				XNode updNode = slotNode.getChildOrNull(IVALUES_UPDATE_ID);

				if (updNode != null) {

					valuesUpdate.setSlot(slot);

					addedValueIndex = updNode.getInteger(ADDED_VALUE_INDEX_ATTR, -1);
				}
			}

			private List<V> getValueSpecsList(XNode valueNode) {

				return valueNode.getBoolean(FIXED_VALUE_STATUS_ATTR)
						? fixedValueSpecs
						: assertedValueSpecs;
			}

			private void processFixedValueSpecs() {

				getSlotEditor().updateFixedValues(getValues(fixedValueSpecs));
			}

			private void processAssertedValueSpecs() {

				CValue<?> valueType = slot.getValueType();
				List<IValue> validNonNewValues = new ArrayList<IValue>();

				int index = 0;

				for (IValue value : getValues(assertedValueSpecs)) {

					if (index++ == addedValueIndex) {

						valuesUpdate.setAddedValue(value);
					}
					else {

						if (valueType.validValue(value)) {

							validNonNewValues.add(value);
						}
					}
				}

				if (!validNonNewValues.isEmpty()) {

					getValuesEditor().update(validNonNewValues);
				}
			}

			private List<IValue> getValues(List<V> valueSpecs) {

				List<IValue> values = new ArrayList<IValue>();

				for (V valueSpec : valueSpecs) {

					values.add(getValue(slot, valueSpec));
				}

				return values;
			}

			private CIdentity getSlotId() {

				return parseIdentity(slotTypeNode);
			}

			private CValue<?> getValueType() {

				return getValueType(slotNode.getChild(getValueTypeId()));
			}

			private CValue<?> getDefaultValueType() {

				return getDefaultValueType(slotNode);
			}

			private CCardinality getCardinality() {

				return slotTypeNode.getEnum(CARDINALITY_ATTR, CCardinality.class);
			}

			private CActivation getActivation() {

				return slotTypeNode.getEnum(ACTIVATION_ATTR, CActivation.class);
			}

			private IEditability getEditability() {

				return slotNode.getEnum(EDITABILITY_ATTR, IEditability.class);
			}

			private ISlotEditor getSlotEditor() {

				return iEditor.getSlotEditor(slot);
			}

			private ISlotValuesEditor getValuesEditor() {

				return iEditor.getSlotValuesEditor(slot);
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

				return parseMFrame(valueTypeNode);
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

				return parseCFrame(valueTypeNode);
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

		OneTimeParser(IInstanceParseInput input) {

			containerNode = resolveContainerNode(input);
			framesByXDocId = input.getFramesByXDocId();
		}

		IFrame parse() {

			IFrame rootFrame = resolveIFrame(getRootFrameNode());

			processSlotValueSpecs();
			completeReinstantiation();

			valuesUpdate.checkApply();

			return rootFrame;
		}

		CFrame parseRootFrameType() {

			return parseCFrame(getRootFrameNode().getChild(CFRAME_ID));
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
			IFrame frame = startInstantiation(frameType);

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

		private MFrame parseMFrame(XNode node) {

			return parseCFrame(node, MFRAME_ID).getType();
		}

		private CFrame parseCFrame(XNode node) {

			return parseCFrame(node, CFRAME_ID);
		}

		private CFrame parseCFrame(XNode node, String disjunctTag) {

			return node.hasAttribute(IDENTIFIER_ATTR)
					? parseAtomicCFrame(node)
					: parseDisjunctionCFrame(node, disjunctTag);
		}

		private CFrame parseDisjunctionCFrame(XNode node, String disjunctTag) {

			List<CFrame> disjuncts = new ArrayList<CFrame>();

			for (XNode disjunctNode : node.getChildren(disjunctTag)) {

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

			return CNumber.range(numberType, min, max);
		}

		private void parseISlot(IFrame container, XNode slotNode) {

			XNode valuesNode = slotNode.getChildOrNull(IVALUES_ID);
			SlotSpec<?> spec = checkCreateSlotSpec(container, slotNode, valuesNode);

			if (spec != null && valuesNode != null) {

				spec.addValueSpecs(valuesNode);
			}
		}

		private IFrame startInstantiation(CFrame frameType) {

			return instantiator.startInstantiation(frameType, frameFunction, freeInstances);
		}

		private CFrame getCFrame(CIdentity id) {

			CFrame rootFrame = model.getRootFrame();

			if (!rootFrame.getIdentity().equals(id)) {

				CFrame frame = model.getFrames().getOrNull(id);

				if (frame != null) {

					return frame;
				}

				invalidCFrameIds.add(id);
			}

			return rootFrame;
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

		private void processSlotValueSpecs() {

			for (SlotSpec<?> spec : slotSpecs) {

				spec.processValueSpecs();
			}
		}

		private void completeReinstantiation() {

			for (IFrame frame : framesByXDocId.values()) {

				instantiator.completeReinstantiation(frame);
			}
		}

		private XNode getRootFrameNode() {

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
	public IInstanceParser(CModel model, IFrameFunction frameFunction) {

		this.model = model;
 		this.frameFunction = frameFunction;

		iEditor = ZCModelAccessor.get().getIEditor(model);
	}

	/**
	 * Determines whether "free-instances", rather than normal instances
	 * are to be generated as a result of the parsing (see
	 * {@link IFreeCopier}).
	 *
	 * @param freeInstances True if free-instances are to be generated
	 */
	public void setFreeInstances(boolean freeInstances) {

		this.freeInstances = freeInstances;
	}

	/**
	 * Parses serialised frame/slot network.
	 *
	 * @param input Input to parsing process
	 * @return Root-frame of generated network
	 */
	public IFrame parse(IInstanceParseInput input) {

		return new OneTimeParser(input).parse();
	}

	/**
	 * Parses only the type of the root-frame of the serialised frame/slot
	 * network.
	 *
	 * @param input Input to parsing process
	 * @return Type of the root-frame
	 */
	public CFrame parseRootFrameType(IInstanceParseInput input) {

		return new OneTimeParser(input).parseRootFrameType();
	}

	private CFrame getRootCFrame() {

		return model.getRootFrame();
	}

	private XNode resolveContainerNode(IInstanceParseInput input) {

		XNode containerNode = input.getContainerNode();

		return containerNode != null
				? containerNode
				: getContainerNode(input.getParentNode());
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

	private CIdentity parseIdentity(XNode node) {

		return CIdentitySerialiser.parse(node);
	}
}
