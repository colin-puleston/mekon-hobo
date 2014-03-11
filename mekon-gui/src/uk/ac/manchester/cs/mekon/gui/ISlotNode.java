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

package uk.ac.manchester.cs.mekon.gui;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class ISlotNode extends GNode {

	private ITree tree;
	private ISlot slot;

	private class ValuesNodeCreator extends CValueVisitor {

		private IValuesNode created = null;

		protected void visit(CFrame type) {

			created = new IFrameValuesNode(tree, slot);
		}

		protected void visit(CNumber type) {

			created = new INumberValuesNode(tree, slot);
		}

		protected void visit(MFrame type) {

			created = new CFrameValuesNode(tree, slot);
		}

		IValuesNode create() {

			visit(slot.getValueType());

			return created;
		}
	}

	protected void addInitialChildren() {

		addChild(new ValuesNodeCreator().create());
	}

	protected boolean autoExpand() {

		return false;
	}

	protected GCellDisplay getDisplay() {

		return EntityDisplays.get().get(slot);
	}

	ISlotNode(ITree tree, ISlot slot) {

		super(tree);

		this.tree = tree;
		this.slot = slot;
	}
}
