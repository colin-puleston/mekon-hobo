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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * @author Colin Puleston
 */
public class IFrameRenderer extends ISerialiser {

	private boolean renderAsTree = false;
	private boolean renderSchema = false;

	private class OneTimeRenderer {

		private XNode containerNode;
		private Map<IFrame, Integer> iFrameRefs = new HashMap<IFrame, Integer>();

		private class ISlotValueTypeRenderer extends CValueVisitor {

			private XNode slotNode;

			protected void visit(CFrame value) {

				renderCFrame(value, slotNode);
			}

			protected void visit(CNumber value) {

				renderCNumber(value, slotNode);
			}

			protected void visit(MFrame value) {

				renderMFrame(value, slotNode);
			}

			ISlotValueTypeRenderer(ISlot slot, XNode slotNode) {

				this.slotNode = slotNode;

				visit(slot.getValueType());
			}
		}

		private class ISlotValuesRenderer extends ISlotValuesVisitor {

			private XNode valuesNode;

			protected void visit(CFrame valueType, List<IFrame> values) {

				for (IFrame value : values) {

					renderIFrame(value, valuesNode);
				}
			}

			protected void visit(CNumber valueType, List<INumber> values) {

				for (INumber value : values) {

					renderINumber(value, valuesNode);
				}
			}

			protected void visit(MFrame valueType, List<CFrame> values) {

				for (CFrame value : values) {

					renderCFrame(value, valuesNode);
				}
			}

			ISlotValuesRenderer(ISlot slot, XNode slotNode) {

				valuesNode = slotNode.addChild(IVALUES_ID);

				visit(slot);
			}
		}

		OneTimeRenderer(XNode containerNode) {

			this.containerNode = containerNode;
		}

		void render(IFrame frame) {

			renderIFrameDirect(frame, containerNode);
		}

		private void renderIFrame(IFrame frame, XNode parentNode) {

			if (renderAsTree) {

				renderIFrameDirect(frame, parentNode);
			}
			else {

				renderIFrameIndirect(frame, parentNode);
			}
		}

		private void renderIFrameDirect(IFrame frame, XNode parentNode) {

			XNode node = parentNode.addChild(IFRAME_ID);

			renderCFrame(frame.getType(), node);

			for (ISlot slot : frame.getSlots().asList()) {

				if (slotToBeRendered(slot)) {

					renderISlot(slot, node);
				}
			}
		}

		private void renderIFrameIndirect(IFrame frame, XNode parentNode) {

			XNode node = parentNode.addChild(IFRAME_ID);

			node.addValue(IFRAME_REF_INDEX_ATTR, resolveIFrameRef(frame));
		}

		private int resolveIFrameRef(IFrame frame) {

			Integer refIndex = iFrameRefs.get(frame);

			if (refIndex == null) {

				refIndex = iFrameRefs.size() + 1;

				iFrameRefs.put(frame, refIndex);
				renderIFrameDirect(frame, containerNode);
			}

			return refIndex;
		}

		private void renderCFrame(CFrame frame, XNode parentNode) {

			renderCFrame(frame, parentNode, CFRAME_ID);
		}

		private void renderCFrame(CFrame frame, XNode parentNode, String tag) {

			if (frame.getCategory().disjunction()) {

				XNode disjunctsNode = parentNode.addChild(tag);

				for (CFrame disjunct : frame.getSubs()) {

					renderIdentity(disjunct, disjunctsNode.addChild(tag));
				}
			}
			else {

				renderIdentity(frame, parentNode.addChild(tag));
			}
		}

		private void renderMFrame(MFrame frame, XNode parentNode) {

			renderCFrame(frame.getRootCFrame(), parentNode, MFRAME_ID);
		}

		private void renderINumber(INumber number, XNode parentNode) {

			XNode node = parentNode.addChild(INUMBER_ID);

			if (number.indefinite()) {

				renderNumberRange(number.getType(), node);
			}
			else {

				node.addValue(NUMBER_VALUE_ATTR, number.asTypeNumber());
			}
		}

		private void renderCNumber(CNumber number, XNode parentNode) {

			XNode node = parentNode.addChild(CNUMBER_ID);

			renderNumberType(number, node);
			renderNumberRange(number, node);
		}

		private void renderNumberType(CNumber number, XNode node) {

			renderClassId(number.getNumberType(), node, NUMBER_TYPE_ATTR);
		}

		private void renderNumberRange(CNumber number, XNode node) {

			if (number.hasMin()) {

				node.addValue(NUMBER_MIN_ATTR, number.getMin().asTypeNumber());
			}

			if (number.hasMax()) {

				node.addValue(NUMBER_MAX_ATTR, number.getMax().asTypeNumber());
			}
		}

		private void renderISlot(ISlot slot, XNode parentNode) {

			XNode node = parentNode.addChild(ISLOT_ID);

			renderCSlot(slot.getType(), node);

			if (renderSchema) {

				node.addValue(EDITABILITY_ATTR, slot.getEditability());

				new ISlotValueTypeRenderer(slot, node);
			}
			else {

				checkRenderSlotNumberType(slot, node);
			}

			if (!slot.getValues().isEmpty()) {

				new ISlotValuesRenderer(slot, node);
			}
		}

		private void renderCSlot(CSlot slot, XNode parentNode) {

			XNode node = parentNode.addChild(CSLOT_ID);

			renderIdentity(slot, node);

			if (renderSchema) {

				node.addValue(CARDINALITY_ATTR, slot.getCardinality());
			}
		}

		private void checkRenderSlotNumberType(ISlot slot, XNode slotNode) {

			CValue<?> valueType = slot.getValueType();

			if (valueType instanceof CNumber) {

				renderNumberType((CNumber)valueType, slotNode);
			}
		}
	}

	/**
	 */
	public void setRenderAsTree(boolean value) {

		renderAsTree = value;
	}

	/**
	 */
	public void setRenderSchema(boolean value) {

		renderSchema = value;
	}

	/**
	 */
	public XDocument render(IFrame frame) {

		XDocument document = new XDocument(getTopLevelId());

		doRender(frame, document.getRootNode());

		return document;
	}

	/**
	 */
	public void render(IFrame frame, XNode parentNode) {

		doRender(frame, parentNode.addChild(getTopLevelId()));
	}

	private void doRender(IFrame frame, XNode containerNode) {

		checkRenderable(frame);

		new OneTimeRenderer(containerNode).render(frame);
	}

	private boolean slotToBeRendered(ISlot slot) {

		return renderSchema || !slot.getValues().isEmpty();
	}

	private void checkRenderable(IFrame frame) {

		if (renderAsTree && frame.leadsToCycle()) {

			throw new KAccessException(
						"Cannot render cyclic instance as tree: "
						+ "Top-level frame: " + frame);
		}
	}

	private XNode getTopLevelGraphNode() {

		return null;
	}

	private String getTopLevelId() {

		return renderAsTree ? ITREE_ID : IGRAPH_ID;
	}
}
