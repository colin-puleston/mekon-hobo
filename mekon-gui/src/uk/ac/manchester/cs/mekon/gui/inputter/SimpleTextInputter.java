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

import javax.swing.*;

/**
 * @author Colin Puleston
 */
public abstract class SimpleTextInputter<I> extends TextInputter<I> {

	static private final long serialVersionUID = -1;

	private TextInputHandler<I> inputHandler = new TextInputHandler<I>(this);

	public void setInitialStringValue(String value) {

		inputHandler.setValueAsText(value);

		updateInputValidity();
	}

	protected SimpleTextInputter(JComponent parent, String title, boolean canClear) {

		super(parent, title, true, canClear);
	}

	protected I resolveInput() {

		return inputHandler.getValue();
	}

	protected boolean validInputText(String text) {

		return !text.isEmpty();
	}

	protected boolean validCurrentInput() {

		return !emptyValue(inputHandler.getValue());
	}

	protected boolean validCompletedInput() {

		return validInputText(inputHandler.getValueAsText());
	}

	protected abstract boolean emptyValue(I value);
}


