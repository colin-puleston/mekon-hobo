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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class InstanceType {

	private Controller controller;
	private Customiser customiser;

	private Store store;

	private CFrame type;

	private InstanceIdsList assertionIds;
	private InstanceIdsList queryIds;

	InstanceType(Controller controller, CFrame type) {

		this.controller = controller;
		this.type = type;

		customiser = controller.getCustomiser();
		store = controller.getStore();

		assertionIds = new InstanceIdsList(false, false);
		queryIds = new InstanceIdsList(true, false);

		for (CIdentity storeId : store.getInstanceIds(type)) {

			getInstanceIdsList(storeId).add(storeId);
		}
	}

	Controller getController() {

		return controller;
	}

	CFrame getType() {

		return type;
	}

	InstanceIdsList getAssertionIdsList() {

		return assertionIds;
	}

	InstanceIdsList getQueryIdsList() {

		return queryIds;
	}

	boolean checkAddInstance(IFrame instance, CIdentity storeId, boolean asNewId) {

		if (store.checkAdd(instance, storeId, asNewId)) {

			getInstanceIdsList(storeId).add(storeId);

			return true;
		}

		return false;
	}

	void checkRemoveInstance(CIdentity storeId) {

		if (store.checkRemove(storeId)) {

			getInstanceIdsList(storeId).remove(storeId);
		}
	}

	void checkRenameInstance(CIdentity storeId, CIdentity newStoreId) {

		if (store.checkRename(storeId, newStoreId)) {

			getInstanceIdsList(storeId).replace(storeId, newStoreId);
		}
	}

	Instantiator createNewAssertion(CIdentity storeId) {

		return createNewInstantiation(IFrameFunction.ASSERTION, storeId);
	}

	Instantiator createNewQuery(CIdentity storeId) {

		return createNewInstantiation(IFrameFunction.QUERY, storeId);
	}

	Instantiator recreateInstantiator(IFrame instantiation) {

		return new Instantiator(this, instantiation);
	}

	private Instantiator createNewInstantiation(IFrameFunction function, CIdentity storeId) {

		IFrame instantiation = type.instantiate(function);

		instantiation = customiser.onNewInstance(instantiation, storeId);

		return new Instantiator(this, instantiation);
	}

	private InstanceIdsList getInstanceIdsList(CIdentity storeId) {

		return assertionId(storeId) ? assertionIds : queryIds;
	}

	private boolean assertionId(CIdentity id) {

		return MekonAppStoreId.assertionId(id);
	}
}
