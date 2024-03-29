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

package uk.ac.manchester.cs.mekon.user.app;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceSubSectionDialog extends InstanceTreeDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_SUFFIX = "Sub-Section";

	static private final String OK_BUTTON_LABEL = "Ok";
	static private final String CLEAR_BUTTON_LABEL = "Clear";
	static private final String REPLACE_BUTTON_LABEL = "Replace...";

	private boolean enableReplace;

	private boolean clearSelected = false;
	private boolean replaceSelected = false;

	private class OkButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			dispose();
		}

		OkButton() {

			super(OK_BUTTON_LABEL);
		}
	}

	private class ClearButton extends EditButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			clearSelected = true;

			dispose();
		}

		ClearButton() {

			super(CLEAR_BUTTON_LABEL);
		}
	}

	private class ReplaceButton extends EditButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			replaceSelected = true;

			dispose();
		}

		ReplaceButton() {

			super(REPLACE_BUTTON_LABEL);
		}
	}

	InstanceSubSectionDialog(InstanceTree parentTree, IFrame rootFrame, boolean enableReplace) {

		super(parentTree, parentTree.getInstantiator(), TITLE_SUFFIX);

		this.enableReplace = enableReplace;

		initialise(rootFrame, parentTree.summaryInstance(), parentTree.getDisplayMode());
	}

	boolean clearSelected() {

		return clearSelected;
	}

	boolean replaceSelected() {

		return replaceSelected;
	}

	ControlsPanel checkCreateControlsPanel() {

		if (!getInstantiator().editableInstance()) {

			return null;
		}

		ControlsPanel panel = new ControlsPanel(true);

		panel.addControl(new OkButton());
		panel.addControl(new ClearButton());

		if (enableReplace) {

			panel.addControl(new ReplaceButton());
		}

		return panel;
	}
}
