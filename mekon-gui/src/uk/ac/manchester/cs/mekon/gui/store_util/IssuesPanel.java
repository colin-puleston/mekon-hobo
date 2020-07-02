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

package uk.ac.manchester.cs.mekon.gui.store_util;

import java.util.*;
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

	private GList<CIdentity> instanceIdsList = new GList<CIdentity>(true, true);

	private class CleanButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			performClean();
			updateInstanceIdsList();

			setEnabled(false);
		}

		CleanButton() {

			super(getCleanLabel());

			setEnabled(anyInstanceIds());
		}
	}

	IssuesPanel() {

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	void display(String title, List<CIdentity> instanceIds) {

		addInstanceIds(instanceIds, false);
		add(createMainPanel(title));
	}

	abstract String getCleanLabel();

	abstract String getListLabel(CIdentity instanceId);

	abstract Icon getIcon(boolean cleaned);

	abstract void performCleanOp(CIdentity instanceId);

	abstract String getCleaningDoneDescriber();

	private JPanel createMainPanel(String title) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBorder(new TitledBorder(title));
		panel.add(new JScrollPane(instanceIdsList), BorderLayout.CENTER);
		panel.add(new CleanButton(), BorderLayout.SOUTH);

		return panel;
	}

	private void performClean() {

		for (CIdentity instanceId : getInstanceIds()) {

			performCleanOp(instanceId);
		}

		reportCleanComplete();
	}

	private void updateInstanceIdsList() {

		List<CIdentity> instanceIds = getInstanceIds();

		instanceIdsList.clearList();
		addInstanceIds(instanceIds, true);
	}

	private void addInstanceIds(List<CIdentity> instanceIds, boolean cleaned) {

		for (CIdentity instanceId : instanceIds) {

			instanceIdsList.addEntity(instanceId, createCellDisplay(instanceId, cleaned));
		}
	}

	private GCellDisplay createCellDisplay(CIdentity instanceId, boolean cleaned) {

		return new GCellDisplay(getListLabel(instanceId), getIcon(cleaned));
	}

	private void reportCleanComplete() {

		JOptionPane.showMessageDialog(
			null,
			"Completed cleaning operation: "
			+ instanceIdCount()
			+ " instances "
			+ getCleaningDoneDescriber());
	}

	private int instanceIdCount() {

		return getInstanceIds().size();
	}

	private boolean anyInstanceIds() {

		return !getInstanceIds().isEmpty();
	}

	private List<CIdentity> getInstanceIds() {

		return instanceIdsList.getEntities();
	}
}
