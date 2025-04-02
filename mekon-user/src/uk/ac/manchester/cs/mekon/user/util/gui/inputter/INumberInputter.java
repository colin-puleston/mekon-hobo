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
public abstract class INumberInputter extends StandardTextInputter<INumber> {

	static private final long serialVersionUID = -1;

	static protected final INumber NO_VALUE = new INumber(0);
	static protected final INumber INVALID_VALUE = new INumber(0);

	private CNumber type;

	protected INumberInputter(JComponent parent, CNumber type, String title, boolean canClear) {

		super(parent, title, true, canClear);

		this.type = type;
	}

	protected INumber resolveInput() {

		return resolveInput(type);
	}

	protected abstract INumber resolveInput(CNumber type);

	protected boolean validInputText(String text) {

		return validNumberValueText(text);
	}

	protected boolean validPartialText(String text) {

		if (text.length() == 0) {

			return false;
		}

		if (text.charAt(0) == '-') {

			if (text.length() == 1) {

				return true;
			}

			if (type.getMin().asInteger() >= 0) {

				return false;
			}

			text = text.substring(1);
		}

		return validPositiveNumber(text);
	}

	protected INumber convertInputValue(String text) {

		if (text.length() == 0) {

			return NO_VALUE;
		}

		if (validNumberValueText(text)) {

			return new INumber(getNumberType(), text);
		}

		return INVALID_VALUE;
	}

	protected boolean validNumberValueText(String text) {

		return type.validNumberValue(text);
	}

	private boolean validPositiveNumber(String text) {

		try {

			if (decimalNumberType()) {

				return Double.parseDouble(text) >= 0;
			}

			return Long.parseLong(text) >= 0;
		}
		catch (NumberFormatException e) {

			return false;
		}
	}

	private boolean decimalNumberType() {

		Class<? extends Number> numType = type.getNumberType();

		return numType == Float.class || numType == Double.class;
	}

	private Class<? extends Number> getNumberType() {

		return type.getNumberType();
	}
}


