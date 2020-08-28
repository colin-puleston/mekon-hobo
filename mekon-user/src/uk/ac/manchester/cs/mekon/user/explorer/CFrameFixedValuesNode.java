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
class CFrameFixedValuesNode extends GNode {

	private CTree tree;

	private List<CIdentity> slotIds;
	private CSlotValues fixedValues;

	private class SlotValuesNode extends GNode {

		private CIdentity slotId;

		protected void addInitialChildren() {

			CValueNodeCreator creator = new CValueNodeCreator(tree);

			for (CValue<?> value : fixedValues.getValues(slotId)) {

				addChild(creator.create(value));
			}
		}

		protected GCellDisplay getDisplay() {

			return EntityDisplays.get().forCSlotValues(slotId);
		}

		SlotValuesNode(CIdentity slotId) {

			super(tree);

			this.slotId = slotId;
		}
	}

	protected void addInitialChildren() {

		for (CIdentity id : slotIds) {

			addChild(new SlotValuesNode(id));
		}
	}

	protected GCellDisplay getDisplay() {

		return EntityDisplays.get().fixedValuesDisplay;
	}

	CFrameFixedValuesNode(CTree tree, CFrame frame, List<CIdentity> slotIds) {

		super(tree);

		this.tree = tree;
		this.slotIds = slotIds;

		fixedValues = frame.getSlotValues();
	}
}
