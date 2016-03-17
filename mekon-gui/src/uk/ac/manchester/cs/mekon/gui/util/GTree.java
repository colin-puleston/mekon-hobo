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

package uk.ac.manchester.cs.mekon.gui.util;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * @author Colin Puleston
 */
public class GTree extends JTree {

	static private final long serialVersionUID = -1;

	private DefaultTreeModel treeModel = null;
	private GNode rootNode = null;

	private ActionInvoker positiveActionInvoker = new PositiveActionInvoker();
	private ActionInvoker negativeActionInvoker = new NegativeActionInvoker();

	private class ExpansionManager implements TreeExpansionListener {

		public void treeExpanded(TreeExpansionEvent event) {

			setNodeExpansion(event, true);
		}

		public void treeCollapsed(TreeExpansionEvent event) {

			setNodeExpansion(event, false);
		}

		ExpansionManager() {

			addTreeExpansionListener(this);
		}

		private void setNodeExpansion(TreeExpansionEvent event, boolean expanded) {

			getNode(event).setLocalExpansion(expanded);
		}

		private GNode getNode(TreeExpansionEvent event) {

			return GNode.cast(event.getPath().getLastPathComponent());
		}
	}

	private abstract class ActionInvoker {

		void checkInvokeOn(GNode node, boolean action2) {

			GNodeAction action = getNodeAction(node, action2);

			if (action.active()) {

				action.perform();
			}
		}

		abstract GNodeAction getNodeAction1(GNode node);

		abstract GNodeAction getNodeAction2(GNode node);

		private GNodeAction getNodeAction(GNode node, boolean action2) {

			return action2 ? getNodeAction2(node) : getNodeAction1(node);
		}
	}

	private class PositiveActionInvoker extends ActionInvoker {

		GNodeAction getNodeAction1(GNode node) {

			return node.getPositiveAction1();
		}

		GNodeAction getNodeAction2(GNode node) {

			return node.getPositiveAction2();
		}
	}

	private class NegativeActionInvoker extends ActionInvoker {

		GNodeAction getNodeAction1(GNode node) {

			return node.getNegativeAction1();
		}

		GNodeAction getNodeAction2(GNode node) {

			return node.getNegativeAction2();
		}
	}

	private class TreeClickMonitor extends MouseAdapter {

		TreeClickMonitor() {

			addMouseListener(this);
		}
	}

	private class DeselectionClickMonitor extends TreeClickMonitor {

		public void mouseClicked(MouseEvent event) {

			clearSelection();
		}
	}

	private class ActiveTreeClickMonitor extends TreeClickMonitor {

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

				actionInvoker.checkInvokeOn(node, event.isAltDown());
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

	public GTree() {

		new ExpansionManager();
	}

	public void setActiveTree() {

		new ActiveTreeClickMonitor();
	}

	public void setNonVisibleSelection() {

		new DeselectionClickMonitor();
	}

	public void initialise(GNode rootNode) {

		this.rootNode = rootNode;
		treeModel = new DefaultTreeModel(rootNode);

		setModel(treeModel);
		GCellRenderers.get().set(this);

		rootNode.setLocalExpansion(true);
		rootNode.initialiseSubTree();
	}

	public void updateAllNodeDisplays() {

		rootNode.updateSubTreeNodeDisplays();
	}

	public GNode getRootNode() {

		return rootNode;
	}

	public boolean isEmptyTree() {

		return rootNode == null || rootNode.getChildCount() == 0;
	}

	DefaultTreeModel getTreeModel() {

		return treeModel;
	}
}
