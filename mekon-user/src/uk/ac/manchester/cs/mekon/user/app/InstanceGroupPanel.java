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

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceGroupPanel extends JTabbedPane {

	static private final long serialVersionUID = -1;

	static private final String EXECUTED_QUERIES_TITLE = "Query Results";

	private ExecutedQueriesPanel executedQueriesPanel;

	private class QueryMatchesDisplayer extends QueryExecutionListener {

		QueryMatchesDisplayer(InstanceGroup group) {

			group.getQueryExecutions().addListener(this);
		}

		void onExecuted(ExecutedQuery executedQuery) {

			setSelectedIndex(getTabCount() - 1);
			executedQueriesPanel.add(executedQuery);
		}

		void onDiscarded(ExecutedQuery executedQuery) {
		}
	}

	InstanceGroupPanel(InstanceGroup group) {

		super(JTabbedPane.TOP);

		executedQueriesPanel = new ExecutedQueriesPanel(group);

		setFont(GFonts.toMedium(getFont()));

		addAssertionsTab(group.getAssertionSubGroup());

		if (group.queriesEnabled()) {

			if (group.isLocalQueriesSubGroup()) {

				addQueriesTab(group.getCentralQuerySubGroup(), true);
				addQueriesTab(group.getLocalQuerySubGroup(), true);
			}
			else {

				addQueriesTab(group.getCentralQuerySubGroup(), false);
			}

			addTab(EXECUTED_QUERIES_TITLE, executedQueriesPanel);

			new QueryMatchesDisplayer(group);
		}
	}

	private void addAssertionsTab(InstanceSubGroup subGroup) {

		String title = subGroup.getSubGroupName(false);

		addTab(title, new AssertionSubGroupPanel(subGroup, title));
	}

	private void addQueriesTab(InstanceSubGroup subGroup, boolean qualified) {

		String title = subGroup.getSubGroupName(qualified);

		addTab(title, new QuerySubGroupPanel(subGroup, title));
	}
}
