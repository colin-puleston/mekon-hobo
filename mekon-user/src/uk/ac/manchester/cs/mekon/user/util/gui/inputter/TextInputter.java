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

import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
public abstract class TextInputter<I> extends Inputter<I> {

	static private final long serialVersionUID = -1;

	private CustomTextInputter<I> customTextInputter = null;

	private boolean checkingInput = false;
	private boolean windowClosing = false;

	private class InputField extends TextInputField<I> {

		static private final long serialVersionUID = -1;

		private TextInputHandler<I> inputHandler;

		private class CustomInputFieldFocusRemover extends FocusAdapter {

			public void focusGained(FocusEvent e) {

				if (customTextInput()) {

					getRootPane().requestFocus();
				}
			}

			CustomInputFieldFocusRemover() {

				addFocusListener(this);
			}
		}

		protected boolean keyInputEnabled() {

			return !customTextInput();
		}

		protected void onMouseClicked() {

			if (customTextInput()) {

				performCustomInput();
			}
		}

		protected void onCharEntered(char enteredChar) {

			inputHandler.clearIncompatibleFields();

			if (!removeValidityPreventingInputChar()) {

				updateInputValidity();
			}
		}

		protected void onFieldExited(String text) {

			if (!customTextInput() && !windowClosing) {

				checkInput(text);
			}
		}

		protected void onTextEntered(String text) {

			if (!customTextInput() && checkInput(text)) {

				exitOnCompletedInput();
			}
		}

		InputField(TextInputHandler<I> inputHandler) {

			this.inputHandler = inputHandler;

			inputHandler.setField(this);

			new CustomInputFieldFocusRemover();
		}

		void setValueAsText(String text) {

			setText(text);
		}

		void clearValue() {

			setText("");
		}

		I getValue() {

			return convertInputValue(getText());
		}

		String getValueAsText() {

			return getText();
		}

		private void performCustomInput() {

			String text = customTextInputter.performCustomInput(inputHandler);

			if (text != null) {

				boolean valueEntered = !text.isEmpty();

				if (valueEntered) {

					inputHandler.clearIncompatibleFields();
				}

				setText(text);

				inputHandler.checkConsistentInput();
				setValidInput(valueEntered);
			}
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

				if (inputHandler.checkConsistentInput()) {

					return true;
				}

				setValidInput(false);
			}
			else {

				setText("");
			}

			return false;
		}

		private boolean removeValidityPreventingInputChar() {

			String text = getText();

			if (text.isEmpty() || potentiallyValidInputText(text)) {

				return false;
			}

			setText(text.substring(0, text.length() - 1));

			return true;
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

	protected GTextField createInputField(TextInputHandler<I> inputHandler) {

		return new InputField(inputHandler);
	}

	protected void setCustomTextInputter(CustomTextInputter<I> inputter) {

		customTextInputter = inputter;
	}

	protected abstract I resolveInput();

	protected abstract boolean validInputText(String text);

	protected abstract boolean potentiallyValidInputText(String text);

	protected abstract I convertInputValue(String text);

	protected abstract boolean validCurrentInput();

	protected void updateInputValidity() {

		setValidInput(validCurrentInput());
	}

	protected boolean customTextInput() {

		return customTextInputter != null;
	}
}


