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
class ITreeCrossLinks {

	private ITree tree;

	private Mode inactive = new Mode();
	private Mode mode = inactive;

	private class ExitKeyListener extends KeyAdapter {

		public void keyReleased(KeyEvent event) {

			if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {

				inactive.enterMode();
			}
		}
	}

	private class Mode {

		void enterMode() {

			updateDisplay(getBackgroundColour());

			mode = this;
		}

		boolean active() {

			return false;
		}

		boolean linking() {

			return false;
		}

		boolean showLinkable(INode node) {

			return false;
		}

		boolean showLinked(INode node) {

			return false;
		}

		void checkCreateLink(IFrame selectedValue) {

			throw new Error("Method should never be invoked!");
		}

		Color getBackgroundColour() {

			return ITree.DEFAULT_BACKGROUND_CLR;
		}
	}

	private abstract class Active extends Mode {

		void checkActivate() {

			if (anyApplicableNodes()) {

				enterMode();
			}
		}

		boolean active() {

			return true;
		}

		boolean showForActiveMode(INode node) {

			if (applicableNode(node)) {

				return true;
			}

			return node.collapsed() && anyApplicableDescendants(node);
		}

		boolean applicableNode(INode node) {

			if (node instanceof IFrameNode) {

				IFrameNode frameNode = (IFrameNode)node;

				return applicableValue(frameNode.getValue());
			}

			return false;
		}

		abstract boolean applicableValue(IFrame value);

		private boolean anyApplicableNodes() {

			return anyApplicableDescendants(getRootINode());
		}

		private boolean anyApplicableDescendants(INode current) {

			for (INode child : current.getIChildren()) {

				if (applicableNode(child) || anyApplicableDescendants(child)) {

					return true;
				}
			}

			return false;
		}
	}

	private class Linking extends Active {

		private IFrameSlotNode slotNode;

		Linking(IFrameSlotNode slotNode) {

			this.slotNode = slotNode;
		}

		Color getBackgroundColour() {

			return ITree.CROSS_LINKING_BACKGROUND_CLR;
		}

		boolean linking() {

			return true;
		}

		boolean showLinkable(INode node) {

			return showForActiveMode(node);
		}

		void checkCreateLink(IFrame selectedValue) {

			if (!currentSlotValue(selectedValue)
				&& slotNode.checkAddValue(selectedValue)) {

				inactive.enterMode();
			}
		}

		boolean applicableValue(IFrame value) {

			return linkableType(value) && !linkingSlotValue(value);
		}

		private boolean currentSlotValue(IFrame value) {

			return slotNode.getISlot().getValues().contains(value);
		}

		private boolean linkableType(IFrame value) {

			return slotNode.getValueType().subsumes(value.getType());
		}

		private boolean linkingSlotValue(IFrame value) {

			return slotNode.getISlot().getValues().contains(value);
		}
	}

	private class ShowingLinked extends Active {

		private IFrame linkingValue;

		ShowingLinked(IFrameNode frameNode) {

			linkingValue = frameNode.getValue();
		}

		Color getBackgroundColour() {

			return ITree.CROSS_LINKS_SHOW_BACKGROUND_CLR;
		}

		boolean showLinked(INode node) {

			return showForActiveMode(node);
		}

		boolean applicableValue(IFrame value) {

			return value == linkingValue;
		}
	}

	ITreeCrossLinks(ITree tree) {

		this.tree = tree;

		tree.addKeyListener(new ExitKeyListener());
	}

	void checkStartLinking(IFrameSlotNode slotNode) {

		new Linking(slotNode).checkActivate();
	}

	void checkStartShowingLinked(IFrameNode frameNode) {

		new ShowingLinked(frameNode).checkActivate();
	}

	void checkCreateLink(IFrame selectedValue) {

		mode.checkCreateLink(selectedValue);
	}

	boolean active() {

		return mode.active();
	}

	boolean linking() {

		return mode.linking();
	}

	boolean showLinkable(INode node) {

		return mode.showLinkable(node);
	}

	boolean showLinked(INode node) {

		return mode.showLinked(node);
	}

	private void updateDisplay(Color background) {

		tree.setBackground(background);
		tree.updateAllNodeDisplays();
	}

	private INode getRootINode() {

		return (INode)tree.getRootNode();
	}
}
