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
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class ExecutedQueriesPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String QUERY_SELECTOR_TITLE = "Executed Queries";

	static private final String DISPLAY_QUERY_LABEL = "View...";
	static private final String DISCARD_QUERY_LABEL = "Discard";

	private InstanceGroup instanceGroup;
	private QueryExecutions queryExecutions;

	private InstanceIdsList querySelectorList;
	private QueryMatchesPanel matchesPanel;

	private class DisplayQueryButton extends SelectedInstanceIdActionButton {

		static private final long serialVersionUID = -1;

		DisplayQueryButton() {

			super(querySelectorList, DISPLAY_QUERY_LABEL);
		}

		void doInstanceThing(CIdentity storeId) {

			displayQuery(storeId, queryExecutions.getExecuted(storeId).getQuery());
		}
	}

	private class DiscardQueryButton extends SelectedInstanceIdActionButton {

		static private final long serialVersionUID = -1;

		DiscardQueryButton() {

			super(querySelectorList, DISCARD_QUERY_LABEL);
		}

		void doInstanceThing(CIdentity storeId) {

			queryExecutions.discardExecuted(storeId);
			querySelectorList.removeEntity(storeId);
		}
	}

	private class MatchesDisplayUpdater extends GSelectionListener<CIdentity> {

		protected void onSelected(CIdentity storeId) {

			displayMatches(storeId);
		}

		protected void onDeselected(CIdentity storeId) {

			matchesPanel.clear();
		}

		MatchesDisplayUpdater() {

			querySelectorList.addSelectionListener(this);
		}
	}

	ExecutedQueriesPanel(InstanceGroup instanceGroup, QueryExecutions queryExecutions) {

		this.instanceGroup = instanceGroup;
		this.queryExecutions = queryExecutions;

		querySelectorList = new InstanceIdsList(instanceGroup, true);
		matchesPanel = new QueryMatchesPanel(instanceGroup);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(createQuerySelectorPanel());
		add(matchesPanel);

		new MatchesDisplayUpdater();
	}

	void add(ExecutedQuery executedQuery) {

		CIdentity storeId = executedQuery.getStoreId();

		querySelectorList.addEntity(storeId);
		querySelectorList.select(storeId);
	}

	private JComponent createQuerySelectorPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		PanelEntitler.entitle(panel, QUERY_SELECTOR_TITLE);

		panel.add(new GListPanel<CIdentity>(querySelectorList), BorderLayout.CENTER);
		panel.add(createQuerySelectorControlsComponent(), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createQuerySelectorControlsComponent() {

		ControlsPanel panel = new ControlsPanel(true);

		panel.addControl(new DisplayQueryButton());
		panel.addControl(new DiscardQueryButton());

		return panel;
	}

	private void displayMatches(CIdentity storeId) {

		ExecutedQuery execQuery = queryExecutions.getExecuted(storeId);

		matchesPanel.displayMatches(storeId, execQuery.getMatches());
	}

	private void displayQuery(CIdentity storeId, IFrame query) {

		Instantiator instantiator = instanceGroup.createInstantiator(query);

		new QueryDialog(this, instantiator, storeId, queryExecutions, true);
	}
}
