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

package uk.ac.manchester.cs.mekon.user.explorer;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class IFrameNode extends IValueNode<IFrame> {

	private ITree tree;
	private ValueNodes valueNodes;

	private class SlotNodeCreator extends CValueVisitor {

		private ISlot slot;
		private ISlotNode created = null;

		protected void visit(CFrame type) {

			created = new IFrameSlotNode(tree, slot);
		}

		protected void visit(CNumber type) {

			created = new INumberSlotNode(tree, slot);
		}

		protected void visit(CString type) {

			created = new IStringSlotNode(tree, slot);
		}

		protected void visit(MFrame type) {

			created = new CFrameSlotNode(tree, slot);
		}

		SlotNodeCreator(ISlot slot) {

			this.slot = slot;
		}

		ISlotNode create() {

			visit(slot.getValueType());

			return created;
		}
	}

	private class ValueNodes extends KListDerivedChildNodes<ISlot> {

		ValueNodes() {

			super(IFrameNode.this, getValue().getSlots());
		}

		boolean childNodeRequiredFor(ISlot slot) {

			return slot.getType().getActivation().active();
		}

		GNode createChildNode(ISlot slot) {

			return new SlotNodeCreator(slot).create();
		}
	}

	protected void addInitialChildren() {

		valueNodes.addInitialChildNodes();
	}

	IFrameNode(ITree tree, IFrame iFrame) {

		super(tree, iFrame);

		this.tree = tree;

		valueNodes = new ValueNodes();
	}

	List<ISlot> getChildEntities() {

		return getValue().getSlots().asList();
	}

	GCellDisplay getDefaultDisplay() {

		return EntityDisplays.get().get(getValue());
	}
}
