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

import java.util.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;
import uk.ac.manchester.cs.mekon_util.remote.client.net.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.*;

/**
 * @author Colin Puleston
 */
public class RAdminClient {

	private RNetClient netClient;

	private RoleNameRetrievals roleNameRetrievals = new RoleNameRetrievals();
	private UserProfileRetrievals userProfileRetrievals = new UserProfileRetrievals();
	private UserUpdates userUpdates = new UserUpdates();
	private LoginChecks loginChecks = new LoginChecks();
	private LockRequests lockRequests = new LockRequests();
	private LockReleases lockReleases = new LockReleases();

	private abstract class ServerActions<I, O> {

		O performAction(I input) {

			XDocument request = renderRequest(input);
			XDocument response = netClient.performActionOnServer(request);

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

	private abstract class InputOnlyServerActions<I> extends ServerActions<I, Object> {

		Object parseOutput(RAdminResponseSerialiser parser) {

			return null;
		}
	}

	private abstract class OutputOnlyServerActions<O> extends ServerActions<Object, O> {

		O performAction() {

			return performAction(null);
		}

		void renderInput(RAdminRequestSerialiser renderer, Object input) {
		}
	}

	private class RoleNameRetrievals extends OutputOnlyServerActions<List<String>> {

		RAdminActionType getActionType() {

			return RAdminActionType.ROLE_NAME_RETRIEVAL;
		}

		List<String> parseOutput(RAdminResponseSerialiser parser) {

			return parser.parseRoleNames();
		}
	}

	private class UserProfileRetrievals extends OutputOnlyServerActions<List<RUserProfile>> {

		RAdminActionType getActionType() {

			return RAdminActionType.USER_PROFILE_RETRIEVAL;
		}

		List<RUserProfile> parseOutput(RAdminResponseSerialiser parser) {

			return parser.parseUserProfiles();
		}
	}

	private class UserUpdates extends ServerActions<RUserUpdate, RUserUpdateResult> {

		RAdminActionType getActionType() {

			return RAdminActionType.USER_UPDATE;
		}

		void renderInput(RAdminRequestSerialiser renderer, RUserUpdate input) {

			renderer.renderUserUpdate(input);
		}

		RUserUpdateResult parseOutput(RAdminResponseSerialiser parser) {

			return parser.parseUserUpdateResult();
		}
	}

	private class LoginChecks extends ServerActions<RLoginId, RLoginResult> {

		RAdminActionType getActionType() {

			return RAdminActionType.LOGIN_CHECK;
		}

		void renderInput(RAdminRequestSerialiser renderer, RLoginId input) {

			renderer.renderLoginId(input);
		}

		RLoginResult parseOutput(RAdminResponseSerialiser parser) {

			return parser.parseLoginResult();
		}
	}

	private class LockRequests extends ServerActions<RLock, RLockingResult> {

		RAdminActionType getActionType() {

			return RAdminActionType.LOCK_REQUEST;
		}

		void renderInput(RAdminRequestSerialiser renderer, RLock input) {

			renderer.renderLock(input);
		}

		RLockingResult parseOutput(RAdminResponseSerialiser parser) {

			return parser.parseLockingResult();
		}
	}

	private class LockReleases extends InputOnlyServerActions<String> {

		RAdminActionType getActionType() {

			return RAdminActionType.LOCK_RELEASE;
		}

		void renderInput(RAdminRequestSerialiser renderer, String input) {

			renderer.renderLockRelease(input);
		}
	}

	public RAdminClient(RNetClient netClient) {

		this.netClient = netClient;
	}

	public List<String> getRoleNames() {

		return roleNameRetrievals.performAction();
	}

	public List<RUserProfile> getUserProfiles() {

		return userProfileRetrievals.performAction();
	}

	public RUserUpdateResult updateUsers(RUserUpdate update) {

		return userUpdates.performAction(update);
	}

	public RLoginResult checkLogin(RLoginId userId) {

		return loginChecks.performAction(userId);
	}

	public RLockingResult requestLock(RLock lock) {

		return lockRequests.performAction(lock);
	}

	public void releaseLock(String resourceId) {

		lockReleases.performAction(resourceId);
	}
}