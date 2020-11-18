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

package uk.ac.manchester.cs.mekon_util.remote.admin.client;

import java.net.*;
import java.util.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;
import uk.ac.manchester.cs.mekon_util.remote.client.net.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.*;

/**
 * @author Colin Puleston
 */
public class RAdminClient {

	private RNetConnection connection;

	private LoginChecks loginChecks = new LoginChecks();
	private UserEdits userEdits = new UserEdits();
	private RoleNameRetrievals roleNameRetrievals = new RoleNameRetrievals();
	private UserProfileRetrievals userProfileRetrievals = new UserProfileRetrievals();

	private abstract class ServerActions<I, O> {

		O performAction(I input) {

			XDocument request = renderRequest(input);
			XDocument response = connection.performActionOnServer(request);

			return parseResponse(response);
		}

		abstract RAdminActionType getActionType();

		abstract void renderInput(RAdminRequestSerialiser renderer, I input);

		abstract O parseOutput(RAdminResponseSerialiser parser);

		private XDocument renderRequest(I input) {

			RAdminRequestSerialiser renderer = new RAdminRequestSerialiser();

			renderer.renderActionType(getActionType());
			renderInput(renderer, input);

			return renderer.getDocument();
		}

		private O parseResponse(XDocument response) {

			return parseOutput(new RAdminResponseSerialiser(response));
		}
	}

	private abstract class InputFreeServerActions<O> extends ServerActions<Object, O> {

		O performAction() {

			return performAction(null);
		}

		void renderInput(RAdminRequestSerialiser renderer, Object input) {
		}
	}

	private class RoleNameRetrievals extends InputFreeServerActions<List<String>> {

		RAdminActionType getActionType() {

			return RAdminActionType.GET_ROLES_NAMES;
		}

		List<String> parseOutput(RAdminResponseSerialiser parser) {

			return parser.parseRoleNameParameters();
		}
	}

	private class UserProfileRetrievals extends InputFreeServerActions<List<RUserProfile>> {

		RAdminActionType getActionType() {

			return RAdminActionType.GET_USER_PROFILES;
		}

		List<RUserProfile> parseOutput(RAdminResponseSerialiser parser) {

			return parser.parseUserProfileParameters();
		}
	}

	private class UserEdits extends ServerActions<RUserEdit, RUserEditResult> {

		RAdminActionType getActionType() {

			return RAdminActionType.EDIT_USERS;
		}

		void renderInput(RAdminRequestSerialiser renderer, RUserEdit input) {

			renderer.renderUserEditParameter(input);
		}

		RUserEditResult parseOutput(RAdminResponseSerialiser parser) {

			return parser.parseUserEditResultParameter();
		}
	}

	private class LoginChecks extends ServerActions<RLoginId, RRole> {

		RAdminActionType getActionType() {

			return RAdminActionType.CHECK_LOGIN;
		}

		void renderInput(RAdminRequestSerialiser renderer, RLoginId input) {

			renderer.renderLoginIdParameter(input);
		}

		RRole parseOutput(RAdminResponseSerialiser parser) {

			return parser.parseRoleParameter();
		}
	}

	public RAdminClient(URL serverURL) {

		connection = new RNetConnection(serverURL);
	}

	public List<String> getRoleNames() {

		return roleNameRetrievals.performAction();
	}

	public List<RUserProfile> getUserProfiles() {

		return userProfileRetrievals.performAction();
	}

	public RUserEditResult editUsers(RUserEdit userEdit) {

		return userEdits.performAction(userEdit);
	}

	public RRole checkLogin(RLoginId userId) {

		return loginChecks.performAction(userId);
	}
}