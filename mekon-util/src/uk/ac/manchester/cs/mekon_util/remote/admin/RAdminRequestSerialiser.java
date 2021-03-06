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
public class RAdminRequestSerialiser extends RAdminMessageSerialiser {

	static private final String ROOT_TAG = "AdminRequest";
	static private final String USER_UPDATE_TAG = "UserUpdate";
	static private final String LOGIN_ID_TAG = "LoginId";
	static private final String LOCK_TAG = "Lock";
	static private final String LOCK_RELEASE_TAG = "LockRelease";

	static private final String ACTION_TYPE_ATTR = "actionType";

	public RAdminRequestSerialiser() {

		super(ROOT_TAG);
	}

	public RAdminRequestSerialiser(XDocument document) {

		super(document);
	}

	public void renderActionType(RAdminActionType actionType) {

		getRootNode().setValue(ACTION_TYPE_ATTR, actionType);
	}

	public void renderUserUpdate(RUserUpdate update) {

		UserUpdateSerialiser.renderUpdate(update, addParameterNode(USER_UPDATE_TAG));
	}

	public void renderLoginId(RLoginId userId) {

		UserSerialiser.renderLoginId(userId, addParameterNode(LOGIN_ID_TAG));
	}

	public void renderLock(RLock lock) {

		LockingSerialiser.renderLock(lock, addParameterNode(LOCK_TAG));
	}

	public void renderLockRelease(String resourceId) {

		LockingSerialiser.renderLockResourceId(resourceId, addParameterNode(LOCK_RELEASE_TAG));
	}

	public RAdminActionType parseActionType() {

		return getRootNode().getEnum(ACTION_TYPE_ATTR, RAdminActionType.class);
	}

	public RUserUpdate parseUserUpdate() {

		return UserUpdateSerialiser.parseUpdate(getParameterNode(USER_UPDATE_TAG));
	}

	public RLoginId parseLoginId() {

		return UserSerialiser.parseLoginId(getParameterNode(LOGIN_ID_TAG));
	}

	public RLock parseLock() {

		return LockingSerialiser.parseLock(getParameterNode(LOCK_TAG));
	}

	public String parseLockRelease() {

		return LockingSerialiser.parseLockResourceId(getParameterNode(LOCK_RELEASE_TAG));
	}
}
