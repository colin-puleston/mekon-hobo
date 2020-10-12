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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
abstract class InstanceDialog extends InstanceTreeDialog {

	static private final long serialVersionUID = -1;

	static private final String STORE_BUTTON_LABEL = "Store";
	static private final String STORE_AS_BUTTON_LABEL = "Store As...";

	private Instantiator instantiator;

	private CIdentity storeId;
	private boolean allowStoreOverwrite = true;

	private boolean instanceStored = false;

	private class StoreButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			perfomStoreAction(storeId);
		}

		StoreButton() {

			super(STORE_BUTTON_LABEL);
		}
	}

	private class StoreAsButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			perfomStoreAsAction();
		}

		StoreAsButton() {

			super(STORE_AS_BUTTON_LABEL);
		}
	}

	InstanceDialog(
		JComponent parent,
		Instantiator instantiator,
		InstanceDisplayMode startMode) {

		this(parent, instantiator, instantiator.getInstance(), startMode);
	}

	InstanceDialog(
		JComponent parent,
		Instantiator instantiator,
		IFrame rootFrame,
		InstanceDisplayMode startMode) {

		super(parent, instantiator, rootFrame, startMode);

		this.instantiator = instantiator;

		storeId = instantiator.getStoreId();
	}

	void setAllowStoreOverwrite(boolean value) {

		allowStoreOverwrite = value;
	}

	ControlsPanel checkCreateControlsPanel() {

		if (!instantiator.editableInstance()) {

			return null;
		}

		ControlsPanel panel = new ControlsPanel(true);

		if (allowStoreOverwrite) {

			panel.addControl(new StoreButton());
		}

		panel.addControl(new StoreAsButton());

		return panel;
	}

	InstanceGroup getInstanceGroup() {

		return instantiator.getInstanceGroup();
	}

	IFrame getInstance() {

		return instantiator.getInstance();
	}

	CIdentity getStoreId() {

		return storeId;
	}

	boolean instanceStored() {

		return instanceStored;
	}

	abstract IFrame resolveInstanceForStoring();

	abstract boolean disposeOnStoring();

	private void perfomStoreAsAction() {

		CIdentity newStoreId = checkObtainNewStoreId();

		if (newStoreId != null) {

			perfomStoreAction(newStoreId);
		}
	}

	private void perfomStoreAction(CIdentity storeAsId) {

		if (storeInstance(storeAsId)) {

			storeId = storeAsId;
			instanceStored = true;

			if (disposeOnStoring()) {

				dispose();
			}
		}
	}

	private void updateInstance(IFrame updatedInstance) {

		dispose();

		createInstanceOps().display(storeId, updatedInstance, getMode(), allowStoreOverwrite);
	}

	private boolean storeInstance(CIdentity storeAsId) {

		IFrame instanceToStore = resolveInstanceForStoring();
		boolean asNewId = !storeAsId.equals(storeId);

		return getInstanceGroup().checkAddInstance(instanceToStore, storeAsId, asNewId);
	}

	private CIdentity checkObtainNewStoreId() {

		return createInstanceOps().checkObtainNewStoreId(getInstance().getType());
	}

	private InstanceOps createInstanceOps() {

		return new InstanceOps(getTree(), getInstanceGroup(), instantiator.getFunction());
	}
}
