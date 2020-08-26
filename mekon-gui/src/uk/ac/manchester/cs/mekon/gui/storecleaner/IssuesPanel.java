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

package uk.ac.manchester.cs.mekon.gui.storecleaner;

import java.util.*;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
abstract class IssuesPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String CLEAN_ALL_LABEL = "All";
	static private final String CLEAN_SELECTIONS_LABEL = "Selections";

	static private final int WIDTH = 300;
	static private final int HEIGHT = 400;

	private GList<CIdentity> instanceIdsList = new GList<CIdentity>(true, true);

	private Set<CIdentity> allInstanceIds = new HashSet<CIdentity>();
	private Set<CIdentity> cleanedInstanceIds = new HashSet<CIdentity>();

	private abstract class Populator {

		Populator() {

			for (CIdentity instanceId : allInstanceIds) {

				instanceIdsList.addEntity(instanceId, createCellDisplay(instanceId));
			}
		}

		abstract Icon getIcon(CIdentity instanceId);

		private GCellDisplay createCellDisplay(CIdentity instanceId) {

			return new GCellDisplay(getListLabel(instanceId), getIcon(instanceId));
		}
	}

	private class PreCleanPopulator extends Populator {

		Icon getIcon(CIdentity instanceId) {

			return getPreCleanIcon();
		}
	}

	private class PostCleanPopulator extends Populator {

		Icon getIcon(CIdentity instanceId) {

			return cleaned(instanceId) ? getPostCleanIcon() : getPreCleanIcon();
		}
	}

	private abstract class Cleaner {

		private int cleanedCount = 0;

		void performClean() {

			performCleanOps();
			repopulate();
			reportCleanComplete();
		}

		abstract boolean cleanSelected(CIdentity instanceId);

		private void performCleanOps() {

			for (CIdentity instanceId : allInstanceIds) {

				if (!cleaned(instanceId) && cleanSelected(instanceId)) {

					performCleanOp(instanceId);
					cleanedInstanceIds.add(instanceId);

					cleanedCount++;
				}
			}
		}

		private void repopulate() {

			instanceIdsList.clearList();

			new PostCleanPopulator();
		}

		private void reportCleanComplete() {

			JOptionPane.showMessageDialog(
				null,
				"Completed cleaning operation: "
				+ cleanedCount
				+ " instances "
				+ getCleaningDoneDescriber());
		}
	}

	private class AllCleaner extends Cleaner {

		boolean cleanSelected(CIdentity instanceId) {

			return true;
		}
	}

	private class SelectionsCleaner extends Cleaner {

		private List<CIdentity> selectedInstanceIds = getSelectedInstanceIds();

		boolean cleanSelected(CIdentity instanceId) {

			return selectedInstanceIds.contains(instanceId);
		}
	}

	private abstract class CleanButton extends GButton {

		static private final long serialVersionUID = -1;

		private CleanButton otherButton = null;

		protected void doButtonThing() {

			createCleaner().performClean();

			updateEnabling();
			otherButton.updateEnabling();
		}

		CleanButton(String label) {

			super(label);
		}

		void setOtherButton(CleanButton otherButton) {

			this.otherButton = otherButton;
		}

		void updateEnabling() {

			setEnabled(anyToClean());
		}

		abstract boolean anyToClean();

		abstract Cleaner createCleaner();
	}

	private class CleanAllButton extends CleanButton {

		static private final long serialVersionUID = -1;

		CleanAllButton() {

			super(CLEAN_ALL_LABEL);

			setEnabled(anyInstanceIds());
		}

		boolean anyToClean() {

			return !allInstanceIds.equals(cleanedInstanceIds);
		}

		Cleaner createCleaner() {

			return new AllCleaner();
		}
	}

	private class CleanSelectionsButton extends CleanButton {

		static private final long serialVersionUID = -1;

		private class Enabler extends GSelectionListener<CIdentity> {

			protected void onSelected(CIdentity instanceId) {

				updateEnabling();
			}

			protected void onDeselected(CIdentity instanceId) {

				updateEnabling();
			}
		}

		CleanSelectionsButton() {

			super(CLEAN_SELECTIONS_LABEL);

			setEnabled(false);

			if (anyInstanceIds()) {

				instanceIdsList.addSelectionListener(new Enabler());
			}
		}

		boolean anyToClean() {

			List<CIdentity> selecteds = getSelectedInstanceIds();

			selecteds.removeAll(cleanedInstanceIds);

			return !selecteds.isEmpty();
		}

		Cleaner createCleaner() {

			return new SelectionsCleaner();
		}
	}

	public Dimension getPreferredSize() {

		return new Dimension(WIDTH, HEIGHT);
	}

	IssuesPanel() {

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	void display(String title, List<CIdentity> instanceIds) {

		allInstanceIds.addAll(instanceIds);

		new PreCleanPopulator();

		add(createMainPanel(title));
	}

	abstract String getCleanLabel();

	abstract String getListLabel(CIdentity instanceId);

	abstract Icon getPreCleanIcon();

	abstract Icon getPostCleanIcon();

	abstract void performCleanOp(CIdentity instanceId);

	abstract String getCleaningDoneDescriber();

	private JPanel createMainPanel(String title) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBorder(new TitledBorder(title));
		panel.add(new JScrollPane(instanceIdsList), BorderLayout.CENTER);
		panel.add(createButtonsPanel(), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createButtonsPanel() {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(new TitledBorder(getCleanLabel()));

		CleanButton allButton = new CleanAllButton();
		CleanButton selsButton = new CleanSelectionsButton();

		allButton.setOtherButton(selsButton);
		selsButton.setOtherButton(allButton);

		addButton(panel, allButton);
		addButton(panel, selsButton);

		return panel;
	}

	private void addButton(JPanel panel, GButton button) {

		JPanel buttonPanel = new JPanel(new BorderLayout());

		buttonPanel.add(button, BorderLayout.CENTER);
		panel.add(buttonPanel);
	}

	private boolean cleaned(CIdentity instanceId) {

		return cleanedInstanceIds.contains(instanceId);
	}

	private boolean anyInstanceIds() {

		return !allInstanceIds.isEmpty();
	}

	private List<CIdentity> getSelectedInstanceIds() {

		return instanceIdsList.getSelectedEntities();
	}
}
