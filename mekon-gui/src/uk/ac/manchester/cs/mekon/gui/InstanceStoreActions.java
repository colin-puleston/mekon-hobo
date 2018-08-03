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

import javax.swing.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class InstanceStoreActions {

	static private final String SELECTOR_TITLE = "Instance Name";

	private IStore store;
	private CFramesTree modelTree;

	InstanceStoreActions(IStore store, CFramesTree modelTree) {

		this.store = store;
		this.modelTree = modelTree;
	}

	void setInstanceStoreComponentEnabling(JComponent component) {

		component.setEnabled(store != null);
	}

	void storeInstance(IFrame instance, CIdentity id) {

		if (checkStorageRequired(id)) {

			getStore().add(instance, id);

			showStoredMessage(id);
		}
	}

	void retrieveAndDisplayInstance(CIdentity id) {

		IFrame instance = getStore().get(id).getRootFrame();

		new AssertionFrame(modelTree, this, instance).display(id);
	}

	void retrieveAndDisplaySelectedInstance() {

		CIdentity id = getIdentityOrNull();

		if (id != null) {

			retrieveAndDisplayInstance(id);
		}
	}

	void removeSelectedInstance() {

		CIdentity id = getIdentityOrNull();

		if (id != null && confirmRemove(id)) {

			getStore().remove(id);
			showRemovedMessage(id);
		}
	}

	IMatches executeQuery(IFrame query) {

		IMatches matches = getStore().match(query);

		if (!matches.anyMatches()) {

			showMessage("No matches for supplied query");
		}

		return matches;
	}

	private CIdentity getIdentityOrNull() {

		CIdentity id = getIdentitySelectionOrNull();

		if (id != null) {

			if (getStore().contains(id)) {

				return id;
			}

			showNotStoredMessage(id);
		}

		return null;
	}

	private CIdentity getIdentitySelectionOrNull() {

		return new CIdentitySelector(modelTree, SELECTOR_TITLE).getSelectionOrNull();
	}

	private boolean checkStorageRequired(CIdentity id) {

		if (getStore().contains(id)) {

			return confirmReplaceStored(id);
		}

		return true;
	}

	private boolean confirmReplaceStored(CIdentity identity) {

		return obtainConfirmation(
					"Replace stored instance: "
					+ "\"" + identity.getLabel() + "\"");
	}

	private boolean confirmRemove(CIdentity id) {

		return obtainConfirmation(
					"Remove stored instance: "
					+ "\"" + id.getLabel() + "\"");
	}

	private void showStoredMessage(CIdentity id) {

		showMessage("Instance stored: \"" + id.getLabel() + "\"");
	}

	private void showNotStoredMessage(CIdentity id) {

		showMessage("Not a stored instance: \"" + id.getLabel() + "\"");
	}

	private void showRemovedMessage(CIdentity id) {

		showMessage("Instance removed from store: \"" + id.getLabel() + "\"");
	}

	private boolean obtainConfirmation(String msg) {

		return obtainConfirmationOption(msg) == JOptionPane.OK_OPTION;
	}

	private int obtainConfirmationOption(String msg) {

		return JOptionPane.showConfirmDialog(
					null,
					msg,
					"Confirm?",
					JOptionPane.OK_CANCEL_OPTION);
	}

	private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}

	private IStore getStore() {

		if (store == null) {

			throw new Error("Instance store not set!");
		}

		return store;
	}
}
