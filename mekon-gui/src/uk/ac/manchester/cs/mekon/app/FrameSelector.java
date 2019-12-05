/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.app;

import java.util.*;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class FrameSelector extends Selector<CFrame> {

	static private final long serialVersionUID = -1;

	static private final String SINGLE_SELECT_TITLE = "Select option";
	static private final String MULTI_SELECT_TITLE = SINGLE_SELECT_TITLE + "(s)";

	static private final Dimension WINDOW_SIZE = new Dimension(300, 300);

	static private String getTitle(boolean multiSelect) {

		return multiSelect ? MULTI_SELECT_TITLE : SINGLE_SELECT_TITLE;
	}

	private boolean multiSelect;
	private FrameTree tree;

	private class FrameTree extends GTree {

		static private final long serialVersionUID = -1;

		private class FrameNode extends GNode {

			final CFrame frame;

			protected void addInitialChildren() {

				for (CFrame subFrame : frame.getSubs()) {

					addChild(new FrameNode(subFrame));
				}
			}

			protected GCellDisplay getDisplay() {

				return new GCellDisplay(frame.getDisplayLabel());
			}

			FrameNode(CFrame frame) {

				super(FrameTree.this);

				this.frame = frame;
			}
		}

		private class SingleSelectDisposer extends MouseAdapter {

			public void mouseReleased(MouseEvent event) {

				if (getSelectionCount() != 0) {

					setCompletedSelection();
					dispose();
				}
			}
		}

		private class MultiSelectSelectionNotifier implements TreeSelectionListener {

			public void valueChanged(TreeSelectionEvent event) {

				setValidSelection(true);
			}
		}

		FrameTree(CFrame rootFrame) {

			setRootVisible(false);
			setShowsRootHandles(true);

			initialise(new FrameNode(rootFrame));

			if (multiSelect) {

				addTreeSelectionListener(new MultiSelectSelectionNotifier());
			}
			else {

				addMouseListener(new SingleSelectDisposer());
			}
		}

		List<CFrame> getSelections() {

			List<CFrame> frames = new ArrayList<CFrame>();
			TreePath[] paths = getSelectionPaths();

			if (paths != null) {

				for (TreePath path : paths) {

					frames.add(getFrame(path));
				}
			}

			return frames;
		}

		private CFrame getFrame(TreePath path) {

			return ((FrameNode)path.getLastPathComponent()).frame;
		}
	}

	FrameSelector(Window rootWindow, CFrame rootFrame, boolean multiSelect) {

		super(rootWindow, getTitle(multiSelect));

		this.multiSelect = multiSelect;

		tree = new FrameTree(rootFrame);
	}

	CFrame getSelection() {

		return CFrame.resolveDisjunction(tree.getSelections());
	}

	JComponent getInputComponent() {

		return new JScrollPane(tree);
	}

	Dimension getWindowSize() {

		return WINDOW_SIZE;
	}

	boolean okRequired() {

		return multiSelect;
	}
}
