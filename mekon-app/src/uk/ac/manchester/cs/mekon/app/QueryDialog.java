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

	static private final String SUB_TITLE = "Query";
	static private final String EXECUTE_LABEL = "Execute";

	static private String createQueryTitle(InstanceType instanceType) {

		return createTitle(instanceType, SUB_TITLE);
	}

	private InstanceType instanceType;
	private QueryExecutor queryExecutor;

	private class ExecuteButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			execute();
		}

		ExecuteButton() {

			super(EXECUTE_LABEL);
		}
	}

	QueryDialog(
		JComponent parent,
		InstanceType instanceType,
		QueryExecutor queryExecutor) {

		this(
			parent,
			instanceType,
			instanceType.createQueryInstantiator(),
			queryExecutor);
	}

	QueryDialog(
		JComponent parent,
		InstanceType instanceType,
		IFrame instantiation,
		QueryExecutor queryExecutor) {

		this(
			parent,
			instanceType,
			instanceType.createInstantiator(instantiation),
			queryExecutor);
	}

	QueryDialog createCopy(JComponent parent) {

		return new QueryDialog(parent, instanceType, getInstantiator(), queryExecutor);
	}

	void addControlComponents(ControlsPanel panel) {

		panel.addControl(new ExecuteButton());

		super.addControlComponents(panel);
	}

	JComponent createExtraControlComponent() {

		return new ExecuteButton();
	}

	boolean directStorage() {

		return false;
	}

	void storeInstantiation() {

		CIdentity storeId = checkObtainStoreId();

		if (storeId != null) {

			instanceType.checkAddInstance(getInstantiation(), storeId);
		}
	}

	private QueryDialog(
				JComponent parent,
				InstanceType instanceType,
				Instantiator instantiator,
				QueryExecutor queryExecutor) {

		super(parent, instantiator, createQueryTitle(instanceType));

		this.instanceType = instanceType;
		this.queryExecutor = queryExecutor;

		display();
	}

	private CIdentity checkObtainStoreId() {

		return new StoreIdSelector(getRootWindow()).getIdSelection(IFrameFunction.QUERY);
	}

	private void execute() {

		dispose();

		queryExecutor.execute(getInstantiation());
	}
}
