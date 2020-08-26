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
import uk.ac.manchester.cs.mekon.model.util.*;
import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * Renderer for the standard XML serialisation of MEKON instances as
 * represented via {@link IFrame}/{@link ISlot} networks.
 *
 * @author Colin Puleston
 */
public class IInstanceRenderer extends FSerialiser implements ISerialiserVocab {

	private boolean renderAsTree = false;

	private class OneTimeRenderer {

		private XNode containerNode;
		private IFrameXDocIds frameXDocIds;
		private IValuesUpdate valuesUpdate;

		private class ISlotValueTypeRenderer extends CValueVisitor {

			private XNode slotNode;

			protected void visit(CFrame value) {

				renderCFrame(value, slotNode);
			}

			protected void visit(CNumber value) {

				renderCNumber(value, slotNode);
			}

			protected void visit(CString value) {

				renderCString(value, slotNode);
			}

			protected void visit(MFrame value) {

				renderMFrame(value, slotNode);
			}

			ISlotValueTypeRenderer(ISlot slot, XNode slotNode) {

				this.slotNode = slotNode;

				visit(slot.getValueType());
			}
		}

		private class ISlotValuesRenderer extends IValueVisitor {

			private XNode valuesNode;
			private boolean fixedValues;

			protected void visit(IFrame value) {

				renderFixedStatus(renderIFrame(value, valuesNode));
			}

			protected void visit(INumber value) {

				renderFixedStatus(renderINumber(value, valuesNode));
			}

			protected void visit(IString value) {

				renderFixedStatus(renderIString(value, valuesNode));
			}

			protected void visit(CFrame value) {

				renderFixedStatus(renderCFrame(value, valuesNode));
			}

			ISlotValuesRenderer(List<IValue> values, XNode valuesNode, boolean fixedValues) {

				this.valuesNode = valuesNode;
				this.fixedValues = fixedValues;

				for (IValue value : values) {

					visit(value);
				}
			}

			private void renderFixedStatus(XNode valueNode) {

				valueNode.setValue(FIXED_VALUE_STATUS_ATTR, fixedValues);
			}
		}

		OneTimeRenderer(IInstanceRenderInput input, XNode containerNode) {

			this.containerNode = containerNode;

			frameXDocIds = new IFrameXDocIds(input.getFrameXDocIds());
			valuesUpdate = input.getValuesUpdate();

			IFrame rootFrame = input.getRootFrame();

			renderInstanceFunction(rootFrame.getFunction());
			renderAtomicIFrame(rootFrame, containerNode, true);
		}

		private void renderInstanceFunction(IFrameFunction function) {

			containerNode.setValue(INSTANCE_FUNCTION_ATTR, function);
		}

		private XNode renderIFrame(IFrame frame, XNode parentNode) {

			if (frame.getCategory().disjunction()) {

				return renderDisjunctionIFrame(frame, parentNode);
			}

			return renderAtomicIFrame(frame, parentNode, renderAsTree);
		}

		private XNode renderAtomicIFrame(IFrame frame, XNode parentNode, boolean direct) {

			IFrameXDocIds.Resolution xidRes = frameXDocIds.resolve(frame);

			if (direct) {

				return renderAtomicIFrameDirect(frame, parentNode, xidRes.getId());
			}

			return renderAtomicIFrameIndirect(frame, parentNode, xidRes);
		}

		private XNode renderAtomicIFrameDirect(IFrame frame, XNode parentNode, String xid) {

			XNode node = renderAtomicIFrameCommon(parentNode, xid, IFRAME_XDOC_ID_ATTR);

			renderCFrame(frame.getType(), node);

			if (frame.getCategory().reference()) {

				renderIReference(frame.getReferenceId(), node);
			}
			else {

				for (ISlot slot : frame.getSlots().asList()) {

					renderISlot(slot, node);
				}
			}

			return node;
		}

		private XNode renderAtomicIFrameIndirect(
						IFrame frame,
						XNode parentNode,
						IFrameXDocIds.Resolution xidRes) {

			String xid = xidRes.getId();
			XNode localNode = renderAtomicIFrameCommon(parentNode, xid, IFRAME_XDOC_ID_REF_ATTR);

			if (xidRes.newFrame()) {

				renderAtomicIFrameDirect(frame, containerNode, xid);
			}

			return localNode;
		}

		private XNode renderAtomicIFrameCommon(XNode parentNode, String xid, String xidTag) {

			XNode node = parentNode.addChild(IFRAME_ID);

			node.setValue(xidTag, xid);

			return node;
		}

		private XNode renderDisjunctionIFrame(IFrame frame, XNode parentNode) {

			XNode node = parentNode.addChild(IFRAME_ID);

			for (IFrame disjunct : frame.asDisjuncts()) {

				renderIFrame(disjunct, node);
			}

			return node;
		}

