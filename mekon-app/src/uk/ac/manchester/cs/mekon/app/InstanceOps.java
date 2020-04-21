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

/**
 * @author Colin Puleston
 */
class InstanceOps {

	private JComponent parent;
	private InstanceGroup instanceGroup;
	private IFrameFunction function;

	private Store store;
	private Customiser customiser;

	InstanceOps(
		JComponent parent,
		InstanceGroup instanceGroup,
		IFrameFunction function) {

		this.parent = parent;
		this.instanceGroup = instanceGroup;
		this.function = function;

		Controller controller = instanceGroup.getController();

		store = controller.getStore();
		customiser = controller.getCustomiser();
	}

	boolean simpleInstancesEnabled() {

		return function.query() && instanceGroup.simpleQueriesEnabled();
	}

	CIdentity checkDisplayNew() {

		return checkDisplayNew(instanceGroup.getRootType(), null);
	}

	CIdentity checkDisplayNew(CFrame rootType, CIdentity refingId) {

		CFrame type = checkDetermineType(rootType);

		if (type != null) {

			CIdentity storeId = checkObtainStoreId(type, refingId, null);

			if (storeId != null) {

				return displayDialog(createInstance(type, storeId), storeId, false);
			}
		}

		return null;
	}

	CIdentity checkDisplayNewSimple() {

		return checkDisplayNew(instanceGroup.getSimpleQueriesRootType(), null);
	}

	CIdentity displayReloaded(CIdentity storeId) {

		return displayDialog(reloadInstance(storeId), storeId, true);
	}

	void checkRename(CIdentity storeId) {

		CFrame type = store.getType(storeId);
		CIdentity newStoreId = checkObtainStoreId(type, null, storeId);

		if (newStoreId != null) {

			if (newStoreId.equals(storeId)) {

				showMessage("Supplied name identical to current name");
			}
			else {

				instanceGroup.checkRenameInstance(storeId, newStoreId);
			}
		}
	}

	CIdentity checkObtainNewStoreId(CFrame type) {

		return checkObtainStoreId(type, null, null);
	}

	private CFrame checkDetermineType(CFrame rootType) {

		return hasSubTypes(rootType) ? checkObtainSubType(rootType) : rootType;
	}

	private boolean hasSubTypes(CFrame rootType) {

		return !rootType.getSubs(CVisibility.EXPOSED).isEmpty();
	}

	private CFrame checkObtainSubType(CFrame rootType) {

		AtomicFrameSelector selector = createTypeSelector(rootType);

		selector.display();

		return selector.getInput();
	}

	private AtomicFrameSelector createTypeSelector(CFrame rootType) {

		return new AtomicFrameSelector(parent, rootType, function.query(), false);
	}

	private CIdentity checkObtainStoreId(CFrame type, CIdentity refingId, CIdentity defaultId) {

		StoreIdInputter inputter = createStoreIdInputter();

		if (function.query()) {

			inputter.setInMemoryIds(getExecutedQueryIds());
		}

		if (defaultId != null) {

			inputter.setInitialValue(defaultId);
		}
		else {

			inputter.setInitialStringValue(getNextInstanceNameDefault(type, refingId));
		}

		return inputter.getIdInput();
	}

	private StoreIdInputter createStoreIdInputter() {

		return new StoreIdInputter(parent, store, function);
	}

	private CIdentity displayDialog(
						Instantiator instantiator,
						CIdentity storeId,
						boolean reloaded) {

		InstanceDialog dialog = createDialog(instantiator, storeId);

		dialog.display(reloaded);

		return dialog.instanceStored() ? dialog.getStoreId() : null;
	}

	private InstanceDialog createDialog(Instantiator instantiator, CIdentity storeId) {

		if (function.assertion()) {

			return new AssertionDialog(parent, instantiator, storeId);
		}

		return new QueryDialog(parent, instantiator, storeId);
	}

	private Instantiator createInstance(CFrame type, CIdentity storeId) {

		IFrame instance = type.instantiate(function);

		instance = customiser.onNewInstance(instance, storeId);

		return createInstantiator(storeId, instance);
	}

	private Instantiator reloadInstance(CIdentity storeId) {

		return createInstantiator(storeId, store.get(storeId));
	}

	private Instantiator createInstantiator(CIdentity storeId, IFrame instance) {

		return new Instantiator(instanceGroup, storeId, instance);
	}

	private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}

	private String getNextInstanceNameDefault(CFrame type, CIdentity refingId) {

		return function.query()
				? getNextQueryNameDefault(type)
				: getNextAssertionNameDefault(type, refingId);
	}

	private String getNextAssertionNameDefault(CFrame type, CIdentity refingId) {

		AssertionNameDefaults defaults = customiser.getAssertionNameDefaults();

		return refingId != null
					? defaults.getNextReferenced(type, refingId)
					: defaults.getNextBase(type);
	}

	private String getNextQueryNameDefault(CFrame type) {

		return customiser.getQueryNameDefaults().getNext(type, getExecutedQueryIds());
	}

	private Set<CIdentity> getExecutedQueryIds() {

		return instanceGroup.getQueryExecutions().getAllExecuteds();
	}
}
