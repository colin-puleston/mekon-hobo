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

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class QuerySubGroupPanel extends InstanceSubGroupPanel {

	static private final long serialVersionUID = -1;

	private QueryExecutions queryExecutions;

	private class ExecutionsChangeDrivenDeselector extends QueryExecutionListener {

		void onExecuted(ExecutedQuery executedQuery) {

			updateDisplay();
		}

		void onDiscarded(ExecutedQuery executedQuery) {

			updateDisplay();
		}
	}

	QuerySubGroupPanel(InstanceSubGroup subGroup, String title) {

		super(subGroup, title);

		queryExecutions = subGroup.getGroup().getQueryExecutions();

		initialise();

		queryExecutions.addListener(new ExecutionsChangeDrivenDeselector());
	}

	boolean allowPotentialEditOp(CIdentity storeId) {

		return !queryExecutions.executed(storeId);
	}
}
