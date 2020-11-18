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

package uk.ac.manchester.cs.mekon_util.remote.admin.server;

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;
import uk.ac.manchester.cs.mekon_util.remote.server.net.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.*;

/**
 * @author Colin Puleston
 */
public abstract class RAdminServer extends RNetServer {

	static private final long serialVersionUID = -1;

	private RAdminManager adminManager;

	private Map<RAdminActionType, Actions<?>> allActions
					= new HashMap<RAdminActionType, Actions<?>>();

	private abstract class Actions<O> {

		Actions(RAdminActionType actionType) {

			allActions.put(actionType, this);
		}

		abstract XDocument performAction(RAdminRequestSerialiser requestParser);

		abstract void renderOutput(RAdminResponseSerialiser renderer, O output);

		XDocument renderResponse(O output) {

			RAdminResponseSerialiser renderer = new RAdminResponseSerialiser();

			renderOutput(renderer, output);

			return renderer.getDocument();
		}
	}

	private abstract class InputFreeActions<O> extends Actions<O> {

		InputFreeActions(RAdminActionType actionType) {

			super(actionType);
		}

		XDocument performAction(RAdminRequestSerialiser requestParser) {

			return renderResponse(performAction());
		}

		abstract O performAction();
	}

	private abstract class InputDrivenActions<I, O> extends Actions<O> {

		InputDrivenActions(RAdminActionType actionType) {

			super(actionType);
		}

		XDocument performAction(RAdminRequestSerialiser requestParser) {

			return renderResponse(performAction(parseInput(requestParser)));
		}

		abstract I parseInput(RAdminRequestSerialiser parser);

		abstract O performAction(I input);
	}

	private class RoleNameRetrievals extends InputFreeActions<List<String>> {

		RoleNameRetrievals() {

			super(RAdminActionType.GET_ROLES_NAMES);
		}

		void renderOutput(RAdminResponseSerialiser renderer, List<String> output) {

			renderer.renderRoleNameParameters(output);
		}

		List<String> performAction() {

			return adminManager.getRoleNames();
		}
	}

	private class UserProfileRetrievals extends InputFreeActions<List<RUserProfile>> {

		UserProfileRetrievals() {

			super(RAdminActionType.GET_USER_PROFILES);
		}

		void renderOutput(RAdminResponseSerialiser renderer, List<RUserProfile> output) {

			renderer.renderUserProfileParameters(output);
		}

		List<RUserProfile> performAction() {

			return adminManager.getUserProfiles();
		}
	}

	private class UsersEdits extends InputDrivenActions<RUserEdit, RUserEditResult> {

		UsersEdits() {

			super(RAdminActionType.EDIT_USERS);
		}

		RUserEdit parseInput(RAdminRequestSerialiser parser) {

			return parser.parseUserEditParameter();
		}

		void renderOutput(RAdminResponseSerialiser renderer, RUserEditResult output) {

			renderer.renderUserEditResultParameter(output);
		}

		RUserEditResult performAction(RUserEdit input) {

			return adminManager.editUsers(input);
		}
	}

	private class LoginChecks extends InputDrivenActions<RLoginId, RRole> {

		LoginChecks() {

			super(RAdminActionType.CHECK_LOGIN);
		}

		RLoginId parseInput(RAdminRequestSerialiser parser) {

			return parser.parseLoginIdParameter();
		}

		void renderOutput(RAdminResponseSerialiser renderer, RRole output) {

			renderer.renderRoleParameter(output);
		}

		RRole performAction(RLoginId input) {

			return adminManager.checkLogin(input);
		}
	}

	protected void initNetServer() {

		adminManager = new RAdminManager(getAdminDirectory());

		new RoleNameRetrievals();
		new UserProfileRetrievals();
		new UsersEdits();
		new LoginChecks();
	}

	protected XDocument performAction(XDocument request) {

		RAdminRequestSerialiser requestParser = new RAdminRequestSerialiser(request);
		RAdminActionType actionType = requestParser.parseActionType();

		return getActions(actionType).performAction(requestParser);
	}

	protected abstract File getAdminDirectory();

	private Actions<?> getActions(RAdminActionType actionType) {

		Actions<?> actions = allActions.get(actionType);

		if (actions == null) {

			throw new XDocumentException("Unrecognised action-type: " + actionType);
		}

		return actions;
	}
}