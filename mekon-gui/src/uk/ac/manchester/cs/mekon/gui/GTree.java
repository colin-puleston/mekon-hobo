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

import javax.swing.*;
import javax.swing.tree.*;

/**
 * @author Colin Puleston
 */
public abstract class GTree extends JTree {

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

	private int getSelectionMode(boolean multiSelect) {

		return multiSelect
				? TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
				: TreeSelectionModel.SINGLE_TREE_SELECTION;
	}
}
