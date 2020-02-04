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

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class QueryDialog extends InstantiationDialog {

	static private final long serialVersionUID = -1;

	static private final String FUNCTION_LABEL = "Query";
	static private final String EXECUTE_BUTTON_LABEL = "Execute";

	private QueryExecutions queryExecutions;

	private class ExecuteButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			dispose();
			execute();
		}

		ExecuteButton() {

			super(EXECUTE_BUTTON_LABEL);
		}
	}

	QueryDialog(
		JComponent parent,
		InstanceType instanceType,
		CIdentity storeId,
		QueryExecutions queryExecutions,
		boolean startAsViewOnly) {

		this(
			parent,
			instanceType.createQueryInstantiator(),
			storeId,
			queryExecutions,
			startAsViewOnly);
	}

	QueryDialog(
		JComponent parent,
		InstanceType instanceType,
		IFrame instantiation,
		CIdentity storeId,
		QueryExecutions queryExecutions,
		boolean startAsViewOnly) {

		this(
			parent,
			instanceType.createInstantiator(instantiation),
			storeId,
			queryExecutions,
			startAsViewOnly);
	}

	QueryDialog createCopy(JComponent parent, CIdentity storeId) {

		return new QueryDialog(parent, getInstantiator(), storeId, queryExecutions, false);
	}

	CIdentity checkObtainNewStoreId(StoreIdSelections idSelections, CIdentity oldId) {

		return idSelections.checkObtainForAssertion(oldId);
	}

	boolean disposeOnStoring() {

		return false;
	}

	void addControlComponents(ControlsPanel panel) {

		panel.addControl(new ExecuteButton());

		super.addControlComponents(panel);
	}
/*
	JComponent createControlsComponent() {

		ControlsPanel panel = new ControlsPanel(false);

		panel.addControl(new ExecuteButton());
		panel.addControl(super.createControlsComponent());

		return panel;
	}*/

	private QueryDialog(
				JComponent parent,
				Instantiator instantiator,
				CIdentity storeId,
				QueryExecutions queryExecutions,
				boolean startAsViewOnly) {

		super(parent, instantiator, storeId, FUNCTION_LABEL, startAsViewOnly);

		this.queryExecutions = queryExecutions;

		display();
	}

	private void execute() {

		queryExecutions.execute(getStoreId(), getInstantiation());
	}
}
