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
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
abstract class Selector<S> extends GDialog {

	static private final long serialVersionUID = -1;

	private ControlPanel controlPanel;
	private EditStatus status = EditStatus.CANCELLED;

	private class ControlPanel extends EditControlPanel {

		static private final long serialVersionUID = -1;

		ControlPanel(boolean okRequired, boolean clearRequired) {

			super(okRequired, clearRequired, true);

			setOkEnabled(false);
		}

		void onEditComplete(EditStatus status) {

			Selector.this.status = status;

			dispose();
		}
	}

	Selector(Window rootWindow, String title, boolean okRequired, boolean clearRequired) {

		super(rootWindow, title, true);

		controlPanel = new ControlPanel(okRequired, clearRequired);
	}

	EditStatus display() {

		display(createDisplay());

		return status;
	}

	abstract S getSelection();

	abstract JComponent getInputComponent();

	abstract Dimension getWindowSize();

	void setValidSelection(boolean valid) {

		controlPanel.setOkEnabled(valid);
	}

	void setCompletedSelection() {

		status = EditStatus.EDITED;
	}

	private JComponent createDisplay() {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(getWindowSize());
		panel.add(getInputComponent());
		panel.add(controlPanel);

		return panel;
	}
}


