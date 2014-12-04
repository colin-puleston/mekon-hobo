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
abstract class FFrameValuesNode<F extends IValue> extends IValuesNode {

	private ITree tree;
	private ISlot slot;

	private class AddDisjunctAction extends GNodeAction {

		private F value;

		protected void perform() {

			CFrame cFrame = valueToCFrame(value);
			CFrame updatedCFrame = checkAddCFrameDisjunct(cFrame);

			if (!updatedCFrame.equals(cFrame)) {

				F updatedValue = checkUpdateValue(value, updatedCFrame);

				if (!updatedValue.equals(value)) {

					removeValue(value);
					addValue(updatedValue);

					value = updatedValue;
				}
			}
		}

		AddDisjunctAction(F value) {

			this.value = value;
		}
	}

	FFrameValuesNode(ITree tree, ISlot slot) {

		super(tree, slot);

		this.tree = tree;
		this.slot = slot;
	}

	GNodeAction getAddDisjunctOrInertAction(F value) {

		return addDisjunctActionRequired()
				? new AddDisjunctAction(value)
				: GNodeAction.INERT_ACTION;
	}

	CFrame checkObtainCFrame() {

		return selectableCFrameOptions() ? obtainCFrame() : getRootCFrame();
	}

	CFrame obtainCFrame() {

		return new CFrameSelector(tree, getRootCFrame()).getSelectionOrNull();
	}

	abstract CFrame getRootCFrame();

	abstract CFrame valueToCFrame(F value);

	abstract F checkUpdateValue(F value, CFrame updatedCFrame);

	private boolean addDisjunctActionRequired() {

		return slot.editable()
				&& slot.abstractValuesAllowed()
				&& selectableCFrameOptions();
	}

	private CFrame checkAddCFrameDisjunct(CFrame cFrame) {

		CFrame newDisjunct = checkObtainCFrame();

		if (newDisjunct != null) {

			List<CFrame> disjuncts = cFrameAsDisjuncts(cFrame);

			if (disjuncts.add(newDisjunct)) {

				cFrame = CFrame.resolveDisjunction(disjuncts);
			}
		}

		return cFrame;
	}

	private CFrame obtainNewCFrameDisjunct() {

		return new CFrameSelector(tree, getRootCFrame()).getSelectionOrNull();
	}

	private List<CFrame> cFrameAsDisjuncts(CFrame cFrame) {

		List<CFrame> disjuncts = new ArrayList<CFrame>();

		if (cFrame.getCategory().disjunction()) {

			disjuncts.addAll(cFrame.getSubs());
		}
		else {

			disjuncts.add(cFrame);
		}

		return disjuncts;
	}

	private boolean selectableCFrameOptions() {

		return !getRootCFrame().getSubs(CFrameVisibility.EXPOSED).isEmpty();
	}
}
