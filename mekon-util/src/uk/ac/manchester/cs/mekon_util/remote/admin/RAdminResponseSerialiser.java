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
public class RAdminResponseSerialiser extends RAdminMessageSerialiser {

	static private final String ROOT_TAG = "AdminResponse";
	static private final String ROLE_NAMES_TAG = "RoleNames";
	static private final String USER_PROFILES_TAG = "UserProfiles";
	static private final String USER_UPDATE_RESULT_TAG = "UserUpdateResult";
	static private final String ROLE_TAG = "Role";

	private XNode rootNode;

	public RAdminResponseSerialiser() {

		super(ROOT_TAG);
	}

	public RAdminResponseSerialiser(XDocument document) {

		super(document);
	}

	public void renderRoleNameParameters(List<String> roleNames) {

		RoleSerialiser.renderRoleNames(roleNames, addParameterNode(ROLE_NAMES_TAG));
	}

	public void renderUserProfileParameters(List<RUserProfile> userProfiles) {

		UserSerialiser.renderProfiles(userProfiles, addParameterNode(USER_PROFILES_TAG));
	}

	public void renderUserUpdateResultParameter(RUserUpdateResult update) {

		UserUpdateSerialiser.renderResult(update, addParameterNode(USER_UPDATE_RESULT_TAG));
	}

	public void renderRoleParameter(RRole role) {

		RoleSerialiser.renderRole(role, addParameterNode(ROLE_TAG));
	}

	public List<String> parseRoleNameParameters() {

		return RoleSerialiser.parseRoleNames(getParameterNode(ROLE_NAMES_TAG));
	}

	public List<RUserProfile> parseUserProfileParameters() {

		return UserSerialiser.parseProfiles(getParameterNode(USER_PROFILES_TAG));
	}

	public RUserUpdateResult parseUserUpdateResultParameter() {

		return UserUpdateSerialiser.parseResult(getParameterNode(USER_UPDATE_RESULT_TAG));
	}

	public RRole parseRoleParameter() {

		return RoleSerialiser.parseRole(getParameterNode(ROLE_TAG));
	}
}
