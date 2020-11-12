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

import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
public class UserSerialiser {

	static final String ROOT_TAG = "Users";
	static final String USER_TAG = "User";

	static private final String NAME_ATTR = "name";
	static private final String PASSWORD_ATTR = "password";
	static private final String NEW_PASSWORD_ATTR = "newPassword";
	static private final String REG_TOKEN_ATTR = "regToken";
	static private final String ROLE_ATTR = "role";

	static public XDocument renderId(UserId userId) {

		XDocument document = new XDocument(USER_TAG);

		renderId(userId, document.getRootNode());

		return document;
	}

	static public UserId parseId(XDocument document) {

		return parseId(document.getRootNode());
	}

	static void render(User user, XNode userNode) {

		userNode.setValue(ROLE_ATTR, user.getRoleName());

		renderId(user.getId(), userNode);
	}

	static void renderId(UserId userId, XNode userNode) {

		userNode.setValue(NAME_ATTR, userId.getName());

		if (userId instanceof NewUserId) {

			userNode.setValue(REG_TOKEN_ATTR, userId.getPassword());
		}
		else {

			userNode.setValue(PASSWORD_ATTR, userId.getPassword());

			if (userId instanceof UserIdUpdate) {

				UserIdUpdate userIdUpd = (UserIdUpdate)userId;

				userNode.setValue(NEW_PASSWORD_ATTR, userIdUpd.getNewPassword());
			}
		}
	}

	static User parse(XNode userNode) {

		return new User(parseId(userNode), userNode.getString(ROLE_ATTR));
	}

	static UserId parseId(XNode userNode) {

		String name = userNode.getString(NAME_ATTR);
		String regToken = userNode.getString(REG_TOKEN_ATTR, null);

		if (regToken != null) {

			return new NewUserId(name, regToken);
		}

		String password = userNode.getString(PASSWORD_ATTR);
		String newPassword = userNode.getString(NEW_PASSWORD_ATTR, null);

		if (newPassword != null) {

			return new UserIdUpdate(name, password, newPassword);
		}

		return new UserId(name, password);
	}
}
