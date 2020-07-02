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
abstract class CFrameSelector extends GDialog {

	static private final long serialVersionUID = -1;

	static private final Dimension WINDOW_SIZE = new Dimension(450, 300);
	static private final String MAIN_TITLE = "%s Selector";

	static String createMainTitle(String selectionRole) {

		return String.format(MAIN_TITLE, selectionRole);
	}

	private CFrame selection = null;

	private class SelectorListener extends CFrameSelectionListener {

		protected void onSelected(CFrame frame) {

			select(frame);
		}
	}

	CFrameSelector(JComponent parent, String selectionRole) {

		super(parent, createMainTitle(selectionRole), true);

		setPreferredSize(WINDOW_SIZE);
	}

	CFrame getSelectionOrNull() {

		display(resolveSelectorPanel(new SelectorListener()));

		return selection;
	}

	abstract JComponent resolveSelectorPanel(CFrameSelectionListener selectorListener);

	private void select(CFrame frame) {

		selection = frame;

		dispose();
	}
}
