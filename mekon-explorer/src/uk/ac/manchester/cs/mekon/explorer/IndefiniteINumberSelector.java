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
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class IndefiniteINumberSelector extends INumberSelector {

	static private final long serialVersionUID = -1;

	static private final Dimension WINDOW_SIZE = new Dimension(200, 200);

	static private final String EXACT_VALUE_LABEL = "Exact";
	static private final String MIN_VALUE_LABEL = "Minimum";
	static private final String MAX_VALUE_LABEL = "Maximum";

	private CNumber type;

	private ComponentField exactField = new ComponentField();
	private LimitField minField = new LimitField();
	private LimitField maxField = new LimitField();

	private class ComponentField extends DefaultInputField {

		static private final long serialVersionUID = -1;

		private Set<ComponentField> conflictingFields = new HashSet<ComponentField>();

		protected void onKeyEntered(KeyEvent event) {

			for (ComponentField field : conflictingFields) {

				field.clear();
			}

			super.onKeyEntered(event);
		}

		void setConflict(ComponentField conflictingField) {

			conflictingFields.add(conflictingField);
			conflictingField.conflictingFields.add(this);
		}
	}

	private class LimitField extends ComponentField {

		static private final long serialVersionUID = -1;

		private LimitField otherLimit = null;

		void setOtherLimit(LimitField otherLimit) {

			this.otherLimit = otherLimit;
			otherLimit.otherLimit = this;
		}

		boolean checkCompatibleSelection() {

			if (invalidRange()) {

				otherLimit.clear();
				onTextUpdate();

				return false;
			}

			return true;
		}
	}

	private class IndefiniteValuePanel extends JPanel {

		static private final long serialVersionUID = -1;

		IndefiniteValuePanel() {

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			addField(EXACT_VALUE_LABEL, exactField);
			addField(MIN_VALUE_LABEL, minField);
			addField(MAX_VALUE_LABEL, maxField);

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

		private void addField(String label, ComponentField field) {

			JPanel panel = new JPanel(new GridLayout(1, 1));

			panel.setBorder(new TitledBorder(label + ":"));
			panel.add(field);

			add(panel);
		}
	}

	private class IndefiniteValueDisplay extends DataValueSelectorDisplay<INumber> {

		private IndefiniteValuePanel panel;

		IndefiniteValueDisplay() {

			panel = new IndefiniteValuePanel();
		}

		JComponent getDisplay() {

			return panel;
		}

		Dimension getDisplaySize() {

			return WINDOW_SIZE;
		}

		INumber getValue() {

			return panel.getValue();
		}

		void clear() {

			panel.clear();
		}
	}

	IndefiniteINumberSelector(JComponent parent, CNumber type) {

		super(parent, type);

		this.type = type;

		initialise(new IndefiniteValueDisplay());
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

		return CNumber.range(type.getNumberType(), min, max).asINumber();
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

