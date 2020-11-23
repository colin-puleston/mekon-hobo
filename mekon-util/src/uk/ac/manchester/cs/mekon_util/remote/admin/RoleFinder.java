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

/**
 * @author Colin Puleston
 */
class RoleFinder {

	private final RoleFile roleFile;

	RoleFinder(RoleFile roleFile) {

		this.roleFile = roleFile;
	}

	boolean isRole(String roleName) {

		return lookForRole(roleName) != null;
	}

	RRole lookForRole(String roleName) {

		RRole role = RRole.SPECIALS_BY_NAME.get(roleName);

		return role != null ? role : roleFile.lookForEntity(roleName);
	}

	List<String> getRoleNames() {

		List<String> roles = new ArrayList<String>();

		roles.addAll(RRole.SPECIAL_NAMES);
		roles.addAll(roleFile.getKeys());

		return roles;
	}
}
