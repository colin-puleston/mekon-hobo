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

package uk.ac.manchester.cs.mekon_util.gui;

import java.awt.event.*;
import javax.swing.tree.*;

/**
 * @author Colin Puleston
 */
public class GActionTree extends GTree {

	static private final long serialVersionUID = -1;

	private ActionInvoker positiveActionInvoker = new PositiveActionInvoker();
	private ActionInvoker negativeActionInvoker = new NegativeActionInvoker();

	private abstract class ActionInvoker {

		void checkInvokeOn(GNode node, MouseEvent event) {

			GNodeAction action = getNodeAction(node, event);

			if (action.active()) {

				action.perform();
			}
		}

		abstract GNodeAction getNodeAction1(GNode node);

		abstract GNodeAction getNodeAction2(GNode node);

		abstract GNodeAction getNodeAction3(GNode node);

		private GNodeAction getNodeAction(GNode node, MouseEvent event) {

			if (event.isAltDown()) {

				return getNodeAction2(node);
			}

			if (event.isShiftDown()) {

				return getNodeAction3(node);
			}

			return getNodeAction1(node);
		}
	}

	private class PositiveActionInvoker extends ActionInvoker {

		GNodeAction getNodeAction1(GNode node) {

			return node.getPositiveAction1();
		}

		GNodeAction getNodeAction2(GNode node) {

			return node.getPositiveAction2();
		}

		GNodeAction getNodeAction3(GNode node) {

			return node.getPositiveAction3();
		}
	}

	private class NegativeActionInvoker extends ActionInvoker {

		GNodeAction getNodeAction1(GNode node) {

			return node.getNegativeAction1();
		}

		GNodeAction getNodeAction2(GNode node) {

			return node.getNegativeAction2();
		}

		GNodeAction getNodeAction3(GNode node) {

			return node.getNegativeAction3();
		}
	}

	private class ClickMonitor extends MouseAdapter {

		public void mouseClicked(MouseEvent event) {

			TreePath path = findTreePath(event);

			if (path != null) {

				processClick(getLeafNode(path), event);
			}
		}

		private void processClick(GNode node, MouseEvent event) {

			clearSelection();

			ActionInvoker actionInvoker = getSelectedActionInvoker(event);

			if (actionInvoker != null) {

				actionInvoker.checkInvokeOn(node, event);
			}
		}

		private ActionInvoker getSelectedActionInvoker(MouseEvent event) {

			int button = event.getButton();

			if (button == MouseEvent.BUTTON3 || event.isControlDown()) {

				return negativeActionInvoker;
			}

			if (button == MouseEvent.BUTTON1) {

				return positiveActionInvoker;
			}

			return null;
		}

		private TreePath findTreePath(MouseEvent event) {

			return getPathForLocation(event.getX(), event.getY());
		}

		private GNode getLeafNode(TreePath path) {

			return GNode.cast(path.getLastPathComponent());
		}
	}

	public GActionTree() {

		super(false);

		addMouseListener(new ClickMonitor());
	}
}
