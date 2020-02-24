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

import java.awt.BorderLayout;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class DisjunctionFrameSelector extends FrameSelector {

	static private final long serialVersionUID = -1;

	static private final String SELECTIONS_TITLE = "Selected options / de-select";

	private SelectionsList selectionsList = new SelectionsList();

	private class SelectionsList extends GList<CFrame> {

		static private final long serialVersionUID = -1;

		private class Deselector extends GSelectionListener<CFrame> {

			protected void onSelected(CFrame frame) {

				removeEntity(frame);
				setValidSelection(anyElements());
			}

			protected void onDeselected(CFrame frame) {
			}
		}

		SelectionsList() {

			super(false, false);

			addSelectionListener(new Deselector());
		}

		void checkAdd(CFrame newSelection) {

			if (!containsEntity(newSelection)) {

				removeConflicts(newSelection);
				addSelection(newSelection);

				setValidSelection(true);
			}
		}

		private void addSelection(CFrame selection) {

			addEntity(selection, getFrameCellDisplay(selection));
		}

		private void removeConflicts(CFrame newSelection) {

			for (CFrame sel : getEntityList()) {

				if (newSelection.subsumes(sel) || sel.subsumes(newSelection)) {

					removeEntity(sel);
				}
			}
		}
	}

	DisjunctionFrameSelector(
		JComponent parent,
		CFrame rootFrame,
		boolean forQuery,
		boolean clearRequired) {

		super(parent, rootFrame, forQuery, true, clearRequired);
	}

	CFrame getSelection() {

		return CFrame.resolveDisjunction(selectionsList.getEntityList());
	}

	JComponent getInputComponent() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createSelectorPanel(), BorderLayout.CENTER);
		panel.add(createSelectionsPanel(), BorderLayout.SOUTH);

		return panel;
	}

	void onSelection(CFrame selected) {

		selectionsList.checkAdd(selected);
	}

	private JPanel createSelectionsPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		PanelEntitler.entitle(panel, SELECTIONS_TITLE);
		panel.add(new JScrollPane(selectionsList));

		return panel;
	}
}
