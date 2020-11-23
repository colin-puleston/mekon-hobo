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

import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
class UserFile extends AdminEntityFile<User, UserId> {

	static private final String FILE_NAME = "users.xml";

	static private final String ROOT_TAG = "Users";
	static private final String USER_TAG = "User";

	UserFile(File adminDirectory) {

		super(adminDirectory, FILE_NAME);
	}

	List<RUserProfile> extractProfiles() {

		List<RUserProfile> profiles = new ArrayList<RUserProfile>();

		for (User user : getEntities()) {

			profiles.add(user.extractProfile());
		}

		return profiles;
	}

	boolean containsUser(String name) {

		return lookForUser(name) != null;
	}

	UserId getUserId(String name) {

		UserId userId = lookForUser(name);

		if (userId == null) {

			throw new Error("Cannot find user: " + name);
		}

		return userId;
	}

	String getRootTag() {

		return ROOT_TAG;
	}

	String getEntityTag() {

		return USER_TAG;
	}

	void renderEntity(User entity, XNode entityNode) {

		UserSerialiser.renderUser(entity, entityNode);
	}

	User parseEntity(XNode entityNode) {

		return UserSerialiser.parseUser(entityNode);
	}

	UserId getEntityKey(User entity) {

		return entity.getId();
	}

	private UserId lookForUser(String name) {

		for (UserId userId : getKeys()) {

			if (name.equals(userId.getName())) {

				return userId;
			}
		}

		return null;
	}
}
