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

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon_util.gui.*;
import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

/**
 * @author Colin Puleston
 */
class IStringSlotNode extends ISlotNode {

	private ITree tree;

	private class ValueNode extends IValueNode<IString> {

		protected GNodeAction getNegativeAction1() {

			return getRemoveValueAction(getValue());
		}

		ValueNode(IString number) {

			super(tree, number);
		}

		GCellDisplay getDefaultDisplay() {

			return EntityDisplays.get().get(getValue());
		}
	}

	IStringSlotNode(ITree tree, ISlot slot) {

		super(tree, slot);

		this.tree = tree;
	}

	GNode createValueNode(IValue value) {

		return new ValueNode((IString)value);
	}

	IValue checkObtainValue() {

		CString valueType = (CString)getISlot().getValueType();
		IStringInputter inputter = new IStringInputter(tree, valueType, false);

		return inputter.display() == EditStatus.INPUTTED ? inputter.getInput() : null;
	}
}
