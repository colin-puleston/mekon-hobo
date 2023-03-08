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

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
public class TextInputHandler<I> {

	private TextInputField<I> field = null;

	private Set<TextInputHandler<I>> incompatibles = new HashSet<TextInputHandler<I>>();

	public TextInputHandler(TextInputter<I> inputter) {

		field = inputter.createInputField(this);
	}

	public void setValueAsText(String text) {

		field.setValueAsText(text);
	}

	public void clearValue() {

		field.clearValue();
	}

	public I getValue() {

		return field.getValue();
	}

	public boolean hasTextValue() {

		return !getValueAsText().isEmpty();
	}

	public String getValueAsText() {

		return field.getValueAsText();
	}

	public GTextField getField() {

		return field;
	}

	public void setIncompatibleField(TextInputHandler<I> incompatible) {

		incompatibles.add(incompatible);
		incompatible.incompatibles.add(this);
	}

	protected boolean checkConsistentInput() {

		return true;
	}

	protected void onTextChange() {
	}

	void handleTextChange() {

		clearIncompatibleFields();
		onTextChange();
	}

	private void clearIncompatibleFields() {

		for (TextInputHandler<I> incompatible : incompatibles) {

			incompatible.clearValue();
		}
	}
}