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

import javax.swing.event.*;
import javax.swing.tree.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
abstract class CTree extends GTree {

	static private final long serialVersionUID = -1;

	private CValueNodeCreator valueNodeCreator = new CValueNodeCreator();
	private CFrameSelectionListeners selectionListeners = new CFrameSelectionListeners();

	private class CValueNodeCreator extends CValueVisitor {

		private GNode created = null;

		protected void visit(CFrame value) {

			created = new CFrameNode(CTree.this, value);
		}

		protected void visit(CNumber value) {

			created = new CNumberNode(CTree.this, value);
		}

		protected void visit(MFrame value) {

			created = new MFrameNode(CTree.this, value);
		}

		GNode create(CValue<?> value) {

			visit(value);

			return created;
		}
	}

	private class SelectionListener implements TreeSelectionListener {

		public void valueChanged(TreeSelectionEvent event) {

			onSelected(event.getPath());
		}
	}

	CTree() {

		addTreeSelectionListener(new SelectionListener());
	}

	CFrameNode initialise(CFrame rootFrame) {

		CFrameNode rootNode = new CFrameNode(this, rootFrame);

		initialise(rootNode);

		return rootNode;
	}

	void addSelectionListener(CFrameSelectionListener selectionListener) {

		selectionListeners.add(selectionListener);
	}

	Boolean leafCFrameNodeFastCheck(CFrameNode node) {

		return null;
	}

	abstract void addCFrameChildren(CFrameNode parent);

	GNode createCValueNode(CValue<?> value) {

		return valueNodeCreator.create(value);
	}

	boolean requiredCValue(CValue<?> value) {

		return true;
	}

	int autoExpandCFrameNodesToLevel() {

		return 1;
	}

	private void onSelected(TreePath path) {

		CFrame selectedCFrame = extractCFrame(path.getLastPathComponent());

		if (selectedCFrame != null) {

			selectionListeners.poll(selectedCFrame);
		}
		else {

			setSelectionPath(null);
		}
	}

	private CFrame extractCFrame(Object selectedNode) {

		if (selectedNode instanceof CFrameNode) {

			return ((CFrameNode)selectedNode).getCFrame();
		}

		if (selectedNode instanceof MFrameNode) {

			return ((MFrameNode)selectedNode).getMFrame().getRootCFrame();
		}

		return null;
	}
}
