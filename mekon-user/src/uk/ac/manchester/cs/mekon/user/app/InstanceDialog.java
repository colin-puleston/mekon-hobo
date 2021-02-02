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
	static private final String STORE_AS_DEFAULT_BUTTON_LABEL = "Store As...";
	static private final String STORE_AS_CENTRAL_BUTTON_LABEL = "Store As (Central)...";
	static private final String STORE_AS_LOCAL_BUTTON_LABEL = "Store As (Local)...";

	static private String getAlternativeStoreAsLabel(boolean centralStore) {

		return centralStore ? STORE_AS_CENTRAL_BUTTON_LABEL : STORE_AS_LOCAL_BUTTON_LABEL;
	}

	private Instantiator instantiator;

	private CIdentity storeId;
	private boolean instanceStored = false;

	private class StoreButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			perfomStoreAction(getDefaultSubGroup(), storeId);
		}

		StoreButton() {

			super(STORE_BUTTON_LABEL);
		}
	}

	private class StoreAsButton extends GButton {

		static private final long serialVersionUID = -1;

		private InstanceSubGroup targetSubGroup;

		protected void doButtonThing() {

			perfomStoreAsAction(targetSubGroup);
		}

		StoreAsButton() {

			this(getDefaultSubGroup(), STORE_AS_DEFAULT_BUTTON_LABEL);
		}

		StoreAsButton(InstanceSubGroup altSubGroup) {

			this(altSubGroup, getAlternativeStoreAsLabel(centralSubGroup(altSubGroup)));
		}

		private StoreAsButton(InstanceSubGroup targetSubGroup, String label) {

			super(label);

			this.targetSubGroup = targetSubGroup;
		}
	}

	InstanceDialog(JComponent parent, Instantiator instantiator, String titleSuffix) {

		super(parent, instantiator, titleSuffix);

		this.instantiator = instantiator;

		storeId = instantiator.getStoreId();
	}

	InstanceSubGroup getDefaultSubGroup() {

		return instantiator.getSubGroup();
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

	ControlsPanel checkCreateControlsPanel() {

		if (!editAllowed()) {

			return null;
		}

		ControlsPanel panel = new ControlsPanel(true);

		InstanceSubGroup subGroup = getDefaultSubGroup();
		InstanceSubGroup altSubGroup = subGroup.getAlternativeSubGroupOrNull();

		if (subGroup.editable()) {

			panel.addControl(new StoreButton());
			panel.addControl(new StoreAsButton());
		}

		if (altSubGroup != null && altSubGroup.editable()) {

			panel.addControl(new StoreAsButton(altSubGroup));
		}

		return panel;
	}

	boolean centralSubGroup(InstanceSubGroup alternativeSubGroup) {

		throw new Error("Method should never be invoked!");
	}

	abstract IFrame resolveInstanceForStoring();

	abstract boolean disposeOnStoring();

	private void perfomStoreAsAction(InstanceSubGroup targetSubGroup) {

		CIdentity newStoreId = checkObtainNewStoreId(targetSubGroup);

		if (newStoreId != null) {

			perfomStoreAction(targetSubGroup, newStoreId);
		}
	}

	private void perfomStoreAction(InstanceSubGroup targetSubGroup, CIdentity storeAsId) {

		if (storeInstance(targetSubGroup, storeAsId)) {

			storeId = storeAsId;
			instanceStored = true;

			if (disposeOnStoring()) {

				dispose();
			}
		}
	}

	private boolean storeInstance(InstanceSubGroup targetSubGroup, CIdentity storeAsId) {

		IFrame instanceToStore = resolveInstanceForStoring();
		boolean asNewId = !storeAsId.equals(storeId);

		return targetSubGroup.checkAdd(instanceToStore, storeAsId, asNewId);
	}

	private CIdentity checkObtainNewStoreId(InstanceSubGroup targetSubGroup) {

		return createDisplayOps(targetSubGroup).checkObtainNewStoreId(getInstance().getType());
	}

	private InstanceDisplayOps createDisplayOps(InstanceSubGroup targetSubGroup) {

		return new InstanceDisplayOps(getTree(), targetSubGroup);
	}
}
