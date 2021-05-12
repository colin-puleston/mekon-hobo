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
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * Parser for the standard XML serialisation of MEKON instances as
 * represented via {@link IFrame}/{@link ISlot} networks.
 * <p>
 * If any updates have occured to the model since an instance was
 * serialised which prevent the instance from being reassembled in
 * its orignial form, the instance will be partially assembled as
 * far as the updates allow, and provided together with information
 * as to the pruning that was required.
 *
 * @author Colin Puleston
 */
public class IInstanceParser extends FSerialiser implements ISerialiserVocab {

	static private IRelaxedInstantiator instantiator = IRelaxedInstantiator.get();

	private CModel model;
	private IEditor iEditor;
	private boolean freeInstances = false;
	private boolean possibleModelUpdates = false;

	private class OneTimeParser {

		private XNode containerNode;

		private IFrameFunction function;

		private Map<String, IFrame> framesByXDocId;
		private Map<String, XNode> frameNodesByXDocId = new HashMap<String, XNode>();

		private List<SlotSpec<?>> slotSpecs = new ArrayList<SlotSpec<?>>();
		private ValuesUpdate valuesUpdate = new ValuesUpdate();

		private Set<CFrame> invalidFrameTypes = new HashSet<CFrame>();
		private InstanceRegenCreator regenCreator = new InstanceRegenCreator();

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

						getValuesEditor(slot).add(addedValue);
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

				if (getActivation().active()) {

					if (!fixedValueSpecs.isEmpty()) {

						processFixedValueSpecs();
					}

					if (!assertedValueSpecs.isEmpty()) {

						processAssertedValueSpecs();
					}
				}
			}

			abstract String getValueTypeId();

			abstract String getValueId();

			abstract CValue<?> getValueType(XNode valueTypeNode);

			abstract CValue<?> getDefaultValueType(XNode slotNode);

			abstract V resolveValueSpec(XNode valueNode);

			abstract IValue getValue(ISlot slot, V valueSpec);

			abstract String valueAsString(IValue value);

			abstract boolean validModelValue(IValue value);

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
							getSource(),
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

