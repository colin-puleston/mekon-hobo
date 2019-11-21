/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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

package uk.ac.manchester.cs.mekon.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
abstract class IDataValueSelector<V extends IValue> extends GDialog {

	static private final long serialVersionUID = -1;

	static private final Dimension DEFAULT_WINDOW_SIZE = new Dimension(200, 70);

	static private final String OK_BUTTON_LABEL = "Ok";
	static private final String CANCEL_BUTTON_LABEL = "Cancel";

	private ValueSelector valueSelector = null;
	private OkButton okButton = new OkButton();
	private boolean windowClosing = false;

	class InputField extends GTextField {

		static private final long serialVersionUID = -1;

		private boolean checkingInput = false;

		protected void onKeyEntered(KeyEvent event) {

			okButton.updateEnabling();
		}

		protected void onFieldExited(String text) {

			if (!windowClosing) {

				checkInput(text);
			}
		}

		protected void onTextEntered(String text) {

			if (checkInput(text)) {

				dispose();
			}
		}

		V getValue() {

			return parseValue(getText(), false);
		}

		void clear() {

			setText("");
		}

		boolean checkCompatibleSelection() {

			return true;
		}

		private boolean checkInput(String text) {

			boolean ok = false;

			if (!checkingInput) {

				checkingInput = true;

				if (validValue(parseValue(text, true))) {

					ok = checkCompatibleSelection();
				}
				else {

					clear();
				}

				checkingInput = false;
			}

			return ok;
		}
	}

	abstract class ValueSelector {

		boolean currentValidValue() {

			return validValue(getValue());
		}

		abstract JComponent getDisplay();

		abstract Dimension getDisplaySize();

		abstract V getValue();

		abstract void clear();
	}

	class DefaultValueSelector extends ValueSelector {

		private InputField inputField;

		DefaultValueSelector() {

			inputField = new InputField();
		}

		JComponent getDisplay() {

			return inputField;
		}

		Dimension getDisplaySize() {

			return DEFAULT_WINDOW_SIZE;
		}

		V getValue() {

			return inputField.getValue();
		}

		void clear() {

			inputField.clear();
		}
	}

	private class WindowCloseListener extends WindowAdapter {

		public void windowClosing(WindowEvent e) {

			windowClosing = true;
		}
	}

	private class OkButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			if (valueSelector.currentValidValue()) {

				dispose();
			}
		}

		OkButton() {

			super(OK_BUTTON_LABEL);

			setEnabled(false);
			setSmallFont();
		}

		void updateEnabling() {

			setEnabled(valueSelector.currentValidValue());
		}
	}

	private class CancelButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			valueSelector.clear();
			dispose();
		}

		CancelButton() {

			super(CANCEL_BUTTON_LABEL);

			setSmallFont();
		}
	}

	IDataValueSelector(JComponent parent, CValue<?> type) {

		super(parent, type.getDisplayLabel(), true);
	}

	void initialise() {

		initialise(new DefaultValueSelector());
	}

	void initialise(ValueSelector valueSelector) {

		this.valueSelector = valueSelector;

		addWindowListener(new WindowCloseListener());
		display(createDisplay());
	}

	V getSelectionOrNull() {

		V value = valueSelector.getValue();

		return validValue(value) ? value : null;
	}

	abstract V checkParseValue(String text);

	abstract V getNoValueObject();

	abstract V getInvalidValueObject();

	void onTextUpdate() {

		okButton.updateEnabling();
	}

	boolean validValue(V value) {

		return value != getNoValueObject() && value != getInvalidValueObject();
	}

	private JComponent createDisplay() {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(valueSelector.getDisplay());
		panel.add(createButtonsComponent());
		panel.setPreferredSize(valueSelector.getDisplaySize());

		return panel;
	}

	private JComponent createButtonsComponent() {

		JPanel panel = new JPanel();

		panel.add(okButton);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(new CancelButton());

		return panel;
	}

	private V parseValue(String text, boolean showErrorIfInvalid) {

		if (text.length() == 0) {

			return getNoValueObject();
		}

		V value = checkParseValue(text);

		if (value != null) {

			return value;
		}

		if (showErrorIfInvalid) {

			JOptionPane.showMessageDialog(null, "Invalid Input!");
		}

		return getInvalidValueObject();
	}
}

