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
class UserUpdateSerialiser {

	static private final String UPDATE_TYPE_ATTR = "updateType";
	static private final String USER_NAME_ATTR = "userName";
	static private final String ROLE_NAME_ATTR = "roleName";

	static private final String RESULT_TYPE_ATTR = "resultType";
	static private final String REG_TOKEN_ATTR = "registrationToken";

	static void renderUpdate(RUserUpdate update, XNode updateNode) {

		RUserUpdateType type = update.getType();

		updateNode.setValue(UPDATE_TYPE_ATTR, type);
		updateNode.setValue(USER_NAME_ATTR, update.getUserName());

		if (type != RUserUpdateType.REMOVAL) {

			updateNode.setValue(ROLE_NAME_ATTR, update.getRoleName());
		}
	}

	static void renderResult(RUserUpdateResult result, XNode resultNode) {

		RUserUpdateResultType type = result.getType();

		resultNode.setValue(RESULT_TYPE_ATTR, type);

		if (type == RUserUpdateResultType.ADDITION_OK) {

			resultNode.setValue(REG_TOKEN_ATTR, result.getRegistrationToken());
		}
	}

	static RUserUpdate parseUpdate(XNode updateNode) {

		RUserUpdateType type = updateNode.getEnum(UPDATE_TYPE_ATTR, RUserUpdateType.class);

		String userName = updateNode.getString(USER_NAME_ATTR);
		String roleName = updateNode.getString(ROLE_NAME_ATTR, null);

		return new RUserUpdate(type, userName, roleName);
	}

	static RUserUpdateResult parseResult(XNode resultNode) {

		RUserUpdateResultType type = resultNode.getEnum(RESULT_TYPE_ATTR, RUserUpdateResultType.class);

		if (type == RUserUpdateResultType.ADDITION_OK) {

			return RUserUpdateResult.additionOk(resultNode.getString(REG_TOKEN_ATTR));
		}

		return type.getFixedTypeResult();
	}
}
