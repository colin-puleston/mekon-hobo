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

package uk.ac.manchester.cs.mekon.explorer;

import javax.swing.tree.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
abstract class CTree extends GSelectorTree {

	static private final long serialVersionUID = -1;

	private CValueNodeCreator valueNodeCreator = new CValueNodeCreator(this);
	private CFrameSelectionListeners selectionListeners = new CFrameSelectionListeners();

	private class NodeSelectionListener extends GSelectionListener<GNode> {

		protected void onSelected(GNode node) {

			onSelectedNode(node);
		}

		protected void onDeselected(GNode node) {
		}
	}

	CTree() {

		super(false);

		addNodeSelectionListener(new NodeSelectionListener());
	}

	CFrameNode initialise(CFrame rootFrame) {

		CFrameNode rootNode = createCFrameNode(rootFrame);

		initialise(rootNode);

		return rootNode;
	}

	void addSelectionListener(CFrameSelectionListener selectionListener) {

		selectionListeners.add(selectionListener);
	}

	CFrameNode createCFrameNode(CFrame frame) {

		return new CFrameNode(this, frame);
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

	private void onSelectedNode(GNode selectedNode) {

		CFrame selectedCFrame = extractCFrame(selectedNode);

		if (selectedCFrame != null) {

			selectionListeners.pollForSelected(selectedCFrame);
		}
		else {

			setSelectionPath(null);
		}
	}

	private CFrame extractCFrame(GNode selectedNode) {

		if (selectedNode instanceof CFrameNode) {

			return ((CFrameNode)selectedNode).getCFrame();
		}

		if (selectedNode instanceof MFrameNode) {

			return ((MFrameNode)selectedNode).getMFrame().getRootCFrame();
		}

		return null;
	}
}
