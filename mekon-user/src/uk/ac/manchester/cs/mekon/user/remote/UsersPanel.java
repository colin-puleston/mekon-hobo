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
	static private final String UNREGISTERED_STATUS_LABEL_FORMAT = "Unregistered (%s)";

	static private final String ADD_BUTTON_LABEL = "Add...";
	static private final String EDIT_BUTTON_LABEL = "Edit...";
	static private final String DELETE_BUTTON_LABEL = "Delete";

	static private final Color USER_NAME_TEXT_CLR = Color.BLUE;
	static private final Color ROLE_NAME_TEXT_CLR = Color.CYAN.darker();
	static private final Color REGISTERED_STATUS_TEXT_CLR = Color.GREEN.darker();
	static private final Color UNREGISTERED_STATUS_TEXT_CLR = Color.RED;

	static private final Color SELECTED_ROW_CLR = Color.LIGHT_GRAY;

	private RAdminClient adminClient;
	private List<String> roleNames;

	private GTable table = new GTable();
	private int selectedRow = -1;

	private List<UserHandler> userHandlers = new ArrayList<UserHandler>();

	private class UserHandler implements Comparable<UserHandler> {

		private String name;
		private String role;
		private String regToken;

		public int compareTo(UserHandler other) {

			return name.compareTo(other.name);
		}

		UserHandler(RUserProfile profile) {

			this(profile.getName(), profile.getRoleName(), getRegTokenText(profile));
		}

		UserHandler(String name, String role, String regToken) {

			this.name = name;
			this.role = role;
			this.regToken = regToken;
		}

		void addToTable() {

			table.addRow(createNameLabel(), createRoleLabel(), createStatusLabel());
		}

		void editUser() {
		}

		void deleteUser() {

			if (editUsers(RUserEdit.removal(name)).editOk()) {

				userHandlers.remove(this);
				updateTable();
			}
		}

		private JLabel createNameLabel() {

			return createLabel(name, USER_NAME_TEXT_CLR);
		}

		private JLabel createRoleLabel() {

			return createLabel(role, ROLE_NAME_TEXT_CLR);
		}

		private JLabel createStatusLabel() {

			if (registered()) {

				return createLabel(REGISTERED_STATUS_LABEL, REGISTERED_STATUS_TEXT_CLR);
			}

			return createLabel(getUnregisteredStatusText(), UNREGISTERED_STATUS_TEXT_CLR);
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

		private String getUnregisteredStatusText() {

			return String.format(UNREGISTERED_STATUS_LABEL_FORMAT, regToken);
		}

		private boolean registered() {

			return regToken.length() == 0;
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

			addUser();
		}

		AddButton() {

			super(ADD_BUTTON_LABEL);
		}
	}

	private class EditButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			getSelectedUserHandler().editUser();
		}

		EditButton() {

			super(EDIT_BUTTON_LABEL);
		}
	}

	private class DeleteButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			getSelectedUserHandler().deleteUser();
		}

		DeleteButton() {

			super(DELETE_BUTTON_LABEL);
		}
	}

	UsersPanel(RAdminClient adminClient) {

		super(new BorderLayout());

		this.adminClient = adminClient;

		roleNames = adminClient.getRoleNames();

		add(new JScrollPane(table), BorderLayout.CENTER);
		add(createButtonsPanel(), BorderLayout.SOUTH);

		table.addColumns(USERS_TITLE, ROLES_TITLE, REG_STATUS_TITLE);

		updateFromServer();

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

	private void addUser() {

		UserDetailsDialog dialog = displayDetailsDialog(true);

		if (dialog.detailsOk()) {

			String name = dialog.getUserName();
			String role = dialog.getRoleName();

			RUserEditResult result = editUsers(RUserEdit.addition(name, role));

			if (result.editOk()) {

				String regToken = result.getRegistrationToken();

				addUserHandler(new UserHandler(name, role, regToken));
			}
		}
	}

	private RUserEditResult editUsers(RUserEdit edit) {

		RUserEditResult result = adminClient.editUsers(edit);

		if (!result.editOk()) {

			showMessage("Operation failed: " + result.getResultType());
		}

		return result;
	}

	private void updateFromServer() {

		for (RUserProfile profile : adminClient.getUserProfiles()) {

			userHandlers.add(new UserHandler(profile));
		}

		sortUserHandlers();
		updateTable();
	}

	private void addUserHandler(UserHandler handler) {

		userHandlers.add(handler);

		sortUserHandlers();
		updateTable();
	}

	private void sortUserHandlers() {

		TreeSet<UserHandler> sorter = new TreeSet<UserHandler>(userHandlers);

		userHandlers.clear();
		userHandlers.addAll(sorter);
	}

	private void updateTable() {

		table.removeAllRows();

		for (UserHandler userHandler : userHandlers) {

			userHandler.addToTable();
		}
	}

	private UserDetailsDialog displayDetailsDialog(boolean newUser) {

		return new UserDetailsDialog(this, roleNames, newUser);
	}

	private String getRegTokenText(RUserProfile profile) {

		return profile.registered() ? "" : profile.getRegistrationToken();
	}

	private UserHandler getSelectedUserHandler() {

		return userHandlers.get(selectedRow);
	}

	private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}
}
