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

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.remote.admin.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.client.*;

/**
 * @author Colin Puleston
 */
class UserUpdates {

	private JComponent parent;

	private RAdminClient adminClient;
	private List<String> roleNames;

	UserUpdates(JComponent parent, RAdminClient adminClient) {

		this.parent = parent;
		this.adminClient = adminClient;

		roleNames = adminClient.getRoleNames();
	}

	RUserProfile checkAddUser() {

		UserDetailsDialog dialog = new UserDetailsDialog(parent);

		if (dialog.display(roleNames)) {

			String name = dialog.getSelectedUserName();
			String role = dialog.getSelectedRoleName();

			RUserUpdateResult result = performUpdate(RUserUpdate.addition(name, role));

			if (result.updateOk()) {

				return new RUserProfile(name, role, result.getRegistrationToken());
			}
		}

		return null;
	}

	String checkEditRole(String userName, String roleName) {

		UserDetailsDialog dialog = new UserDetailsDialog(parent);

		dialog.setFixedUserName(userName);

		if (dialog.display(roleNames)) {

			String newRoleName = dialog.getSelectedRoleName();

			if (!newRoleName.equals(roleName)) {

				if (performUpdate(RUserUpdate.edit(userName, newRoleName)).updateOk()) {

					return dialog.getSelectedRoleName();
				}
			}
		}

		return null;
	}

	boolean checkRemoveUser(String userName) {

		if (!obtainConfirmation("Deleting user \"" + userName + "\"")) {

			return false;
		}

		return performUpdate(RUserUpdate.removal(userName)).updateOk();
	}

	private RUserUpdateResult performUpdate(RUserUpdate update) {

		RUserUpdateResult result = adminClient.updateUsers(update);

		if (!result.updateOk()) {

			showMessage("Operation failed: " + result.getType());
		}

		return result;
	}

	private boolean obtainConfirmation(String msg) {

		return obtainConfirmationOption(msg) == JOptionPane.OK_OPTION;
	}

	private int obtainConfirmationOption(String msg) {

		return JOptionPane.showConfirmDialog(
					null,
					msg,
					"Confirm?",
					JOptionPane.OK_CANCEL_OPTION);
	}

	private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}
}
