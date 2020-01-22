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

package uk.ac.manchester.cs.mekon.app;

import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceTypePanel extends JTabbedPane {

	static private final long serialVersionUID = -1;

	static private final String DISPLAY_QUERY_LABEL = "Query...";
	static private final String REMOVE_TAB_LABEL = "Discard";

	private InstanceType instanceType;

	private QueryExecutor queryExecutor;
	private int executedQueryCount = 0;

	private RemoveTabButton removeTabButton = new RemoveTabButton();

	private class QueryExecutorLocal extends QueryExecutor {

		QueryExecutorLocal(Store store) {

			super(store);
		}

		void onExecuted(IFrame query, List<CIdentity> matches) {

			addMatches(query, matches);
		}
	}

	private class DisplayQueryButton extends GButton {

		static private final long serialVersionUID = -1;

		private IFrame query;

		protected void doButtonThing() {

			new QueryDialog(InstanceTypePanel.this, instanceType, query, queryExecutor);
		}

		DisplayQueryButton(IFrame query) {

			super(DISPLAY_QUERY_LABEL);

			this.query = query;
		}
	}

	private class RemoveTabButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			removeTabAt(getSelectedIndex());
		}

		RemoveTabButton() {

			super(REMOVE_TAB_LABEL);
		}
	}

	InstanceTypePanel(Store store, InstanceType instanceType) {

		super(JTabbedPane.LEFT);

		this.instanceType = instanceType;

		queryExecutor = new QueryExecutorLocal(store);

		addTab(InstancesPanel.TITLE, new InstancesPanel(instanceType));
		addTab(QueriesPanel.TITLE, new QueriesPanel(instanceType, queryExecutor));
	}

	private void addMatches(IFrame query, List<CIdentity> matches) {

		addTab(getNextMatchesTabTitle(), createMatchesComponent(query, matches));
		setSelectedIndex(getTabCount() - 1);
	}

	private JComponent createMatchesComponent(IFrame query, List<CIdentity> matches) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new QueryMatchesPanel(instanceType, matches), BorderLayout.CENTER);
		panel.add(createMatchesControlsComponent(query), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createMatchesControlsComponent(IFrame query) {

		ControlsPanel panel = new ControlsPanel(false);

		panel.addControl(new DisplayQueryButton(query));
		panel.addControl(removeTabButton);

		return panel;
	}

	private String getNextMatchesTabTitle() {

		return QueryMatchesPanel.TITLE + " [" + (++executedQueryCount) + "]";
	}
}
