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
class CFrameValuesNode extends FFrameValuesNode<CFrame> {

	private ITree tree;
	private ISlot slot;

	private class ValueNode extends GNode {

		private CFrame value;

		protected GNodeAction getPositiveAction() {

			return getAdditionAction(value);
		}

		protected GNodeAction getNegativeAction() {

			return getRemovalAction(value);
		}

		protected GCellDisplay getDisplay() {

			return EntityDisplays.get().get(value, false);
		}

		ValueNode(CFrame value) {

			super(tree);

			this.value = value;
		}
	}

	protected GCellDisplay getDisplay() {

		return EntityDisplays.get().get(getValueType(), true);
	}

	CFrameValuesNode(ITree tree, ISlot slot) {

		super(tree, slot);

		this.tree = tree;
		this.slot = slot;
	}

	GNode createValueNode(IValue value) {

		return new ValueNode(asCFrame(value));
	}

	IValue checkObtainValue() {

		return checkObtainCFrameAddition();
	}

	String getCFrameRole() {

		return "Value";
	}

	CFrame getRootCFrame() {

		return getValueType().getRootCFrame();
	}

	CFrame valueToCFrame(CFrame value) {

		return value;
	}

	CFrame checkUpdateValue(CFrame value, CFrame updatedCFrame) {

		return updatedCFrame;
	}

	private CFrame asCFrame(IValue value) {

		return getValueType().castValue(value);
	}

	private MFrame getValueType() {

		return slot.getValueType().castAs(MFrame.class);
	}
}