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

import java.util.*;
import java.awt.event.*;

import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * @author Colin Puleston
 */
public class GSelectorTree extends GTree {

	static private final long serialVersionUID = -1;

	private GSelectionListeners<GNode> nodeSelectionListeners = new GSelectionListeners<GNode>();

	private class NodeSelectionListenerInvoker implements TreeSelectionListener {

		public void valueChanged(TreeSelectionEvent event) {

			GNode node = extractLeafNode(event);

			if (event.isAddedPath()) {

				nodeSelectionListeners.pollForSelected(node);
			}
			else {

				nodeSelectionListeners.pollForDeselected(node);
			}
		}

		private GNode extractLeafNode(TreeSelectionEvent event) {

			return (GNode)event.getPath().getLastPathComponent();
		}
	}

	private class DeselectionClickMonitor extends MouseAdapter {

		public void mouseClicked(MouseEvent event) {

			clearSelection();
		}
	}

	public GSelectorTree(boolean multiSelect) {

		super(multiSelect);

		addTreeSelectionListener(new NodeSelectionListenerInvoker());
	}

	public void setNonVisibleSelection() {

		addMouseListener(new DeselectionClickMonitor());
	}

	public void addNodeSelectionListener(GSelectionListener<GNode> nodeSelectionListener) {

		nodeSelectionListeners.add(nodeSelectionListener);
	}

	public GNode getSelectedNode() {

		return (GNode)getLastSelectedPathComponent();
	}

	public List<GNode> getAllSelectedNodes() {

		List<GNode> nodes = new ArrayList<GNode>();

		for (TreePath path : getSelectionModel().getSelectionPaths()) {

			nodes.add((GNode)path.getLastPathComponent());
		}

		return nodes;
	}
}
