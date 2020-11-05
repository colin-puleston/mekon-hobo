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

package uk.ac.manchester.cs.mekon.user.util.admin;

import java.io.*;
import java.util.*;

/**
 * @author Colin Puleston
 */
public class LoginManager {

	private Map<String, User> usersByName;
	private Map<String, Role> rolesByName;

	public LoginManager(File adminDirectory) {

		usersByName = new UserFile(adminDirectory).parseAll();
		rolesByName = new RoleFile(adminDirectory).parseAll();
	}

	public Role processLogin(UserId userId) {

		User user = usersByName.get(userId.getName());

		if (user != null) {

			Role role = rolesByName.get(user.getRoleName());

			if (role != null) {

				return role;
			}

			reportUserRoleError(user);
		}

		return Role.INVALID_USER;
	}

	private void reportUserRoleError(User user) {

		String userId = user.getId().getName();
		String roleName = user.getRoleName();

		reportAdminError(
			"Cannot find role: \"" + roleName + "\""
			+ " (specified for user \"" + userId + "\")");
	}

	private void reportAdminError(String message) {
	}
}
