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

package uk.ac.manchester.cs.mekon.explorer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
abstract class IDataValueSelector<V extends IValue> extends GDialog {

	static private final long serialVersionUID = -1;

	static private final Dimension DEFAULT_WINDOW_SIZE = new Dimension(200, 70);

	static private final String OK_BUTTON_LABEL = "Ok";
	static private final String CANCEL_BUTTON_LABEL = "Cancel";

	private DataValueSelectorDisplay<V> display = null;
	private OkButton okButton = new OkButton();
	private boolean windowClosing = false;

	class DefaultInputField extends DataValueField<V> {

		static private final long serialVersionUID = -1;

		void updateForKeyEntered() {

			okButton.updateEnabling();
		}

		V parseValue(String text, boolean showErrorIfInvalid) {

			return IDataValueSelector.this.parseValue(text, showErrorIfInvalid);
		}

		boolean validValue(V value) {

			return IDataValueSelector.this.validValue(value);
		}

		void disposeWindow() {

			dispose();
		}

		boolean windowClosing() {

			return windowClosing;
		}
	}

	private class DefaultDisplay extends DataValueSelectorDisplay<V> {

		private DefaultInputField inputField;

		DefaultDisplay() {

			inputField = new DefaultInputField();
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

			if (currentValidValue()) {

				dispose();
			}
		}

		OkButton() {

			super(OK_BUTTON_LABEL);

			setEnabled(false);
			setSmallFont();
		}

		void updateEnabling() {

			setEnabled(currentValidValue());
		}
	}

	private class CancelButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			display.clear();
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

		initialise(new DefaultDisplay());
	}

	void initialise(DataValueSelectorDisplay<V> display) {

		this.display = display;

		addWindowListener(new WindowCloseListener());
		display(createDisplay());
	}

	V getSelectionOrNull() {

		V value = display.getValue();

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
		panel.add(display.getDisplay());
		panel.add(createButtonsComponent());
		panel.setPreferredSize(display.getDisplaySize());

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

	private boolean currentValidValue() {

		return validValue(display.getValue());
	}
}
