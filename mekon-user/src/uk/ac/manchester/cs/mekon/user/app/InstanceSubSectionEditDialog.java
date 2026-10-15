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
class InstanceSubSectionEditDialog extends InstanceTreeEditDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_SUFFIX = "Sub-Section";

	static private final String OK_BUTTON_LABEL = "Ok";
	static private final String CLEAR_BUTTON_LABEL = "Clear";

	private boolean clearSelected = false;

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

	InstanceSubSectionEditDialog(InstanceTree parentTree, IFrame subRootFrame) {

		super(parentTree, parentTree.getInstance(), TITLE_SUFFIX);

		initialise(subRootFrame, parentTree.summaryInstance(), parentTree.getDisplayMode());
	}

	boolean clearSelected() {

		return clearSelected;
	}

	ControlsPanel checkCreateControlsPanel() {

		if (!getInstance().editableInstance()) {

			return null;
		}

		ControlsPanel panel = new ControlsPanel(true);

		panel.addControl(new OkButton());
		panel.addControl(new ClearButton());

		return panel;
	}
}
