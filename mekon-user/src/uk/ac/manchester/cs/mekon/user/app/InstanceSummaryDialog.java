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

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceSummaryDialog extends InstanceTreeDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "%s %s";
	static private final String ASSERTION_SUMMARY_LABEL = "Summary";
	static private final String QUERY_SUMMARY_LABEL = "Short-form";

	static private final String OK_BUTTON_LABEL = "Ok";
	static private final String CANCEL_BUTTON_LABEL = "Cancel";

	static private String createSummaryTitle(Instantiator instantiator) {

		String suffix = InstanceSummaryHandler.getSummaryLabel(instantiator);

		return createInstanceTitle(instantiator, suffix);
	}

	private boolean fixedMode;

	private boolean summaryEdited = false;
	private boolean summaryEditOk = false;

	private abstract class SummaryEditButton extends EditButton {

		static private final long serialVersionUID = -1;

		SummaryEditButton(String label) {

			super(label);
		}

		boolean enableButton() {

			return super.enableButton() && summaryEdited;
		}
	}

	private class OkButton extends SummaryEditButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			summaryEditOk = true;

			dispose();
		}

		OkButton() {

			super(OK_BUTTON_LABEL);
		}
	}

	private class CancelButton extends SummaryEditButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			dispose();
		}

		CancelButton() {

			super(CANCEL_BUTTON_LABEL);
		}
	}

	private class SummaryEditListener extends EditListener {

		void onTreeEdited() {

			summaryEdited = true;
		}
	}

	InstanceSummaryDialog(
		InstanceTree sourceTree,
		Instantiator instantiator,
		IFrame rootSummaryFrame) {

		super(
			sourceTree,
			instantiator,
			rootSummaryFrame,
			createSummaryTitle(instantiator),
			sourceTree.getMode());

		fixedMode = rootSummaryFrame.getFunction().assertion();

		addEditListener(new SummaryEditListener());
	}

	boolean summaryEditOk() {

		return summaryEditOk;
	}

	boolean fixedMode() {

		return fixedMode;
	}

	ControlsPanel checkCreateControlsPanel() {

		if (!getInstantiator().editableSummary()) {

			return null;
		}

		ControlsPanel panel = new ControlsPanel(true);

		panel.addControl(new OkButton());
		panel.addControl(new CancelButton());

		return panel;
	}
}
