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
abstract class InstanceDialog extends InstanceSectionDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "%s (%s)";

	static private final String STORE_BUTTON_LABEL = "Store";
	static private final String STORE_AS_BUTTON_LABEL = "Store As...";

	static private String createTitle(Instantiator instantiator, CIdentity storeId) {

		return String.format(
					TITLE_FORMAT,
					createSectionTitle(instantiator),
					storeId.getLabel());
	}

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

			perfomStoreAsAction(this);
		}

		StoreAsButton() {

			super(STORE_AS_BUTTON_LABEL);
		}
	}

	InstanceDialog(
		JComponent parent,
		Instantiator instantiator,
		CIdentity storeId,
		InstanceDisplayMode startMode) {

		super(
			parent,
			instantiator,
			instantiator.getInstance(),
			createTitle(instantiator, storeId),
			startMode);

		this.storeId = storeId;
	}

	void setAllowStoreOverwrite(boolean value) {

		allowStoreOverwrite = value;
	}

	ControlsPanel checkCreateControlsPanel() {

		if (!getInstanceGroup().editable()) {

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

		return getInstantiator().getInstanceGroup();
	}

	IFrame getInstance() {

		return getInstantiator().getInstance();
	}

	CIdentity getStoreId() {

		return storeId;
	}

	boolean instanceStored() {

		return instanceStored;
	}

	abstract boolean disposeOnStoring();

	private void perfomStoreAsAction(JComponent parent) {

		CIdentity newStoreId = checkObtainNewStoreId(parent);

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

	private boolean storeInstance(CIdentity storeAsId) {

		boolean asNewId = !storeAsId.equals(storeId);

		return getInstanceGroup().checkAddInstance(getInstance(), storeAsId, asNewId);
	}

	private CIdentity checkObtainNewStoreId(JComponent parent) {

		return createInstanceOps(parent).checkObtainNewStoreId(getInstance().getType());
	}

	private InstanceOps createInstanceOps(JComponent parent) {

		return new InstanceOps(parent, getInstanceGroup(), getInstantiator().getFunction());
	}
}
