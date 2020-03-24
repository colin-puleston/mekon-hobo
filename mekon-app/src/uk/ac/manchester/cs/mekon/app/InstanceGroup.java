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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class InstanceGroup {

	private Controller controller;
	private Store store;

	private CFrame rootType;
	private InstanceTypes instanceTypes;

	private InstanceIdsList rootAssertionIds;
	private InstanceIdsList rootQueryIds;

	private QueryExecutions queryExecutions;

	private abstract class InstanceTypes {

		void onAddedInstance(CIdentity storeId) {
		}

		void onRemovedInstance(CIdentity storeId) {
		}

		void onReplacedInstance(CIdentity storeId, CIdentity newStoreId) {
		}

		abstract CFrame getType(CIdentity storeId);
	}

	private class RootOnlyInstanceTypes extends InstanceTypes {

		CFrame getType(CIdentity storeId) {

			return rootType;
		}
	}

	private class InstanceSubTypes extends InstanceTypes {

		private Map<CIdentity, CFrame> subTypes = new HashMap<CIdentity, CFrame>();

		void onAddedInstance(CIdentity storeId) {

			CFrame type = store.getType(storeId);

			if (!type.equals(rootType)) {

				subTypes.put(storeId, type);
			}
		}

		void onRemovedInstance(CIdentity storeId) {

			subTypes.remove(storeId);
		}

		void onReplacedInstance(CIdentity storeId, CIdentity newStoreId) {

			CFrame type = subTypes.remove(storeId);

			if (type != null) {

				subTypes.put(newStoreId, type);
			}
		}

		CFrame getType(CIdentity storeId) {

			CFrame type = subTypes.get(storeId);

			return type != null ? type : rootType;
		}
	}

	InstanceGroup(Controller controller, CFrame rootType) {

		this.controller = controller;
		this.rootType = rootType;

		store = controller.getStore();

		instanceTypes = createInstanceTypes();

		rootAssertionIds = new InstanceIdsList(this, false);
		rootQueryIds = new InstanceIdsList(this, true);

		queryExecutions = new QueryExecutions(store);

		for (CIdentity storeId : store.getInstanceIds(rootType)) {

			instanceTypes.onAddedInstance(storeId);
			getInstanceIdsList(storeId).addId(storeId);
		}
	}

	InstanceIdsList createAssertionIdsList(CFrame type) {

		InstanceIdsList typeAssertIds = new InstanceIdsList(this, false);

		if (type.equals(rootType)) {

			typeAssertIds.addIds(rootAssertionIds.getEntityList());
		}
		else {

			for (CIdentity storeId : store.getInstanceIds(type)) {

				if (assertionId(storeId)) {

					typeAssertIds.addId(storeId);
				}
			}
		}

		return typeAssertIds;
	}

	Controller getController() {

		return controller;
	}

	CFrame getRootType() {

		return rootType;
	}

	boolean hasSubTypes() {

		return !rootType.getSubs(CVisibility.EXPOSED).isEmpty();
	}

	CFrame getInstanceType(CIdentity storeId) {

		return instanceTypes.getType(storeId);
	}

	InstanceIdsList getRootAssertionIdsList() {

		return rootAssertionIds;
	}

	InstanceIdsList getRootQueryIdsList() {

		return rootQueryIds;
	}

	QueryExecutions getQueryExecutions() {

		return queryExecutions;
	}

	boolean checkAddInstance(IFrame instance, CIdentity storeId, boolean asNewId) {

		if (store.checkAdd(instance, storeId, asNewId)) {

			instanceTypes.onAddedInstance(storeId);
			getInstanceIdsList(storeId).checkAddId(storeId);

			return true;
		}

		return false;
	}

	void checkRemoveInstance(CIdentity storeId) {

		if (store.checkRemove(storeId)) {

			instanceTypes.onRemovedInstance(storeId);
			getInstanceIdsList(storeId).removeEntity(storeId);
		}
	}

	void checkRenameInstance(CIdentity storeId, CIdentity newStoreId) {

		if (store.checkRename(storeId, newStoreId)) {

			instanceTypes.onReplacedInstance(storeId, newStoreId);
			getInstanceIdsList(storeId).replaceId(storeId, newStoreId);
		}
	}

	private InstanceTypes createInstanceTypes() {

		return hasSubTypes() ? new InstanceSubTypes() : new RootOnlyInstanceTypes();
	}

	private InstanceIdsList getInstanceIdsList(CIdentity storeId) {

		return assertionId(storeId) ? rootAssertionIds : rootQueryIds;
	}

	private boolean assertionId(CIdentity id) {

		return MekonAppStoreId.assertionId(id);
	}
}
