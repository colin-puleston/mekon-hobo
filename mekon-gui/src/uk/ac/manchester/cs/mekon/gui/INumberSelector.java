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

	static private final Dimension EXACT_VALUE_WINDOW_SIZE = new Dimension(200, 70);
	static private final Dimension RANGE_VALUE_WINDOW_SIZE = new Dimension(200, 200);

	static private final String EXACT_VALUE_LABEL = "Exact";
	static private final String MIN_VALUE_LABEL = "Minimum";
	static private final String MAX_VALUE_LABEL = "Maximum";

	private CNumber type;
	private INumber selection = null;

	private class InputField extends GTextField {

		static private final long serialVersionUID = -1;

		private Set<InputField> conflictingFields = new HashSet<InputField>();

		protected void onTextEntered(String text) {

			INumber value = parseValue(text);

			if (value != null) {

				selection = getSelection(value);

				dispose();
			}
		}

		protected void onFieldExited(String text) {

			INumber value = parseValue(text);

			if (value != null) {

				onValueEntered(value);
			}
		}

		protected void onKeyPressed(KeyEvent event) {

			for (InputField field : conflictingFields) {

				field.clear();
			}
		}

		void addConflictingField(InputField conflictingField) {

			conflictingFields.add(conflictingField);
		}

		void clear() {

			setText("");
		}

		void onValueEntered(INumber value) {
		}

		INumber getSelection(INumber value) {

			return value;
		}
	}

	private class RangeDisplay extends JPanel {

		static private final long serialVersionUID = -1;

		private InputField exactField = new InputField();

		private LimitField minField = new LimitField();
		private LimitField maxField = new LimitField();

		private class LimitField extends InputField {

			static private final long serialVersionUID = -1;

			private INumber limit = null;
			private LimitField otherLimitField = null;

			void setOtherLimitField(LimitField otherLimitField) {

				this.otherLimitField = otherLimitField;
			}

			void clear() {

				super.clear();

				limit = null;
			}

			void onValueEntered(INumber value) {

				setLimit(value);
			}

			INumber getSelection(INumber value) {

				setLimit(value);

				return createRangeValue();
			}

			private void setLimit(INumber value) {

				limit = value;

				if (illegalRange()) {

					otherLimitField.clear();
				}
			}
		}

		RangeDisplay() {

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			exactField.addConflictingField(minField);
			exactField.addConflictingField(maxField);

			minField.addConflictingField(exactField);
			maxField.addConflictingField(exactField);

			minField.setOtherLimitField(maxField);
			maxField.setOtherLimitField(minField);

			addField(EXACT_VALUE_LABEL, exactField);
			addField(MIN_VALUE_LABEL, minField);
			addField(MAX_VALUE_LABEL, maxField);
		}

		private void addField(String label, InputField field) {

			JPanel panel = new JPanel(new GridLayout(1, 1));

			panel.setBorder(new TitledBorder(label + ":"));
			panel.add(field);

			add(panel);
		}

		private boolean illegalRange() {

			INumber min = minField.limit;
			INumber max = maxField.limit;

			return min != null && max != null && min.moreThan(max);
		}

		private INumber createRangeValue() {

			return createRangeDef().createNumber().asINumber();
		}

		private CNumberDef createRangeDef() {

			return CNumberDef.range(resolveMin(), resolveMax());
		}

		private INumber resolveMin() {

			INumber min = minField.limit;
			INumber typeMin = type.getMin();

			return min != null ? min.max(typeMin) : typeMin;
		}

		private INumber resolveMax() {

			INumber max = maxField.limit;
			INumber typeMax = type.getMax();

			return max != null ? max.min(typeMax) : typeMax;
		}
	}

	INumberSelector(JComponent parent, CNumber type, boolean queryInstance) {

		super(parent, type.getDisplayLabel(), true);

		this.type = type;

		setPreferredSize(getPreferredSize(queryInstance));
		display(getDisplay(queryInstance));
	}

	INumber getSelectionOrNull() {

		return selection;
	}

	private Dimension getPreferredSize(boolean queryInstance) {

		return queryInstance
					? RANGE_VALUE_WINDOW_SIZE
					: EXACT_VALUE_WINDOW_SIZE;
	}

	private JComponent getDisplay(boolean queryInstance) {

		return queryInstance ? new RangeDisplay() : new InputField();
	}

	private INumber parseValue(String value) {

		if (value.length() != 0) {

			if (type.validNumberValue(value)) {

				return INumber.create(type.getNumberType(), value);
			}

			JOptionPane.showMessageDialog(null, "Invalid Input!");
		}

		return null;
	}
}

