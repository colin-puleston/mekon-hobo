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

import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.misc.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.*;

/**
 * @author Colin Puleston
 */
public class RPasswordUpdateDialog extends RLoginUpdateDialog {

	static private final long serialVersionUID = -1;

	static private final String ACTION_TYPE_TITLE = "Password Update";

	static private final String PASSWORD_LABEL = "Current password";
	static private final String NEW_PASSWORD_DESCRIPTION = "new password";

	private LoginPasswordField passwordField = new LoginPasswordField();

	public RPasswordUpdateDialog(
				RAdminClient adminClient,
				KProxyPasswords proxyPasswords,
				String appName) {

		super(adminClient, proxyPasswords, appName, ACTION_TYPE_TITLE);
	}

	void addValidationField(JPanel panel) {

		addField(panel, passwordField, PASSWORD_LABEL);
	}

	String getNewPasswordDescription() {

		return NEW_PASSWORD_DESCRIPTION;
	}

	RLoginId createLoginId(String username, String newPassword) {

		return new RLoginId(username, passwordField.getProxyPassword(), newPassword);
	}

	void checkShowLoginSuccessMessage() {

		showMessage("Password updated for user \"" + getUserName() + "\"");
	}
}
