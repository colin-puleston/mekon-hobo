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

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon_util.gui.*;
import uk.ac.manchester.cs.mekon_util.misc.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.*;

/**
 * @author Colin Puleston
 */
abstract class RLoginActionDialog extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "%s: %s";

	static private final String USERNAME_LABEL = "Username";

	static private final String OK_BUTTON_LABEL = "Ok";
	static private final String CANCEL_BUTTON_LABEL = "Cancel";

	static private final int WINDOW_WIDTH = 350;
	static private final int WINDOW_BASE_HEIGHT = 80;
	static private final int FIELD_HEIGHT = 55;

	private RAdminClient adminClient;
	private KProxyPasswords proxyPasswords;

	private boolean administratorLoginOnly = false;

	private RLoginResult loginResult = RLoginResult.LOGIN_FAILED;

	private List<JTextField> fields = new ArrayList<JTextField>();
	private LoginTextField usernameField = new LoginTextField();

	private OkButton okButton = new OkButton();

	class LoginTextField extends GTextField {

		static private final long serialVersionUID = -1;

		protected void onCharEntered(char enteredChar) {

			okButton.updateEnabling();
		}

		protected void onTextEntered(String text) {

			checkAttemptLogin();
		}
	}

	class LoginPasswordField extends GPasswordField {

		static private final long serialVersionUID = -1;

		protected void onCharEntered(char enteredChar) {

			okButton.updateEnabling();
		}

		protected void onPasswordEntered(char[] password) {

			checkAttemptLogin();
		}

		String getPasswordInput() {

			return String.valueOf(getPassword());
		}

		String getProxyPassword() {

			return proxyPasswords.toProxy(getPassword());
		}
	}

	private class OkButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			checkAttemptLogin();
		}

		OkButton() {

			super(OK_BUTTON_LABEL);

			setEnabled(false);
		}

		void updateEnabling() {

			setEnabled(allFieldsPopulated());
		}
	}

	private class CancelButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			loginResult = RLoginResult.LOGIN_FAILED;

			dispose();
		}

		CancelButton() {

			super(CANCEL_BUTTON_LABEL);
		}
	}

	public void setAdministratorLoginOnly(boolean value) {

		administratorLoginOnly = value;
	}

	public RLoginResult checkLoginAction() {

		JPanel mainPanel = createMainPanel();

		setPreferredSize(getWindowSize());
		display(mainPanel);

		return loginResult;
	}

	public String getUserName() {

		return usernameField.getText();
	}

	RLoginActionDialog(
		RAdminClient adminClient,
		KProxyPasswords proxyPasswords,
		String appName,
		String entryType) {

		super(String.format(TITLE_FORMAT, appName, entryType), true);

		this.adminClient = adminClient;
		this.proxyPasswords = proxyPasswords;

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	void addFields(JPanel panel) {

		addField(panel, usernameField, USERNAME_LABEL);
	}

	void addField(JPanel panel, JTextField field, String title) {

		panel.add(createFieldComponent(field, title));

		fields.add(field);
	}

	boolean checkCanAttemptLogin() {

		return allFieldsPopulated();
	}

	abstract RLoginId createLoginId(String username);

	void checkShowLoginSuccessMessage() {
	}

	void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}

	private JPanel createMainPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createFieldsPanel(), BorderLayout.CENTER);
		panel.add(createButtonsPanel(), BorderLayout.SOUTH);

		return panel;
	}

	private JPanel createFieldsPanel() {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		addFields(panel);

		return panel;
	}

	private JPanel createButtonsPanel() {

		JPanel panel = new JPanel();

		panel.add(okButton);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(new CancelButton());

		return panel;
	}

	private JComponent createFieldComponent(JTextField field, String title) {

		JPanel panel = new JPanel(new GridLayout(1, 1));

		panel.setBorder(new TitledBorder(title));
		panel.add(field);

		return panel;
	}

	private Dimension getWindowSize() {

		return new Dimension(WINDOW_WIDTH, getWindowHeight());
	}

	private int getWindowHeight() {

		return WINDOW_BASE_HEIGHT + (FIELD_HEIGHT * fields.size());
	}

	private void checkAttemptLogin() {

		if (checkCanAttemptLogin() && attemptLogin()) {

			dispose();
		}
	}

	private boolean attemptLogin() {

		loginResult = adminClient.checkLogin(createLoginId());

		if (loginResult.loginOk()) {

			if (!administratorLoginFail()) {

				checkShowLoginSuccessMessage();

				return true;
			}

			loginResult = RLoginResult.LOGIN_FAILED;

			showMessage("Administrator login failed!");
		}
		else {

			showMessage("Login failed!");
		}

		return false;
	}

	private RLoginId createLoginId() {

		return createLoginId(usernameField.getText());
	}

	private boolean administratorLoginFail() {

		return administratorLoginOnly && loginResult.getRole() != RRole.ADMIN;
	}

	private boolean allFieldsPopulated() {

		for (JTextField field : fields) {

			if (field.getText().isEmpty()) {

				return false;
			}
		}

		return true;
	}
}
