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
abstract class DisjunctionEntitySelector<E> extends EntitySelector<E> {

	static private final long serialVersionUID = -1;

	static private final String SELECTIONS_TITLE = "Selected options / de-select";

	private SelectionsList selectionsList = new SelectionsList();

	private class SelectionsList extends GList<E> {

		static private final long serialVersionUID = -1;

		private class Deselector extends GSelectionListener<E> {

			protected void onSelected(E frame) {

				removeEntity(frame);
				setValidInput(anyElements());
			}

			protected void onDeselected(E frame) {
			}
		}

		SelectionsList() {

			super(false, false);

			addSelectionListener(new Deselector());
		}

		void checkAdd(E newSelection) {

			if (!containsEntity(newSelection)) {

				removeSelectionConflicts(this, newSelection);
				addSelection(newSelection);

				setValidInput(true);
			}
		}

		private void addSelection(E selection) {

			addEntity(selection, getEntityCellDisplay(selection));
		}
	}

	protected JComponent getInputComponent() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createOptionsComponent(), BorderLayout.CENTER);
		panel.add(createSelectionsPanel(), BorderLayout.SOUTH);

		return panel;
	}

	DisjunctionEntitySelector(JComponent parent, String typeName, boolean canClear) {

		super(parent, typeName, true, canClear);
	}

	void onSelectedOption(E selected) {

		selectionsList.checkAdd(selected);
	}

	abstract GCellDisplay getEntityCellDisplay(E entity);

	abstract void removeSelectionConflicts(GList<E> selectionsList, E newSelection);

	List<E> getDisjunctSelections() {

		return selectionsList.getEntities();
	}

	private JPanel createSelectionsPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		PanelEntitler.entitle(panel, SELECTIONS_TITLE);
		panel.add(new JScrollPane(selectionsList));

		return panel;
	}
}
