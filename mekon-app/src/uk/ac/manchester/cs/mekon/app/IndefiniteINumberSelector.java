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
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class IndefiniteINumberSelector extends INumberSelector {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Enter Limits";
	static private final String MIN_VALUE_LABEL = "Minimum";
	static private final String MAX_VALUE_LABEL = "Maximum";

	static private final Dimension WINDOW_SIZE = new Dimension(300, 150);

	private LimitField minField = new LimitField();
	private LimitField maxField = new LimitField();

	private class LimitField extends InputField {

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

	IndefiniteINumberSelector(JComponent parent, CNumber type, boolean clearRequired) {

		super(parent, type, TITLE, clearRequired);

		minField.setOtherLimit(maxField);
	}

	INumber resolveSelection(CNumber type) {

		INumber min = getMin();
		INumber max = getMax();

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

	JComponent getInputComponent() {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(createLimitComponent(MIN_VALUE_LABEL, minField));
		panel.add(createLimitComponent(MAX_VALUE_LABEL, maxField));

		return panel;
	}

	Dimension getWindowSize() {

		return WINDOW_SIZE;
	}

	boolean validInput() {

		return !invalidRange();
	}

	boolean multipleInputFields() {

		return true;
	}

	private JComponent createLimitComponent(String label, LimitField field) {

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


