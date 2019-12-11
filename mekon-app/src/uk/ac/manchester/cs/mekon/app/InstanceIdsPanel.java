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

/**
 * @author Colin Puleston
 */
class InstanceIdsPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String LOAD_LABEL = "Load";
	static private final String REMOVE_LABEL = "Remove";

	private InstanceType instanceType;
	private InstanceIdsList idsList;

	private class LoadButton extends SelectedInstanceIdActionButton {

		static private final long serialVersionUID = -1;

		LoadButton() {

			super(idsList, LOAD_LABEL);
		}

		void doInstanceThing(CIdentity id) {

			displayInstance(id);
		}
	}

	private class RemoveButton extends SelectedInstanceIdActionButton {

		static private final long serialVersionUID = -1;

		RemoveButton() {

			super(idsList, REMOVE_LABEL);
		}

		void doInstanceThing(CIdentity id) {

			instanceType.checkRemoveInstance(id);
		}
	}

	void displayIds(Collection<CIdentity> ids) {

		idsList.update(ids);
	}

	InstanceIdsPanel(InstanceType instanceType, String title, boolean allowRemove) {

		this(instanceType, new InstanceIdsList(false), title, allowRemove);
	}

	InstanceIdsPanel(
		InstanceType instanceType,
		InstanceIdsList idsList,
		String title,
		boolean allowRemove) {

		super(new BorderLayout());

		this.instanceType = instanceType;
		this.idsList = idsList;

		PanelEntitler.entitle(this, title);

		add(new JScrollPane(idsList), BorderLayout.CENTER);
		add(createControlsComponent(allowRemove), BorderLayout.SOUTH);
	}

	private JComponent createControlsComponent(boolean allowRemove) {

		return allowRemove ? createFullControlsComponent() : new LoadButton();
	}

	private JComponent createFullControlsComponent() {

		ControlsPanel panel = new ControlsPanel(true);

		panel.addControl(new LoadButton());
		panel.addControl(new RemoveButton());

		return panel;
	}

	private void displayInstance(CIdentity id) {

		new InstanceGFrame(instanceType, getInstance(id), id);
	}

	private IFrame getInstance(CIdentity id) {

		return instanceType.getController().getStore().get(id);
	}
}
