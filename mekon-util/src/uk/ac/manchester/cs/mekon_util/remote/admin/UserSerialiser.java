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

import java.util.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
class UserSerialiser {

	static private final String PROFILE_TAG = "Profile";

	static private final String NAME_ATTR = "name";
	static private final String PASSWORD_ATTR = "password";
	static private final String NEW_PASSWORD_ATTR = "newPassword";
	static private final String REG_TOKEN_ATTR = "registrationToken";
	static private final String ROLE_ATTR = "role";

	static void renderProfiles(List<RUserProfile> profiles, XNode profilesNode) {

		for (RUserProfile profile : profiles) {

			renderProfile(profile, profilesNode.addChild(PROFILE_TAG));
		}
	}

	static void renderUser(User user, XNode userNode) {

		userNode.setValue(ROLE_ATTR, user.getRoleName());

		renderUserId(user.getId(), userNode);
	}

	static void renderLoginId(RLoginId loginId, XNode idNode) {

		renderUserId(loginId.getUserId(), idNode);

		if (loginId.newPassword()) {

			idNode.setValue(NEW_PASSWORD_ATTR, loginId.getNewPassword());
		}
	}

	static List<RUserProfile> parseProfiles(XNode profilesNode) {

		List<RUserProfile> profiles = new ArrayList<RUserProfile>();

		for (XNode profileNode : profilesNode.getChildren(PROFILE_TAG)) {

			profiles.add(parseProfile(profileNode));
		}

		return profiles;
	}

	static User parseUser(XNode userNode) {

		return new User(parseUserId(userNode), userNode.getString(ROLE_ATTR));
	}

	static RLoginId parseLoginId(XNode idNode) {

		UserId userId = parseUserId(idNode);
		String newPassword = idNode.getString(NEW_PASSWORD_ATTR, null);

		return new RLoginId(userId, newPassword);
	}

	static private void renderProfile(RUserProfile profile, XNode profileNode) {

		profileNode.setValue(NAME_ATTR, profile.getName());
		profileNode.setValue(ROLE_ATTR, profile.getRoleName());

		if (!profile.registered()) {

			profileNode.setValue(NAME_ATTR, profile.getName());
		}
	}

	static private void renderUserId(UserId userId, XNode idNode) {

		idNode.setValue(NAME_ATTR, userId.getName());
		idNode.setValue(getIdPasswordTag(userId), userId.getPassword());
	}

	static private RUserProfile parseProfile(XNode profileNode) {

		String name = profileNode.getString(NAME_ATTR);
		String roleName = profileNode.getString(ROLE_ATTR);
		String regToken = profileNode.getString(REG_TOKEN_ATTR, null);

		return new RUserProfile(name, roleName, regToken);
	}

	static private UserId parseUserId(XNode idNode) {

		String name = idNode.getString(NAME_ATTR);
		String regToken = idNode.getString(REG_TOKEN_ATTR, null);

		if (regToken != null) {

			return new NewUserId(name, regToken);
		}

		return new UserId(name, idNode.getString(PASSWORD_ATTR));
	}

	static private String getIdPasswordTag(UserId userId) {

		return userId instanceof NewUserId ? REG_TOKEN_ATTR : PASSWORD_ATTR;
	}
}
