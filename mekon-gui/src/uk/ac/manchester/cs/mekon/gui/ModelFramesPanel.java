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

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class ModelFramesPanel extends CFramesComboPanel {

	static private final long serialVersionUID = -1;

	static private final String SUB_TREE_LIST_TITLE_PREFIX = "Sub-Tree";
	static private final int SUB_TREE_LIST_TAB_INDEX = 2;

	private SubTreeListListener subTreeListListener = new SubTreeListListener();

	private class TreeListener extends CFrameSelectionListener {

		protected void onSelected(CFrame frame) {

			updateSubTreeList(frame);
			setSelectedIndex(0);
		}

		TreeListener() {

			getTree().addSelectionListener(this);
		}
	}

	private class SubTreeListListener extends CFrameSelectionListener {

		protected void onSelected(CFrame frame) {

			getTree().select(frame);
		}
	}

	ModelFramesPanel(CModel model) {

		super(model.getRootFrame(), CVisibility.ALL, false);

		addDefaultTree();
		addDefaultList();

		new TreeListener();
	}

	CFrameSelectionRelay getSelectionRelay() {

		return getTree().getSelectionRelay();
	}

	private void updateSubTreeList(CFrame selected) {

		checkRemoveOldSubTreeList();
		checkAddNewSubTreeList(selected);
	}

	private void checkRemoveOldSubTreeList() {

		if (getTabCount() > SUB_TREE_LIST_TAB_INDEX) {

			removeTabAt(SUB_TREE_LIST_TAB_INDEX);
		}
	}

	private void checkAddNewSubTreeList(CFrame selected) {

		if (!selected.getSubs().isEmpty()) {

			addSubTreeList(selected);
		}
	}

	private void addSubTreeList(CFrame selected) {

		CFramesList list = new CFramesList(selected, CVisibility.ALL, false);

		addList(SUB_TREE_LIST_TITLE_PREFIX, list);

		list.addSelectionListener(subTreeListListener);
	}
}
