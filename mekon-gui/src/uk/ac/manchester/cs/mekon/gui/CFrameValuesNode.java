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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class CFrameValuesNode extends IValuesNode {

	private ITree tree;
	private ISlot slot;

	private class ValueNode extends GNode {

		final CFrame frame;

		private class AddDisjunctAction extends GNodeAction {

			protected void perform() {

				checkAddValueDisjunct(ValueNode.this);
			}
		}

		protected GNodeAction getPositiveAction() {

			return abstractInstance()
					? new AddDisjunctAction()
					: super.getPositiveAction();
		}

		protected GNodeAction getNegativeAction() {

			return getRemoveValueAction(frame);
		}

		protected GCellDisplay getDisplay() {

			return EntityDisplays.get().get(frame, false);
		}

		ValueNode(CFrame frame) {

			super(tree);

			this.frame = frame;
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

		return new ValueNode(getCFrameValue(value));
	}

	IValue checkObtainValue() {

		return checkObtainFrameValue();
	}

	private void checkAddValueDisjunct(ValueNode valueNode) {

		CFrame newDisjunct = checkObtainFrameValue();

		if (newDisjunct != null) {

			List<CFrame> disjuncts = valueAsDisjuncts(valueNode.frame);

			if (disjuncts.add(newDisjunct)) {

				removeValue(valueNode.frame);
				addValue(CFrame.createDisjunction(disjuncts));
			}
		}
	}

	private List<CFrame> valueAsDisjuncts(CFrame value) {

		List<CFrame> disjuncts = new ArrayList<CFrame>();

		if (value.disjunction()) {

			disjuncts.addAll(value.getSubs());
		}
		else {

			disjuncts.add(value);
		}

		return disjuncts;
	}

	private CFrame checkObtainFrameValue() {

		CFrame rootValue = getValueType().getRootCFrame();

		if (rootValue.getSubs(CFrameVisibility.EXPOSED).isEmpty()) {

			return slot.getValues().isEmpty() ? rootValue : null;
		}

		return new CFrameSelector(tree, rootValue).getSelectionOrNull();
	}

	private CFrame getCFrameValue(IValue value) {

		return getValueType().castValue(value);
	}

	private MFrame getValueType() {

		return slot.getValueType().castAs(MFrame.class);
	}

	private boolean abstractInstance() {

		return slot.getContainer().getType().getModel().abstractInstantiations();
	}
}
