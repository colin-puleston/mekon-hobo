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

import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * @author Colin Puleston
 */
public abstract class GNode extends GMutableTreeNode {

	static private final long serialVersionUID = -1;

	static GNode cast(Object node) {

		if (node instanceof GNode) {

			return (GNode)node;
		}

		throw new Error("Object is not of type GNode: " + node);
	}

	private GTree tree;
	private GNode parent = null;
	private ChildList childList = null;
	private boolean initialised = false;

	private class ChildList extends GCellDisplaySortedList<GNode> {

		private boolean replacing = false;
		private Integer replacingIndex = null;

		ChildList() {

			super(orderedChildren());
		}

		void startReplacementOperation() {

			replacing = true;
		}

		void endReplacementOperation() {

			replacing = false;
		}

		boolean replacing() {

			return replacing;
		}

		void performAddition(GNode child, int index) {

			insert(child, resolveIndex(index));
		}

		void performRemoval(GNode child) {

			if (replacing) {

				replacingIndex = indexOf(child);
			}

			remove(child);
		}

		int compareOrdered(GNode first, GNode second) {

			int c = compareChildrenPriorToLabelCompare(first, second);

			return c != 0 ? c : super.compareOrdered(first, second);
		}

		GCellDisplay getDisplay(GNode node) {

			return node.getDisplay();
		}

		private int resolveIndex(int index) {

			if (replacingIndex != null) {

				return resolveReplacingIndex(index);
			}

			if (index == -1) {

				return size();
			}

			if (orderedChildren()) {

				throw new RuntimeException("Cannot specify index for ordered GNode children");
			}

			return index;
		}

		private int resolveReplacingIndex(int index) {

			if (index != -1) {

				throw new RuntimeException("Cannot specify index for GNode replacement");
			}

			index = replacingIndex;
			replacingIndex = null;

			return index;
		}
	}

	public GNode(GTree tree) {

		this.tree = tree;
	}

	public void addChild(GNode child) {

		addChild(child, -1);
	}

	public void addChild(GNode child, int index) {

		child.parent = this;

		getChildList().performAddition(child, index);

		if (initialised) {

			getTreeModel().nodesWereInserted(this, new int[]{getIndex(child)});

			if (child.autoExpand()) {

				child.checkExpanded();
			}

			tree.registerNodeAdded(child, initialised);
		}
	}

	public void clearChildren() {

		childList.clear();

		getTreeModel().nodeStructureChanged(this);
	}

	public void remove() {

		if (parent != null) {

			parent.removeChild(this, true);
		}
	}

	public void removeChild(int index) {

		removeChild(childList.get(index), true);
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

	public void expandAll() {

		expand();
		expandAllDescendants();
	}

	public void expandAllDescendants() {

		for (GNode child : getChildren()) {

			child.expandAll();
		}
	}

	public void collapse() {

		tree.collapsePath(getTreePath());
	}

	public void collapseAll() {

		collapseAllDescendants();
		collapse();
	}

	public void collapseAllDescendants() {

		for (GNode child : getChildren()) {

			child.collapseAll();
		}
	}

	public void select() {

		tree.selectAll(Collections.singletonList(this));
	}

	public void updateNodeDisplay() {

		getTreeModel().nodeChanged(this);
	}

	public void startChildReplacementOperation() {

		getChildList().startReplacementOperation();
	}

	public void endChildReplacementOperation() {

		getChildList().endReplacementOperation();
	}

	public boolean replacingChild() {

		return getChildList().replacing();
	}

	public String toString() {

		return getClass().getSimpleName() + "[" + entityToString() + "]";
	}

	public String getLabel() {

		return getDisplay().getText();
	}

	public boolean isRootNode() {

		return parent == null;
	}

	public GNode getParent() {

		return parent;
	}

	public List<GNode> getChildren() {

		return childList != null
				? getChildList().asList()
				: Collections.<GNode>emptyList();
	}

	public <C extends GNode>List<C> getChildren(Class<C> childClass) {

		List<C> selectedChildren = new ArrayList<C>();

		for (GNode child : getChildren()) {

			if (childClass.isAssignableFrom(child.getClass())) {

				selectedChildren.add(childClass.cast(child));
			}
		}

		return selectedChildren;
	}

	public List<GNode> ensureChildren() {

		return getChildList().asList();
	}

	public int indexInSiblings() {

		return isRootNode() ? -1 : parent.getIndex(this);
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

	protected void addInitialChildren() {
	}

	protected void onChildrenInitialised() {
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

	protected GNodeAction getPositiveAction1() {

		return GNodeAction.INERT_ACTION;
	}

	protected GNodeAction getPositiveAction2() {

		return GNodeAction.INERT_ACTION;
	}

	protected GNodeAction getPositiveAction3() {

		return GNodeAction.INERT_ACTION;
	}

	protected GNodeAction getNegativeAction1() {

		return GNodeAction.INERT_ACTION;
	}

	protected GNodeAction getNegativeAction2() {

		return GNodeAction.INERT_ACTION;
	}

	protected GNodeAction getNegativeAction3() {

		return GNodeAction.INERT_ACTION;
	}

	protected abstract GCellDisplay getDisplay();

	protected String entityToString() {

		return getDisplay().getText();
	}

	void initialiseSubTree() {

		getChildList();

		if (autoExpand()) {

			checkExpanded();

			for (GNode child : ensureChildren()) {

				child.initialiseSubTree();
			}

			onChildrenInitialised();
		}
	}

	void reinitialiseSubTree() {

		childList = null;
		initialised = false;

		initialiseSubTree();

		getTreeModel().nodeStructureChanged(this);
	}

	void updateSubTreeNodeDisplays() {

		updateNodeDisplay();

		for (GNode child : getChildren()) {

			child.updateSubTreeNodeDisplays();
		}
	}

	private void removeChild(GNode child, boolean notifyTreeModel) {

		int[] oldIndex = new int[]{getIndex(child)};
		Object[] oldChild = new Object[]{child};

		child.collapse();
		child.parent = null;
		getChildList().performRemoval(child);

		if (notifyTreeModel) {

			getTreeModel().nodesWereRemoved(this, oldIndex, oldChild);
		}

		tree.registerNodeRemoved(child);
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

	private int[] getAllIndicesAsArray() {

		int i = getChildCount();
		int[] indices = new int[i];

		while (--i > 0) {

			indices[i] = i;
		}

		return indices;
	}
}
