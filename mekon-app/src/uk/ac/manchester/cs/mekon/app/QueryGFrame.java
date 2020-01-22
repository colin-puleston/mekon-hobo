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
class QueryGFrame extends InstantiationGFrame {

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

	QueryGFrame(InstanceType instanceType, QueryExecutor queryExecutor) {

		this(instanceType, instanceType.createQueryInstantiator(), queryExecutor);
	}

	QueryGFrame(
		InstanceType instanceType,
		IFrame instantiation,
		QueryExecutor queryExecutor) {

		this(instanceType, instanceType.createInstantiator(instantiation), queryExecutor);
	}

	QueryGFrame createCopy() {

		return new QueryGFrame(instanceType, getInstantiator(), queryExecutor);
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

	private QueryGFrame(
				InstanceType instanceType,
				Instantiator instantiator,
				QueryExecutor queryExecutor) {

		super(instantiator, createQueryTitle(instanceType));

		this.instanceType = instanceType;
		this.queryExecutor = queryExecutor;

		display();
	}

	private CIdentity checkObtainStoreId() {

		return new StoreIdSelector(findOwnerFrame()).getIdSelection(IFrameFunction.QUERY);
	}

	private JFrame findOwnerFrame() {

		return (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, this);
	}

	private void execute() {

		dispose();

		queryExecutor.execute(getInstantiation());
	}
}