				return fixedValueNode(valueNode) ? fixedValueSpecs : assertedValueSpecs;
			}

			private boolean fixedValueNode(XNode valueNode) {

				return valueNode.getBoolean(FIXED_VALUE_STATUS_ATTR);
			}

			private void processFixedValueSpecs() {

				getSlotEditor(slot).updateFixedValues(getValues(fixedValueSpecs));
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

						if (validModelValue(value) && valueType.validValue(value)) {

							validNonNewValues.add(value);
						}
						else {

							regenCreator.addPrunedValue(slot, value);
						}
					}
				}

				if (!validNonNewValues.isEmpty()) {

					getValuesEditor(slot).update(validNonNewValues);
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

			private CSource getSource() {

				return slotTypeNode.getEnum(SOURCE_ATTR, CSource.class);
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
		}

		private abstract class FrameSlotSpec extends SlotSpec<IValue> {

			FrameSlotSpec(IFrame container, XNode slotNode) {

				super(container, slotNode);
			}

			IValue getValue(ISlot slot, IValue valueSpec) {

				return valueSpec;
			}

			boolean validModelValue(IValue value) {

				return validFrameType(valueToCFrame(value));
			}

			String valueAsString(IValue value) {

				return valueToCFrame(value).getIdentity().toString();
			}

			abstract CFrame valueToCFrame(IValue value);
		}

		private class CFrameSlotSpec extends FrameSlotSpec {

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

			CFrame valueToCFrame(IValue value) {

				return (CFrame)value;
			}
		}

		private class IFrameSlotSpec extends FrameSlotSpec {

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

			CFrame valueToCFrame(IValue value) {

				return ((IFrame)value).getType();
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

				return parseCNumber(valueTypeNode);
			}

			CValue<?> getDefaultValueType(XNode slotNode) {

				XNode valueTypeNode = slotNode.getChild(CNUMBER_ID);

				return parseCNumber(valueTypeNode).toUnconstrained();
			}

			XNode resolveValueSpec(XNode valueNode) {

				return valueNode;
			}

			IValue getValue(ISlot slot, XNode valueSpec) {

				return parseINumber(getValueType(slot), valueSpec);
			}

			boolean validModelValue(IValue value) {

				return true;
			}

			String valueAsString(IValue value) {

				return ((INumber)value).toString();
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

				return parseCString(valueTypeNode);
			}

			CValue<?> getDefaultValueType(XNode slotNode) {

				return CString.FREE;
			}

			IValue resolveValueSpec(XNode valueNode) {

				return parseIString(valueNode);
			}

			IValue getValue(ISlot slot, IValue valueSpec) {

				return valueSpec;
			}

			boolean validModelValue(IValue value) {

				return true;
			}

			String valueAsString(IValue value) {

				return ((IString)value).get();
			}
		}

		OneTimeParser(IInstanceParseInput input) {

			containerNode = resolveContainerNode(input);
			function = parseFunction();
			framesByXDocId = input.getFramesByXDocId();
		}

		IRegenInstance parse() {

			IFrame rootFrame = resolveIFrame(getRootFrameNode());
			boolean validRootType = validFrame(rootFrame);

			if (validRootType) {

				processSlotValueSpecs();

				regenCreator.processPrePruned(rootFrame);
				completeReinstantiation();

				valuesUpdate.checkApply();

				return regenCreator.createValid(rootFrame);
			}

			return regenCreator.createInvalid(rootFrame.getType().getIdentity());
		}

		IRegenType parseRootType() {

			CFrame rootType = parseCFrame(getRootTypeNode());

			return validFrameType(rootType)
					? new IRegenValidType(rootType)
					: new IRegenInvalidType(rootType.getIdentity());
		}

		IFrameFunction parseFunction() {

			return containerNode.getEnum(
						INSTANCE_FUNCTION_ATTR,
						IFrameFunction.class,
						IFrameFunction.ASSERTION);
		}

		private IFrame resolveIFrame(XNode node) {

			String xid = node.getString(IFRAME_XDOC_ID_REF_ATTR, null);

			if (xid != null) {

				return resolveNonDisjunctionIFrame(node, xid, true);
			}

			xid = node.getString(IFRAME_XDOC_ID_ATTR, null);

			if (xid != null) {

				return resolveNonDisjunctionIFrame(node, xid, false);
			}

			return parseIFrame(node);
		}

		private IFrame resolveNonDisjunctionIFrame(XNode node, String xid, boolean refXid) {

			IFrame frame = framesByXDocId.get(xid);

			if (frame == null) {

				frame = parseIFrame(refXid ? getGraphFrameNode(xid) : node);
				framesByXDocId.put(xid, frame);
			}

			return frame;
		}

		private IFrame parseIFrame(XNode node) {

			if (node.hasChild(CFRAME_ID)) {

				CFrame frameType = parseCFrame(node.getChild(CFRAME_ID));

				if (node.hasChild(IREFERENCE_ID)) {

					return parseReferenceIFrame(frameType, node);
				}

				return parseAtomicIFrame(frameType, node);
			}

			return parseDisjunctionIFrame(node);
		}

		private IFrame parseAtomicIFrame(CFrame frameType, XNode node) {

			IFrame frame = createAtomicFrame(frameType);

			if (validFrame(frame)) {

				for (XNode slotNode : node.getChildren(ISLOT_ID)) {

					parseISlot(frame, slotNode);
				}
			}

			return frame;
		}

		private IFrame parseReferenceIFrame(CFrame frameType, XNode node) {

			XNode refNode = node.getChild(IREFERENCE_ID);

			return createReferenceFrame(frameType, parseIdentity(refNode));
		}

		private IFrame parseDisjunctionIFrame(XNode node) {

			List<IFrame> disjuncts = new ArrayList<IFrame>();

			for (XNode disjunctNode : node.getChildren(IFRAME_ID)) {

				disjuncts.add(resolveIFrame(disjunctNode));
			}

			return IFrame.createDisjunction(disjuncts);
		}

		private MFrame parseMFrame(XNode node) {

			return resolveCFrame(parseMFrameAsDisjunctIds(node)).getType();
		}

		private CFrame parseCFrame(XNode node) {

			return resolveCFrame(parseCFrameAsDisjunctIds(node));
		}

		private CFrame resolveCFrame(List<CIdentity> disjunctIds) {

			List<CFrame> disjuncts = new ArrayList<CFrame>();

			for (CIdentity disjunctId : disjunctIds) {

				disjuncts.add(getCFrame(disjunctId));
			}

			return CFrame.resolveDisjunction(disjuncts);
		}

		private void parseISlot(IFrame container, XNode slotNode) {

			XNode valuesNode = slotNode.getChildOrNull(IVALUES_ID);
			SlotSpec<?> spec = checkCreateSlotSpec(container, slotNode, valuesNode);

			if (spec != null && valuesNode != null) {

				spec.addValueSpecs(valuesNode);
			}
		}

		private IFrame createAtomicFrame(CFrame frameType) {

			return instantiator.createAtomicFrame(frameType, function, freeInstances);
		}

		private IFrame createReferenceFrame(CFrame frameType, CIdentity refId) {

			return instantiator.createReferenceFrame(frameType, refId, function, freeInstances);
		}

		private CFrame getCFrame(CIdentity id) {

			CFrame frame = model.getRootFrame();

			if (!frame.getIdentity().equals(id)) {

				frame = model.getFrames().getOrNull(id);

				if (frame == null) {

					frame = instantiator.createNonModelFrameType(id);

					invalidFrameTypes.add(frame);
				}
			}

			return frame;
		}

		private boolean validFrame(IFrame frame) {

			return validFrameType(frame.getType());
		}

		private boolean validFrameType(CFrame type) {

			return !invalidFrameTypes.contains(type);
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

				instantiator.completeReinstantiation(frame, possibleModelUpdates);
			}
		}

		private XNode getRootTypeNode() {

			return getRootFrameNode().getChild(CFRAME_ID);
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
	 */
	public IInstanceParser(CModel model) {

		this.model = model;

		iEditor = ZCModelAccessor.get().getIEditor(model);
	}

	/**
	 * Sets whether "free-instances", rather than normal instances are
	 * to be generated as a result of the parsing (see {@link IFreeCopier}).
	 *
	 * @param freeInstances True if free-instances are to be generated
	 */
	public void setFreeInstances(boolean freeInstances) {

		this.freeInstances = freeInstances;
	}

	/**
	 * Sets whether model could possibly have been updated since
	 * frame/slot network was serialized.
	 *
	 * @param possibleModelUpdates True if model possibly updated since
	 * serialiszation
	 */
	public void setPossibleModelUpdates(boolean possibleModelUpdates) {

		this.possibleModelUpdates = possibleModelUpdates;
	}

	/**
	 * Parses serialised frame/slot network.
	 *
	 * @param input Input to parsing process
	 * @return Output of parsing process
	 */
	public IRegenInstance parse(IInstanceParseInput input) {

		return new OneTimeParser(input).parse();
	}

	/**
	 * Parses only the type of the root-frame of the serialised frame/slot
	 * network.
	 *
	 * @param input Input to parsing process
	 * @return Output of parsing process
	 */
	public IRegenType parseRootType(IInstanceParseInput input) {

		return new OneTimeParser(input).parseRootType();
	}

	/**
	 * Parses only the function of the instance represented by the serialised
	 * frame/slot network.
	 *
	 * @param input Input to parsing process
	 * @return Resulting instance function
	 */
	public IFrameFunction parseFunction(IInstanceParseInput input) {

		return new OneTimeParser(input).parseFunction();
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

	private ISlotEditor getSlotEditor(ISlot slot) {

		return iEditor.getSlotEditor(slot);
	}

	private ISlotValuesEditor getValuesEditor(ISlot slot) {

		return iEditor.getSlotValuesEditor(slot);
	}
}
