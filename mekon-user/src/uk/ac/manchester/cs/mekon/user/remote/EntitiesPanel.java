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

package uk.ac.manchester.cs.mekon.user.remote;

import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import uk.ac.manchester.cs.mekon_util.remote.admin.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.client.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
abstract class EntitiesPanel<E> extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String ADD_BUTTON_LABEL = "Add...";
	static private final String EDIT_BUTTON_LABEL = "Edit...";
	static private final String DELETE_BUTTON_LABEL = "Delete";

	static private final Color SELECTED_ROW_CLR = Color.LIGHT_GRAY;

	private GTable table = new GTable();
	private int selectedRow = -1;

	private EntityManager<E> entityManager;

	private List<SelectionDependentButton> selectionDependentButtons
								= new ArrayList<SelectionDependentButton>();

	abstract class SelectionDependentButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			performEntityAction(getSelectedEntity());
		}

		SelectionDependentButton(String title) {

			super(title);

			selectionDependentButtons.add(this);

			setEnabled(false);
		}

		void updateEnabling() {

			setEnabled(enableButton());
		}

		boolean enableButton() {

			return selectedRow != -1 && enableButtonOnSelection(getSelectedEntity());
		}

		boolean enableButtonOnSelection(E entity) {

			return true;
		}

		abstract void performEntityAction(E entity);
	}

	class AddButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			entityManager.addEntity();
		}

		AddButton() {

			super(ADD_BUTTON_LABEL);
		}
	}

	class EditButton extends SelectionDependentButton {

		static private final long serialVersionUID = -1;

		EditButton() {

			super(EDIT_BUTTON_LABEL);
		}

		void performEntityAction(E entity) {

			entityManager.editEntity(entity);
		}
	}

	class DeleteButton extends SelectionDependentButton {

		static private final long serialVersionUID = -1;

		DeleteButton() {

			super(DELETE_BUTTON_LABEL);
		}

		void performEntityAction(E entity) {

			entityManager.deleteEntity(entity);
		}
	}

	private class RowSelectionListener implements ListSelectionListener {

		private boolean processingUpdate = false;

		public void valueChanged(ListSelectionEvent e) {

			if (!e.getValueIsAdjusting() && !processingUpdate) {

				int wasSelectedRow = selectedRow;

				selectedRow = table.getSelectedRow();

				if (selectedRow != wasSelectedRow) {

					processingUpdate = true;
					updateDisplay();
					processingUpdate = false;
				}
			}
		}

		RowSelectionListener() {

			table.getSelectionModel().addListSelectionListener(this);
		}
	}

	EntitiesPanel() {

		super(new BorderLayout());
	}

	void initialise(EntityManager<E> entityManager) {

		this.entityManager = entityManager;

		add(new JScrollPane(table), BorderLayout.CENTER);
		add(createButtonsPanel(), BorderLayout.SOUTH);

		addTableColumns(table);

		entityManager.updateFromServer();

		new RowSelectionListener();
	}

	synchronized void updateDisplay() {

		updateTable();
		updateButtonEnabling();
	}

	abstract void addTableColumns(GTable table);

	abstract void addTableRow(GTable table, E entity);

	abstract void populateUpdateButtonsPanel(JPanel panel);

	abstract void populateCustomButtonsPanel(JPanel panel);

	JLabel createTableLabel(String text, Color clr) {

		JLabel label = new JLabel(text);

		GFonts.setLarge(label);

		label.setForeground(clr);
		label.setFont(label.getFont().deriveFont(Font.BOLD));

		if (table.getRowCount() == selectedRow) {

			label.setOpaque(true);
			label.setBackground(SELECTED_ROW_CLR);
		}

		return label;
	}

	private JPanel createButtonsPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createUpdateButtonsPanel(), BorderLayout.WEST);
		panel.add(createCustomButtonsPanel(), BorderLayout.EAST);

		return panel;
	}

	private JPanel createUpdateButtonsPanel() {

		JPanel panel = new JPanel();

		populateUpdateButtonsPanel(panel);

		return panel;
	}

	private JPanel createCustomButtonsPanel() {

		JPanel panel = new JPanel();

		populateCustomButtonsPanel(panel);

		return panel;
	}

	private void updateTable() {

		table.removeAllRows();

		for (E entity : entityManager.getEntities()) {

			addTableRow(table, entity);
		}
	}

	private void updateButtonEnabling() {

		for (SelectionDependentButton button : selectionDependentButtons) {

			button.updateEnabling();
		}
	}

	private E getSelectedEntity() {

		return entityManager.getEntity(selectedRow);
	}
}
