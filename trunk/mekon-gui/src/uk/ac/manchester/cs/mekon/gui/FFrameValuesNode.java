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

	private abstract class UpdateDisjunctsAction extends GNodeAction {

		private F value;

		protected void perform() {

			F updatedValue = getUpdatedValue();

			if (updatedValue != value) {

				removeValue(value);

				if (updatedValue != null) {

					addValue(updatedValue);
				}
			}
		}

		UpdateDisjunctsAction(F value) {

			this.value = value;
		}

		abstract CFrame checkUpdateCFrameDisjuncts(CFrame cFrame);

		private F getUpdatedValue() {

			CFrame cFrame = valueToCFrame(value);
			CFrame updatedCFrame = checkUpdateCFrameDisjuncts(cFrame);

			if (updatedCFrame == null) {

				return null;
			}

			if (updatedCFrame.equals(cFrame)) {

				return value;
			}

			return checkUpdateValue(value, updatedCFrame);
		}
	}

	private class AddDisjunctAction extends UpdateDisjunctsAction {

		AddDisjunctAction(F value) {

			super(value);
		}

		CFrame checkUpdateCFrameDisjuncts(CFrame cFrame) {

			return checkAddCFrameDisjunct(cFrame);
		}
	}

	private class RemoveDisjunctAction extends UpdateDisjunctsAction {

		RemoveDisjunctAction(F value) {

			super(value);
		}

		CFrame checkUpdateCFrameDisjuncts(CFrame cFrame) {

			return checkRemoveCFrameDisjunct(cFrame);
		}
	}

	FFrameValuesNode(ITree tree, ISlot slot) {

		super(tree, slot);

		this.tree = tree;
		this.slot = slot;
	}

	GNodeAction getAdditionAction(F value) {

		return addDisjunctActionRequired()
				? new AddDisjunctAction(value)
				: GNodeAction.INERT_ACTION;
	}

	GNodeAction getRemovalAction(F value) {

		return removeDisjunctActionRequired(value)
				? new RemoveDisjunctAction(value)
				: getRemoveValueAction(value);
	}

	CFrame checkObtainCFrameAddition() {

		return selectableCFrameOptions()
					? getCFrameAdditionSelectionOrNull()
					: getRootCFrame();
	}

	abstract String getCFrameRole();

	abstract CFrame getRootCFrame();

	abstract CFrame valueToCFrame(F value);

	abstract F checkUpdateValue(F value, CFrame updatedCFrame);

	private boolean addDisjunctActionRequired() {

		return slot.editable()
				&& slot.abstractValuesAllowed()
				&& selectableCFrameOptions();
	}

	private boolean removeDisjunctActionRequired(F value) {

		return slot.editable() && valueToCFrame(value).getCategory().disjunction();
	}

	private CFrame checkAddCFrameDisjunct(CFrame cFrame) {

		CFrame newDisjunct = getCFrameAdditionSelectionOrNull();

		if (newDisjunct != null) {

			List<CFrame> disjuncts = cFrameAsDisjuncts(cFrame);

			if (updateCFrameDisjuncts(disjuncts, newDisjunct)) {

				return CFrame.resolveDisjunction(disjuncts);
			}
		}

		return cFrame;
	}

	private boolean updateCFrameDisjuncts(
						List<CFrame> disjuncts,
						CFrame newDisjunct) {

		if (disjuncts.contains(newDisjunct)) {

			return false;
		}

		for (CFrame disjunct : new ArrayList<CFrame>(disjuncts)) {

			if (disjunct.subsumes(newDisjunct)) {

				disjuncts.remove(disjunct);
			}
		}

		disjuncts.add(newDisjunct);

		return true;
	}

	private CFrame checkRemoveCFrameDisjunct(CFrame cFrame) {

		List<CFrame> disjuncts = cFrameAsDisjuncts(cFrame);
		List<CFrame> oldDisjuncts = getCFrameRemovalSelections(disjuncts);

		if (!oldDisjuncts.isEmpty()) {

			disjuncts.removeAll(oldDisjuncts);

			if (disjuncts.isEmpty()) {

				return null;
			}

			return CFrame.resolveDisjunction(disjuncts);
		}

		return cFrame;
	}

	private CFrame getCFrameAdditionSelectionOrNull() {

		return new CFrameAdditionSelector(
						tree,
						getCFrameRole(),
						getRootCFrame())
							.getSelectionOrNull();
	}

	private List<CFrame> getCFrameRemovalSelections(List<CFrame> options) {

		return new CFrameRemovalsSelector(
						tree,
						getCFrameRole(),
						options)
							.getSelections();
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
