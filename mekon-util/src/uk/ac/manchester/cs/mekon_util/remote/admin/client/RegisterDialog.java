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

import uk.ac.manchester.cs.mekon_util.remote.admin.*;

/**
 * @author Colin Puleston
 */
class RegisterDialog extends EntryDialog {

	static private final long serialVersionUID = -1;

	static private final String ENTRY_TYPE_TITLE = "Register";

	static private final String USERNAME_LABEL = "Username";
	static private final String REG_TOKEN_LABEL = "Registration Token";
	static private final String PASSWORD_1_LABEL_FORMAT = "Select Password (%s)";
	static private final String PASSWORD_2_LABEL = "Confirm Password";

	static private final int WINDOW_HEIGHT = 300;

	private EntryTextField usernameField = new EntryTextField();
	private EntryTextField regTokenField = new EntryTextField();
	private EntryPasswordField password1Field = new EntryPasswordField();
	private EntryPasswordField password2Field = new EntryPasswordField();

	private NewPasswordChecker newPasswordChecker;

	RegisterDialog(RLoginDialog parentDialog, NewPasswordChecker newPasswordChecker) {

		super(parentDialog, ENTRY_TYPE_TITLE);

		this.newPasswordChecker = newPasswordChecker;
	}

	void addFields(JPanel panel) {

		addField(panel, usernameField, USERNAME_LABEL);
		addField(panel, regTokenField, REG_TOKEN_LABEL);
		addField(panel, password1Field, getPassword1Label());
		addField(panel, password2Field, PASSWORD_2_LABEL);
	}

	int getWindowHeight() {

		return WINDOW_HEIGHT;
	}

	boolean checkCanPerformLogin() {

		return super.checkCanPerformLogin() && checkPasswordInput();
	}

	RLoginId createLoginId() {

		return new RLoginId(
					usernameField.getText(),
					regTokenField.getText(),
					password1Field.getProxyPassword());
	}

	private String getPassword1Label() {

		String validLenDesc = newPasswordChecker.getValidLengthDescription();

		return String.format(PASSWORD_1_LABEL_FORMAT, validLenDesc);
	}

	private boolean checkPasswordInput() {

		String input1 = password1Field.getPasswordInput();
		String input2 = password2Field.getPasswordInput();

		if (newPasswordChecker.check(input1, input2)) {

			return true;
		}

		password1Field.setText("");
		password2Field.setText("");

		return false;
	}
}
