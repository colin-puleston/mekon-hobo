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
abstract class InstantiationDialog extends GDialog implements AspectWindow {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "%s %s (%s)";
	static private final String STORE_BUTTON_LABEL = "Store";

	static private final int FRAME_WIDTH = 700;

	static private String createTitle(
							Instantiator instantiator,
							CIdentity storeId,
							String functionLabel) {

		String typeLabel = getTypeLabel(instantiator);
		String idLabel = storeId.getLabel();

		return String.format(TITLE_FORMAT, typeLabel, functionLabel, idLabel);
	}

	static private String getTypeLabel(Instantiator instantiator) {

		return instantiator.getInstanceType().getType().getIdentity().getLabel();
	}

	private JComponent parent;

	private Instantiator instantiator;
	private CIdentity storeId;

	private class StoreButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			if (storeInstantiation() && disposeOnStoring()) {

				dispose();
			}
		}

		StoreButton() {

			super(STORE_BUTTON_LABEL);
		}
	}

	public Dimension getPreferredSize() {

		return new Dimension(FRAME_WIDTH, getPreferredHeight());
	}

	public Window getRootWindow() {

		return (Window)SwingUtilities.getAncestorOfClass(Window.class, this);
	}

	public Instantiator getInstantiator() {

		return instantiator;
	}

	public void displayCopy() {

		createCopy(parent, storeId);
	}

	InstantiationDialog(
		JComponent parent,
		Instantiator instantiator,
		CIdentity storeId,
		String functionTitle) {

		super(parent, createTitle(instantiator, storeId, functionTitle), true);

		this.parent = parent;
		this.instantiator = instantiator;
		this.storeId = storeId;
	}

	void display() {

		display(createDisplay());
	}

	abstract InstantiationDialog createCopy(JComponent parent, CIdentity storeId);

	void addControlComponents(ControlsPanel panel) {

		panel.addControl(new StoreButton());
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

	abstract boolean disposeOnStoring();

	private JComponent createDisplay() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createMainComponent(), BorderLayout.CENTER);
		panel.add(createControlsComponent(), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createMainComponent() {

		return new JScrollPane(createDescriptorsTable());
	}

	private JComponent createControlsComponent() {

		ControlsPanel panel = new ControlsPanel(false);

		addControlComponents(panel);

		return panel;
	}

	private DescriptorsTable createDescriptorsTable() {

		return new DescriptorsTable(this, createDescriptorsList());
	}

	private DescriptorsList createDescriptorsList() {

		return new DescriptorsList(instantiator, getInstantiation());
	}

	private boolean storeInstantiation() {

		return getInstanceType().checkAddInstance(getInstantiation(), storeId);
	}

	private int getPreferredHeight() {

		return (int)super.getPreferredSize().getHeight();
	}
}
