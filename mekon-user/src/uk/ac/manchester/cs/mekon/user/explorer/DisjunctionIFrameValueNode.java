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
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class DisjunctionIFrameValueNode extends IFrameSlotNode {

	private IFrame disjunction;

	protected boolean autoExpand() {

		return true;
	}

	protected GNodeAction getNegativeAction1() {

		return getParentSlotNode().getRemoveValueAction(disjunction);
	}

	DisjunctionIFrameValueNode(ITree tree, IFrame disjunction) {

		super(tree, disjunction.getDisjunctsSlot());

		this.disjunction = disjunction;
	}

	void removeValue(IValue value) {

		super.removeValue(value);

		if (!replacingChild()) {

			checkRevertToAtomicValue();
		}
	}

	boolean addIFrameDisjunctActionRequired() {

		return false;
	}

	IFrame getValue() {

		return disjunction;
	}

	private void checkRevertToAtomicValue() {

		List<IFrame> disjuncts = disjunction.asDisjuncts();

		if (disjuncts.size() == 1) {

			getParentSlotNode().replaceValue(disjunction, disjuncts.get(0));
		}
	}

	private IFrameSlotNode getParentSlotNode() {

		return (IFrameSlotNode)getParent();
	}
}
