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

package uk.ac.manchester.cs.mekon.network;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Provides an intermediate network-based representation, which
 * provides a more malleable alternative to the standard MEKON
 * frames representation, for use by specific implementations
 * of {@link IReasoner} and {@link IMatcher}. The node/link
 * networks are generated from the corresponding frame/slot
 * networks.
 *
 * @author Colin Puleston
 */
class NNetwork {

	private NNode rootNode;

	private IFrameSlotRenderer iFrameSlotRenderer = new IFrameSlotRenderer();
	private CFrameSlotRenderer cFrameSlotRenderer = new CFrameSlotRenderer();
	private INumberSlotRenderer iNumberSlotRenderer = new INumberSlotRenderer();

	private Map<IFrame, NNode> nodesByFrame = new HashMap<IFrame, NNode>();

	private abstract class TypeSlotRenderer<V, F extends NFeature<V>, IV> {

		F render(NNode node, ISlot slot, List<IV> iValues) {

			CIdentity slotId = slot.getType().getIdentity();

			return render(node, slotId, slot, iValues);
		}

		void render(NNode node, CIdentity slotId, List<IV> iValues) {

			render(node, slotId, null, iValues);
		}

		abstract V getValue(IV iValue);

		abstract F createFeature(CIdentity id, ISlot slot);

		abstract void addFeature(NNode node, F feature);

		private F render(NNode node, CIdentity id, ISlot slot, List<IV> iValues) {

			F feature = createFeature(id, slot);

			for (IV iValue : iValues) {

				feature.addValue(getValue(iValue));
			}

			addFeature(node, feature);

			return feature;
		}
	}

	private abstract class FrameSlotRenderer<IV>
								extends
									TypeSlotRenderer<NNode, NLink, IV> {

		NLink createFeature(CIdentity id, ISlot slot) {

			return new NLink(id, slot);
		}

		void addFeature(NNode node, NLink feature) {

			node.addFeature(feature);
		}
	}

	private class IFrameSlotRenderer extends FrameSlotRenderer<IFrame> {

		NLink render(NNode node, ISlot slot, List<IFrame> iValues) {

			List<IFrame> iConjunctionValues = new ArrayList<IFrame>();

			for (IFrame iValue : iValues) {

				if (iValue.getCategory().disjunction()) {

					checkRenderDisjunction(node, slot, iValue);
				}
				else {

					iConjunctionValues.add(iValue);
				}
			}

			return super.render(node, slot, iConjunctionValues);
		}

		NNode getValue(IFrame iValue) {

			return getNode(iValue);
		}

		private void checkRenderDisjunction(NNode node, ISlot slot, IFrame iValue) {

			List<IFrame> disjuncts = iValue.asDisjuncts();

			if (!disjuncts.isEmpty()) {

				NLink link = super.render(node, slot, disjuncts);

				link.setDisjunctionLink(true);
			}
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

		NNumeric createFeature(CIdentity id, ISlot slot) {

			return new NNumeric(id, slot);
		}

		void addFeature(NNode node, NNumeric feature) {

			node.addFeature(feature);
		}
	}

	private class ISlotRenderer extends ISlotValuesVisitor {

		private ISlot slot;
		private NNode node;

		protected void visit(CFrame valueType, List<IFrame> values) {

			iFrameSlotRenderer.render(node, slot, values);
		}

		protected void visit(CNumber valueType, List<INumber> values) {

			iNumberSlotRenderer.render(node, slot, values);
		}

		protected void visit(MFrame valueType, List<CFrame> values) {

			cFrameSlotRenderer.render(node, slot, values);
		}

		ISlotRenderer(ISlot slot, NNode node) {

			this.slot = slot;
			this.node = node;

			visit(slot);
		}
	}

	private class CSlotValuesRenderer extends CValueVisitor {

		private CSlotValues cSlotValues;
		private CIdentity slotId;
		private NNode node;

		protected void visit(CFrame value) {

			cFrameSlotRenderer.render(node, slotId, getValues(CFrame.class));
		}

		protected void visit(CNumber value) {

			iNumberSlotRenderer.render(node, slotId, getCNumberValuesAsINumbers());
		}

		protected void visit(MFrame value) {

			cFrameSlotRenderer.render(node, slotId, getMFrameValuesAsCFrames());
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

	/**
	 * Constructor that creates a network corresponding to the specified
	 * frame/slot network.
	 *
	 * @param rootFrame Root-frame in the frame/slot network
	 */
	public NNetwork(IFrame rootFrame) {

		rootNode = getNode(rootFrame);
	}

	/**
	 * Provides the root-node of the node/link network.
	 *
	 * @return Root-node of network
	 */
	public NNode getRootNode() {

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

		for (ISlot slot : frame.getSlots().asList()) {

			new ISlotRenderer(slot, node);
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
