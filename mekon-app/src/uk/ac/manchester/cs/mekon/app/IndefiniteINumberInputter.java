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

package uk.ac.manchester.cs.mekon.app;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class IndefiniteINumberInputter extends INumberInputter {

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

		private Set<ConstraintField> conflictingFields = new HashSet<ConstraintField>();

		protected void onKeyEntered(KeyEvent event) {

			for (ConstraintField field : conflictingFields) {

				field.clear();
			}

			super.onKeyEntered(event);
		}

		void setConflict(ConstraintField conflictingField) {

			conflictingFields.add(conflictingField);
			conflictingField.conflictingFields.add(this);
		}
	}

	private class LimitField extends ConstraintField {

		static private final long serialVersionUID = -1;

		private LimitField otherLimit = null;

		void setOtherLimit(LimitField otherLimit) {

			this.otherLimit = otherLimit;
			otherLimit.otherLimit = this;
		}

		boolean checkConsistentSelection() {

			if (invalidRange()) {

				otherLimit.clear();

				return false;
			}

			return true;
		}
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

	IndefiniteINumberInputter(JComponent parent, CNumber type, boolean clearRequired) {

		super(parent, type, TITLE, clearRequired);

		exactField.setConflict(minField);
		exactField.setConflict(maxField);
		minField.setOtherLimit(maxField);
	}

	INumber resolveInput(CNumber type) {

		INumber exact = exactField.getValue();

		if (exact == INVALID_VALUE) {

			return NO_VALUE;
		}

		if (exact != NO_VALUE) {

			return exact;
		}

		INumber min = getMin();
		INumber max = getMax();

		if (min == NO_VALUE && max == NO_VALUE) {

			return NO_VALUE;
		}

		if (invalidRange(min, max)) {

			return NO_VALUE;
		}

		if (min == NO_VALUE) {

			min = type.getMin();
		}

		if (max == NO_VALUE) {

			max = type.getMax();
		}

		return CNumber.range(type.getNumberType(), min, max).asINumber();
	}

	boolean validInput() {

		INumber exact = exactField.getValue();

		if (exact != NO_VALUE) {

			return exact != INVALID_VALUE;
		}

		INumber min = getMin();
		INumber max = getMax();

		if (min == NO_VALUE && max == NO_VALUE) {

			return false;
		}

		return !invalidRange(min, max);
	}

	boolean multipleInputFields() {

		return true;
	}

	private JComponent createFieldComponent(String label, ConstraintField field) {

		JPanel panel = new JPanel(new GridLayout(1, 1));

		panel.setBorder(new TitledBorder(label + ":"));
		panel.add(field);

		return panel;
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