		private void renderMFrame(MFrame frame, XNode parentNode) {

			FSerialiser.renderMFrame(frame, parentNode.addChild(MFRAME_ID));
		}

		private XNode renderCFrame(CFrame frame, XNode parentNode) {

			XNode node = parentNode.addChild(CFRAME_ID);

			FSerialiser.renderCFrame(frame, node);

			return node;
		}

		private void renderCNumber(CNumber number, XNode parentNode) {

			FSerialiser.renderCNumber(number, parentNode.addChild(CNUMBER_ID));
		}

		private void renderCString(CString string, XNode parentNode) {

			FSerialiser.renderCString(string, parentNode.addChild(CSTRING_ID));
		}

		private void renderIReference(CIdentity reference, XNode parentNode) {

			renderIdentity(reference, parentNode.addChild(IREFERENCE_ID));
		}

		private XNode renderINumber(INumber number, XNode parentNode) {

			XNode node = parentNode.addChild(INUMBER_ID);

			FSerialiser.renderINumber(number, node);

			return node;
		}

		private XNode renderIString(IString string, XNode parentNode) {

			XNode node = parentNode.addChild(ISTRING_ID);

			FSerialiser.renderIString(string, node);

			return node;
		}

		private void renderISlot(ISlot slot, XNode parentNode) {

			XNode node = parentNode.addChild(ISLOT_ID);

			renderCSlot(slot.getType(), node);
			new ISlotValueTypeRenderer(slot, node);

			node.setValue(EDITABILITY_ATTR, slot.getEditability());

			if (!slot.getValues().isEmpty()) {

				renderISlotValues(slot, node);
			}

			if (slot == valuesUpdate.getSlot()) {

				renderISlotValuesUpdate(slot, node);
			}
		}

		private void renderCSlot(CSlot slot, XNode parentNode) {

			XNode node = parentNode.addChild(CSLOT_ID);

			renderIdentity(slot, node);

			node.setValue(SOURCE_ATTR, slot.getSource());
			node.setValue(CARDINALITY_ATTR, slot.getCardinality());
			node.setValue(ACTIVATION_ATTR, slot.getActivation());
		}

		private void renderISlotValues(ISlot slot, XNode slotNode) {

			ISlotValues slotValues = slot.getValues();
			XNode node = slotNode.addChild(IVALUES_ID);

			new ISlotValuesRenderer(slotValues.getFixedValues(), node, true);
			new ISlotValuesRenderer(slotValues.getAssertedValues(), node, false);
		}

		private void renderISlotValuesUpdate(ISlot slot, XNode slotNode) {

			XNode node = slotNode.addChild(IVALUES_UPDATE_ID);

			if (valuesUpdate.addition()) {

				node.setValue(ADDED_VALUE_INDEX_ATTR, valuesUpdate.getAddedValueIndex());
			}
		}
	}

	/**
	 * Sets whether the recursive frame description should be rendered
	 * as a tree, rather than a graph. By default it will be rendered
	 * as a graph.
	 *
	 * @param renderAsTree True if tree rendering required
	 */
	public void setRenderAsTree(boolean renderAsTree) {

		this.renderAsTree = renderAsTree;
	}

	/**
	 * Renders a frame/slot network to produce an XML document.
	 *
	 * @param input Input to rendering process
	 * @return Rendered document
	 */
	public XDocument render(IInstanceRenderInput input) {

		XDocument document = new XDocument(getContainerNodeId());

		renderToContainerNode(input, document.getRootNode());

		return document;
	}

	/**
	 * Renders a frame/slot network to the specified parent-node.
	 *
	 * @param input Input to rendering process
	 * @param parentNode Parent-node for rendering
	 */
	public void render(IInstanceRenderInput input, XNode parentNode) {

		renderToContainerNode(input, parentNode.addChild(getContainerNodeId()));
	}

	private void renderToContainerNode(IInstanceRenderInput input, XNode containerNode) {

		IFrame frame = input.getRootFrame();

		checkAtomicRootFrame(frame);
		checkNonCyclicIfRenderingAsTree(frame);

		new OneTimeRenderer(input, containerNode);
	}

	private void checkAtomicRootFrame(IFrame frame) {

		if (frame.getCategory().disjunction()) {

			throw new KAccessException(
						"Cannot render instance whose root-frame "
						+ "has category DISJUNCTION: "
						+ frame);
		}
	}

	private void checkNonCyclicIfRenderingAsTree(IFrame frame) {

		if (renderAsTree && frame.leadsToCycle()) {

			throw new KAccessException(
						"Cannot render cyclic instance as tree: "
						+ frame);
		}
	}

	private String getContainerNodeId() {

		return renderAsTree ? ITREE_ID : IGRAPH_ID;
	}
}
