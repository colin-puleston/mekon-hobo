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

package uk.ac.manchester.cs.mekon.user.util.gui.inputter;

import javax.swing.*;

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

	private INumberInputHandler exactHandler = new INumberInputHandler(this);

	private LimitInputHandler minHandler = new LimitInputHandler();
	private LimitInputHandler maxHandler = new LimitInputHandler();

	private class LimitInputHandler extends INumberInputHandler {

		static private final long serialVersionUID = -1;

		private LimitInputHandler otherLimit = null;

		protected boolean checkConsistentInput() {

			if (invalidRange()) {

				otherLimit.clearValue();

				return false;
			}

			return true;
		}

		LimitInputHandler() {

			super(IndefiniteINumberInputter.this);
		}

		void setOtherLimit(LimitInputHandler otherLimit) {

			this.otherLimit = otherLimit;

			otherLimit.otherLimit = this;
		}
	}

	public IndefiniteINumberInputter(JComponent parent, CNumber type, boolean canClear) {

		super(parent, type, TITLE, canClear);

		addInputField(EXACT_VALUE_LABEL, exactHandler);
		addInputField(MIN_VALUE_LABEL, minHandler);
		addInputField(MAX_VALUE_LABEL, maxHandler);

		exactHandler.setIncompatibleField(minHandler);
		exactHandler.setIncompatibleField(maxHandler);

		minHandler.setOtherLimit(maxHandler);
	}

	protected INumber resolveInput(CNumber type) {

		if (!anyValidValues()) {

			return NO_VALUE;
		}

		if (exactHandler.hasValue()) {

			return exactHandler.getValue();
		}

		if (invalidRange()) {

			return NO_VALUE;
		}

		return resolveValidRange(type);
	}

	protected boolean validCurrentInput() {

		return anyValidValues() && !invalidRange();
	}

	protected boolean multipleInputHandlers() {

		return true;
	}

	private boolean invalidRange() {

		if (minHandler.hasValue() && maxHandler.hasValue()) {

			return minHandler.getValue().moreThan(maxHandler.getValue());
		}

		return false;
	}

	private boolean anyValidValues() {

		return anyValues() && !anyInvalidValues();
	}

	private boolean anyValues() {

		return exactHandler.hasValue() || minHandler.hasValue() || maxHandler.hasValue();
	}

	private boolean anyInvalidValues() {

		return exactHandler.invalidValue() || minHandler.invalidValue() || maxHandler.invalidValue();
	}

	private INumber resolveValidRange(CNumber type) {

		INumber min = minHandler.getValue();
		INumber max = maxHandler.getValue();

		if (min == NO_VALUE) {

			min = type.getMin();
		}
		else if (max == NO_VALUE) {

			max = type.getMax();
		}

		return INumber.range(min, max);
	}
}
