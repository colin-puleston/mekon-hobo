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
import java.awt.BorderLayout;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class DisjunctionInstanceRefSelector extends InstanceRefSelector {

	static private final long serialVersionUID = -1;

	static private final String SELECTIONS_TITLE = "Selected options / de-select";

	private SelectionsList selectedIdsList = new SelectionsList();

	private class SelectionsList extends GList<CIdentity> {

		static private final long serialVersionUID = -1;

		private class Deselector extends GSelectionListener<CIdentity> {

			protected void onSelected(CIdentity refId) {

				removeEntity(refId);
				setValidSelection(anyElements());
			}

			protected void onDeselected(CIdentity refId) {
			}
		}

		SelectionsList() {

			super(false, false);

			addSelectionListener(new Deselector());
		}

		void checkAdd(CIdentity newSelection) {

			if (!containsEntity(newSelection)) {

				addSelection(newSelection);

				setValidSelection(true);
			}
		}

		private void addSelection(CIdentity selection) {

			addEntity(selection, getRefCellDisplay(selection));
		}
	}

	DisjunctionInstanceRefSelector(
		JComponent parent,
		Instantiator instantiator,
		CFrame type,
		boolean clearRequired) {

		super(parent, instantiator, type, true, clearRequired);
	}

	IFrame getSelection() {

		List<IFrame> refs = selectedIdsToRefs();

		if (refs.isEmpty()) {

			throw new Error("No current selections!");
		}

		return refs.size() == 1 ? refs.get(0) : IFrame.createDisjunction(refs);
	}

	JComponent getInputComponent() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createSelectorPanel(), BorderLayout.CENTER);
		panel.add(createSelectionsPanel(), BorderLayout.SOUTH);

		return panel;
	}

	void onSelection(CIdentity selectedRefId) {

		selectedIdsList.checkAdd(selectedRefId);
	}

	private JPanel createSelectionsPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		PanelEntitler.entitle(panel, SELECTIONS_TITLE);
		panel.add(new JScrollPane(selectedIdsList));

		return panel;
	}

	private List<IFrame> selectedIdsToRefs() {

		List<IFrame> refs = new ArrayList<IFrame>();

		for (CIdentity refId : selectedIdsList.getEntityList()) {

			refs.add(createRef(refId));
		}

		return refs;
	}
}
