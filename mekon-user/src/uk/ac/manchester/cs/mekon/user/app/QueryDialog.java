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

	static private final String COMPRESSED_VIEW_TITLE_SUFFIX = "COMPRESSED VIEW";

	static private final String TO_COMPRESSED_BUTTON_LABEL = "Compressed...";
	static private final String TO_EXPANDED_BUTTON_LABEL = "Expanded...";
	static private final String EXECUTE_BUTTON_LABEL = "Execute";

	static private class Creator {

		private Instantiator instantiator;
		private InstanceSummariser summariser;

		private IFrame rootFrame;

		Creator(Instantiator instantiator) {

			this(instantiator, null);
		}

		Creator(Instantiator instantiator, IFrame requiredRootFrame) {

			this.instantiator = instantiator;

			summariser = getSummariser();
			rootFrame = resolveRootFrame(requiredRootFrame);
		}

		QueryDialog create(JComponent parent, InstanceDisplayMode startMode) {

			return new QueryDialog(
							parent,
							instantiator,
							summariser,
							rootFrame,
							startMode,
							getTitleSuffix());
		}

		private IFrame resolveRootFrame(IFrame requiredRoot) {

			if (requiredRoot != null) {

				return requiredRoot;
			}

			IFrame expandedRoot = instantiator.getInstance();

			if (summariesEnabled() && summariser.reversiblySummarisable(expandedRoot)) {

				return summariser.toSummary(expandedRoot);
			}

			return expandedRoot;
		}

		private String getTitleSuffix() {

			return displayCompressed() ? COMPRESSED_VIEW_TITLE_SUFFIX : null;
		}

		private boolean displayCompressed() {

			return rootFrame != instantiator.getInstance();
		}

		private boolean summariesEnabled() {

			return instantiator.getGroup().summariesEnabled();
		}

		private InstanceSummariser getSummariser() {

			return instantiator.getCustomiser().getInstanceSummariser();
		}
	}

	static QueryDialog create(
						JComponent parent,
						Instantiator instantiator,
						InstanceDisplayMode startMode) {

		return new Creator(instantiator).create(parent, startMode);
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

			if (getGroup().summariesEnabled()) {

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
				InstanceSummariser summariser,
				IFrame rootFrame,
				InstanceDisplayMode startMode,
				String titleSuffix) {

		super(parent, instantiator, titleSuffix);

		this.parent = parent;
		this.summariser = summariser;
		this.rootFrame = rootFrame;

		queryExecutions = instantiator.getGroup().getQueryExecutions();

		initialise(rootFrame, displayingCompressed(), startMode);
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

		new Creator(newInstantiator, newRootFrame).create(parent, getMode()).display();
	}

	private void execute() {

		dispose();

		queryExecutions.execute(getStoreId(), resolveToExpanded(), getSubGroup());
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
