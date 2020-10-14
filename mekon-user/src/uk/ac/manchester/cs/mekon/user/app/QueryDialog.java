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
class QueryDialog extends InstanceDialog {

	static private final long serialVersionUID = -1;

	static private final String TO_COMPRESSED_BUTTON_LABEL = "Compressed...";
	static private final String TO_EXPANDED_BUTTON_LABEL = "Expanded...";
	static private final String EXECUTE_BUTTON_LABEL = "Execute";

	static private IFrame determineActiveRootFrame(Instantiator instantiator) {

		IFrame defaultRootFrame = instantiator.getInstance();

		if (summariesEnabled(instantiator)) {

			InstanceSummariser summariser = getSummariser(instantiator);

			if (summariser.reversiblySummarisable(defaultRootFrame)) {

				return summariser.toSummary(defaultRootFrame);
			}
		}

		return defaultRootFrame;
	}

	static private boolean summariesEnabled(Instantiator instantiator) {

		return instantiator.getInstanceGroup().summariesEnabled();
	}

	static private InstanceSummariser getSummariser(Instantiator instantiator) {

		return instantiator.getController().getCustomiser().getInstanceSummariser();
	}

	private JComponent parent;

	private IFrame rootFrame;

	private InstanceSummariser summariser;
	private QueryExecutions queryExecutions;

	private class ToCompressedButton extends GButton {

		static private final long serialVersionUID = -1;

		private class Enabler extends EditListener {

			Enabler() {

				updateEnabling();
				addEditListener(this);
			}

			void onTreeEdited() {

				updateEnabling();
			}

			private void updateEnabling() {

				setEnabled(compressible());
			}
		}

		protected void doButtonThing() {

			switchToCompressedDisplay();
		}

		ToCompressedButton() {

			super(TO_COMPRESSED_BUTTON_LABEL);

			new Enabler();
		}
	}

	private class ToExpandedButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			switchToExpandedDisplay();
		}

		ToExpandedButton() {

			super(TO_EXPANDED_BUTTON_LABEL);
		}
	}

	private class ExecuteButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			execute();
		}

		ExecuteButton() {

			super(EXECUTE_BUTTON_LABEL);
		}
	}

	QueryDialog(
		JComponent parent,
		Instantiator instantiator,
		InstanceDisplayMode startMode) {

		this(parent, instantiator, determineActiveRootFrame(instantiator), startMode);
	}

	ControlsPanel checkCreateControlsPanel( ) {

		ControlsPanel panel = super.checkCreateControlsPanel();

		if (panel == null) {

			panel = new ControlsPanel(true);
		}

		panel.addControl(new ExecuteButton());

		if (displayingCompressed()) {

			panel.addControl(new ToExpandedButton());
		}
		else {

			if (getInstanceGroup().summariesEnabled()) {

				panel.addControl(new ToCompressedButton());
			}
		}

		return panel;
	}

	IFrame resolveInstanceForStoring() {

		return resolveToExpanded();
	}

	boolean disposeOnStoring() {

		return false;
	}

	private QueryDialog(
				JComponent parent,
				Instantiator instantiator,
				IFrame rootFrame,
				InstanceDisplayMode startMode) {

		super(parent, instantiator, rootFrame, startMode);

		this.parent = parent;
		this.rootFrame = rootFrame;

		summariser = getSummariser(instantiator);
		queryExecutions = instantiator.getInstanceGroup().getQueryExecutions();
	}

	private void switchToCompressedDisplay() {

		switchDisplay(getInstantiator(), compressExpanded());
	}

	private void switchToExpandedDisplay() {

		IFrame expanded = expandCompressed();

		switchDisplay(getInstantiator().deriveInstantiator(expanded), expanded);
	}

	private void switchDisplay(Instantiator newInstantiator, IFrame newRootFrame) {

		dispose();

		new QueryDialog(parent, newInstantiator, newRootFrame, getMode()).display();
	}

	private void execute() {

		dispose();

		queryExecutions.execute(getStoreId(), resolveToExpanded());
	}

	private IFrame resolveToExpanded() {

		return displayingCompressed() ? expandCompressed() : rootFrame;
	}

	private IFrame expandCompressed() {

		return summariser.toInstance(rootFrame);
	}

	private IFrame compressExpanded() {

		return summariser.toSummary(rootFrame);
	}

	private boolean compressible() {

		return summariser.reversiblySummarisable(rootFrame);
	}

	private boolean displayingCompressed() {

		return rootFrame != getInstance();
	}
}
