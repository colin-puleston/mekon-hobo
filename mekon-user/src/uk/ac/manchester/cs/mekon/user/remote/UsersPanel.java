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
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.remote.admin.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.client.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class UsersPanel extends EntitiesPanel<RUserProfile> {

	static private final long serialVersionUID = -1;

	static private final String USERS_TITLE = "User";
	static private final String ROLES_TITLE = "Role";
	static private final String REG_STATUS_TITLE = "Status";

	static private final String REGISTERED_LABEL = "Registered";
	static private final String UNREGISTERED_LABEL = "Not Registered";

	static private final String REG_INFO_BUTTON_LABEL = "Registration Info...";

	static private final Color USER_NAME_TEXT_CLR = Color.BLUE;
	static private final Color ROLE_NAME_TEXT_CLR = Color.RED;
	static private final Color REGISTERED_TEXT_CLR = Color.GREEN.darker();
	static private final Color UNREGISTERED_TEXT_CLR = Color.ORANGE.darker();

	private UserManager userManager;

	private class RegistrationInfoButton extends SelectionDependentButton {

		static private final long serialVersionUID = -1;

		RegistrationInfoButton() {

			super(REG_INFO_BUTTON_LABEL);
		}

		boolean enableButtonOnSelection(RUserProfile entity) {

			return entity.unregistered();
		}

		void performEntityAction(RUserProfile entity) {

			showRegistrationInfo(entity);
		}
	}

	UsersPanel(RAdminClient adminClient) {

		userManager = new UserManager(this, adminClient);

		initialise(userManager);
	}

	JPanel createUpdateButtonsPanel() {

		JPanel panel = new JPanel();

		panel.add(new AddButton());
		panel.add(Box.createHorizontalStrut(10));
		panel.add(new EditButton());
		panel.add(Box.createHorizontalStrut(10));
		panel.add(new DeleteButton());

		return panel;
	}

	JPanel checkCreateCustomButtonsPanel() {

		JPanel panel = new JPanel();

		panel.add(new RegistrationInfoButton());

		return panel;
	}

	void addTableColumns(GTable table) {

		table.addColumns(USERS_TITLE, ROLES_TITLE, REG_STATUS_TITLE);
	}

	void addTableRow(GTable table, RUserProfile profile) {

		table.addRow(
			createNameLabel(profile),
			createRoleLabel(profile),
			createStatusLabel(profile));
	}

	private JLabel createNameLabel(RUserProfile profile) {

		return createTableLabel(profile.getName(), USER_NAME_TEXT_CLR);
	}

	private JLabel createRoleLabel(RUserProfile profile) {

		return createTableLabel(profile.getRoleName(), ROLE_NAME_TEXT_CLR);
	}

	private JLabel createStatusLabel(RUserProfile profile) {

		if (profile.registered()) {

			return createTableLabel(REGISTERED_LABEL, REGISTERED_TEXT_CLR);
		}

		return createTableLabel(UNREGISTERED_LABEL, UNREGISTERED_TEXT_CLR);
	}

	private void showRegistrationInfo(RUserProfile profile) {

		new UserRegistrationInfoDialog(this, profile);
	}
}
