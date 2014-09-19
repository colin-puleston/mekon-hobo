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
class InstanceStorePanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String RETRIEVE_BUTTON_LABEL = "Retrieve Instance...";
	static private final String SELECTOR_TITLE = "Instance Name";

	private CModel model;
	private CFramesTree modelTree;

	private class RetrieveButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			retrieve();
		}

		RetrieveButton() {

			super(RETRIEVE_BUTTON_LABEL);
		}
	}

	InstanceStorePanel(CModel model, CFramesTree modelTree) {

		super(new BorderLayout());

		this.model = model;
		this.modelTree = modelTree;

		add(new RetrieveButton(), BorderLayout.EAST);
	}

	private void retrieve() {

		CIdentity id = getIdentityOrNull();

		if (id != null) {

			if (getIStore().contains(id)) {

				ConcreteInstanceFrame.display(modelTree, getIStore(), id);
			}
			else {

				showMessage("Not a stored instance: " + id.getLabel());
			}
		}
	}

	private CIdentity getIdentityOrNull() {

		return new CIdentitySelector(this, SELECTOR_TITLE).getSelectionOrNull();
	}

	private IStore getIStore() {

		return model.getIStore();
	}

	private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}
}
