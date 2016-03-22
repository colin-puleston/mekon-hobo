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
class ITreeNodeDisplays {

	private ITreeUpdates updates;
	private ITreeCrossLinks crossLinks;

	private class Highlighter {

		private INode node;
		private GCellDisplay mainDisplay;
		private GCellDisplay valueTypeDisplay;

		Highlighter(
			INode node,
			GCellDisplay mainDisplay,
			GCellDisplay valueTypeDisplay) {

			this.node = node;
			this.mainDisplay = mainDisplay;
			this.valueTypeDisplay = valueTypeDisplay;

			if (crossLinks.active()) {

				checkForCrossLinking();
			}
			else {

				checkForUpdate();
			}
		}

		private void checkForUpdate() {

			if (updates.showDirectUpdate(node)) {

				highlightMain(ITree.DIRECT_UPDATES_CLR);
			}
			else {

				if (updates.showGeneralIndirectUpdate(node)) {

					highlightMain(ITree.INDIRECT_UPDATES_CLR);
				}

				if (valueTypeDisplay != null) {

					if (updates.showValueTypeIndirectUpdate(node)) {

						highlightValueType(ITree.INDIRECT_UPDATES_CLR);
					}
				}
			}
		}

		private void checkForCrossLinking() {

			if (crossLinks.showLinkable(node)) {

				highlightMain(ITree.CROSS_LINKABLE_IFRAME_CLR);
			}
			else if (crossLinks.showLinked(node)) {

				highlightMain(ITree.CROSS_LINKED_IFRAME_CLR);
			}
		}

		private void highlightMain(Color colour) {

			mainDisplay.setBackgroundColour(colour);
		}

		private void highlightValueType(Color colour) {

			valueTypeDisplay.setBackgroundColour(colour);
		}
	}

	ITreeNodeDisplays(ITree tree) {

		updates = tree.getUpdates();
		crossLinks = tree.getCrossLinks();
	}

	GCellDisplay get(IValueNode<?> valueNode) {

		GCellDisplay display = valueNode.getDefaultDisplay();

		new Highlighter(valueNode, display, null);

		return display;
	}

	GCellDisplay get(ISlotNode slotNode) {

		ISlot slot = slotNode.getISlot();
		EntityDisplays displays = EntityDisplays.get();

		GCellDisplay main = displays.get(slot);
		GCellDisplay valueType = displays.forSlotValueTypeModifier(slot);
		GCellDisplay cardinality = displays.forSlotCardinalityModifier(slot);

		main.setModifier(valueType);
		valueType.setModifier(cardinality);

		new Highlighter(slotNode, main, valueType);

		return main;
	}
}
