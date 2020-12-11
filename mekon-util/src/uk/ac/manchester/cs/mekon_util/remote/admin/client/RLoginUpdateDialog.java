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
public abstract class RLoginUpdateDialog extends RLoginActionDialog {

	static private final long serialVersionUID = -1;

	static private final String PASSWORD_SELECT_LABEL_FORMAT = "Select %s (%s)";
	static private final String PASSWORD_CONFIRM_LABEL_FORMAT = "Confirm %s";

	private LoginPasswordField passwordSelectField = new LoginPasswordField();
	private LoginPasswordField passwordConfirmField = new LoginPasswordField();

	private NewPasswordChecker newPasswordChecker = new NewPasswordChecker();

	public RLoginUpdateDialog(
				RAdminClient adminClient,
				KProxyPasswords proxyPasswords,
				String appName,
				String updateType) {

		super(adminClient, proxyPasswords, appName, updateType);
	}

	public void setValidPasswordLength(int min, int max) {

		newPasswordChecker.setValidLength(min, max);
	}

	void addFields(JPanel panel) {

		super.addFields(panel);

		addValidationField(panel);

		addField(panel, passwordSelectField, getPasswordSelectLabel());
		addField(panel, passwordConfirmField, getPasswordConfirmLabel());
	}

	abstract void addValidationField(JPanel panel);

	abstract String getNewPasswordDescription();

	boolean checkCanAttemptLogin() {

		return super.checkCanAttemptLogin() && checkPasswordInput();
	}

	RLoginId createLoginId(String username) {

		return createLoginId(username, passwordSelectField.getProxyPassword());
	}

	abstract RLoginId createLoginId(String username, String newPassword);

	private String getPasswordSelectLabel() {

		String validLenDesc = newPasswordChecker.getValidLengthDescription();
		String newPwdDesc = getNewPasswordDescription();

		return String.format(PASSWORD_SELECT_LABEL_FORMAT, newPwdDesc, validLenDesc);
	}

	private String getPasswordConfirmLabel() {

		String newPwdDesc = getNewPasswordDescription();

		return String.format(PASSWORD_CONFIRM_LABEL_FORMAT, newPwdDesc);
	}

	private boolean checkPasswordInput() {

		String input1 = passwordSelectField.getPasswordInput();
		String input2 = passwordConfirmField.getPasswordInput();

		if (newPasswordChecker.check(input1, input2)) {

			return true;
		}

		passwordSelectField.setText("");
		passwordConfirmField.setText("");

		return false;
	}
}
