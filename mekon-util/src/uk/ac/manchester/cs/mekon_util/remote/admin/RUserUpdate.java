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

/**
 * @author Colin Puleston
 */
public class RUserUpdate {

	static public RUserUpdate addition(String userName, String roleName) {

		return new RUserUpdate(RUserUpdateType.ADDITION, userName, roleName);
	}

	static public RUserUpdate edit(String userName, String roleName) {

		return new RUserUpdate(RUserUpdateType.EDIT, userName, roleName);
	}

	static public RUserUpdate removal(String userName) {

		return new RUserUpdate(RUserUpdateType.REMOVAL, userName, null);
	}

	private RUserUpdateType type;

	private String userName;
	private String roleName;

	public RUserUpdateType getType() {

		return type;
	}

	public String getUserName() {

		return userName;
	}

	public String getRoleName() {

		return roleName;
	}

	RUserUpdate(RUserUpdateType type, String userName, String roleName) {

		this.type = type;
		this.userName = userName;
		this.roleName = roleName;
	}

	RUserUpdateResult performUpdate(RoleFinder roleFinder, UserFile userFile) {

		RUserUpdateResultType errorType = validateUpdate(roleFinder, userFile);

		if (errorType != null) {

			return errorType.getFixedTypeResult();
		}

		return type.performUpdate(userFile, this);
	}

	RUserUpdateResult performAddition(UserFile userFile) {

		NewUserId userId = new NewUserId(userName);
		User user = new User(userId, roleName);

		userFile.addEntity(user);

		return RUserUpdateResult.additionOk(userId.getRegistrationToken());
	}

	RUserUpdateResult performEdit(UserFile userFile) {

		UserId userId = userFile.getUserId(userName);

		userFile.removeEntity(userId);
		userFile.addEntity(new User(userId, roleName));

		return RUserUpdateResultType.EDIT_OK.getFixedTypeResult();
	}

	RUserUpdateResult performRemoval(UserFile userFile) {

		UserId userId = userFile.getUserId(userName);

		userFile.removeEntity(userId);

		return RUserUpdateResultType.REMOVAL_OK.getFixedTypeResult();
	}

	private RUserUpdateResultType validateUpdate(RoleFinder roleFinder, UserFile userFile) {

		if (type.forExistingUser()) {

			if (!userFile.containsUser(userName)) {

				return RUserUpdateResultType.INVALID_USER_ERROR;
			}
		}
		else {

			if (userFile.containsUser(userName)) {

				return RUserUpdateResultType.EXISTING_USER_ERROR;
			}
		}

		if (type.includesRole()) {

			if (!roleFinder.isRole(roleName)) {

				return RUserUpdateResultType.INVALID_ROLE_ERROR;
			}
		}

		return null;
	}
}
