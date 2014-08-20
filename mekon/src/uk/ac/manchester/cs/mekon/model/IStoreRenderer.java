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

package uk.ac.manchester.cs.mekon.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class IStoreRenderer implements IStoreSerialiser {

	private XFile file;

	private class SlotValuesRenderer extends ISlotValuesVisitor {

		private XNode node;

		protected void visit(CFrame valueType, List<IFrame> values) {

			for (IFrame value : values) {

				renderFrame(value, node);
			}
		}

		protected void visit(CNumber valueType, List<INumber> values) {

			renderNumber(values.get(0), node);
		}

		protected void visit(MFrame valueType, List<CFrame> values) {

			for (CFrame value : values) {

				renderFrame(value, node);
			}
		}

		SlotValuesRenderer(ISlot slot, XNode node) {

			this.node = node;

			visit(slot);
		}
	}

	IStoreRenderer() {

		this(DEFAULT_FILE_NAME);
	}

	IStoreRenderer(String fileName) {

		file = new XFile(fileName, ROOT_ID);
	}

 	void render(IStore store) {

		for (CIdentity id : store.getAllIdentities()) {

			renderInstance(id, store.get(id));
		}

		file.writeToFile();
	}

	private void renderInstance(CIdentity id, IFrame frame) {

		XNode rootNode = file.getRootNode();
		XNode node = rootNode.addChild(INSTANCE_ID);

		renderIdentity(id, node);
		renderFrame(frame, node);
	}

	private void renderFrame(IFrame frame, XNode parent) {

		XNode node = renderFrame(frame.getType(), parent);

		for (ISlot slot : frame.getSlots().asList()) {

			if (!slot.getValues().isEmpty()) {

				renderSlot(slot, node);
			}
		}
	}

	private XNode renderFrame(CFrame frameType, XNode parent) {

		XNode node = parent.addChild(FRAME_ID);

		renderIdentity(frameType, node);

		return node;
	}

	private void renderSlot(ISlot slot, XNode parent) {

		XNode node = parent.addChild(SLOT_ID);

		renderIdentity(slot.getType().getProperty(), node);
		new SlotValuesRenderer(slot, node);
	}

	protected void renderNumber(INumber number, XNode node) {

		node.addValue(NUMBER_VALUE_ATTR, number.asTypeNumber());
	}

	private void renderIdentity(CIdentified id, XNode node) {

		renderIdentity(id.getIdentity(), node);
	}

	private void renderIdentity(CIdentity id, XNode node) {

		node.addValue(IDENTITY_ATTR, id.getIdentifier());
	}
}
