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

import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class InstantiationOptionsPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String STORE_CONCRETE_BUTTON_LABEL = "Store...";
	static private final String EXECUTE_QUERY_BUTTON_LABEL = "Execute";
	static private final String CONCRETE_SELECTOR_TITLE = "Instance Name";
	static private final String QUERY_MATCHES_DIALOG_TITLE = "Query Matches";

	private IFrame frame;

	private class StoreConcreteButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			storeConcrete();
		}

		StoreConcreteButton() {

			super(STORE_CONCRETE_BUTTON_LABEL);
		}
	}

	private class ExecuteQueryButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			executeQuery();
		}

		ExecuteQueryButton() {

			super(EXECUTE_QUERY_BUTTON_LABEL);
		}
	}

	InstantiationOptionsPanel(IFrame frame) {

		super(new BorderLayout());

		this.frame = frame;

		add(createButton(), BorderLayout.EAST);
	}

	private JButton createButton() {

		return frame.getCategory().query()
				? new ExecuteQueryButton()
				: new StoreConcreteButton();
	}

	private void storeConcrete() {

		CIdentity identity = checkObtainConcreteIdentity();

		if (identity != null && checkConcreteToBeStored(identity)) {

			getIStore().add(frame, identity);

			showConcreteStoredMessage(identity);
		}
	}

	private void executeQuery() {

		List<CIdentity> matches = getIStore().match(frame);

		if (matches.isEmpty()) {
		}
		else {

			new QueryMatchesDialog(this, matches);
		}
	}

	private CIdentity checkObtainConcreteIdentity() {

		return createIdentitySelector().getSelectionOrNull();
	}

	private CIdentitySelector createIdentitySelector() {

		return new CIdentitySelector(this, CONCRETE_SELECTOR_TITLE);
	}

	private boolean checkConcreteToBeStored(CIdentity identity) {

		if (getIStore().contains(identity)) {

			return confirmReplaceStoredConcrete(identity);
		}

		return true;
	}

	private boolean confirmReplaceStoredConcrete(CIdentity identity) {

		return obtainConfirmation(
					"Replacing instance: "
					+ "\"" + identity.getLabel() + "\"");
	}

	private void showConcreteStoredMessage(CIdentity identity) {

		showMessage("Instance stored: \"" + identity.getLabel() + "\"");
	}

	private void showNoQueryMatchesMessage(CIdentity identity) {

		showMessage("No matches for supplied query");
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

	private IStore getIStore() {

		return frame.getType().getModel().getIStore();
	}
}
