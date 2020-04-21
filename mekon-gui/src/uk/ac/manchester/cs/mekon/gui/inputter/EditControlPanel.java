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

import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
abstract class EditControlPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String OK_LABEL = "Ok";
	static private final String CANCEL_LABEL = "Cancel";
	static private final String CLEAR_LABEL = "Clear";

	private EditButton okButton = null;

	private abstract class EditButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			onEditTerminationSelection(getEditStatus());
		}

		EditButton(String label) {

			super(label);
		}

		abstract EditStatus getEditStatus();
	}

	private class OkButton extends EditButton {

		static private final long serialVersionUID = -1;

		OkButton() {

			super(OK_LABEL);
		}

		EditStatus getEditStatus() {

			return EditStatus.INPUTTED;
		}
	}

	private class ClearButton extends EditButton {

		static private final long serialVersionUID = -1;

		ClearButton() {

			super(CLEAR_LABEL);
		}

		EditStatus getEditStatus() {

			return EditStatus.CLEARED;
		}
	}

	private class CancelButton extends EditButton {

		static private final long serialVersionUID = -1;

		CancelButton() {

			super(CANCEL_LABEL);
		}

		EditStatus getEditStatus() {

			return EditStatus.CANCELLED;
		}
	}

	private class Populator {

		private boolean first = true;

		EditButton addButton(EditButton button) {

			if (first) {

				first = false;
			}
			else {

				addSeparator();
			}

			add(button);

			return button;
		}
	}

	EditControlPanel(boolean canOk, boolean canClear) {

		Populator populator = new Populator();

		if (canOk) {

			okButton = populator.addButton(new OkButton());
		}

		if (canClear) {

			populator.addButton(new ClearButton());
		}

		populator.addButton(new CancelButton());
	}

	void setOkEnabled(boolean enable) {

		if (okButton != null) {

			okButton.setEnabled(enable);
		}
	}

	void addExtraButton(GButton button) {

		addSeparator();
		add(button);
	}

	abstract void onEditTerminationSelection(EditStatus status);

	void addSeparator() {

		add(Box.createHorizontalStrut(10));
	}
}


