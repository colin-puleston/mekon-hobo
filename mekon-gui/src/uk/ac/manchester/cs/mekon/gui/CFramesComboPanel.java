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
import javax.swing.event.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class CFramesComboPanel extends JTabbedPane {

	static private final long serialVersionUID = -1;

	static private final String TREE_TITLE = "Tree";
	static private final String LIST_TITLE = "List/Search";
	static private final String SUB_TREE_LIST_TITLE = "Sub-Tree List/Search";

	static private final int SUB_TREE_LIST_TAB_INDEX = 2;

	private CVisibility visibility;
	private CFramesTree tree;

	private class SubTreeListManager {

		private SubTreeListListener subTreeListListener = new SubTreeListListener();

		private class TreeListener extends CFrameSelectionListener {

			protected void onSelected(CFrame frame) {

				select(frame);
			}
		}

		private class SubTreeListListener extends CFrameSelectionListener {

			protected void onSelected(CFrame frame) {

				tree.select(frame);
			}
		}

		SubTreeListManager() {

			tree.addSelectionListener(new TreeListener());
		}

		private void select(CFrame frame) {

			int index = getSelectedIndex();

			checkRemoveOldSubTreeList();
			checkAddNewSubTreeList(frame);

			if (getTabCount() > index) {

				setSelectedIndex(index);
			}
		}

		private void checkRemoveOldSubTreeList() {

			if (getTabCount() > SUB_TREE_LIST_TAB_INDEX) {

				removeTabAt(SUB_TREE_LIST_TAB_INDEX);
			}
		}

		private void checkAddNewSubTreeList(CFrame frame) {

			if (!frame.getSubs(visibility).isEmpty()) {

				CFramesList subTreeList = addList(SUB_TREE_LIST_TITLE, frame, false);

				subTreeList.addSelectionListener(subTreeListListener);
			}
		}
	}

	private class TreeTabReselector extends CFrameSelectionListener {

		protected void onSelected(CFrame frame) {

			setSelectedIndex(0);
		}

		TreeTabReselector() {

			tree.addSelectionListener(this);
		}
	}

	private class ListPanelFocuser implements ChangeListener {

		private GListPanel<CFrame> panel;

		public void stateChanged(ChangeEvent e) {

			if (getSelectedComponent() == panel) {

				panel.setFocusToFilterField();
			}
		}

		ListPanelFocuser(GListPanel<CFrame> panel) {

			this.panel = panel;

			addChangeListener(this);
		}
	}

	CFramesComboPanel(CFrame rootFrame, CVisibility visibility, boolean showRoot) {

		this.visibility = visibility;

		tree = addTree(rootFrame, showRoot);

		CFramesList fullList = addList(LIST_TITLE, rootFrame, showRoot);
		CFrameSelectionSynchroniser synchroniser = new CFrameSelectionSynchroniser();

		synchroniser.add(tree.getSelectionRelay());
		synchroniser.add(fullList.getSelectionRelay());

		new TreeTabReselector();
	}

	void enableSubTreeList() {

		new SubTreeListManager();
	}

	void addSelectionListener(CFrameSelectionListener listener) {

		tree.addSelectionListener(listener);
	}

	CFramesTree getTree() {

		return tree;
	}

	private CFramesTree addTree(CFrame rootFrame, boolean showRoot) {

		CFramesTree tree = new CFramesTree(rootFrame, visibility, showRoot);

		addTab(TREE_TITLE, new JScrollPane(tree));

		return tree;
	}

	private CFramesList addList(String title, CFrame rootFrame, boolean showRoot) {

		CFramesList list = new CFramesList(rootFrame, visibility, showRoot);
		GListPanel<CFrame> panel = new GListPanel<CFrame>(list);

		addTab(title, panel);
		new ListPanelFocuser(panel);

		return list;
	}
}
