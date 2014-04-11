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
abstract class AddCFrameDisjunctAction extends GNodeAction {

	static boolean actionRequired(ISlot slot, CFrame rootCFrame) {

		return slot.editable()
				&& slot.queryInstance()
				&& selectableOptions(rootCFrame);
	}

	static private boolean selectableOptions(CFrame rootCFrame) {

		return !rootCFrame.getSubs(CFrameVisibility.EXPOSED).isEmpty();
	}

	private CFrame frame;

	protected void perform() {

		checkAddDisjunct();
	}

	AddCFrameDisjunctAction(CFrame frame) {

		this.frame = frame;
	}

	abstract CFrame checkObtainNewDisjunct();

	abstract void onDisjunctAdded(CFrame updatedFrame);

	private void checkAddDisjunct() {

		CFrame newDisjunct = checkObtainNewDisjunct();

		if (newDisjunct != null) {

			List<CFrame> disjuncts = valueAsDisjuncts(frame);

			if (disjuncts.add(newDisjunct)) {

				frame = CFrame.createDisjunction(disjuncts);

				onDisjunctAdded(frame);
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
}
