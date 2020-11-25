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
	private RoleFinder roleFinder;

	private LockManager lockManager = new LockManager();

	public RAdminManager(File adminDirectory) {

		userFile = new UserFile(adminDirectory);
		roleFinder = new RoleFinder(new RoleFile(adminDirectory));
	}

	public synchronized List<String> getRoleNames() {

		return roleFinder.getRoleNames();
	}

	public synchronized List<RUserProfile> getUserProfiles() {

		return userFile.extractProfiles();
	}

	public synchronized RUserUpdateResult updateUsers(RUserUpdate update) {

		return update.performUpdate(roleFinder, userFile);
	}

	public synchronized RLoginResult checkLogin(RLoginId loginId) {

		User user = resolveUser(loginId);

		if (user != null) {

			RRole role = roleFinder.lookForRole(user.getRoleName());

			if (role != null) {

				return new RLoginResult(role);
			}

			reportUserRoleError(user);
		}

		return RLoginResult.LOGIN_FAILED;
	}

	public synchronized RLockingResult requestLock(RLock lock) {

		return lockManager.requestLock(lock);
	}

	public synchronized void releaseLock(String resourceId) {

		lockManager.releaseLock(resourceId);
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

		String userName = user.getId().getName();
		String roleName = user.getRoleName();

		reportAdminConfigError(
			"Cannot find role: \"" + roleName + "\""
			+ " (specified for user \"" + userName + "\")");
	}

	private void reportAdminConfigError(String message) {

		System.out.println("ADMIN CONFIG ERROR: " + message);
	}
}
