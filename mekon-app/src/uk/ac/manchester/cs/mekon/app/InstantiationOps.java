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

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class InstantiationOps {

	private JComponent parent;
	private InstanceGroup instanceGroup;
	private IFrameFunction function;

	private Store store;

	InstantiationOps(
		JComponent parent,
		InstanceGroup instanceGroup,
		IFrameFunction function) {

		this.parent = parent;
		this.instanceGroup = instanceGroup;
		this.function = function;

		store = instanceGroup.getController().getStore();
	}

	CIdentity checkDisplayNew() {

		CFrame type = checkDetermineType();

		if (type != null) {

			CIdentity storeId = checkObtainNewStoreId();

			if (storeId != null) {

				return displayDialog(createInstantiator(type, storeId), storeId, false);
			}
		}

		return null;
	}

	CIdentity displayReloaded(CIdentity storeId) {

		return displayDialog(recreateInstantiator(store.get(storeId)), storeId, true);
	}

	void checkRename(CIdentity storeId) {

		CIdentity newStoreId = checkObtainStoreId(storeId);

		if (newStoreId != null) {

			if (newStoreId.equals(storeId)) {

				showMessage("Supplied name identical to current name");
			}
			else {

				instanceGroup.checkRenameInstance(storeId, newStoreId);
			}
		}
	}

	CIdentity checkObtainNewStoreId() {

		return checkObtainStoreId(null);
	}

	private CFrame checkDetermineType() {

		return instanceGroup.hasSubTypes() ? checkObtainSubType() : getRootType();
	}

	private CFrame checkObtainSubType() {

		AtomicFrameSelector selector = createTypeSelector();

		selector.display();

		return selector.getSelection();
	}

	private AtomicFrameSelector createTypeSelector() {

		return new AtomicFrameSelector(parent, getRootType(), function.query(), false);
	}

	private CIdentity checkObtainStoreId(CIdentity defaultId) {

		StoreIdSelector selector = createStoreIdSelector();

		if (function.assertion()) {

			configureIdSelectorForAssertion(selector, defaultId);
		}
		else {

			configureIdSelectorForQuery(selector, defaultId);
		}

		return selector.getIdSelection();
	}

	private StoreIdSelector createStoreIdSelector() {

		return new StoreIdSelector(parent, getStore(), function);
	}

	private void configureIdSelectorForAssertion(StoreIdSelector selector, CIdentity defaultId) {

		if (defaultId != null) {

			selector.setInitialValue(defaultId);
		}
	}

	private void configureIdSelectorForQuery(StoreIdSelector selector, CIdentity defaultId) {

		setInMemoryQueryIds(selector);

		if (defaultId != null && !hasDefaultQueryFormat(defaultId)) {

			selector.setInitialValue(defaultId);
		}
		else {

			selector.setInitialStringValue(getNextQueryNameDefault());
		}
	}

	private void setInMemoryQueryIds(StoreIdSelector selector) {

		selector.setInMemoryIds(instanceGroup.getQueryExecutions().getAllExecuteds());
	}

	private CIdentity displayDialog(
							Instantiator instantiator,
							CIdentity storeId,
							boolean reloaded) {

		InstantiationDialog dialog = createDialog(instantiator, storeId, reloaded);

		return dialog.instantiationStored() ? dialog.getStoreId() : null;
	}

	private InstantiationDialog createDialog(
									Instantiator instantiator,
									CIdentity storeId,
									boolean reloaded) {

		if (function.assertion()) {

			return new InstanceDialog(parent, instantiator, storeId, reloaded);
		}

		return new QueryDialog(parent, instantiator, storeId, reloaded);
	}

	private Instantiator createInstantiator(CFrame type, CIdentity storeId) {

		return instanceGroup.createInstantiator(type, function, storeId);
	}

	private Instantiator recreateInstantiator(IFrame instantiation) {

		return instanceGroup.createInstantiator(instantiation);
	}

	private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}

	private Store getStore() {

		return getController().getStore();
	}

	private CFrame getRootType() {

		return instanceGroup.getRootType();
	}

	private String getNextQueryNameDefault() {

		return getController().getQueryNameDefaults().getNext();
	}

	private boolean hasDefaultQueryFormat(CIdentity id) {

		return QueryNameDefaults.defaultNameFormat(id.getLabel());
	}

	private Controller getController() {

		return instanceGroup.getController();
	}
}
