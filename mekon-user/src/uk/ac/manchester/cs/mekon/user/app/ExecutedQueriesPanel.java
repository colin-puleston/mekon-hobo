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

import java.awt.BorderLayout;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class ExecutedQueriesPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String QUERY_SELECTOR_TITLE = "Executed Queries";

	static private final String DISPLAY_QUERY_LABEL = "View...";
	static private final String COPY_QUERY_LABEL = "Copy...";
	static private final String DISCARD_QUERY_LABEL = "Discard";

	private InstanceGroup group;

	private QueryExecutions queryExecutions;
	private InstanceSubGroup instanceDisplayOps;

	private InstanceIdsList querySelectorList;
	private QueryMatchesPanel matchesPanel;

	private abstract class QueryAction {

		void perform(CIdentity storeId) {

			ExecutedQuery executed = queryExecutions.getExecuted(storeId);

			perform(createSubGroupDisplayOps(executed), storeId, executed.getQuery());
		}

		abstract void perform(InstanceDisplayOps ops, CIdentity storeId, IFrame query);
	}

	private class QueryDisplayAction extends QueryAction {

		void perform(InstanceDisplayOps ops, CIdentity storeId, IFrame query) {

			ops.displayExecutedQuery(storeId, query);
		}
	}

	private class QueryCopyAction extends QueryAction {

		void perform(InstanceDisplayOps ops, CIdentity storeId, IFrame query) {

			ops.copyExecutedQueryAndDisplay(query);
		}
	}

	private abstract class QueryActionButton extends SelectedInstanceActionButton {

		static private final long serialVersionUID = -1;

		private QueryAction action;

		QueryActionButton(QueryAction action, String title) {

			super(querySelectorList, title);

			this.action = action;
		}

		void doInstanceThing(CIdentity storeId) {

			action.perform(storeId);
		}
	}

	private class DisplayQueryButton extends QueryActionButton {

		static private final long serialVersionUID = -1;

		DisplayQueryButton() {

			super(new QueryDisplayAction(), DISPLAY_QUERY_LABEL);
		}
	}

	private class CopyQueryButton extends QueryActionButton {

		static private final long serialVersionUID = -1;

		CopyQueryButton() {

			super(new QueryCopyAction(), COPY_QUERY_LABEL);
		}
	}

	private class DiscardQueryButton extends SelectedInstanceActionButton {

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

	ExecutedQueriesPanel(InstanceGroup group) {

		this.group = group;

		queryExecutions = group.getQueryExecutions();

		querySelectorList = new InstanceIdsList(group, true);
		matchesPanel = new QueryMatchesPanel(group);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(createQuerySelectorPanel());
		add(matchesPanel);

		new MatchesDisplayUpdater();
	}

	void add(ExecutedQuery executedQuery) {

		CIdentity storeId = executedQuery.getStoreId();

		querySelectorList.checkAddId(storeId);
		querySelectorList.clearSelection();
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
		panel.addControl(new CopyQueryButton());
		panel.addControl(new DiscardQueryButton());

		return panel;
	}

	private void displayMatches(CIdentity storeId) {

		ExecutedQuery execQuery = queryExecutions.getExecuted(storeId);

		matchesPanel.displayMatches(storeId, execQuery.getMatches());
	}

	private InstanceDisplayOps createSubGroupDisplayOps(ExecutedQuery executed) {

		return new InstanceDisplayOps(this, executed.getSubGroup());
	}
}
