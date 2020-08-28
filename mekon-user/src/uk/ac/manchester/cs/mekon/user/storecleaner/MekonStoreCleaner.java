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

package uk.ac.manchester.cs.mekon.user.storecleaner;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon_util.gui.*;
import uk.ac.manchester.cs.mekon_util.gui.icon.*;

/**
 * @author Colin Puleston
 */
public class MekonStoreCleaner extends GFrame {

	static private final long serialVersionUID = -1;

	static private final String MAIN_TITLE = "Mekon Store Cleaner";
	static private final String INVALIDS_TITLE = "Invalid instances (types)";
	static private final String PART_VALIDS_TITLE = "Partially valid instances";
	static private final String CLEAN_INVALIDS_LABEL = "Remove";
	static private final String CLEAN_PART_VALIDS_LABEL = "Prune";
	static private final String VIEW_LOG_LABEL = "View log file...";

	static private final Icon VALID_ICON = createIcon(Color.GREEN.darker());
	static private final Icon INVALID_ICON = createIcon(Color.RED.darker());
	static private final Icon PART_VALID_ICON = createIcon(Color.YELLOW.darker());
	static private final Icon REMOVED_ICON = createIcon(Color.GRAY.brighter());

	static public void main(String[] args) {

		CBuilder cBuilder = CManager.createBuilder();

		cBuilder.build();

		new MekonStoreCleaner(IDiskStoreManager.getBuilder(cBuilder).build());
	}

	static private GIcon createIcon(Color clr) {

		return new GIcon(new GOvalRenderer(clr, 12));
	}

	private IStore store;
	private IStoreRegenReport regenReport;

	private class FullyInvalidsPanel extends IssuesPanel {

		static private final long serialVersionUID = -1;

		private Map<CIdentity, String> listLabels = new HashMap<CIdentity, String>();

		FullyInvalidsPanel() {

			display(INVALIDS_TITLE, regenReport.getFullyInvalidIds());
		}

		String getCleanLabel() {

			return CLEAN_INVALIDS_LABEL;
		}

		String getListLabel(CIdentity instanceId) {

			String label = listLabels.get(instanceId);

			if (label == null) {

				label = createListLabel(instanceId);

				listLabels.put(instanceId, label);
			}

			return label;
		}

		Icon getPreCleanIcon() {

			return INVALID_ICON;
		}

		Icon getPostCleanIcon() {

			return REMOVED_ICON;
		}

		void performCleanOp(CIdentity instanceId) {

			store.remove(instanceId);
		}

		String getCleaningDoneDescriber() {

			return "removed";
		}

		private String createListLabel(CIdentity instanceId) {

			return instanceId.getLabel() + " (" + getTypeLabel(instanceId) + ")";
		}

		private String getTypeLabel(CIdentity instanceId) {

			return store.get(instanceId).getRootTypeId().getLabel();
		}
	}

	private class PartiallyValidsPanel extends IssuesPanel {

		static private final long serialVersionUID = -1;

		PartiallyValidsPanel() {

			display(PART_VALIDS_TITLE, regenReport.getPartiallyValidIds());
		}

		String getCleanLabel() {

			return CLEAN_PART_VALIDS_LABEL;
		}

		String getListLabel(CIdentity instanceId) {

			return instanceId.getLabel();
		}

		Icon getPreCleanIcon() {

			return PART_VALID_ICON;
		}

		Icon getPostCleanIcon() {

			return VALID_ICON;
		}

		String getCleaningDoneDescriber() {

			return "prunned";
		}

		void performCleanOp(CIdentity instanceId) {

			store.add(store.get(instanceId).getRootFrame(), instanceId);
		}
	}

	private class ViewLogButton extends GButton {

		static private final long serialVersionUID = -1;

		private File logFile = regenReport.getLogFileOrNull();

		protected void doButtonThing() {

			new LogFileDialog(this, logFile);
		}

		ViewLogButton() {

			super(VIEW_LOG_LABEL);

			setEnabled(logFile != null);
		}
	}

	private class InitialCheckInvoker extends WindowAdapter {

		public void windowOpened(WindowEvent e) {

			performInitialCheck();
		}
	}

	public MekonStoreCleaner(IStore store) {

		super(MAIN_TITLE);

		this.store = store;

		regenReport = store.getRegenReport();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addWindowListener(new InitialCheckInvoker());

		display(createMainPanel());
	}

	private JPanel createMainPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createIssuesPanel(), BorderLayout.CENTER);
		panel.add(new ViewLogButton(), BorderLayout.SOUTH);

		return panel;
	}

	private JPanel createIssuesPanel() {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(new FullyInvalidsPanel());
		panel.add(new PartiallyValidsPanel());

		return panel;
	}

	private void performInitialCheck() {

		if (!fullyInvalids() && !partiallyValids()) {

			reportNoIssues();
			dispose();
		}
	}

	private boolean fullyInvalids() {

		return regenReport.fullyInvalidRegens();
	}

	private boolean partiallyValids() {

		return regenReport.partiallyValidRegens();
	}

	private void reportNoIssues() {

		JOptionPane.showMessageDialog(
			null,
			"No data/model compatiblity or internal data consistency issues detected");
	}
}
