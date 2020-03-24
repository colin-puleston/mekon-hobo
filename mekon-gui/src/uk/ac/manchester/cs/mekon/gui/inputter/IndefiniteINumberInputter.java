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

package uk.ac.manchester.cs.mekon.gui.inputter;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
public class IndefiniteINumberInputter extends INumberInputter {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Enter Limits";

	static private final String EXACT_VALUE_LABEL = "Exact";
	static private final String MIN_VALUE_LABEL = "Minimum";
	static private final String MAX_VALUE_LABEL = "Maximum";

	static private final Dimension WINDOW_SIZE = new Dimension(300, 200);

	private ConstraintField exactField = new ConstraintField();
	private LimitField minField = new LimitField();
	private LimitField maxField = new LimitField();

	private class ConstraintField extends InputField {

		static private final long serialVersionUID = -1;

		private Set<ConstraintField> incompatibleFields = new HashSet<ConstraintField>();

		protected void onCharEntered(char enteredChar) {

			for (ConstraintField field : incompatibleFields) {

				field.clear();
			}

			super.onCharEntered(enteredChar);
		}

		void setIncompatible(ConstraintField incompatibleField) {

			incompatibleFields.add(incompatibleField);
			incompatibleField.incompatibleFields.add(this);
		}

		boolean hasValue() {

			return getValue() != NO_VALUE;
		}

		boolean invalidValue() {

			return getValue() == INVALID_VALUE;
		}
	}

	private class LimitField extends ConstraintField {

		static private final long serialVersionUID = -1;

		private LimitField otherLimit = null;

		void setOtherLimit(LimitField otherLimit) {

			this.otherLimit = otherLimit;
			otherLimit.otherLimit = this;
		}

		boolean checkConsistentInput() {

			if (invalidRange()) {

				otherLimit.clear();

				return false;
			}

			return true;
		}
	}

	public IndefiniteINumberInputter(JComponent parent, CNumber type, boolean clearRequired) {

		super(parent, type, TITLE, clearRequired);

		exactField.setIncompatible(minField);
		exactField.setIncompatible(maxField);
		minField.setOtherLimit(maxField);
	}

	protected JComponent getInputComponent() {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(createFieldComponent(EXACT_VALUE_LABEL, exactField));
		panel.add(createFieldComponent(MIN_VALUE_LABEL, minField));
		panel.add(createFieldComponent(MAX_VALUE_LABEL, maxField));

		return panel;
	}

	protected Dimension getWindowSize() {

		return WINDOW_SIZE;
	}

	protected INumber resolveInput(CNumber type) {

		if (!anyValidValues()) {

			return NO_VALUE;
		}

		if (exactField.hasValue()) {

			return exactField.getValue();
		}

		if (invalidRange()) {

			return NO_VALUE;
		}

		return resolveValidRange(type);
	}

	protected boolean validInput() {

		return anyValidValues() && !invalidRange();
	}

	protected boolean multipleInputFields() {

		return true;
	}

	private JComponent createFieldComponent(String label, ConstraintField field) {

		JPanel panel = new JPanel(new GridLayout(1, 1));

		panel.setBorder(new TitledBorder(label + ":"));
		panel.add(field);

		return panel;
	}

	private boolean invalidRange() {

		if (minField.hasValue() && maxField.hasValue()) {

			return minField.getValue().moreThan(maxField.getValue());
		}

		return false;
	}

	private boolean anyValidValues() {

		return anyValues() && !anyInvalidValues();
	}

	private boolean anyValues() {

		return exactField.hasValue() || minField.hasValue() || maxField.hasValue();
	}

	private boolean anyInvalidValues() {

		return exactField.invalidValue() || minField.invalidValue() || maxField.invalidValue();
	}

	private INumber resolveValidRange(CNumber type) {

		INumber min = minField.getValue();
		INumber max = maxField.getValue();

		if (min == NO_VALUE) {

			min = type.getMin();
		}

		if (max == NO_VALUE) {

			max = type.getMax();
		}

		return CNumber.range(type.getNumberType(), min, max).asINumber();
	}
}
