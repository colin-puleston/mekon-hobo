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

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
abstract class InstanceDialog extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "%s %s (%s)";

	static private final String STORE_BUTTON_LABEL = "Store";
	static private final String STORE_AS_BUTTON_LABEL = "Store As...";

	static private final String MODE_SELECTOR_LABEL = "View only";

	static private final int FRAME_WIDTH = 600;

	static private String createTitle(
							Instantiator instantiator,
							CIdentity storeId,
							String functionLabel) {

		String typeLabel = getTypeLabel(instantiator);
		String idLabel = storeId.getLabel();

		return String.format(TITLE_FORMAT, typeLabel, functionLabel, idLabel);
	}

	static private String getTypeLabel(Instantiator instantiator) {

		Customiser customiser = instantiator.getController().getCustomiser();

		return customiser.getValueDisplayLabel(instantiator.getInstance());
	}

	private Instantiator instantiator;
	private CIdentity storeId;

	private InstanceTree instanceTree;

	private boolean instanceStored = false;

	private class ViewOnlySelector extends GCheckBox {

		static private final long serialVersionUID = -1;

		protected void onSelectionUpdate(boolean selected) {

			setViewOnly(selected);
		}

		ViewOnlySelector() {

			super(MODE_SELECTOR_LABEL);

			setSelected(instanceTree.viewOnly());
		}
	}

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

	public Dimension getPreferredSize() {

		return new Dimension(FRAME_WIDTH, getPreferredHeight());
	}

	InstanceDialog(
		JComponent parent,
		Instantiator instantiator,
		CIdentity storeId,
		String functionTitle) {

		super(parent, createTitle(instantiator, storeId, functionTitle), true);

		this.instantiator = instantiator;
		this.storeId = storeId;

		instanceTree = new InstanceTree(instantiator);
	}

	void display(boolean startAsViewOnly, boolean enableDefaultStore) {

		setViewOnly(startAsViewOnly);

		display(createDisplay(enableDefaultStore));
	}

	void addExtraControlComponents(ControlsPanel panel) {
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

	abstract boolean disposeOnStoring();

	private JComponent createDisplay(boolean enableDefaultStore) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new ViewOnlySelector(), BorderLayout.NORTH);
		panel.add(new JScrollPane(instanceTree), BorderLayout.CENTER);
		panel.add(createControlsComponent(enableDefaultStore), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createControlsComponent(boolean enableDefaultStore) {

		ControlsPanel panel = new ControlsPanel(true);

		if (enableDefaultStore) {

			panel.addControl(new StoreButton());
		}

		panel.addControl(new StoreAsButton());
		addExtraControlComponents(panel);

		return panel;
	}

	private void setViewOnly(boolean value) {

		instanceTree.setViewOnly(value);
	}

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

		return createInstanceOps(parent).checkObtainNewStoreId(getInstanceType());
	}

	private InstanceOps createInstanceOps(JComponent parent) {

		return new InstanceOps(parent, getInstanceGroup(), instantiator.getFunction());
	}

	private CFrame getInstanceType() {

		return getInstance().getType();
	}

	private int getPreferredHeight() {

		return (int)super.getPreferredSize().getHeight();
	}
}
