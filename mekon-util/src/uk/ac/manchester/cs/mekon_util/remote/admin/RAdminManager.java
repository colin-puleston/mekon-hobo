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
import java.util.*;

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

	public synchronized List<String> getRoleNames() {

		List<String> roles = new ArrayList<String>();

		roles.addAll(RRole.SPECIAL_NAMES);
		roles.addAll(roleFile.getKeys());

		return roles;
	}

	public synchronized List<RUserProfile> getUserProfiles() {

		return userFile.extractProfiles();
	}

	public synchronized RUserUpdateResult updateUsers(RUserUpdate update) {

		String userName = update.getUserName();

		return update.additionUpdate()
				? addUser(userName, update.getRoleName())
				: removeUser(userName);
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

	private RUserUpdateResult addUser(String userName, String roleName) {

		if (userFile.containsUser(userName)) {

			return RUserUpdateResultType.ADDITION_ERROR_EXISTING_USER.getFixedTypeResult();
		}

		if (lookForRole(roleName) == null) {

			return RUserUpdateResultType.ADDITION_ERROR_INVALID_ROLE.getFixedTypeResult();
		}

		NewUserId userId = new NewUserId(userName);
		User user = new User(userId, roleName);

		userFile.addEntity(user);

		return RUserUpdateResult.additionOk(userId.getRegistrationToken());
	}

	private RUserUpdateResult removeUser(String name) {

		UserId userId = userFile.lookForUser(name);

		if (userId == null) {

			return RUserUpdateResultType.REMOVAL_ERROR_INVALID_USER.getFixedTypeResult();
		}

		userFile.removeEntity(userId);

		return RUserUpdateResultType.REMOVAL_OK.getFixedTypeResult();
	}

	private RRole lookForRole(String name) {

		RRole role = RRole.SPECIALS_BY_NAME.get(name);

		return role != null ? role : roleFile.lookForEntity(name);
	}

	private User resolveUser(RLoginId loginId) {

		UserId userId = loginId.getUserId();
		User user = userFile.lookForEntity(userId);

		if (user != null) {

			User newUser = loginId.checkUpdateUser(user);

			if (newUser != null) {

				userFile.replaceEntity(user, newUser);

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
