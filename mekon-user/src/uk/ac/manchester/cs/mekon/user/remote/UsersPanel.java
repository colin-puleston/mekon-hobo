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
class UsersPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String USERS_TITLE = "User";
	static private final String ROLES_TITLE = "Role";
	static private final String REG_STATUS_TITLE = "Status (Registration Token)";

	static private final String REGISTERED_STATUS_LABEL = "Registered";
	static private final String UNREGISTERED_STATUS_LABEL_FORMAT = "Not Registered (%s)";

	static private final String ADD_BUTTON_LABEL = "Add...";
	static private final String EDIT_BUTTON_LABEL = "Edit...";
	static private final String DELETE_BUTTON_LABEL = "Delete";

	static private final Color USER_NAME_TEXT_CLR = Color.BLUE;
	static private final Color ROLE_NAME_TEXT_CLR = Color.RED;
	static private final Color REGISTERED_STATUS_TEXT_CLR = Color.GREEN.darker();
	static private final Color UNREGISTERED_STATUS_TEXT_CLR = Color.ORANGE.darker();

	static private final Color SELECTED_ROW_CLR = Color.LIGHT_GRAY;

	private GTable table = new GTable();
	private int selectedRow = -1;

	private UserManager userManager;

	private List<SelectionDependentButton> selectionDependentButtons
								= new ArrayList<SelectionDependentButton>();

	private class UserManagerLocal extends UserManager {

		UserManagerLocal(RAdminClient adminClient) {

			super(adminClient, new UserUpdates(UsersPanel.this, adminClient));
		}

		void onUpdate() {

			updateTable();
		}
	}

	private class TableRow {

		private RUserProfile profile;

		TableRow(RUserProfile profile) {

			this.profile = profile;

			table.addRow(createNameLabel(), createRoleLabel(), createStatusLabel());
		}

		private JLabel createNameLabel() {

			return createLabel(profile.getName(), USER_NAME_TEXT_CLR);
		}

		private JLabel createRoleLabel() {

			return createLabel(profile.getRoleName(), ROLE_NAME_TEXT_CLR);
		}

		private JLabel createStatusLabel() {

			if (profile.registered()) {

				return createLabel(REGISTERED_STATUS_LABEL, REGISTERED_STATUS_TEXT_CLR);
			}

			return createLabel(getUnregisteredStatusText(), UNREGISTERED_STATUS_TEXT_CLR);
		}

		private String getUnregisteredStatusText() {

			String regToken = profile.getRegistrationToken();

			return String.format(UNREGISTERED_STATUS_LABEL_FORMAT, regToken);
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
					updateTable();
					processingUpdate = false;
				}
			}
		}

		RowSelectionListener() {

			table.getSelectionModel().addListSelectionListener(this);
		}
	}

	private class AddButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			userManager.addUser();
		}

		AddButton() {

			super(ADD_BUTTON_LABEL);
		}
	}

	private abstract class SelectionDependentButton extends GButton {

		static private final long serialVersionUID = -1;

		SelectionDependentButton(String title) {

			super(title);

			selectionDependentButtons.add(this);

			setEnabled(false);
		}

		void updateEnabling() {

			setEnabled(selectedRow != -1);
		}
	}

	private class EditButton extends SelectionDependentButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			userManager.editUser(selectedRow);
		}

		EditButton() {

			super(EDIT_BUTTON_LABEL);
		}
	}

	private class DeleteButton extends SelectionDependentButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			userManager.deleteUser(selectedRow);
		}

		DeleteButton() {

			super(DELETE_BUTTON_LABEL);
		}
	}

	UsersPanel(RAdminClient adminClient) {

		super(new BorderLayout());

		userManager = new UserManagerLocal(adminClient);

		add(new JScrollPane(table), BorderLayout.CENTER);
		add(createButtonsPanel(), BorderLayout.SOUTH);

		table.addColumns(USERS_TITLE, ROLES_TITLE, REG_STATUS_TITLE);

		userManager.updateFromServer();

		new RowSelectionListener();
	}

	private JPanel createButtonsPanel() {

		JPanel panel = new JPanel();

		panel.add(new AddButton());
		panel.add(Box.createHorizontalStrut(10));
		panel.add(new EditButton());
		panel.add(Box.createHorizontalStrut(10));
		panel.add(new DeleteButton());

		return panel;
	}

	private void updateTable() {

		table.removeAllRows();

		for (RUserProfile profile : userManager.getProfiles()) {

			new TableRow(profile);
		}

		updateButtonEnabling();
	}

	private void updateButtonEnabling() {

		for (SelectionDependentButton button : selectionDependentButtons) {

			button.updateEnabling();
		}
	}

	private JLabel createLabel(String text, Color clr) {

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
}
