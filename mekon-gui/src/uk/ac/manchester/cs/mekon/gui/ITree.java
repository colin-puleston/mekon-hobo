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

import java.awt.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class ITree extends GTree {

	static private final long serialVersionUID = -1;

	static final Color DIRECT_UPDATES_CLR = Color.yellow;
	static final Color INDIRECT_UPDATES_CLR = Color.cyan;

	private ITreeCollapsedNodes collapseds = null;
	private ITreeUpdates updates = null;

	private class ISlotValueUpdates {

		private ISlotNode slotNode;
		private ISlotValuesEditor editor;

		ISlotValueUpdates(ISlotNode slotNode) {

			this.slotNode = slotNode;

			editor = slotNode.getISlot().getValuesEditor();
		}

		void performUpdate(IValue valueToAdd, IValue valueToRemove) {

			collapseds.update(slotNode);
			updates.onSlotToBeUpdated(slotNode, valueToAdd, valueToRemove);

			if (valueToAdd == null && valueToRemove == null) {

				editor.clear();
			}
			else {

				if (valueToAdd != null) {

					editor.add(valueToAdd);
				}

				if (valueToRemove != null) {

					editor.remove(valueToRemove);
				}
			}

			collapseds.restore();
		}
	}

	ITree(IFrame rootFrame) {

		initialise(new IFrameNode(this, rootFrame));

		updates = new ITreeUpdates(getRootNode());
		collapseds = new ITreeCollapsedNodes(getRootNode());

		setActiveTree();
	}

	void addValue(ISlotNode slotNode, IValue value) {

		new ISlotValueUpdates(slotNode).performUpdate(value, null);
	}

	void removeValue(ISlotNode slotNode, IValue value) {

		new ISlotValueUpdates(slotNode).performUpdate(null, value);
	}

	void replaceValue(ISlotNode slotNode, IValue oldValue, IValue newValue) {

		new ISlotValueUpdates(slotNode).performUpdate(oldValue, newValue);
	}

	void clearValues(ISlotNode slotNode) {

		new ISlotValueUpdates(slotNode).performUpdate(null, null);
	}

	void checkMarkForGeneralUpdate(GNode node, GCellDisplay display) {

		if (updates == null) {

			return;
		}

		if (updates.indirectGeneralUpdateMarkRequired(node)) {

			display.setBackgroundColour(INDIRECT_UPDATES_CLR);
		}
		else if (updates.directGeneralUpdateMarkRequired(node)) {

			display.setBackgroundColour(DIRECT_UPDATES_CLR);
		}
	}

	void checkMarkForSlotValueTypeUpdate(ISlotNode node, GCellDisplay display) {

		if (updates == null) {

			return;
		}

		if (updates.indirectSlotValueTypeUpdateMarkRequired(node)) {

			display.setBackgroundColour(INDIRECT_UPDATES_CLR);
		}
	}
}
