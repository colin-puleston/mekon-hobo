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
public class DefiniteINumberInputter extends INumberInputter {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "Enter Value (%s - %s)";

	static private final String createTitle(CNumber type) {

		String min = limitToString(type.getMin(), INumber.MINUS_INFINITY);
		String max = limitToString(type.getMax(), INumber.PLUS_INFINITY);

		return String.format(TITLE_FORMAT, min, max);
	}

	static private String limitToString(INumber limit, INumber absLimit) {

		return limit.equals(absLimit) ? "?" : limit.getDisplayLabel();
	}

	private NumberInputHandler inputHandler = new NumberInputHandler();

	public DefiniteINumberInputter(JComponent parent, CNumber type, boolean canClear) {

		super(parent, type, createTitle(type), canClear);

		addInputField(inputHandler);
	}

	protected INumber resolveInput(CNumber type) {

		return inputHandler.getValue();
	}

	protected boolean validCurrentInput() {

		return validNumberValueText(inputHandler.getValueAsText());
	}
}


