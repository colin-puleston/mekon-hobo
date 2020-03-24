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
import javax.swing.*;

/**
 * @author Colin Puleston
 */
public abstract class SimpleTextInputter<I> extends TextInputter<I> {

	static private final long serialVersionUID = -1;

	static private final Dimension WINDOW_SIZE = new Dimension(250, 70);

	private InputField valueField = new InputField();

	public void setInitialStringValue(String value) {

		valueField.setText(value);
		updateInputValidity();
	}

	protected SimpleTextInputter(JComponent parent, String title, boolean canClear) {

		super(parent, title, true, canClear);
	}

	protected JComponent getInputComponent() {

		return valueField;
	}

	protected Dimension getWindowSize() {

		return WINDOW_SIZE;
	}

	protected I resolveInput() {

		return valueField.getValue();
	}

	protected boolean validInputText(String text) {

		return !text.isEmpty();
	}

	protected boolean validInput() {

		return !emptyValue(valueField.getValue());
	}

	protected abstract boolean emptyValue(I value);
}


