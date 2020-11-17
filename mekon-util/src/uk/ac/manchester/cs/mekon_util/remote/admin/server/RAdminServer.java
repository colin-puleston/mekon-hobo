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

	private Map<RAdminActionType, ServerAction<?, ?>> serverActions
					= new HashMap<RAdminActionType, ServerAction<?, ?>>();

	private abstract class ServerAction<I, O> {

		ServerAction(RAdminActionType actionType) {

			serverActions.put(actionType, this);
		}

		XDocument perform(RAdminRequestSerialiser requestParser) {

			return renderResponse(performAction(parseInputParameter(requestParser)));
		}

		abstract I parseInputParameter(RAdminRequestSerialiser parser);

		abstract void renderOutputParameter(RAdminResponseSerialiser renderer, O output);

		abstract O performAction(I input);

		private XDocument renderResponse(O output) {

			RAdminResponseSerialiser renderer = new RAdminResponseSerialiser();

			renderOutputParameter(renderer, output);

			return renderer.getDocument();
		}
	}

	private class LoginAction extends ServerAction<RLoginId, RRole> {

		LoginAction() {

			super(RAdminActionType.USER_LOGIN);
		}

		RLoginId parseInputParameter(RAdminRequestSerialiser parser) {

			return parser.parseLoginIdParameter();
		}

		void renderOutputParameter(RAdminResponseSerialiser renderer, RRole output) {

			renderer.renderRoleParameter(output);
		}

		RRole performAction(RLoginId input) {

			return adminManager.checkLogin(input);
		}
	}

	private class UserEditAction extends ServerAction<RUserEdit, RUserEditResult> {

		UserEditAction() {

			super(RAdminActionType.USER_EDIT);
		}

		RUserEdit parseInputParameter(RAdminRequestSerialiser parser) {

			return parser.parseUserEditParameter();
		}

		void renderOutputParameter(RAdminResponseSerialiser renderer, RUserEditResult output) {

			renderer.renderUserEditResultParameter(output);
		}

		RUserEditResult performAction(RUserEdit input) {

			return adminManager.performUserEdit(input);
		}
	}

	protected void initNetServer() {

		adminManager = new RAdminManager(getAdminDirectory());

		new LoginAction();
		new UserEditAction();
	}

	protected XDocument performAction(XDocument request) {

		RAdminRequestSerialiser requestParser = new RAdminRequestSerialiser(request);
		RAdminActionType actionType = requestParser.parseActionType();

		return getAction(actionType).perform(requestParser);
	}

	protected abstract File getAdminDirectory();

	private ServerAction<?, ?> getAction(RAdminActionType actionType) {

		ServerAction<?, ?> action = serverActions.get(actionType);

		if (action == null) {

			throw new XDocumentException("Unrecognised action-type: " + actionType);
		}

		return action;
	}
}