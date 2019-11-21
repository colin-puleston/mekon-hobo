/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package uk.ac.manchester.cs.mekon.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class ModelFrameSelectionPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String DETAILS_TITLE = "Details";
	static private final String USAGE_TITLE = "Usage";
	static private final String ANNOTATIONS_TITLE = "Annotations";

	private CFramesTree modelTree;
	private InstanceStoreActions storeActions;

	private ReselectionListener reselectionListener = new ReselectionListener();
	private CFrameSelectionListeners reselectors = new CFrameSelectionListeners();

	private int currentViewedAspectIndex = 0;

	private class ReselectionListener extends CFrameSelectionListener {

		protected void onSelected(CFrame frame) {

			if (frame.getCategory().atomic()) {

				reselectors.pollForSelected(frame);
				showSelected(frame);
			}
		}
	}

	private class NewSelectionRelay extends CFrameSelectionRelay {

		void addUpdateListener(CFrameSelectionListener listener) {

			reselectors.add(listener);
		}

		void update(CFrame selection) {

			showSelected(selection);
		}
	}

	private class ViewedAspectChangeListener implements ChangeListener {

		private JTabbedPane aspectsPanel;

		public void stateChanged(ChangeEvent e) {

			currentViewedAspectIndex = aspectsPanel.getSelectedIndex();
		}

		ViewedAspectChangeListener(JTabbedPane aspectsPanel) {

			this.aspectsPanel = aspectsPanel;

			aspectsPanel.addChangeListener(this);
		}
	}

	ModelFrameSelectionPanel(
		CFramesTree modelTree,
		InstanceStoreActions storeActions) {

		super(new BorderLayout());

		this.modelTree = modelTree;
		this.storeActions = storeActions;
	}

	CFrameSelectionRelay getSelectionRelay() {

		return new NewSelectionRelay();
	}

	private void showSelected(CFrame selected) {

		removeAll();

		add(createAspectsPanel(selected), BorderLayout.CENTER);
		add(new InstantiationsPanel(modelTree, selected, storeActions), BorderLayout.SOUTH);

		revalidate();
	}

	private JComponent createAspectsPanel(CFrame selected) {

		JTabbedPane panel = new JTabbedPane();

		panel.addTab(DETAILS_TITLE, createDetailsPanel(selected));
		panel.addTab(USAGE_TITLE, createUsagePanel(selected));
		panel.addTab(ANNOTATIONS_TITLE, createAnnotationsPanel(selected));

		panel.setSelectedIndex(currentViewedAspectIndex);

		new ViewedAspectChangeListener(panel);

		return panel;
	}

	private JComponent createDetailsPanel(CFrame selected) {

		CFrameDetailsTree tree = new CFrameDetailsTree(selected);

		tree.addSelectionListener(reselectionListener);

		return new JScrollPane(tree);
	}

	private JComponent createUsagePanel(CFrame selected) {

		return new JScrollPane(new CFrameUsagePanel(selected, reselectionListener));
	}

	private JComponent createAnnotationsPanel(CFrame selected) {

		return new JScrollPane(new CFrameAnnotationsTree(selected, reselectionListener));
	}
}
