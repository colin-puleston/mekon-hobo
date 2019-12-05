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

import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class QueryGFrame extends InstantiationGFrame {

	static private final long serialVersionUID = -1;

	static private final String SUB_TITLE = "Query";
	static private final String MATCHES_TITLE = "Matches";

	static private final String EXECUTE_LABEL = "Execute";

	static private final Dimension WINDOW_SIZE = new Dimension(800, 500);

	static private String createQueryTitle(InstanceType instanceType) {

		return createTitle(instanceType, SUB_TITLE);
	}

	private InstanceType instanceType;
	private InstanceIdsPanel matchesPanel;

	private class ExecuteButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			executeAndDisplayMatches();
		}

		ExecuteButton() {

			super(EXECUTE_LABEL);
		}
	}

	public Dimension getPreferredSize() {

		return WINDOW_SIZE;
	}

	QueryGFrame(InstanceType instanceType) {

		this(instanceType, instanceType.createQueryInstantiator());
	}

	QueryGFrame createCopy() {

		return new QueryGFrame(instanceType, getInstantiator());
	}

	JComponent createMainComponent(JComponent instantiationComponent) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(instantiationComponent, BorderLayout.CENTER);
		panel.add(matchesPanel, BorderLayout.EAST);

		return panel;
	}

	JComponent createControlsComponent() {

		return new ExecuteButton();
	}

	private QueryGFrame(InstanceType instanceType, Instantiator instantiator) {

		super(instantiator, createQueryTitle(instanceType));

		this.instanceType = instanceType;

		matchesPanel = createMatchesPanel();

		display();
	}

	private InstanceIdsPanel createMatchesPanel() {

		return new InstanceIdsPanel(instanceType, MATCHES_TITLE, false);
	}

	private void executeAndDisplayMatches() {

		matchesPanel.displayIds(getStore().match(getInstantiation()));
	}

	private Store getStore() {

		return instanceType.getController().getStore();
	}
}
