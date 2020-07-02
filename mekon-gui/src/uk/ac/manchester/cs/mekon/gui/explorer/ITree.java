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

package uk.ac.manchester.cs.mekon.gui.explorer;

import java.awt.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class ITree extends GActionTree {

	static private final long serialVersionUID = -1;

	static final Color DIRECT_UPDATES_CLR = Color.yellow;
	static final Color INDIRECT_UPDATES_CLR = Color.cyan;
	static final Color DIRECT_AND_INDIRECT_UPDATES_CLR
							= mergeColours(
								DIRECT_UPDATES_CLR,
								INDIRECT_UPDATES_CLR);

	static final Color CROSS_LINKABLE_IFRAME_CLR = Color.green;
	static final Color CROSS_LINKED_IFRAME_CLR = Color.green;

	static final Color DEFAULT_BACKGROUND_CLR = Color.white;
	static final Color CROSS_LINKING_BACKGROUND_CLR = Color.gray.brighter();
	static final Color CROSS_LINKS_SHOW_BACKGROUND_CLR = Color.white;

	static private Color mergeColours(Color c1, Color c2) {

		int r = (c1.getRed() + c2.getRed()) / 2;
		int g = (c1.getGreen() + c2.getGreen()) / 2;
		int b = (c1.getBlue() + c2.getBlue()) / 2;

		return new Color(r, g, b);
	}

	private InstanceStoreActions storeActions;

	private ITreeUpdates updates;
	private ITreeExpansions expansions;
	private ITreeCrossLinks crossLinks;
	private ITreeNodeDisplays nodeDisplays;

	ITree(IFrame rootFrame, InstanceStoreActions storeActions) {

		this.storeActions = storeActions;

		IFrameNode rootNode = new IFrameNode(this, rootFrame);

		updates = new ITreeUpdates(rootNode);
		expansions = new ITreeExpansions(rootNode);
		crossLinks = new ITreeCrossLinks(this);
		nodeDisplays = new ITreeNodeDisplays(this);

		initialise(rootNode);

		updates.initialise();
	}

	void addValue(ISlotNode slotNode, IValue value) {

		update(slotNode, value, null);
	}

	void removeValue(ISlotNode slotNode, IValue value) {

		update(slotNode, null, value);
	}

	void replaceValue(ISlotNode slotNode, IValue oldValue, IValue newValue) {

		update(slotNode, oldValue, newValue);
	}

	void clearValues(ISlotNode slotNode) {

		update(slotNode, null, null);
	}

	InstanceStoreActions getStoreActions() {

		return storeActions;
	}

	ITreeUpdates getUpdates() {

		return updates;
	}

	ITreeCrossLinks getCrossLinks() {

		return crossLinks;
	}

	ITreeNodeDisplays getNodeDisplays() {

		return nodeDisplays;
	}

	private void update(ISlotNode slotNode, IValue valueToAdd, IValue valueToRemove) {

		expansions.update(slotNode);
		updates.update(slotNode, valueToAdd, valueToRemove);
		expansions.restore();
	}
}
