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

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class InstanceDisplayOps {

	private JComponent parent;

	private InstanceGroup group;
	private InstanceSubGroup subGroup;

	private class InstanceDisplayer {

		private CIdentity storeId;
		private InstanceDisplayMode startMode = InstanceDisplayMode.VIEW;
		private boolean allowStoreOverwrite = true;

		InstanceDisplayer(CIdentity storeId) {

			this.storeId = storeId;
		}

		void setStartInEditMode() {

			startMode = InstanceDisplayMode.EDIT;
		}

		void setDisallowStoreOverwrite() {

			allowStoreOverwrite = false;
		}

		CIdentity createAndDisplay(CFrame type) {

			return display(instantiate(type));
		}

		CIdentity reloadAndDisplay() {

			return display(subGroup.get(storeId));
		}

		CIdentity display(IFrame instance) {

			InstanceDialog dialog = createDialog(instance);

			dialog.setAllowStoreOverwrite(allowStoreOverwrite);
			dialog.display();

			return dialog.instanceStored() ? dialog.getStoreId() : null;
		}

		private InstanceDialog createDialog(IFrame instance) {

			Instantiator instantiator = createInstantiator(instance);

			if (assertion()) {

				return new AssertionDialog(parent, instantiator, startMode);
			}

			return QueryDialog.create(parent, instantiator, startMode);
		}

		private IFrame instantiate(CFrame type) {

			return customise(type.instantiate(getFunction()));
		}

		private IFrame customise(IFrame instance) {

			return getCustomiser().onNewInstance(instance, storeId);
		}

		private Instantiator createInstantiator(IFrame instance) {

			return new Instantiator(subGroup, storeId, instance);
		}
	}

	InstanceDisplayOps(JComponent parent, InstanceSubGroup subGroup) {

		this.parent = parent;
		this.subGroup = subGroup;

		group = subGroup.getGroup();
	}

	CIdentity checkCreateAndDisplay() {

		return checkCreateAndDisplay(group.getRootType(), null);
	}

	CIdentity checkCreateAndDisplay(CFrame rootType, CIdentity refingId) {

		CFrame type = checkDetermineType(rootType);

		if (type != null) {

			CIdentity storeId = checkObtainStoreId(type, refingId, null);

			if (storeId != null) {

				return createAndDisplay(type, storeId);
			}
		}

		return null;
	}

	void reloadAndDisplay(CIdentity storeId) {

		new InstanceDisplayer(storeId).reloadAndDisplay();
	}

	void displayExecutedQuery(CIdentity storeId, IFrame instance) {

		InstanceDisplayer displayer = new InstanceDisplayer(storeId);

		displayer.setDisallowStoreOverwrite();
		displayer.display(instance);
	}

	boolean checkRename(CIdentity storeId) {

		CIdentity newStoreId = checkObtainReplacementStoreId(storeId);

		return newStoreId != null && subGroup.checkRename(storeId, newStoreId);
	}

	CIdentity checkObtainNewStoreId(CFrame type) {

		return checkObtainStoreId(type, null, null);
	}

	private CIdentity createAndDisplay(CFrame type, CIdentity storeId) {

		InstanceDisplayer displayer = new InstanceDisplayer(storeId);

		displayer.setStartInEditMode();

		return displayer.createAndDisplay(type);
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

		return new AtomicFrameSelector(parent, rootType, query(), false, getCustomiser());
	}

	private CIdentity checkObtainReplacementStoreId(CIdentity replacingId) {

		return checkObtainStoreId(group.getType(replacingId), null, replacingId);
	}

	private CIdentity checkObtainStoreId(CFrame type, CIdentity refingId, CIdentity replacingId) {

		StoreIdInputter inputter = new StoreIdInputter(parent, getController(), getFunction());

		if (query()) {

			inputter.setInMemoryIds(getExecutedQueryIds());
		}

		if (replacingId != null) {

			inputter.setReplacingIdValue(replacingId);
		}
		else {

			inputter.setInitialStringValue(subGroup.createInstanceNameDefault(type, refingId));
		}

		return inputter.getIdInput();
	}

	private Set<CIdentity> getExecutedQueryIds() {

		return group.getQueryExecutions().getAllExecuteds();
	}

	private boolean assertion() {

		return getFunction().assertion();
	}

	private boolean query() {

		return getFunction().query();
	}

	private IFrameFunction getFunction() {

		return subGroup.getFunction();
	}

	private Customiser getCustomiser() {

		return getController().getCustomiser();
	}

	private Controller getController() {

		return group.getController();
	}
}
