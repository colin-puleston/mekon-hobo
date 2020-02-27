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
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
abstract class InstantiationDialog extends GDialog {

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

		return instantiator.getInstantiation().getType().getIdentity().getLabel();
	}

	private Instantiator instantiator;
	private CIdentity storeId;

	private InstantiationTree instantiationTree;

	private class ModeSelector extends JCheckBox implements ActionListener {

		static private final long serialVersionUID = -1;

		public void actionPerformed(ActionEvent event) {

			instantiationTree.setViewOnly(isSelected());
		}

		ModeSelector() {

			super(MODE_SELECTOR_LABEL);

			setSelected(instantiationTree.viewOnly());
			addActionListener(this);
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

	InstantiationDialog(
		JComponent parent,
		Instantiator instantiator,
		CIdentity storeId,
		String functionTitle,
		boolean startAsViewOnly) {

		super(parent, createTitle(instantiator, storeId, functionTitle), true);

		this.instantiator = instantiator;
		this.storeId = storeId;

		instantiationTree = new InstantiationTree(instantiator, startAsViewOnly);
	}

	void display() {

		display(createDisplay());
	}

	void addControlComponents(ControlsPanel panel) {

		panel.addControl(new StoreButton());
		panel.addControl(new StoreAsButton());
	}

	InstanceType getInstanceType() {

		return instantiator.getInstanceType();
	}

	IFrame getInstantiation() {

		return instantiator.getInstantiation();
	}

	CIdentity getStoreId() {

		return storeId;
	}

	abstract CIdentity checkObtainNewStoreId(StoreIdSelections idSelections, CIdentity oldId);

	abstract boolean disposeOnStoring();

	private JComponent createDisplay() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new ModeSelector(), BorderLayout.NORTH);
		panel.add(new JScrollPane(instantiationTree), BorderLayout.CENTER);
		panel.add(createControlsComponent(), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createControlsComponent() {

		ControlsPanel panel = new ControlsPanel(true);

		addControlComponents(panel);

		return panel;
	}

	private void perfomStoreAsAction(JComponent parent) {

		CIdentity newStoreId = checkObtainNewStoreId(parent);

		if (newStoreId != null) {

			perfomStoreAction(newStoreId);
		}
	}

	private void perfomStoreAction(CIdentity storeAsId) {

		if (storeInstantiation(storeAsId) && disposeOnStoring()) {

			dispose();
		}
	}

	private boolean storeInstantiation(CIdentity storeAsId) {

		boolean asNewId = !storeAsId.equals(storeId);

		return getInstanceType().checkAddInstance(getInstantiation(), storeAsId, asNewId);
	}

	private CIdentity checkObtainNewStoreId(JComponent parent) {

		return checkObtainNewStoreId(new StoreIdSelections(parent, getController()), storeId);
	}

	private int getPreferredHeight() {

		return (int)super.getPreferredSize().getHeight();
	}

	private Controller getController() {

		return getInstanceType().getController();
	}
}
