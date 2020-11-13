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

import uk.ac.manchester.cs.mekon_util.gui.*;
import uk.ac.manchester.cs.mekon_util.misc.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.*;

/**
 * @author Colin Puleston
 */
public class RLoginDialog extends EntryDialog {

	static private final long serialVersionUID = -1;

	static private final String ENTRY_TYPE_TITLE = "Login";

	static private final String USERNAME_LABEL = "Username";
	static private final String PASSWORD_LABEL = "Password";

	static private final String REGISTER_BUTTON_LABEL = "Register...";

	static private final int WINDOW_HEIGHT = 190;

	private EntryTextField usernameField = new EntryTextField();
	private EntryPasswordField passwordField = new EntryPasswordField();

	private NewPasswordChecker newPasswordChecker = new NewPasswordChecker();

	private class RegisterButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			checkRegister();
			dispose();
		}

		RegisterButton() {

			super(REGISTER_BUTTON_LABEL);
		}
	}

	public RLoginDialog(
			RLoginClient loginClient,
			KProxyPasswords proxyPasswords,
			String appName) {

		this(null, loginClient, proxyPasswords, appName);
	}

	public RLoginDialog(
			JComponent parent,
			RLoginClient loginClient,
			KProxyPasswords proxyPasswords,
			String appName) {

		super(parent, loginClient, proxyPasswords, appName, ENTRY_TYPE_TITLE);
	}

	public void setValidPasswordLength(int min, int max) {

		newPasswordChecker.setValidLength(min, max);
	}

	public RRole checkLogin() {

		return checkEntry();
	}

	void addFields(JPanel panel) {

		addField(panel, usernameField, USERNAME_LABEL);
		addField(panel, passwordField, PASSWORD_LABEL);
	}

	void addExtraButtons(JPanel panel) {

		super.addExtraButtons(panel);

		addExtraButton(panel, new RegisterButton());
	}

	int getWindowHeight() {

		return WINDOW_HEIGHT;
	}

	RUserId createUserId() {

		return new RUserId(usernameField.getText(), passwordField.getProxyPassword());
	}

	private void checkRegister() {

		setLoggedInRole(new RegisterDialog(this, newPasswordChecker).checkEntry());
	}
}
