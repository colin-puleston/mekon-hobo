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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
abstract class SelectedInstanceActionButton extends GButton {

	static private final long serialVersionUID = -1;

	private InstanceIdsList idsList;

	private class SelectionBasedEnablingUpdater extends GSelectionListener<CIdentity> {

		protected void onSelected(CIdentity storeId) {

			updateEnabling();
		}

		protected void onDeselected(CIdentity storeId) {

			updateEnabling();
		}

		SelectionBasedEnablingUpdater() {

			idsList.addSelectionListener(this);
		}
	}

	private class RemovalBasedDisabler extends GListListener<CIdentity> {

		protected void onAdded(CIdentity entity) {
		}

		protected void onRemoved(CIdentity entity) {

			setEnabled(false);
		}

		RemovalBasedDisabler() {

			idsList.addListListener(this);
		}
	}

	protected void doButtonThing() {

		doInstanceThing(idsList.getSelectedEntity());
	}

	SelectedInstanceActionButton(InstanceIdsList idsList, String label) {

		super(label);

		this.idsList = idsList;

		updateEnabling();

		new SelectionBasedEnablingUpdater();
		new RemovalBasedDisabler();
	}

	boolean enableIfSelection() {

		return true;
	}

	abstract void doInstanceThing(CIdentity storeId);

	private void updateEnabling() {

		setEnabled(enableIfSelection() && idsList.anySelections());
	}
}
