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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
public abstract class Inputter<I> extends GDialog {

	static private final long serialVersionUID = -1;

	private ControlPanel controlPanel;
	private EditStatus status = EditStatus.CANCELLED;

	private class ControlPanel extends EditControlPanel {

		static private final long serialVersionUID = -1;

		ControlPanel(boolean canOk, boolean canClear) {

			super(canOk, canClear);

			setOkEnabled(false);
		}

		void onEditTerminationSelection(EditStatus status) {

			Inputter.this.status = status;

			dispose();
		}
	}

	public void addExtraControlButton(GButton button) {

		controlPanel.addExtraButton(button);
	}

	public void setValidInput(boolean valid) {

		controlPanel.setOkEnabled(valid);
	}

	public void exitOnCompletedInput() {

		status = EditStatus.INPUTTED;

		dispose();
	}

	public EditStatus display() {

		display(createDisplay());

		return status;
	}

	public abstract I getInput();

	protected Inputter(JComponent parent, String title, boolean canOk, boolean canClear) {

		super(parent, title, true);

		controlPanel = new ControlPanel(canOk, canClear);
	}

	protected JComponent getHeaderComponentOrNull() {

		return null;
	}

	protected abstract JComponent getInputComponent();

	protected abstract Dimension getWindowSize();

	private JComponent createDisplay() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setPreferredSize(getWindowSize());
		panel.add(getInputComponent(), BorderLayout.CENTER);
		panel.add(controlPanel, BorderLayout.SOUTH);

		JComponent header = getHeaderComponentOrNull();

		if (header != null) {

			panel.add(header, BorderLayout.NORTH);
		}

		return panel;
	}
}


