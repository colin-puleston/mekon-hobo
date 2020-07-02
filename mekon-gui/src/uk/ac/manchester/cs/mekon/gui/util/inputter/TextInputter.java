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

package uk.ac.manchester.cs.mekon.gui.util.inputter;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
public abstract class TextInputter<I> extends Inputter<I> {

	static private final long serialVersionUID = -1;

	static private final int WINDOW_WIDTH = 300;
	static private final int WINDOW_HEIGHT_BASE = 40;
	static private final int PLAIN_INPUT_HEIGHT = 30;
	static private final int TITLED_INPUT_HEIGHT = 60;

	private JPanel inputFieldsPanel = new JPanel();

	private int inputFieldCount = 0;
	private int windowHeight = WINDOW_HEIGHT_BASE;

	private CustomTextInputter<I> customTextInputter = null;

	private boolean checkingInput = false;
	private boolean windowClosing = false;

	private class InputField extends GTextField {

		static private final long serialVersionUID = -1;

		private TextInputHandler<I> inputHandler;

		private class Proxy extends InputFieldProxy<I> {

			private JComponent component;

			Proxy(JComponent component) {

				this.component = component;
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

			JComponent getFieldComponent() {

				return component;
			}
		}

		private class CustomInputFieldFocusRemover extends FocusAdapter {

			public void focusGained(FocusEvent e) {

				if (customTextInput()) {

					getRootPane().requestFocus();
				}
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

			updateInputValidity();
		}

		protected void onFieldExited(String text) {

			if (!customTextInput() && inputFieldCount > 1 && !windowClosing) {

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

			addFocusListener(new CustomInputFieldFocusRemover());
		}

		InputFieldProxy<I> createProxy(JComponent component) {

			return new Proxy(component);
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

		inputFieldsPanel.setLayout(new BoxLayout(inputFieldsPanel, BoxLayout.Y_AXIS));

		addWindowListener(new WindowCloseListener());
	}

	protected void setCustomTextInputter(CustomTextInputter<I> inputter) {

		customTextInputter = inputter;
	}

	protected JComponent getInputComponent() {

		return inputFieldsPanel;
	}

	protected Dimension getWindowSize() {

		return new Dimension(WINDOW_WIDTH, windowHeight);
	}

	protected abstract I resolveInput();

	protected abstract boolean validInputText(String text);

	protected abstract I convertInputValue(String text);

	protected abstract boolean validCurrentInput();

	protected void updateInputValidity() {

		setValidInput(validCurrentInput());
	}

	protected boolean customTextInput() {

		return customTextInputter != null;
	}

	InputFieldProxy<I> addInputField(TextInputHandler<I> inputHandler) {

		InputField field = new InputField(inputHandler);

		return addInputField(field, field, PLAIN_INPUT_HEIGHT);
	}

	InputFieldProxy<I> addInputField(String title, TextInputHandler<I> inputHandler) {

		InputField field = new InputField(inputHandler);
		JComponent component = createTitledFieldComponent(title, field);

		return addInputField(field, component, TITLED_INPUT_HEIGHT);
	}

	private InputFieldProxy<I> addInputField(
									InputField field,
									JComponent component,
									int componentHeight) {

		inputFieldsPanel.add(component);

		inputFieldCount++;
		windowHeight += componentHeight;

		return field.createProxy(component);
	}

	private JComponent createTitledFieldComponent(String title, InputField field) {

		JPanel panel = new JPanel(new GridLayout(1, 1));

		panel.setBorder(new TitledBorder(title));
		panel.add(field);

		return panel;
	}
}


