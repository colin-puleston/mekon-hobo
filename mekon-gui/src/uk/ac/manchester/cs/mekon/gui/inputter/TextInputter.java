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

	private boolean checkingInput = false;
	private boolean windowClosing = false;

	class InputField extends GTextField {

		static private final long serialVersionUID = -1;

		protected void onCharEntered(char enteredChar) {

			updateInputValidity();
		}

		protected void onFieldExited(String text) {

			if (multipleInputFields() && !windowClosing) {

				checkInput(text);
			}
		}

		protected void onTextEntered(String text) {

			if (checkInput(text)) {

				dispose();
			}
		}

		I getValue() {

			return convertInputValue(getText());
		}

		void clear() {

			setText("");
		}

		boolean checkConsistentInput() {

			return true;
		}

		private boolean checkInput(String text) {

			boolean ok = false;

			if (!checkingInput) {

				checkingInput = true;

				if (validInputText(text)) {

					if (checkConsistentInput()) {

						setCompletedInput();

						ok = true;
					}
					else {

						updateInputValidity();
					}
				}
				else {

					clear();
				}

				checkingInput = false;
			}

			return ok;
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
}


