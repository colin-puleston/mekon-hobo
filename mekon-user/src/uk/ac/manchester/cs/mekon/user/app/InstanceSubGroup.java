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

/**
 * @author Colin Puleston
 */
abstract class InstanceSubGroup {

	private InstanceGroup group;
	private Store targetStore;

	private CFrame rootType;
	private InstanceIdsList rootInstanceIds;

	InstanceSubGroup(InstanceGroup group) {

		this.group = group;

		rootType = group.getRootType();
		targetStore = getTargetStore();
		rootInstanceIds = createInstanceIdsList(rootType);

		for (CIdentity storeId : rootInstanceIds.getEntities()) {

			onAddition(storeId);
		}
	}

	InstanceGroup getGroup() {

		return group;
	}

	abstract InstanceSubGroup getAlternativeSubGroupOrNull();

	InstanceIdsList getRootInstanceIdsList() {

		return rootInstanceIds;
	}

	InstanceIdsList createInstanceIdsList(CFrame type) {

		InstanceIdsList idsList = new InstanceIdsList(group, queryInstances());

		for (CIdentity storeId : targetStore.getInstanceIds(type)) {

			if (subGroupInstance(storeId)) {

				idsList.addId(storeId);
			}
		}

		return idsList;
	}

	boolean editable() {

		return group.editable();
	}

	boolean checkAdd(IFrame instance, CIdentity storeId, boolean asNewId) {

		if (targetStore.checkAdd(instance, storeId, asNewId)) {

			onAddition(storeId);
			rootInstanceIds.checkAddId(storeId);

			return true;
		}

		return false;
	}

	boolean checkRemove(CIdentity storeId) {

		if (targetStore.checkRemove(storeId)) {

			group.onRemoval(storeId);
			rootInstanceIds.removeEntity(storeId);

			return true;
		}

		return false;
	}

	boolean checkRename(CIdentity storeId, CIdentity newStoreId) {

		if (targetStore.checkRename(storeId, newStoreId)) {

			group.onReplacement(storeId, newStoreId);
			rootInstanceIds.replaceId(storeId, newStoreId);

			return true;
		}

		return false;
	}

	boolean contains(CIdentity storeId) {

		return rootInstanceIds.containsEntity(storeId);
	}

	IFrame get(CIdentity storeId) {

		return targetStore.get(storeId);
	}

	Store getTargetStore() {

		return group.getController().getCentralStore();
	}

	abstract IFrameFunction getFunction();

	abstract boolean instanceCreationEnabled();

	abstract boolean subGroupInstance(CIdentity storeId);

	abstract String createInstanceNameDefault(CFrame type, CIdentity refingId);

	private void onAddition(CIdentity storeId) {

		group.onAddition(storeId, targetStore.getType(storeId));
	}

	private boolean queryInstances() {

		return getFunction().query();
	}
}
