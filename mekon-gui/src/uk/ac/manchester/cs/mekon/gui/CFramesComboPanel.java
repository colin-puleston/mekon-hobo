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

	private CFrame rootFrame;
	private CVisibility visibility;
	private boolean showRoot;

	private CFramesTree tree;

	private class ListPanelFocuser implements ChangeListener {

		private GListPanel<?> panel;

		public void stateChanged(ChangeEvent e) {

			if (getSelectedComponent() == panel) {

				panel.setFocusToFilterField();
			}
		}

		ListPanelFocuser(GListPanel<?> panel) {

			this.panel = panel;

			addChangeListener(this);
		}
	}

	CFramesComboPanel(CFrame rootFrame, CVisibility visibility, boolean showRoot) {

		this.rootFrame = rootFrame;
		this.visibility = visibility;
		this.showRoot = showRoot;
	}

	void addDefaultTree() {

		addTree(createDefaultTree());
	}

	void addTree(CFramesTree tree) {

		this.tree = tree;

		addTab(TREE_TITLE, new JScrollPane(tree));
	}

	CFramesList addDefaultList() {

		return addDefaultList("");
	}

	CFramesList addDefaultList(String titlePrefix) {

		CFramesList list = new CFramesList(rootFrame, visibility, showRoot);
		CFrameSelectionSynchroniser synchroniser = new CFrameSelectionSynchroniser();

		addList(titlePrefix, list);

		synchroniser.add(tree.getSelectionRelay());
		synchroniser.add(list.getSelectionRelay());

		return list;
	}

	<E>void addList(String titlePrefix, GList<E> list) {

		GListPanel<E> panel = new GListPanel<E>(list);

		addTab(getListTitle(titlePrefix), panel);
		new ListPanelFocuser(panel);
	}

	void addSelectionListener(CFrameSelectionListener listener) {

		tree.addSelectionListener(listener);
	}

	CFramesTree getTree() {

		return tree;
	}

	private CFramesTree createDefaultTree() {

		CFramesTree tree = new CFramesTree(visibility);

		tree.initialise(rootFrame, showRoot);

		return tree;
	}

	private String getListTitle(String prefix) {

		if (prefix.length() > 0) {

			prefix += " ";
		}

		return prefix + LIST_TITLE;
	}
}
