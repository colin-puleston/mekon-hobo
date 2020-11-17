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

package uk.ac.manchester.cs.mekon_util.remote.admin;

import java.io.*;

/**
 * @author Colin Puleston
 */
public class RAdminManager {

	private UserFile userFile;
	private RoleFile roleFile;

	public RAdminManager(File adminDirectory) {

		userFile = new UserFile(adminDirectory);
		roleFile = new RoleFile(adminDirectory);
	}

	public synchronized RRole checkLogin(RLoginId loginId) {

		User user = resolveUser(loginId);

		if (user != null) {

			RRole role = lookForRole(user.getRoleName());

			if (role != null) {

				return role;
			}

			reportUserRoleError(user);
		}

		return RRole.NO_ACCESS;
	}

	public synchronized RUserEditResult performUserEdit(RUserEdit edit) {

		String userName = edit.getUserName();

		return edit.additionEdit()
				? addUser(userName, edit.getRoleName())
				: removeUser(userName);
	}

	private RUserEditResult addUser(String userName, String roleName) {

		if (userFile.containsUser(userName)) {

			return RUserEditResultType.ADDITION_ERROR_EXISTING_USER.getFixedTypeResult();
		}

		if (!roleFile.containsEntity(roleName)) {

			return RUserEditResultType.ADDITION_ERROR_INVALID_ROLE.getFixedTypeResult();
		}

		NewUserId userId = new NewUserId(userName);
		User user = new User(userId, roleName);

		userFile.addEntity(user);

		return RUserEditResult.additionOk(userId.getRegistrationToken());
	}

	private RUserEditResult removeUser(String name) {

		UserId userId = userFile.lookForUser(name);

		if (userId == null) {

			return RUserEditResultType.REMOVAL_ERROR_NOT_USER.getFixedTypeResult();
		}

		userFile.removeEntity(userId);

		return RUserEditResultType.REMOVAL_OK.getFixedTypeResult();
	}

	private RRole lookForRole(String name) {

		RRole role = RRole.lookForSpecial(name);

		return role != null ? role : roleFile.lookForEntity(name);
	}

	private User resolveUser(RLoginId loginId) {

		UserId userId = loginId.getUserId();
		User user = userFile.lookForEntity(userId);

		if (user != null) {

			User newUser = loginId.checkUpdateUser(user);

			if (newUser != null) {

				userFile.replaceEntity(userId, newUser);

				user = newUser;
			}
		}

		return user;
	}

	private void reportUserRoleError(User user) {

		String userId = user.getId().getName();
		String roleName = user.getRoleName();

		reportAdminConfigError(
			"Cannot find role: \"" + roleName + "\""
			+ " (specified for user \"" + userId + "\")");
	}

	private void reportAdminConfigError(String message) {

		System.out.println("ADMIN CONFIG ERROR: " + message);
	}
}
