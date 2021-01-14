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

		Actions() {

			allActions.put(getActionType(), this);
		}

		abstract RAdminActionType getActionType();

		abstract XDocument performAction(RAdminRequestSerialiser requestParser);

		abstract void renderOutput(RAdminResponseSerialiser renderer, O output);

		XDocument renderResponse(O output) {

			RAdminResponseSerialiser renderer = new RAdminResponseSerialiser();

			renderOutput(renderer, output);

			return renderer.getDocument();
		}
	}

	private abstract class OutputOnlyActions<O> extends Actions<O> {

		XDocument performAction(RAdminRequestSerialiser requestParser) {

			return renderResponse(performOutputOnlyAction());
		}

		abstract O performOutputOnlyAction();
	}

	private abstract class InputDrivenActions<I, O> extends Actions<O> {

		XDocument performAction(RAdminRequestSerialiser requestParser) {

			return renderResponse(performAction(parseInput(requestParser)));
		}

		abstract I parseInput(RAdminRequestSerialiser parser);

		abstract O performAction(I input);
	}

	private abstract class InputOnlyActions<I> extends InputDrivenActions<I, Object> {

		Object performAction(String input) {

			performInputOnlyAction(input);

			return null;
		}

		void renderOutput(RAdminResponseSerialiser renderer, Object output) {
		}

		abstract void performInputOnlyAction(String input);
	}

	private class RoleNameRetrievals extends OutputOnlyActions<List<String>> {

		RAdminActionType getActionType() {

			return RAdminActionType.ROLE_NAME_RETRIEVAL;
		}

		void renderOutput(RAdminResponseSerialiser renderer, List<String> output) {

			renderer.renderRoleNames(output);
		}

		List<String> performOutputOnlyAction() {

			return adminManager.getRoleNames();
		}
	}

	private class UserProfileRetrievals extends OutputOnlyActions<List<RUserProfile>> {

		RAdminActionType getActionType() {

			return RAdminActionType.USER_PROFILE_RETRIEVAL;
		}

		void renderOutput(RAdminResponseSerialiser renderer, List<RUserProfile> output) {

			renderer.renderUserProfiles(output);
		}

		List<RUserProfile> performOutputOnlyAction() {

			return adminManager.getUserProfiles();
		}
	}

	private class ActiveLockRetrievals extends OutputOnlyActions<List<RLock>> {

		RAdminActionType getActionType() {

			return RAdminActionType.ACTIVE_LOCK_RETRIEVAL;
		}

		void renderOutput(RAdminResponseSerialiser renderer, List<RLock> output) {

			renderer.renderActiveLocks(output);
		}

		List<RLock> performOutputOnlyAction() {

			return adminManager.getActiveLocks();
		}
	}

	private class UserUpdates extends InputDrivenActions<RUserUpdate, RUserUpdateResult> {

		RAdminActionType getActionType() {

			return RAdminActionType.USER_UPDATE;
		}

		RUserUpdate parseInput(RAdminRequestSerialiser parser) {

			return parser.parseUserUpdate();
		}

		void renderOutput(RAdminResponseSerialiser renderer, RUserUpdateResult output) {

			renderer.renderUserUpdateResult(output);
		}

		RUserUpdateResult performAction(RUserUpdate input) {

			return adminManager.updateUsers(input);
		}
	}

	private class LoginChecks extends InputDrivenActions<RLoginId, RLoginResult> {

		RAdminActionType getActionType() {

			return RAdminActionType.LOGIN_CHECK;
		}

		RLoginId parseInput(RAdminRequestSerialiser parser) {

			return parser.parseLoginId();
		}

		void renderOutput(RAdminResponseSerialiser renderer, RLoginResult output) {

			renderer.renderLoginResult(output);
		}

		RLoginResult performAction(RLoginId input) {

			return adminManager.checkLogin(input);
		}
	}

	private class LockRequests extends InputDrivenActions<RLock, RLockingResult> {

		RAdminActionType getActionType() {

			return RAdminActionType.LOCK_REQUEST;
		}

		RLock parseInput(RAdminRequestSerialiser parser) {

			return parser.parseLock();
		}

		void renderOutput(RAdminResponseSerialiser renderer, RLockingResult output) {

			renderer.renderLockingResult(output);
		}

		RLockingResult performAction(RLock input) {

			return adminManager.requestLock(input);
		}
	}

	private class LockReleases extends InputOnlyActions<String> {

		RAdminActionType getActionType() {

			return RAdminActionType.LOCK_RELEASE;
		}

		String parseInput(RAdminRequestSerialiser parser) {

			return parser.parseLockRelease();
		}

		void performInputOnlyAction(String input) {

			adminManager.releaseLock(input);
		}
	}

	protected void initNetServer() {

		adminManager = new RAdminManager(getAdminDirectory());

		new RoleNameRetrievals();
		new UserProfileRetrievals();
		new ActiveLockRetrievals();
		new UserUpdates();
		new LoginChecks();
		new LockRequests();
		new LockReleases();
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