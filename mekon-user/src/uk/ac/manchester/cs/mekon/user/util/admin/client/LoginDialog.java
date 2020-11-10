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

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.user.util.admin.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
public class LoginDialog extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "%s: Login";

	static private final String USERNAME_LABEL = "Username";
	static private final String PASSWORD_LABEL = "Password";

	static private final String OK_BUTTON_LABEL = "Ok";
	static private final String CANCEL_BUTTON_LABEL = "Cancel";

	static private final Dimension WINDOW_SIZE = new Dimension(300, 200);

	private LoginClient loginClient;

	private Role loggedInRole = Role.INVALID_USER;

	private UsernameField usernameField = new UsernameField();
	private PasswordField passwordField = new PasswordField();

	private OkButton okButton = new OkButton();

	private class UsernameField extends GTextField {

		static private final long serialVersionUID = -1;

		protected void onCharEntered(char enteredChar) {

			okButton.updateEnabling();
		}

		protected void onTextEntered(String text) {

			checkPerformLogin();
		}
	}

	private class PasswordField extends GPasswordField {

		static private final long serialVersionUID = -1;

		protected void onCharEntered(char enteredChar) {

			okButton.updateEnabling();
		}

		protected void onPasswordEntered(char[] password) {

			checkPerformLogin();
		}
	}

	private class OkButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			checkPerformLogin();
		}

		OkButton() {

			super(OK_BUTTON_LABEL);

			setEnabled(false);
		}

		void updateEnabling() {

			setEnabled(populatedInputFields());
		}
	}

	private class CancelButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			loggedInRole = Role.INVALID_USER;

			dispose();
		}

		CancelButton() {

			super(CANCEL_BUTTON_LABEL);
		}
	}

	private class WindowCloseListener extends WindowAdapter {

		public void windowClosing(WindowEvent e) {

			dispose();
		}
	}

	public LoginDialog(JComponent parent, URL serverURL, String targetName) {

		super(parent, String.format(TITLE_FORMAT, targetName), true);

		loginClient = new LoginClient(serverURL);

		setPreferredSize(WINDOW_SIZE);
		addWindowListener(new WindowCloseListener());
	}

	public boolean checkLogin() {

		display(createMainPanel());

		return loggedInRole != Role.INVALID_USER;
	}

	public Role getLoggedInRole() {

		return loggedInRole;
	}

	private JPanel createMainPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createLoginFieldsPanel(), BorderLayout.CENTER);
		panel.add(createButtonsPanel(), BorderLayout.SOUTH);

		return panel;
	}

	private JPanel createLoginFieldsPanel() {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(createLoginComponent(usernameField, USERNAME_LABEL));
		panel.add(createLoginComponent(passwordField, PASSWORD_LABEL));

		return panel;
	}

	private JPanel createButtonsPanel() {

		JPanel panel = new JPanel();

		panel.add(okButton);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(new CancelButton());

		return panel;
	}

	private JComponent createLoginComponent(JTextField field, String title) {

		JPanel panel = new JPanel(new GridLayout(1, 1));

		panel.setBorder(new TitledBorder(title));
		panel.add(field);

		return panel;
	}

	private void checkPerformLogin() {

		if (populatedInputFields()) {

			loggedInRole = loginClient.checkLogin(createUserId());

			if (loggedInRole == Role.INVALID_USER) {

				showMessage("Login failed!");
			}
			else {

				dispose();
			}
		}
	}

	private UserId createUserId() {

		return new UserId(getUsernameInput(), getPasswordInput());
	}

	private boolean populatedInputFields() {

		return !getUsernameInput().isEmpty() && !getPasswordInput().isEmpty();
	}

	private String getUsernameInput() {

		return usernameField.getText();
	}

	private String getPasswordInput() {

		return String.valueOf(passwordField.getPassword());
	}

	private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}
}
