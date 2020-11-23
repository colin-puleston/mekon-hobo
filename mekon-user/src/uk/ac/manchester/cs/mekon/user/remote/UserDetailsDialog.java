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

package uk.ac.manchester.cs.mekon.user.remote;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon_util.remote.admin.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class UserDetailsDialog extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "User Details";

	static private final String NAME_LABEL = "Name";
	static private final String USER_ROLE_LABEL = "Role";

	static private final String OK_BUTTON_LABEL = "Ok";
	static private final String CANCEL_BUTTON_LABEL = "Cancel";

	static private final Dimension WINDOW_SIZE = new Dimension(250, 200);

	private String userName = null;
	private String roleName = null;

	private OkButton okButton = new OkButton();

	private class NameField extends GTextField {

		static private final long serialVersionUID = -1;

		protected void onCharEntered(char enteredChar) {

			userName = getText();

			okButton.updateEnabling();
		}

		protected void onTextEntered(String text) {

			userName = text;

			okButton.updateEnabling();
		}

		NameField(boolean newUser) {

			setEnabled(newUser);
		}
	}

	private class RoleSelector extends GStringSelectorBox {

		static private final long serialVersionUID = -1;

		static private final String NO_SELECTION_VALUE = "";

		protected void onSelection(String value) {

			roleName = value;

			removeItem(NO_SELECTION_VALUE);
			okButton.updateEnabling();
		}

		RoleSelector(List<String> roleNames) {

			addOption(NO_SELECTION_VALUE);
			addOptions(roleNames);

			activate();
		}
	}

	private class OkButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			dispose();
		}

		OkButton() {

			super(OK_BUTTON_LABEL);

			setEnabled(false);
		}

		void updateEnabling() {

			setEnabled(detailsOk());
		}
	}

	private class CancelButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			userName = null;
			roleName = null;

			dispose();
		}

		CancelButton() {

			super(CANCEL_BUTTON_LABEL);
		}
	}

	UserDetailsDialog(JComponent parent, List<String> roleNames, boolean newUser) {

		super(parent, TITLE, true);

		setPreferredSize(WINDOW_SIZE);

		display(createMainPanel(roleNames, newUser));
	}

	void setUserName(String userName) {

		this.userName = userName;
	}

	void setRoleName(String roleName) {

		this.roleName = roleName;
	}

	String getUserName() {

		return userName;
	}

	String getRoleName() {

		return roleName;
	}

	boolean detailsOk() {

		return userName != null && roleName != null;
	}

	private JPanel createMainPanel(List<String> roleNames, boolean newUser) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createDetailsPanel(roleNames, newUser), BorderLayout.CENTER);
		panel.add(createButtonsPanel(), BorderLayout.SOUTH);

		return panel;
	}

	private JPanel createDetailsPanel(List<String> roleNames, boolean newUser) {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(createTitledPanel(NAME_LABEL, new NameField(newUser)));
		panel.add(createTitledPanel(USER_ROLE_LABEL, new RoleSelector(roleNames)));

		return panel;
	}

	private JPanel createButtonsPanel() {

		JPanel panel = new JPanel();

		panel.add(okButton);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(new CancelButton());

		return panel;
	}

	private JPanel createTitledPanel(String title, JComponent component) {

		JPanel panel = new JPanel(new GridLayout(1, 1));

		panel.setBorder(new TitledBorder(title));
		panel.add(component);

		return panel;
	}
}
