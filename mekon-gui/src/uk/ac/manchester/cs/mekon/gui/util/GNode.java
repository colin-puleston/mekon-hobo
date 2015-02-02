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

import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * @author Colin Puleston
 */
public abstract class GNode extends GMutableTreeNode {

	static private final long serialVersionUID = -1;

	private GTree tree;
	private GNode parent = null;
	private ChildList childList = null;
	private boolean initialised = false;
	private boolean locallyExpanded = false;

	private class ChildList extends GCellDisplaySortedList<GNode> {

		ChildList() {

			super(orderedChildren());
		}

		int compareOrdered(GNode first, GNode second) {

			int c = compareChildrenPriorToLabelCompare(first, second);

			return c != 0 ? c : super.compareOrdered(first, second);
		}

		GCellDisplay getDisplay(GNode node) {

			return node.getDisplay();
		}
	}

	public GNode(GTree tree) {

		this.tree = tree;
	}

	public void addChild(GNode child) {

		child.parent = this;

		getChildList().add(child);

		if (initialised) {

			getTreeModel().nodesWereInserted(this, getIndexAsArray(child));
		}
	}

	public void clearChildren() {

		for (GNode child : new ArrayList<GNode>(getChildren())) {

			child.remove();
		}
	}

	public void remove() {

		if (parent != null) {

			parent.removeChild(this);
		}
	}

	public void checkExpanded() {

		if (!expanded()) {

			if (parent != null) {

				parent.checkExpanded();
			}

			expand();
		}
	}

	public void expand() {

		tree.expandPath(getTreePath());
	}

	public void collapse() {

		tree.collapsePath(getTreePath());
	}

	public void notifyUpdatedDisplay() {

		getTreeModel().nodeChanged(this);
	}

	public String toString() {

		return tree.decorateNodeLabel(this, getLabel());
	}

	public String getLabel() {

		return getDisplay().getLabel();
	}

	public GNode getParent() {

		return parent;
	}

	public List<GNode> getChildren() {

		return getChildList().asList();
	}

	public GTree getTree() {

		return tree;
	}

	public TreePath getTreePath() {

		return parent == null
				? new TreePath(this)
				: parent.getTreePath().pathByAddingChild(this);
	}

	public int getNodeLevel() {

		return parent == null ? 0 : (parent.getNodeLevel() + 1);
	}

	public boolean expanded() {

		return tree.isExpanded(getTreePath());
	}

	public boolean collapsed() {

		return tree.isCollapsed(getTreePath());
	}

	public boolean locallyExpanded() {

		return locallyExpanded;
	}

	public boolean locallyCollapsed() {

		return !locallyExpanded;
	}

	protected void addInitialChildren() {
	}

	protected boolean orderedChildren() {

		return false;
	}

	protected int compareChildrenPriorToLabelCompare(GNode first, GNode second) {

		return 0;
	}

	protected boolean autoExpand() {

		return true;
	}

	protected GNodeAction getPositiveAction() {

		return GNodeAction.INERT_ACTION;
	}

	protected GNodeAction getNegativeAction() {

		return GNodeAction.INERT_ACTION;
	}

	protected abstract GCellDisplay getDisplay();

	void initialiseSubTree() {

		getChildList();

		if (autoExpand()) {

			checkExpanded();

			for (GNode child : getChildren()) {

				child.initialiseSubTree();
			}
		}
	}

	void setLocalExpansion(boolean locallyExpanded) {

		this.locallyExpanded = locallyExpanded;
	}

	private void removeChild(GNode child) {

		int[] oldIndex = new int[]{getIndex(child)};
		Object[] oldChild = new Object[]{child};

		child.collapse();
		child.parent = null;
		getChildList().remove(child);
		getTreeModel().nodesWereRemoved(this, oldIndex, oldChild);
	}

	private ChildList getChildList() {

		if (childList == null) {

			childList = new ChildList();

			addInitialChildren();
			getTreeModel().nodesWereInserted(this, getAllIndicesAsArray());

			initialised = true;
		}

		return childList;
	}

	private DefaultTreeModel getTreeModel() {

		return tree.getTreeModel();
	}

	private int[] getIndexAsArray(GNode child) {

		return new int[]{getIndex(child)};
	}

	private int[] getAllIndicesAsArray() {

		int i = getChildCount();
		int[] indices = new int[i];

		while (--i > 0) {

			indices[i] = i;
		}

		return indices;
	}

	private void throwInertMethodError() {

		throw new Error("Method should never be invoked!");
	}
}