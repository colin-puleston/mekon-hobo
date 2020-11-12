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

package uk.ac.manchester.cs.mekon.user.util.admin.client;

import javax.swing.*;

/**
 * @author Colin Puleston
 */
class NewPasswordChecker {

	static private final int MIN_LENGTH_DEFAULT = 8;
	static private final int MAX_LENGTH_DEFAULT = 20;

	static private final String VALID_LENGTH_DESCRIPTION_FORMAT = "%d - %d characters";

	private int minLength = MIN_LENGTH_DEFAULT;
	private int maxLength = MAX_LENGTH_DEFAULT;

	void setValidLength(int min, int max) {

		minLength = min;
		maxLength = max;
	}

	boolean check(String input1, String input2) {

		if (!input1.equals(input2)) {

			showInvalidInputMessage("Passwords are not identical!");

			return false;
		}

		if (!validLength(input1)) {

			showInvalidInputMessage("Password must contain " + getValidLengthDescription());

			return false;
		}

		return true;
	}

	String getValidLengthDescription() {

		return String.format(VALID_LENGTH_DESCRIPTION_FORMAT, minLength, maxLength);
	}

	private boolean validLength(String input) {

		int length = input.length();

		return length >= minLength && length <= maxLength;
	}

	private void showInvalidInputMessage(String msg) {

		showMessage("Invalid Password Input: " + msg);
	}

	private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}
}
