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
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class FrameSelector extends Selector<CFrame> {

	static private final long serialVersionUID = -1;

	static private final String FILTER_PANEL_TITLE = "Filter";
	static private final String SINGLE_SELECT_TITLE = "Select option";
	static private final String MULTI_SELECT_TITLE = SINGLE_SELECT_TITLE + "(s)";

	static private final Dimension WINDOW_SIZE = new Dimension(500, 500);

	static private final Color LEXICAL_MATCH_BACKGROUND_CLR = new Color(255,237,160);

	static private String getTitle(boolean multiSelect) {

		return multiSelect ? MULTI_SELECT_TITLE : SINGLE_SELECT_TITLE;
	}

	private CFrame rootFrame;

	private FrameTree tree;

	private FramesFilter inactiveFilter = new InactiveFramesFilter();
	private FramesFilter currentFilter = inactiveFilter;

	private abstract class FramesFilter {

		abstract boolean lexicalMatch(CFrame frame);

		abstract boolean displayInTree(CFrame frame);
	}

	private class InactiveFramesFilter extends FramesFilter {

		boolean lexicalMatch(CFrame frame) {

			return false;
		}

		boolean displayInTree(CFrame frame) {

			return true;
		}
	}

	private class ActiveFramesFilter extends FramesFilter {

		private GLexicalFilter lexicalFilter;
		private Set<CFrame> displayInTrees = new HashSet<CFrame>();

		ActiveFramesFilter(GLexicalFilter lexicalFilter) {

			this.lexicalFilter = lexicalFilter;

			initialiseFromDescendants(rootFrame);
		}

		boolean lexicalMatch(CFrame frame) {

			return lexicalFilter.pass(frame.getIdentity().getLabel());
		}

		boolean displayInTree(CFrame frame) {

			return displayInTrees.contains(frame);
		}

		private boolean initialiseFromDescendants(CFrame current) {

			boolean anyLexicalPasses = false;

			for (CFrame sub : current.getSubs(CVisibility.EXPOSED)) {

				anyLexicalPasses |= initialiseFrom(sub);
			}

			return anyLexicalPasses;
		}

		private boolean initialiseFrom(CFrame current) {

			boolean lexicalMatch = lexicalMatch(current);
			boolean anyDescendantLexicalPasses = initialiseFromDescendants(current);

			if (lexicalMatch(current) || anyDescendantLexicalPasses) {

				displayInTrees.add(current);

				return true;
			}

			return false;
		}
	}

	private class FramesFilterPanel extends GFilterPanel {

		static private final long serialVersionUID = -1;

		protected void applyFilter(GLexicalFilter filter) {

			setFramesFilter(new ActiveFramesFilter(filter));
		}

		protected void clearFilter() {

			setFramesFilter(inactiveFilter);
		}

		FramesFilterPanel() {

			PanelEntitler.entitle(this, FILTER_PANEL_TITLE);
		}

		private void setFramesFilter(FramesFilter filter) {

			currentFilter = filter;

			tree.reinitialise();
		}
	}

	private class FrameTree extends GTree {

		static private final long serialVersionUID = -1;

		private Icon icon;

		private class FrameNode extends GNode {

			final CFrame frame;

			protected void addInitialChildren() {

				for (CFrame subFrame : frame.getSubs()) {

					if (currentFilter.displayInTree(subFrame)) {

						addChild(new FrameNode(subFrame));
					}
				}
			}

			protected GCellDisplay getDisplay() {

				GCellDisplay display = new GCellDisplay(frame.getDisplayLabel(), icon);

				if (currentFilter.lexicalMatch(frame)) {

					display.setBackgroundColour(LEXICAL_MATCH_BACKGROUND_CLR);
				}

				return display;
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

		FrameTree(boolean forQuery, boolean multiSelect) {

			icon = getIcon(forQuery);

			setRootVisible(false);
			setShowsRootHandles(true);

			if (multiSelect) {

				addTreeSelectionListener(new MultiSelectSelectionNotifier());
			}
			else {

				addMouseListener(new SingleSelectDisposer());
			}

			initialise(new FrameNode(rootFrame));
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

		private Icon getIcon(boolean forQuery) {

			return forQuery ? MekonAppIcons.QUERY_VALUE : MekonAppIcons.ASSERTION_VALUE;
		}
	}

	FrameSelector(
		Window rootWindow,
		CFrame rootFrame,
		boolean forQuery,
		boolean multiSelect,
		boolean clearRequired) {

		super(rootWindow, getTitle(multiSelect), multiSelect, clearRequired);

		this.rootFrame = rootFrame;

		tree = new FrameTree(forQuery, multiSelect);
	}

	CFrame getSelection() {

		return CFrame.resolveDisjunction(tree.getSelections());
	}

	JComponent getInputComponent() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new JScrollPane(tree), BorderLayout.CENTER);
		panel.add(new FramesFilterPanel(), BorderLayout.SOUTH);

		return panel;
	}

	Dimension getWindowSize() {

		return WINDOW_SIZE;
	}
}
