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
public class GTree extends JTree {

	static private final long serialVersionUID = -1;

	private DefaultTreeModel treeModel = null;
	private GNode rootNode = null;

	public GTree(boolean multiSelect) {

		getSelectionModel().setSelectionMode(getSelectionMode(multiSelect));
	}

	public void initialise(GNode rootNode) {

		this.rootNode = rootNode;

		treeModel = new DefaultTreeModel(rootNode);

		setModel(treeModel);
		GCellRenderers.get().set(this);

		rootNode.initialiseSubTree();
	}

	public void reinitialise() {

		rootNode.reinitialiseSubTree();
	}

	public void expandAll() {

		rootNode.expandAll();
	}

	public void collapseAll() {

		rootNode.collapseAll();
	}

	public void selectAll(Collection<? extends GNode> nodes) {

		if (nodes.isEmpty()) {

			setSelectionPath(null);
		}
		else {

			setSelectionPaths(getTreePaths(nodes));
			scrollRowToVisible(getSelectionRows()[0]);
		}
	}

	public void reselectSelected() {

		GNode selected = getSelectedNode();

		if (selected != null) {

			setSelectionPath(null);
			selected.select();
		}
	}

	public void updateAllNodeDisplays() {

		rootNode.updateSubTreeNodeDisplays();
	}

	public boolean multiSelect() {

		return getSelectionModel().getSelectionMode()
				== TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION;
	}

	public GNode getRootNode() {

		return rootNode;
	}

	public boolean isEmptyTree() {

		return rootNode == null || rootNode.getChildCount() == 0;
	}

	public GNode getSelectedNode() {

		return (GNode)getLastSelectedPathComponent();
	}

	public List<GNode> getSelectedNodes() {

		List<GNode> nodes = new ArrayList<GNode>();

		for (TreePath path : getSelectionModel().getSelectionPaths()) {

			nodes.add((GNode)path.getLastPathComponent());
		}

		return nodes;
	}

	DefaultTreeModel getTreeModel() {

		return treeModel;
	}

	private TreePath[] getTreePaths(Collection<? extends GNode> nodes) {

		TreePath[] paths = new TreePath[nodes.size()];
		int i = 0;

		for (GNode node : nodes) {

			paths[i++] = node.getTreePath();
		}

		return paths;
	}

	private int getSelectionMode(boolean multiSelect) {

		return multiSelect
				? TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
				: TreeSelectionModel.SINGLE_TREE_SELECTION;
	}
}
