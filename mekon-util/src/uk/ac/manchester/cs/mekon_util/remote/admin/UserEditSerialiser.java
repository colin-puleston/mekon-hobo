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
class UserEditSerialiser {

	static private final String USERNAME_ATTR = "username";
	static private final String ROLE_ATTR = "role";

	static private final String RESULT_TYPE_ATTR = "resultType";
	static private final String REG_TOKEN_ATTR = "regToken";

	static void renderEdit(RUserEdit edit, XNode editNode) {

		editNode.setValue(USERNAME_ATTR, edit.getUserName());

		if (edit.additionEdit()) {

			editNode.setValue(ROLE_ATTR, edit.getRoleName());
		}
	}

	static void renderResult(RUserEditResult result, XNode resultNode) {

		RUserEditResultType type = result.getResultType();

		resultNode.setValue(RESULT_TYPE_ATTR, type);

		if (type == RUserEditResultType.ADDITION_OK) {

			resultNode.setValue(REG_TOKEN_ATTR, type);
		}
	}

	static RUserEdit parseEdit(XNode editNode) {

		String username = editNode.getString(USERNAME_ATTR);
		String role = editNode.getString(ROLE_ATTR, null);

		return role != null
				? RUserEdit.addition(username, role)
				: RUserEdit.removal(username);
	}

	static RUserEditResult parseResult(XNode resultNode) {

		RUserEditResultType type = resultNode.getEnum(RESULT_TYPE_ATTR, RUserEditResultType.class);

		if (type == RUserEditResultType.ADDITION_OK) {

			return RUserEditResult.additionOk(resultNode.getString(REG_TOKEN_ATTR));
		}

		return type.getFixedTypeResult();
	}
}
