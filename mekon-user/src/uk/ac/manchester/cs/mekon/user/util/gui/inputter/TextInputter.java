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

	private class InputField extends TextInputField<I> {

		static private final long serialVersionUID = -1;

		private TextInputHandler<I> inputHandler;
		private String currentValidPartialText = "";

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

			if (!removeValidityPreventingInputChar()) {

				inputHandler.handleTextChange();

				setValidInput(validCurrentInput());
			}
		}

		protected void onTextEntered(String text) {

			if (!customTextInput() && !checkingInput) {

				checkingInput = true;

				if (checkInput(text)) {

					exitOnCompletedInput();
				}

				checkingInput = false;
			}
		}

		InputField(TextInputHandler<I> inputHandler) {

			this.inputHandler = inputHandler;

			GFonts.setMedium(this);

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

					inputHandler.handleTextChange();
				}

				setText(text);

				inputHandler.checkConsistentInput();
				setValidInput(valueEntered);
			}
		}

		private boolean checkInput(String text) {

			if (text.length() != 0) {

				if (validInputText(text) && inputHandler.checkConsistentInput()) {

					return true;
				}

				JOptionPane.showMessageDialog(null, "Invalid Input!");
			}

			setValidInput(false);

			return false;
		}

		private boolean removeValidityPreventingInputChar() {

			String text = getText();

			if (text.isEmpty() || validPartialText(text)) {

				currentValidPartialText = text;

				return false;
			}

			setText(currentValidPartialText);

			return true;
		}
	}

	public I getInput() {

		return resolveInput();
	}

	protected TextInputter(JComponent parent, String title, boolean canOk, boolean canClear) {

		super(parent, title, canOk, canClear);
	}

	protected void setCustomTextInputter(CustomTextInputter<I> inputter) {

		customTextInputter = inputter;
	}

	protected abstract I resolveInput();

	protected abstract boolean validInputText(String text);

	protected abstract boolean validPartialText(String text);

	protected abstract I convertInputValue(String text);

	protected abstract boolean validCurrentInput();

	protected boolean customTextInput() {

		return customTextInputter != null;
	}

	TextInputField<I> createInputField(TextInputHandler<I> inputHandler) {

		return new InputField(inputHandler);
	}
}


