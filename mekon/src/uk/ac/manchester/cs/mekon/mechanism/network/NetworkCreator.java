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

package uk.ac.manchester.cs.mekon.mechanism.network;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class NetworkCreator {

	private NNode rootNode;

	private IFrameSlotRenderer iFrameSlotsRenderer = new IFrameSlotRenderer();
	private CFrameSlotRenderer cFrameSlotsRenderer = new CFrameSlotRenderer();
	private INumberSlotRenderer iNumberSlotsRenderer = new INumberSlotRenderer();

	private Map<IFrame, NNode> nodesByFrame = new HashMap<IFrame, NNode>();

	private abstract class TypeSlotRenderer<V, A extends NAttribute<V>, IV> {

		void render(NNode node, ISlot iSlot, List<IV> iValues) {

			render(node, iSlot.getType().getIdentity(), iSlot, iValues);
		}

		void render(NNode node, CIdentity slotId, List<IV> iValues) {

			render(node, slotId, null, iValues);
		}

		abstract V getValue(IV iValue);

		abstract A createAttribute(CIdentity id, ISlot iSlot);

		abstract void addAttribute(NNode node, A attribute);

		private void render(NNode node, CIdentity id, ISlot iSlot, List<IV> iValues) {

			A attribute = createAttribute(id, iSlot);

			for (IV iValue : iValues) {

				attribute.addValue(getValue(iValue));
			}

			addAttribute(node, attribute);
		}
	}

	private abstract class FrameSlotRenderer<IV>
								extends
									TypeSlotRenderer<NNode, NLink, IV> {

		NLink createAttribute(CIdentity id, ISlot iSlot) {

			return new NLink(id, iSlot);
		}

		void addAttribute(NNode node, NLink attribute) {

			node.addAttribute(attribute);
		}
	}

	private class IFrameSlotRenderer extends FrameSlotRenderer<IFrame> {

		NNode getValue(IFrame iValue) {

			return getNode(iValue);
		}
	}

	private class CFrameSlotRenderer extends FrameSlotRenderer<CFrame> {

		NNode getValue(CFrame iValue) {

			return createNode(iValue);
		}
	}

	private class INumberSlotRenderer
						extends
							TypeSlotRenderer<INumber, NNumeric, INumber> {

		INumber getValue(INumber iValue) {

			return iValue;
		}

		NNumeric createAttribute(CIdentity id, ISlot iSlot) {

			return new NNumeric(id, iSlot);
		}

		void addAttribute(NNode node, NNumeric attribute) {

			node.addAttribute(attribute);
		}
	}

	private class ISlotRenderer extends ISlotValuesVisitor {

		private ISlot iSlot;
		private NNode node;

		protected void visit(CFrame valueType, List<IFrame> values) {

			iFrameSlotsRenderer.render(node, iSlot, values);
		}

		protected void visit(CNumber valueType, List<INumber> values) {

			iNumberSlotsRenderer.render(node, iSlot, values);
		}

		protected void visit(MFrame valueType, List<CFrame> values) {

			cFrameSlotsRenderer.render(node, iSlot, values);
		}

		ISlotRenderer(ISlot iSlot, NNode node) {

			this.iSlot = iSlot;
			this.node = node;

			visit(iSlot);
		}
	}

	private class CSlotValuesRenderer extends CValueVisitor {

		private CSlotValues cSlotValues;
		private CIdentity slotId;
		private NNode node;

		protected void visit(CFrame value) {

			cFrameSlotsRenderer.render(node, slotId, getValues(CFrame.class));
		}

		protected void visit(CNumber value) {

			iNumberSlotsRenderer.render(node, slotId, getCNumberValuesAsINumbers());
		}

		protected void visit(MFrame value) {

			cFrameSlotsRenderer.render(node, slotId, getMFrameValuesAsCFrames());
		}

		CSlotValuesRenderer(CSlotValues cSlotValues, CIdentity slotId, NNode node) {

			this.cSlotValues = cSlotValues;
			this.slotId = slotId;
			this.node = node;

			visit(cSlotValues.getValues(slotId).get(0));
		}

		private List<CFrame> getMFrameValuesAsCFrames() {

			List<CFrame> cFrames = new ArrayList<CFrame>();

			for (MFrame mFrame : getValues(MFrame.class)) {

				cFrames.add(mFrame.getRootCFrame());
			}

			return cFrames;
		}

		private List<INumber> getCNumberValuesAsINumbers() {

			List<INumber> iNumbers = new ArrayList<INumber>();

			for (CNumber cNumber : getValues(CNumber.class)) {

				iNumbers.add(cNumber.asINumber());
			}

			return iNumbers;
		}

		private <V extends CValue<?>>List<V> getValues(Class<V> valueClass) {

			return cSlotValues.getValues(slotId, valueClass);
		}
	}

	NetworkCreator(IFrame rootFrame) {

		rootNode = getNode(rootFrame);
	}

	NNode getRootNode() {

		return rootNode;
	}

	private NNode getNode(IFrame frame) {

		NNode node = nodesByFrame.get(frame);

		if (node == null) {

			node = renderNode(frame);
			nodesByFrame.put(frame, node);
		}

		return node;
	}

	private NNode renderNode(IFrame frame) {

		NNode node = createNode(frame.getType());

		node.setIFrame(frame);

		for (ISlot iSlot : frame.getSlots().asList()) {

			new ISlotRenderer(iSlot, node);
		}

		return node;
	}

	private NNode createNode(CFrame cFrame) {

		NNode node = new NNode(cFrame);

		if (cFrame.getCategory().extension()) {

			configureExtensionNode(node, cFrame);
		}

		return node;
	}

	private void configureExtensionNode(NNode node, CFrame cFrame) {

		CSlotValues slotValues = cFrame.getSlotValues();

		for (CIdentity slotId : slotValues.getSlotIdentities()) {

			new CSlotValuesRenderer(slotValues, slotId, node);
		}
	}
}
