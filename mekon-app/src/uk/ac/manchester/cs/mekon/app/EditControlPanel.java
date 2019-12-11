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

			onEditComplete(getEditStatus());
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

			return EditStatus.EDITED;
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

	void configure(boolean okRequired, boolean clearRequired, boolean cancelRequired) {

		boolean first = true;

		if (okRequired) {

			okButton = addButton(new OkButton(), first);
			first = false;
		}

		if (clearRequired) {

			addButton(new ClearButton(), first);
			first = false;
		}

		if (cancelRequired) {

			addButton(new CancelButton(), first);
		}
	}

	void setOkEnabled(boolean enable) {

		if (okButton != null) {

			okButton.setEnabled(enable);
		}
	}

	abstract void onEditComplete(EditStatus status);

	private EditButton addButton(EditButton button, boolean first) {

		if (!first) {

			add(Box.createHorizontalStrut(10));
		}

		add(button);

		return button;
	}
}


