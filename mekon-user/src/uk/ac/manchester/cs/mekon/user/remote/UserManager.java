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
class UserManager extends EntityManager<RUserProfile> {

	private UsersPanel panel;

	private RAdminClient adminClient;
	private List<String> roleNames;

	UserManager(UsersPanel panel, RAdminClient adminClient) {

		super(panel);

		this.panel = panel;
		this.adminClient = adminClient;

		roleNames = adminClient.getRoleNames();
	}

	RUserProfile addServerEntity() {

		UserDetailsDialog dialog = new UserDetailsDialog(panel);

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

	RUserProfile editServerEntity(RUserProfile profile) {

		String name = profile.getName();
		String role = profile.getRoleName();

		UserDetailsDialog dialog = new UserDetailsDialog(panel);

		dialog.setFixedUserName(name);

		if (dialog.display(roleNames)) {

			String newRole = dialog.getSelectedRoleName();

			if (!newRole.equals(role)) {

				if (performUpdate(RUserUpdate.edit(name, newRole)).updateOk()) {

					return profile.deriveProfileWithRole(newRole);
				}
			}
		}

		return null;
	}

	boolean deleteServerEntity(RUserProfile profile) {

		String name = profile.getName();

		return performUpdate(RUserUpdate.removal(name)).updateOk();
	}

	List<RUserProfile> getServerEntities() {

		return adminClient.getUserProfiles();
	}

	String describe(RUserProfile profile) {

		return "user \"" + profile.getName() + "\"";
	}

	String getSorterName(RUserProfile profile) {

		return profile.getName();
	}

	private RUserUpdateResult performUpdate(RUserUpdate update) {

		RUserUpdateResult result = adminClient.updateUsers(update);

		if (!result.updateOk()) {

			showMessage("Operation failed: " + result.getType());
		}

		return result;
	}

	private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}
}
