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
class INumberValuesNode extends IValuesNode {

	private ITree tree;
	private ISlot slot;

	private class ValueNode extends GNode {

		private INumber number;

		protected GNodeAction getNegativeAction() {

			return getRemoveValueAction(number);
		}

		protected GCellDisplay getDisplay() {

			return EntityDisplays.get().get(number);
		}

		ValueNode(INumber number) {

			super(tree);

			this.number = number;
		}
	}

	protected GCellDisplay getDisplay() {

		return EntityDisplays.get().get(getValueType(), true);
	}

	INumberValuesNode(ITree tree, ISlot slot) {

		super(tree, slot);

		this.tree = tree;
		this.slot = slot;
	}

	GNode createValueNode(IValue value) {

		return new ValueNode(getNumberValue(value));
	}

	IValue checkObtainValue() {

		return createSelector().getSelectionOrNull();
	}

	private INumberSelector createSelector() {

		boolean abtractInst = slot.queryInstance();

		return new INumberSelector(tree, getValueType(), abtractInst);
	}

	private INumber getNumberValue(IValue value) {

		return getValueType().castValue(value);
	}

	private CNumber getValueType() {

		return slot.getValueType().castAs(CNumber.class);
	}
}
