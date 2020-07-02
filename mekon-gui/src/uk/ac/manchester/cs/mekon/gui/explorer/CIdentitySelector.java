/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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

package uk.ac.manchester.cs.mekon.gui.explorer;

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class CIdentitySelector extends GDialog {

	static private final long serialVersionUID = -1;

	static private final Dimension WINDOW_SIZE = new Dimension(250, 70);

	private CIdentity selection = null;

	private class InputField extends GTextField {

		static private final long serialVersionUID = -1;

		protected void onTextEntered(String text) {

			checkSelect(text);
		}
	}

	CIdentitySelector(JComponent parent, String label) {

		super(parent, label, true);

		setPreferredSize(WINDOW_SIZE);
		display(new InputField());
	}

	CIdentity getSelectionOrNull() {

		return selection;
	}

	private void checkSelect(String text) {

		selection = CIdentityCreator.createOrNull(text);

		if (selection != null) {

			dispose();
		}
	}
}

