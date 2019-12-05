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

package uk.ac.manchester.cs.mekon.app;

import java.awt.*;
import java.awt.event.*;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
abstract class TextInputSelector<S> extends Selector<S> {

	static private final long serialVersionUID = -1;

	private boolean checkingInput = false;
	private boolean windowClosing = false;

	class InputField extends GTextField {

		static private final long serialVersionUID = -1;

		protected void onKeyEntered(KeyEvent event) {

			updateInputValidity();
		}

		protected void onFieldExited(String text) {

			if (!windowClosing) {

				checkInput(text);
			}
		}

		protected void onTextEntered(String text) {

			if (checkInput(text)) {

				dispose();
			}
		}

		S getValue() {

			return convertInputValue(getText());
		}

		void clear() {

			setText("");
		}

		boolean checkConsistentSelection() {

			return true;
		}

		private boolean checkInput(String text) {

			boolean ok = false;

			if (!checkingInput) {

				checkingInput = true;

				if (validInputText(text)) {

					if (checkConsistentSelection()) {

						setCompletedSelection();

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

	TextInputSelector(Window rootWindow, String title) {

		super(rootWindow, title);

		addWindowListener(new WindowCloseListener());
	}

	S getSelection() {

		return resolveSelection();
	}

	abstract S resolveSelection();

	abstract boolean validInputText(String text);

	abstract S convertInputValue(String text);

	abstract boolean validInput();

	private void updateInputValidity() {

		setValidSelection(validInput());
	}
}


