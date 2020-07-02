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

package uk.ac.manchester.cs.mekon.gui.app;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceGroupPanel extends JTabbedPane {

	static private final long serialVersionUID = -1;

	static private final String ASSERTIONS_TITLE = "Instances";
	static private final String QUERIES_TITLE = "Queries";
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
	}

	InstanceGroupPanel(InstanceGroup group) {

		super(JTabbedPane.TOP);

		executedQueriesPanel = new ExecutedQueriesPanel(group);

		setFont(GFonts.toMedium(getFont()));
		addTab(ASSERTIONS_TITLE, new AssertionsPanel(group));

		if (editableQueries(group)) {

			addTab(QUERIES_TITLE, new QueriesPanel(group));
			addTab(EXECUTED_QUERIES_TITLE, executedQueriesPanel);

			new QueryMatchesDisplayer(group);
		}
	}

	private boolean editableQueries(InstanceGroup group) {

		Controller controller = group.getController();
		IFrame testQuery = instantiateRootTypeQuery(group);

		return Instantiator.editableInstance(controller, testQuery);
	}

	private IFrame instantiateRootTypeQuery(InstanceGroup group) {

		return group.getRootType().instantiate(IFrameFunction.QUERY);
	}
}
