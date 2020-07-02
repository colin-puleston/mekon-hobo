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

package uk.ac.manchester.cs.mekon.gui.app;

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class Store {

	private IStore store;
	private Customiser customiser;

	Store(IStore store, Customiser customiser) {

		this.store = store;
		this.customiser = customiser;
	}

	boolean checkAdd(IFrame instance, CIdentity id, boolean asNewId) {

		if (!store.contains(id) || confirmReplace(id)) {

			if (asNewId) {

				instance = customiser.onNewInstance(instance, id);
			}

			store.add(instance, id);
			showStoredMessage(id);

			return true;
		}

		return false;
	}

	boolean checkRemove(CIdentity id) {

		if (confirmRemove(id)) {

			store.remove(id);
			showRemovedMessage(id);

			return true;
		}

		return false;
	}

	boolean checkRename(CIdentity id, CIdentity newId) {

		if (confirmRename(id, newId)) {

			IFrame instance = get(id);

			if (assertionId(id)) {

				instance = customiser.onRenamingInstance(instance, id, newId);
			}

			store.remove(id);
			store.add(instance, newId);

			showRenamedMessage(id, newId);

			return true;
		}

		return false;
	}

	boolean contains(CIdentity id) {

		return store.contains(id);
	}

	IFrame get(CIdentity id) {

		return store.get(id).getRootFrame();
	}

	CFrame getType(CIdentity id) {

		return getCFrame(store.getType(id).getRootTypeId());
	}

	List<CIdentity> match(IFrame query) {

		List<CIdentity> matches = getAssertionMatches(query);

		showQueryMatchesMessage(matches.size());

		return matches;
	}

	List<CIdentity> getInstanceIds(CFrame type) {

		return store.match(type.instantiate()).getAllMatches();
	}

	List<CIdentity> getAllInstanceIds() {

		return store.getAllIdentities();
	}

	private List<CIdentity> getAssertionMatches(IFrame query) {

		return extractAssertions(store.match(query).getAllMatches());
	}

	private List<CIdentity> extractAssertions(List<CIdentity> all) {

		List<CIdentity> assertions = new ArrayList<CIdentity>();

		for (CIdentity match : all) {

			if (assertionId(match)) {

				assertions.add(match);
			}
		}

		return assertions;
	}

	private boolean confirmReplace(CIdentity id) {

		return obtainConfirmation("Replace stored " + describeInstance(id));
	}

	private boolean confirmRemove(CIdentity id) {

		return obtainConfirmation("Remove stored " + describeInstance(id));
	}

	private boolean confirmRename(CIdentity id, CIdentity newId) {

		return obtainConfirmation(
					"Rename stored " + describeInstance(id)
					+ " to " + nameInstance(newId));
	}

	private void showStoredMessage(CIdentity id) {

		showMessage("Stored " + describeInstance(id));
	}

	private void showRemovedMessage(CIdentity id) {

		showMessage("Removed " + describeInstance(id));
	}

	private void showRenamedMessage(CIdentity id, CIdentity newId) {

		showMessage(
			"Renamed " + describeInstance(id)
			+ " to " + nameInstance(newId));
	}

	private void showQueryMatchesMessage(int count) {

		showMessage("Found " + count + " matches for supplied query");
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

	private String describeInstance(CIdentity id) {

		return describeFunction(id) + " " + nameInstance(id);
	}

	private String nameInstance(CIdentity id) {

		return "\"" + id.getLabel() + "\"";
	}

	private CFrame getCFrame(CIdentity id) {

		return store.getModel().getFrames().get(id);
	}

	private String describeFunction(CIdentity id) {

		return assertionId(id) ? "instance" : "query";
	}

	private boolean assertionId(CIdentity id) {

		return MekonAppStoreId.assertionId(id);
	}
}
