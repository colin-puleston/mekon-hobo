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

import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
class UserSerialiser {

	static private final String NAME_ATTR = "name";
	static private final String PASSWORD_ATTR = "password";
	static private final String NEW_PASSWORD_ATTR = "newPassword";
	static private final String REG_TOKEN_ATTR = "regToken";
	static private final String ROLE_ATTR = "role";

	static void render(User user, XNode userNode) {

		userNode.setValue(ROLE_ATTR, user.getRoleName());

		renderId(user.getId(), userNode);
	}

	static void renderId(UserId userId, XNode idNode) {

		idNode.setValue(NAME_ATTR, userId.getName());
		idNode.setValue(getIdPasswordTag(userId), userId.getPassword());
	}

	static void renderLoginId(RLoginId loginId, XNode idNode) {

		renderId(loginId.getUserId(), idNode);

		if (loginId.newPassword()) {

			idNode.setValue(NEW_PASSWORD_ATTR, loginId.getNewPassword());
		}
	}

	static User parse(XNode userNode) {

		return new User(parseId(userNode), userNode.getString(ROLE_ATTR));
	}

	static UserId parseId(XNode idNode) {

		String name = idNode.getString(NAME_ATTR);
		String regToken = idNode.getString(REG_TOKEN_ATTR, null);

		if (regToken != null) {

			return new NewUserId(name, regToken);
		}

		return new UserId(name, idNode.getString(PASSWORD_ATTR));
	}

	static RLoginId parseLoginId(XNode idNode) {

		UserId userId = parseId(idNode);
		String newPassword = idNode.getString(NEW_PASSWORD_ATTR, null);

		return new RLoginId(userId, newPassword);
	}

	static private String getIdPasswordTag(UserId userId) {

		return userId instanceof NewUserId ? REG_TOKEN_ATTR : PASSWORD_ATTR;
	}
}
