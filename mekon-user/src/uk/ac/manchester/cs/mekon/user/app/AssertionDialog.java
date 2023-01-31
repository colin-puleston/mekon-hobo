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
class AssertionDialog extends InstanceDialog {

	static private final long serialVersionUID = -1;

	static private final String SHOW_SUMMARY_BUTTON_LABEL = "Summary...";
	static private final String SUMMARY_DIALOG_TITLE_SUFFIX = "SUMMARY VIEW";

	private class SummaryDialog extends InstanceTreeDialog {

		static private final long serialVersionUID = -1;

		SummaryDialog(JComponent parent, Instantiator instantiator) {

			super(parent, instantiator, SUMMARY_DIALOG_TITLE_SUFFIX);

			initialise(getRootSummaryFrame(), true, InstanceDisplayMode.VIEW);
		}

		boolean fixedDisplayMode() {

			return true;
		}
	}

	private class ShowSummaryButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			new SummaryDialog(getTree(), getInstantiator()).display();
		}

		ShowSummaryButton() {

			super(SHOW_SUMMARY_BUTTON_LABEL);

			setBackground(MekonAppIcons.ASSERT_SUMMARY_CLR);
		}
	}

	AssertionDialog(
		JComponent parent,
		Instantiator instantiator,
		InstanceDisplayMode startMode) {

		super(parent, instantiator, null);

		initialise(false, startMode);
	}

	GButton checkCreateAlternativeViewButton() {

		return getGroup().summariesEnabled() ? new ShowSummaryButton() : null;
	}

	IFrame resolveInstanceForStoring() {

		return getInstance();
	}

	boolean disposeOnStoring() {

		return true;
	}

	private IFrame getRootSummaryFrame() {

		return getSummariser().toSummary(getInstance());
	}

	private InstanceSummariser getSummariser() {

		return getInstantiator().getCustomiser().getInstanceSummariser();
	}
}
