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

package uk.ac.manchester.cs.mekon.user.util.admin.client;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.user.util.admin.*;

/**
 * @author Colin Puleston
 */
class RegisterDialog extends EntryDialog {

	static private final long serialVersionUID = -1;

	static private final int MIN_PASSWORD_LENGTH = 8;
	static private final int MAX_PASSWORD_LENGTH = 20;

	static private final String ENTRY_TYPE_TITLE = "Register";

	static private final String PASSWORD_LENGTH_MSG
									= "" + MIN_PASSWORD_LENGTH
									+ " - " + MAX_PASSWORD_LENGTH
									+ " characters";

	static private final String USERNAME_LABEL = "Username";
	static private final String REG_TOKEN_LABEL = "Registration token";
	static private final String PASSWORD_1_LABEL = "Select password (" + PASSWORD_LENGTH_MSG + ")";
	static private final String PASSWORD_2_LABEL = "Confirm password";

	static private final int WINDOW_HEIGHT = 300;

	private EntryTextField usernameField = new EntryTextField();
	private EntryTextField regTokenField = new EntryTextField();
	private EntryPasswordField password1Field = new EntryPasswordField();
	private EntryPasswordField password2Field = new EntryPasswordField();

	RegisterDialog(LoginDialog parentDialog) {

		super(parentDialog, ENTRY_TYPE_TITLE);
	}

	void addFields(JPanel panel) {

		addField(panel, usernameField, USERNAME_LABEL);
		addField(panel, regTokenField, REG_TOKEN_LABEL);
		addField(panel, password1Field, PASSWORD_1_LABEL);
		addField(panel, password2Field, PASSWORD_2_LABEL);
	}

	int getWindowHeight() {

		return WINDOW_HEIGHT;
	}

	boolean checkCanPerformLogin() {

		return super.checkCanPerformLogin() && checkPasswordsMatch();
	}

	UserId createUserId() {

		return new UserIdUpdate(
					usernameField.getText(),
					regTokenField.getText(),
					password1Field.getProxyPassword());
	}

	private boolean checkPasswordsMatch() {

		if (checkIdenticalPasswords() && checkPasswordLength()) {

			return true;
		}

		password1Field.setText("");
		password2Field.setText("");

		return false;
	}

	private boolean checkIdenticalPasswords() {

		if (password1Field.passwordsMatch(password2Field)) {

			return true;
		}

		showInvalidPasswordInputMessage("Passwords are not identical!");

		return false;
	}

	private boolean checkPasswordLength() {

		if (password1Field.passwordLengthOk(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH)) {

			return true;
		}

		showInvalidPasswordInputMessage("Password must contain " + PASSWORD_LENGTH_MSG);

		return false;
	}

	private void showInvalidPasswordInputMessage(String msg) {

		showMessage("Invalid password input: " + msg);
	}

	private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}
}
