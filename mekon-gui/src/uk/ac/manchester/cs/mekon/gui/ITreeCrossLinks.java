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

	private Mode inactive = new Inactive();
	private Mode mode = inactive;

	private class ExitKeyListener extends KeyAdapter {

		public void keyReleased(KeyEvent event) {

			if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {

				deactivate();
			}
		}
	}

	private abstract class Mode {

		abstract boolean canStart();

		Mode checkStart() {

			if (canStart()) {

				updateDisplay(getBackgroundColour());

				return this;
			}

			return inactive;
		}

		abstract boolean showForMode(INode node);

		abstract Color getBackgroundColour();

		void creatLink(IFrame selectedValue) {

			throw new Error("Method should never be invoked!");
		}
	}

	private class Inactive extends Mode {

		boolean canStart() {

			return true;
		}

		boolean showForMode(INode node) {

			return false;
		}

		Color getBackgroundColour() {

			return ITree.DEFAULT_BACKGROUND_CLR;
		}
	}

	private abstract class Active extends Mode {

		boolean canStart() {

			return anyApplicableNodes();
		}

		boolean applicableNode(INode node) {

			return node instanceof IFrameNode && applicableNode((IFrameNode)node);
		}

		abstract boolean applicableNode(IFrameNode node);

		boolean anyApplicableDescendants(INode current) {

			for (INode child : current.getIChildren()) {

				if (applicableNode(child) || anyApplicableDescendants(child)) {

					return true;
				}
			}

			return false;
		}

		private boolean anyApplicableNodes() {

			return anyApplicableDescendants(getRootINode());
		}
	}

	private class Linking extends Active {

		private IFrameSlotNode slotNode;

		Linking(IFrameSlotNode slotNode) {

			this.slotNode = slotNode;
		}

		boolean showForMode(INode node) {

			return applicableNode(node);
		}

		void creatLink(IFrame selectedValue) {

			slotNode.addValue(selectedValue);
		}

		Color getBackgroundColour() {

			return ITree.CROSS_LINKING_BACKGROUND_CLR;
		}

		boolean applicableNode(IFrameNode node) {

			IFrame iFrame = node.getValue();

			return linkableType(iFrame) && !linkingSlotValue(iFrame);
		}

		private boolean linkableType(IFrame iFrame) {

			return slotNode.getValueType().subsumes(iFrame.getType());
		}

		private boolean linkingSlotValue(IFrame iFrame) {

			return slotNode.getISlot().getValues().asList().contains(iFrame);
		}
	}

	private class ShowingLinked extends Active {

		private IFrame linkedFrame;

		ShowingLinked(IFrame linkedFrame) {

			this.linkedFrame = linkedFrame;
		}

		boolean showForMode(INode node) {

			if (applicableNode(node)) {

				return true;
			}

			return node.collapsed() && anyApplicableDescendants(node);
		}

		Color getBackgroundColour() {

			return ITree.CROSS_LINKS_SHOW_BACKGROUND_CLR;
		}

		boolean applicableNode(IFrameNode node) {

			return node.getValue() == linkedFrame;
		}
	}

	ITreeCrossLinks(ITree tree) {

		this.tree = tree;

		tree.addKeyListener(new ExitKeyListener());
	}

	void checkStartLinking(IFrameSlotNode slotNode) {

		checkStartMode(new Linking(slotNode));
	}

	void checkStartShowingLinked(IFrameNode frameNode) {

		checkStartMode(new ShowingLinked(frameNode.getValue()));
	}

	void endLinking(IFrame selectedValue) {

		mode.creatLink(selectedValue);

		deactivate();
	}

	void deactivate() {

		checkStartMode(inactive);
	}

	boolean active() {

		return mode instanceof Active;
	}

	boolean linking() {

		return mode instanceof Linking;
	}

	boolean showingLinked() {

		return mode instanceof ShowingLinked;
	}

	boolean showLinkable(INode node) {

		return linking() && mode.showForMode(node);
	}

	boolean showLinked(INode node) {

		return showingLinked() && mode.showForMode(node);
	}

	private void checkStartMode(Mode newMode) {

		mode = newMode.checkStart();
	}

	private void updateDisplay(Color background) {

		tree.setBackground(background);
		tree.updateAllNodeDisplays();
	}

	private INode getRootINode() {

		return (INode)tree.getRootNode();
	}
}
