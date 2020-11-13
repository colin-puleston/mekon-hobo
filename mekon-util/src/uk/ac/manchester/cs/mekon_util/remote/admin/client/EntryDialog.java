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
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon_util.gui.*;
import uk.ac.manchester.cs.mekon_util.misc.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.*;

/**
 * @author Colin Puleston
 */
abstract class EntryDialog extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "%s: %s";

	static private final String OK_BUTTON_LABEL = "Ok";
	static private final String CANCEL_BUTTON_LABEL = "Cancel";

	static private final int WINDOW_WIDTH = 350;

	private JComponent parent;

	private RLoginClient loginClient;
	private KProxyPasswords proxyPasswords;
	private String appName;

	private RRole loggedInRole = RRole.NO_ACCESS;

	private List<JTextField> fields = new ArrayList<JTextField>();
	private OkButton okButton = new OkButton();

	class EntryTextField extends GTextField {

		static private final long serialVersionUID = -1;

		protected void onCharEntered(char enteredChar) {

			okButton.updateEnabling();
		}

		protected void onTextEntered(String text) {

			checkPerformLogin();
		}
	}

	class EntryPasswordField extends GPasswordField {

		static private final long serialVersionUID = -1;

		protected void onCharEntered(char enteredChar) {

			okButton.updateEnabling();
		}

		protected void onPasswordEntered(char[] password) {

			checkPerformLogin();
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

			checkPerformLogin();
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

			loggedInRole = RRole.NO_ACCESS;

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

	EntryDialog(EntryDialog parentDialog, String entryType) {

		this(
			parentDialog.parent,
			parentDialog.loginClient,
			parentDialog.proxyPasswords,
			parentDialog.appName,
			entryType);
	}

	EntryDialog(
		JComponent parent,
		RLoginClient loginClient,
		KProxyPasswords proxyPasswords,
		String appName,
		String entryType) {

		super(parent, String.format(TITLE_FORMAT, appName, entryType), true);

		this.parent = parent;
		this.loginClient = loginClient;
		this.proxyPasswords = proxyPasswords;
		this.appName = appName;

		setPreferredSize(new Dimension(WINDOW_WIDTH, getWindowHeight()));
		addWindowListener(new WindowCloseListener());
	}

	RRole checkEntry() {

		display(createMainPanel());

		return loggedInRole;
	}

	abstract void addFields(JPanel panel);

	void addExtraButtons(JPanel panel) {
	}

	void addField(JPanel panel, JTextField field, String title) {

		panel.add(createFieldComponent(field, title));

		fields.add(field);
	}

	void addExtraButton(JPanel panel, GButton button) {

		panel.add(Box.createHorizontalStrut(10));
		panel.add(button);
	}

	abstract int getWindowHeight();

	boolean checkCanPerformLogin() {

		return allFieldsPopulated();
	}

	abstract RUserId createUserId();

	void setLoggedInRole(RRole role) {

		loggedInRole = role;
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

		addExtraButton(panel, new CancelButton());
		addExtraButtons(panel);

		return panel;
	}

	private JComponent createFieldComponent(JTextField field, String title) {

		JPanel panel = new JPanel(new GridLayout(1, 1));

		panel.setBorder(new TitledBorder(title));
		panel.add(field);

		return panel;
	}

	private void checkPerformLogin() {

		if (checkCanPerformLogin()) {

			loggedInRole = loginClient.checkLogin(createUserId());

			if (loggedInRole == RRole.NO_ACCESS) {

				showMessage("Login failed!");
			}
			else {

				dispose();
			}
		}
	}

	private boolean allFieldsPopulated() {

		for (JTextField field : fields) {

			if (field.getText().isEmpty()) {

				return false;
			}
		}

		return true;
	}

	private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}
}
