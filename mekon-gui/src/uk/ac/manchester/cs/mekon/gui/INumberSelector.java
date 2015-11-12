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
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class INumberSelector extends GDialog {

	static private final long serialVersionUID = -1;

	static private final Dimension DEFINITE_VALUE_WINDOW_SIZE = new Dimension(200, 70);
	static private final Dimension INDEFINITE_VALUE_WINDOW_SIZE = new Dimension(200, 200);

	static private final String EXACT_VALUE_LABEL = "Exact";
	static private final String MIN_VALUE_LABEL = "Minimum";
	static private final String MAX_VALUE_LABEL = "Maximum";

	static private final String OK_BUTTON_LABEL = "Ok";
	static private final String CANCEL_BUTTON_LABEL = "Cancel";

	static private final INumber NO_VALUE = new INumber(0);
	static private final INumber INVALID_VALUE = new INumber(0);

	private CNumber type;

	private ValueSelector valueSelector;
	private OkButton okButton = new OkButton();
	private boolean windowClosing = false;

	private class WindowCloseListener extends WindowAdapter {

		public void windowClosing(WindowEvent e) {

			windowClosing = true;
		}
	}

	private class InputField extends GTextField {

		static private final long serialVersionUID = -1;

		private boolean checkingInput = false;

		protected void onTextEntered(String text) {

			if (checkInput(text)) {

				dispose();
			}
		}

		protected void onFieldExited(String text) {

			if (!windowClosing) {

				checkInput(text);
			}
		}

		protected void onKeyEntered(KeyEvent event) {

			okButton.updateEnabling();
		}

		INumber getValue() {

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

	private class IndefiniteValueDisplay extends JPanel {

		static private final long serialVersionUID = -1;

		private ComponentField exactField;
		private LimitField minField;
		private LimitField maxField;

		private class ComponentField extends InputField {

			static private final long serialVersionUID = -1;

			private Set<InputField> conflictingFields = new HashSet<InputField>();

			protected void onKeyEntered(KeyEvent event) {

				for (InputField field : conflictingFields) {

					field.clear();
				}

				super.onKeyEntered(event);
			}

			ComponentField(String label) {

				addField(label, this);
			}

			void setConflict(ComponentField conflictingField) {

				conflictingFields.add(conflictingField);
				conflictingField.conflictingFields.add(this);
			}
		}

		private class LimitField extends ComponentField {

			static private final long serialVersionUID = -1;

			private LimitField otherLimitField = null;

			LimitField(String label) {

				super(label);
			}

			void setOtherLimit(LimitField otherLimitField) {

				this.otherLimitField = otherLimitField;
				otherLimitField.otherLimitField = this;
			}

			boolean checkCompatibleSelection() {

				if (invalidRange()) {

					otherLimitField.clear();
					okButton.updateEnabling();

					return false;
				}

				return true;
			}
		}

		IndefiniteValueDisplay() {

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			exactField = new ComponentField(EXACT_VALUE_LABEL);
			minField = new LimitField(MIN_VALUE_LABEL);
			maxField = new LimitField(MAX_VALUE_LABEL);

			exactField.setConflict(minField);
			exactField.setConflict(maxField);
			minField.setOtherLimit(maxField);
		}

		INumber getValue() {

			INumber exact = exactField.getValue();

			return validValue(exact) ? exact : getRangeValue();
		}

		void clear() {

			exactField.clear();
			minField.clear();
			maxField.clear();
		}

		private void addField(String label, InputField field) {

			JPanel panel = new JPanel(new GridLayout(1, 1));

			panel.setBorder(new TitledBorder(label + ":"));
			panel.add(field);

			add(panel);
		}

		private INumber getRangeValue() {

			INumber min = getMin();
			INumber max = getMax();

			if (min == NO_VALUE && max == NO_VALUE) {

				return NO_VALUE;
			}

			return invalidRange(min, max) ? INVALID_VALUE : getRangeValue(min, max);
		}

		private INumber getRangeValue(INumber min, INumber max) {

			if (min == NO_VALUE) {

				min = type.getMin();
			}
			else if (max == NO_VALUE) {

				max = type.getMax();
			}

			return CNumber.range(min, max).asINumber();
		}

		private boolean invalidRange() {

			return invalidRange(getMin(), getMax());
		}

		private boolean invalidRange(INumber min, INumber max) {

			if (min == INVALID_VALUE || max == INVALID_VALUE) {

				return true;
			}

			return min != NO_VALUE && max != NO_VALUE && min.moreThan(max);
		}

		private INumber getMin() {

			return minField.getValue();
		}

		private INumber getMax() {

			return maxField.getValue();
		}
	}

	private abstract class ValueSelector {

		boolean currentValidValue() {

			return validValue(getValue());
		}

		abstract JComponent getDisplay();

		abstract Dimension getDisplaySize();

		abstract INumber getValue();

		abstract void clear();
	}

	private class DefiniteValueSelector extends ValueSelector {

		private InputField inputField;

		DefiniteValueSelector() {

			inputField = new InputField();
		}

		JComponent getDisplay() {

			return inputField;
		}

		Dimension getDisplaySize() {

			return DEFINITE_VALUE_WINDOW_SIZE;
		}

		INumber getValue() {

			return inputField.getValue();
		}

		void clear() {

			inputField.clear();
		}
	}

	private class IndefiniteValueSelector extends ValueSelector {

		private IndefiniteValueDisplay display;

		IndefiniteValueSelector() {

			display = new IndefiniteValueDisplay();
		}

		JComponent getDisplay() {

			return display;
		}

		Dimension getDisplaySize() {

			return INDEFINITE_VALUE_WINDOW_SIZE;
		}

		INumber getValue() {

			return display.getValue();
		}

		void clear() {

			display.clear();
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

	INumberSelector(JComponent parent, CNumber type, boolean rangeEnabled) {

		super(parent, type.getDisplayLabel(), true);

		this.type = type;

		valueSelector = createValueSelector(rangeEnabled);

		addWindowListener(new WindowCloseListener());
		display(createDisplay());
	}

	INumber getSelectionOrNull() {

		INumber value = valueSelector.getValue();

		return validValue(value) ? value : null;
	}

	private ValueSelector createValueSelector(boolean rangeEnabled) {

		return rangeEnabled
					? new IndefiniteValueSelector()
					: new DefiniteValueSelector();
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

	private INumber parseValue(String value, boolean showErrorIfInvalid) {

		if (value.length() == 0) {

			return NO_VALUE;
		}

		if (type.validNumberValue(value)) {

			return new INumber(type.getNumberType(), value);
		}

		if (showErrorIfInvalid) {

			JOptionPane.showMessageDialog(null, "Invalid Input!");
		}

		return INVALID_VALUE;
	}

	private boolean validValue(INumber value) {

		return value != NO_VALUE && value != INVALID_VALUE;
	}
}

