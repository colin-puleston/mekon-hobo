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
import java.awt.event.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class IFrameReselector {

	private ITree tree;
	private IFrameSlotNode reselectionSlotNode = null;

	private class ExitKeyListener extends KeyAdapter {

		public void keyReleased(KeyEvent event) {

			if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {

				end();
			}
		}
	}

	IFrameReselector(ITree tree) {

		this.tree = tree;

		tree.addKeyListener(new ExitKeyListener());
	}

	void checkStart(IFrameSlotNode slotNode) {

		reselectionSlotNode = slotNode;

		if (anyReselectable()) {

			updateDisplay(ITree.IFRAME_RESELECT_BACKGROUND_CLR);
		}
		else {

			reselectionSlotNode = null;
		}
	}

	void end(IFrame selection) {

		reselectionSlotNode.addValue(selection);

		end();
	}

	void end() {

		reselectionSlotNode = null;

		updateDisplay(ITree.DEFAULT_BACKGROUND_CLR);
	}

	boolean reselecting() {

		return reselectionSlotNode != null;
	}

	boolean reselectable(INode node) {

		return node instanceof IFrameNode && reselectable((IFrameNode)node);
	}

	private boolean anyReselectable() {

		return anyReselectableDescendants((INode)tree.getRootNode());
	}

	private boolean anyReselectableDescendants(INode node) {

		for (INode child : node.getIChildren()) {

			if (reselectable(child) || anyReselectableDescendants(child)) {

				return true;
			}
		}

		return false;
	}

	private boolean reselectable(IFrameNode node) {

		IFrame iFrame = node.getValue();

		return reselectableType(iFrame) && !reselectionSlotValue(iFrame);
	}

	private boolean reselectableType(IFrame iFrame) {

		return reselectionSlotNode.getValueType().subsumes(iFrame.getType());
	}

	private boolean reselectionSlotValue(IFrame iFrame) {

		return reselectionSlotNode.getISlot().getValues().asList().contains(iFrame);
	}

	private void updateDisplay(Color background) {

		tree.setBackground(background);
		tree.updateAllNodeDisplays();
	}
}
