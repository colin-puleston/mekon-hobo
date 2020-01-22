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

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class Store {

	private IStore store;

	Store(IStore store) {

		this.store = store;
	}

	boolean checkAdd(IFrame instance, CIdentity id) {

		if (checkInstanceStorageRequired(id)) {

			store.add(instance, id);
			showInstanceStoredMessage(id);

			return true;
		}

		return false;
	}

	boolean checkRemove(CIdentity id) {

		if (confirmRemoveStoredInstance(id)) {

			store.remove(id);
			showInstanceRemovedMessage(id);

			return true;
		}

		return false;
	}

	IFrame get(CIdentity id) {

		return store.get(id, getFunction(id)).getRootFrame();
	}

	List<CIdentity> match(IFrame query) {

		List<CIdentity> matches = getFilteredMatches(query);

		showQueryMatchesMessage(matches.size());

		return matches;
	}

	List<CIdentity> getInstanceIds(CFrame type) {

		return store.match(type.instantiate()).getAllMatches();
	}

	private List<CIdentity> getFilteredMatches(IFrame query) {

		return filterQueryMatches(store.match(query).getAllMatches());
	}

	private List<CIdentity> filterQueryMatches(List<CIdentity> all) {

		List<CIdentity> filtered = new ArrayList<CIdentity>();

		for (CIdentity match : all) {

			if (assertionId(match)) {

				filtered.add(match);
			}
		}

		return filtered;
	}

	private boolean checkInstanceStorageRequired(CIdentity id) {

		return !store.contains(id) || confirmReplaceStoredInstance(id);
	}

	private boolean confirmReplaceStoredInstance(CIdentity id) {

		return obtainConfirmation("Replace stored " + describeInstance(id));
	}

	private boolean confirmRemoveStoredInstance(CIdentity id) {

		return obtainConfirmation("Remove stored " + describeInstance(id));
	}

	private void showInstanceStoredMessage(CIdentity id) {

		showMessage("Stored " + describeInstance(id));
	}

	private void showInstanceRemovedMessage(CIdentity id) {

		showMessage("Removed " + describeInstance(id));
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

		return describeFunction(id) + ": \"" + id.getLabel() + "\"";
	}

	private IFrameFunction getFunction(CIdentity id) {

		return assertionId(id) ? IFrameFunction.ASSERTION : IFrameFunction.QUERY;
	}

	private String describeFunction(CIdentity id) {

		return assertionId(id) ? "instance" : "query";
	}

	private boolean assertionId(CIdentity id) {

		return MekonAppStoreId.assertionId(id);
	}
}
