/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package uk.ac.manchester.cs.mekon.gui;

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class AssertionFrame extends InstantiationFrame {

	static private final long serialVersionUID = -1;

	static private final String ASSERTION_LABEL = "Assertion";
	static private final String STORE_ACTION_LABEL = "Store";
	static private final String NAME_FIELD_TITLE = "Name:";

	static private final Dimension NAME_FIELD_SIZE = new Dimension(250, 25);

	private JTextField nameField = new JTextField();

	private class StoreButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			checkStore();
		}

		StoreButton() {

			super(STORE_ACTION_LABEL);

			setEnabling(this);
		}
	}

	AssertionFrame(
		CFramesTree modelTree,
		InstanceStoreActions storeActions,
		IFrame frame) {

		super(modelTree, storeActions, frame);

		GFonts.setMedium(nameField);
		nameField.setPreferredSize(NAME_FIELD_SIZE);
	}

	void display(CIdentity identity) {

		nameField.setText(identity.getLabel());

		display();
	}

	String getCategoryLabel() {

		return ASSERTION_LABEL;
	}

	JComponent createControlsPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createNameComponent(), BorderLayout.WEST);
		panel.add(new StoreButton(), BorderLayout.EAST);

		return panel;
	}

	private JComponent createNameComponent() {

		JPanel panel = new JPanel();

		panel.add(setEnabling(createNameFieldLabel()));
		panel.add(setEnabling(nameField));

		return panel;
	}

	private JLabel createNameFieldLabel() {

		JLabel label = new JLabel(NAME_FIELD_TITLE);

		GFonts.setLarge(nameField);

		return label;
	}

	private JComponent setEnabling(JComponent component) {

		getStoreActions().setInstanceStoreComponentEnabling(component);

		return component;
	}

	private void checkStore() {

		CIdentity id = getIdentityOrNull();

		if (id != null) {

			getStoreActions().storeInstance(getFrame(), id);
		}
	}

	private CIdentity getIdentityOrNull() {

		return CIdentityCreator.createOrNull(nameField.getText());
	}
}
