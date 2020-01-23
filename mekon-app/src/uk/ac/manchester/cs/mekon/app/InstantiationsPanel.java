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
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
abstract class InstantiationsPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String CREATE_LABEL = "Create...";
	static private final String LOAD_LABEL = "Load";
	static private final String REMOVE_LABEL = "Remove";

	private InstanceType instanceType;
	private InstanceIdsList idsList;

	private class CreateButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			checkInstantiate();
		}

		CreateButton() {

			super(CREATE_LABEL);
		}
	}

	private class LoadButton extends SelectedInstanceIdActionButton {

		static private final long serialVersionUID = -1;

		LoadButton() {

			super(idsList, LOAD_LABEL);
		}

		void doInstanceThing(CIdentity storeId) {

			loadInstantiation(storeId);
		}
	}

	private class RemoveButton extends SelectedInstanceIdActionButton {

		static private final long serialVersionUID = -1;

		RemoveButton() {

			super(idsList, REMOVE_LABEL);
		}

		void doInstanceThing(CIdentity storeId) {

			instanceType.checkRemoveInstance(storeId);
		}
	}

	InstantiationsPanel(InstanceType instanceType, InstanceIdsList idsList, String title) {

		super(new BorderLayout());

		this.instanceType = instanceType;
		this.idsList = idsList;

		PanelEntitler.entitle(this, title);

		add(new JScrollPane(idsList), BorderLayout.CENTER);
		add(createControlsComponent(), BorderLayout.SOUTH);
	}

	void displayIds(Collection<CIdentity> ids) {

		idsList.update(ids);
	}

	boolean allowLoadActionOnly() {

		return false;
	}

	IFrameFunction getFunction() {

		return IFrameFunction.ASSERTION;
	}

	void initialiseNewIdSelector(StoreIdSelector selector) {
	}

	abstract void displayNewInstantiation(InstanceType instanceType, CIdentity storeId);

	abstract void displayInstantiation(
					InstanceType instanceType,
					IFrame instantiation,
					CIdentity storeId);

	private JComponent createControlsComponent() {

		return allowLoadActionOnly() ? new LoadButton() : createFullControlsComponent();
	}

	private JComponent createFullControlsComponent() {

		ControlsPanel panel = new ControlsPanel(true);

		panel.addControl(new CreateButton());
		panel.addControl(new LoadButton());
		panel.addControl(new RemoveButton());

		return panel;
	}

	private void checkInstantiate() {

		CIdentity storeId = checkObtainStoreId();

		if (storeId != null) {

			displayNewInstantiation(instanceType, storeId);
		}
	}

	private void loadInstantiation(CIdentity storeId) {

		displayInstantiation(instanceType, getStoredInstantiation(storeId), storeId);
	}

	private IFrame getStoredInstantiation(CIdentity storeId) {

		return instanceType.getController().getStore().get(storeId);
	}

	private CIdentity checkObtainStoreId() {

		StoreIdSelector selector = new StoreIdSelector(findOwnerFrame());

		initialiseNewIdSelector(selector);

		return selector.getIdSelection(getFunction());
	}

	private JFrame findOwnerFrame() {

		return (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, this);
	}
}
