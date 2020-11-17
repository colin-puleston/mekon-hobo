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

import uk.ac.manchester.cs.mekon_util.xdoc.*;
import uk.ac.manchester.cs.mekon_util.remote.client.net.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.*;

/**
 * @author Colin Puleston
 */
public class RLoginClient {

	private RNetConnection connection;

	private LoginAction loginAction = new LoginAction();
	private UserEditAction userEditAction = new UserEditAction();

	private abstract class ServerAction<I, O> {

		O perform(I input) {

			XDocument request = renderRequest(input);
			XDocument response = connection.performActionOnServer(request);

			return parseResponse(response);
		}

		abstract RAdminActionType getActionType();

		abstract void renderInputParameter(RAdminRequestSerialiser renderer, I input);

		abstract O parseOutputParameter(RAdminResponseSerialiser parser);

		private XDocument renderRequest(I input) {

			RAdminRequestSerialiser renderer = new RAdminRequestSerialiser();

			renderer.renderActionType(getActionType());
			renderInputParameter(renderer, input);

			return renderer.getDocument();
		}

		private O parseResponse(XDocument response) {

			return parseOutputParameter(new RAdminResponseSerialiser(response));
		}
	}

	private class LoginAction extends ServerAction<RLoginId, RRole> {

		RAdminActionType getActionType() {

			return RAdminActionType.USER_LOGIN;
		}

		void renderInputParameter(RAdminRequestSerialiser renderer, RLoginId input) {

			renderer.renderLoginIdParameter(input);
		}

		RRole parseOutputParameter(RAdminResponseSerialiser parser) {

			return parser.parseRoleParameter();
		}
	}

	private class UserEditAction extends ServerAction<RUserEdit, RUserEditResult> {

		RAdminActionType getActionType() {

			return RAdminActionType.USER_EDIT;
		}

		void renderInputParameter(RAdminRequestSerialiser renderer, RUserEdit input) {

			renderer.renderUserEditParameter(input);
		}

		RUserEditResult parseOutputParameter(RAdminResponseSerialiser parser) {

			return parser.parseUserEditResultParameter();
		}
	}

	public RLoginClient(URL serverURL) {

		connection = new RNetConnection(serverURL);
	}

	public RRole checkLogin(RLoginId userId) {

		return loginAction.perform(userId);
	}

	public RUserEditResult performUserEdit(RUserEdit userEdit) {

		return userEditAction.perform(userEdit);
	}
}