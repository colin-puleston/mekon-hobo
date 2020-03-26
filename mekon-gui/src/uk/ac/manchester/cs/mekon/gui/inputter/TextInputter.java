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

import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
public abstract class TextInputter<I> extends Inputter<I> {

	static private final long serialVersionUID = -1;

	private CustomTextInputter customTextInputter = null;

	private boolean checkingInput = false;
	private boolean windowClosing = false;

	protected abstract class CustomTextInputter {

		protected abstract String performCustomTextEntry(InputField field);
	}

	protected class InputField extends GTextField {

		static private final long serialVersionUID = -1;

		protected boolean keyInputEnabled() {

			return !customTextInput();
		}

		protected void onMouseClicked() {

			if (customTextInput()) {

				String text = customTextInputter.performCustomTextEntry(this);

				if (text != null) {

					setText(text);
					onCustomTextEntered(text);

					setValidInput(!text.isEmpty());
				}
			}
		}

		protected void onCharEntered(char enteredChar) {

			updateInputValidity();
		}

		protected void onFieldExited(String text) {

			if (!customTextInput() && multipleInputFields() && !windowClosing) {

				checkInput(text);
			}
		}

		protected void onTextEntered(String text) {

			if (!customTextInput() && checkInput(text)) {

				exitOnCompletedInput();
			}
		}

		protected void onCustomTextEntered(String text) {

			checkConsistentInput();
		}

		protected I getValue() {

			return convertInputValue(getText());
		}

		protected void clear() {

			setText("");
		}

		protected boolean checkConsistentInput() {

			return true;
		}

		private boolean checkInput(String text) {

			boolean ok = false;

			if (!checkingInput) {

				checkingInput = true;
				ok = performInputCheck(text);
				checkingInput = false;
			}

			return ok;
		}

		private boolean performInputCheck(String text) {

			if (validInputText(text)) {

				if (checkConsistentInput()) {

					return true;
				}

				setValidInput(false);
			}
			else {

				clear();
			}

			return false;
		}
	}

	private class WindowCloseListener extends WindowAdapter {

		public void windowClosing(WindowEvent e) {

			windowClosing = true;
		}
	}

	public I getInput() {

		return resolveInput();
	}

	protected TextInputter(JComponent parent, String title, boolean canOk, boolean canClear) {

		super(parent, title, canOk, canClear);

		addWindowListener(new WindowCloseListener());
	}

	protected void setCustomTextInputter(CustomTextInputter inputter) {

		customTextInputter = inputter;
	}

	protected abstract I resolveInput();

	protected abstract boolean validInputText(String text);

	protected abstract I convertInputValue(String text);

	protected abstract boolean validInput();

	protected boolean multipleInputFields() {

		return false;
	}

	protected void updateInputValidity() {

		setValidInput(validInput());
	}

	protected boolean customTextInput() {

		return customTextInputter != null;
	}
}


