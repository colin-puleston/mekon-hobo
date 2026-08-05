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

	private abstract class InstanceEditDisplayer {

		private CIdentity storeId;

		InstanceEditDisplayer(CIdentity storeId) {

			this.storeId = storeId;
		}

		CIdentity display(IFrame rootFrame) {

			Instance instance = createInstance(rootFrame);
			CompleteInstanceEditDialog dialog = createDialog(instance);

			dialog.setEditMode(getEditMode(instance));
			dialog.display();

			return dialog.instanceStored() ? dialog.getStoreId() : null;
		}

		abstract InstanceDisplayMode startMode();

		boolean fullEditMode(Instance instance) {

			return !assertion() || instance.editableInstance();
		}

		InstanceEditMode getRestrictedEditMode() {

			return InstanceEditMode.NONE;
		}

		private CompleteInstanceEditDialog createDialog(Instance instance) {

			if (assertion()) {

				return new AssertionDialog(parent, instance, startMode());
			}

			return QueryDialog.create(parent, instance, startMode());
		}

		private Instance createInstance(IFrame instance) {

			return new Instance(subGroup, storeId, instance);
		}

		private InstanceEditMode getEditMode(Instance instance) {

			return fullEditMode(instance) ? InstanceEditMode.FULL : getRestrictedEditMode();
		}
	}

	private class CreatedInstanceEditDisplayer extends InstanceEditDisplayer {

		CreatedInstanceEditDisplayer(CIdentity storeId) {

			super(storeId);
		}

		InstanceDisplayMode startMode() {

			return InstanceDisplayMode.EDIT;
		}

		InstanceEditMode getRestrictedEditMode() {

			return InstanceEditMode.SAVE_ONLY;
		}
	}

	private class LoadedInstanceEditDisplayer extends InstanceEditDisplayer {

		LoadedInstanceEditDisplayer(CIdentity storeId) {

			super(storeId);
		}

		InstanceDisplayMode startMode() {

			return InstanceDisplayMode.VIEW;
		}
	}

	private class ExecutedQueryDisplayer extends InstanceEditDisplayer {

		private boolean copy;

		ExecutedQueryDisplayer(CIdentity storeId, boolean copy) {

			super(storeId);

			this.copy = copy;
		}

		InstanceDisplayMode startMode() {

			return InstanceDisplayMode.VIEW;
		}

		boolean fullEditMode(Instance instance) {

			return copy;
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

		CIdentity storeId = null;
		CFrame type = checkDetermineType(rootType);

		if (type != null) {

			storeId = checkObtainStoreId(type, refingId, null);

			if (storeId != null) {

				IFrame instance = instantiate(type, storeId);

				if (instance.getSlots().isEmpty()) {

					subGroup.checkAdd(instance, storeId, true);
				}
				else {

					new CreatedInstanceEditDisplayer(storeId).display(instance);
				}
			}
		}

		return storeId;
	}

	void loadAndDisplay(CIdentity storeId, boolean asCopy) {

		IFrame instance = subGroup.get(storeId);

		if (asCopy) {

			storeId = checkResolveAsCopy(instance);
		}

		if (storeId != null) {

			new LoadedInstanceEditDisplayer(storeId).display(instance);
		}
	}

	void loadAndDisplayViewOnly(CIdentity storeId) {

		IFrame rootFrame = subGroup.get(storeId);
		Instance instance = new Instance(subGroup, storeId, rootFrame);

		new CompleteInstanceViewDialog(parent, instance).display();
	}

	void displayExecutedQuery(CIdentity storeId, IFrame query) {

		new ExecutedQueryDisplayer(storeId, false).display(query);
	}

	void copyExecutedQueryAndDisplay(IFrame query) {

		query = query.copy();

		CIdentity storeId = checkResolveAsCopy(query);

		if (storeId != null) {

			new ExecutedQueryDisplayer(storeId, true).display(query);
		}
	}

	boolean checkRename(CIdentity storeId) {

		CIdentity newStoreId = checkObtainReplacementStoreId(storeId);

		return newStoreId != null && subGroup.checkRename(storeId, newStoreId);
	}

	CIdentity checkObtainNewStoreId(CFrame type) {

		return checkObtainStoreId(type, null, null);
	}

	private CIdentity checkResolveAsCopy(IFrame instance) {

		CIdentity storeId = checkObtainNewStoreId(instance.getType());

		if (storeId != null) {

			customise(instance, storeId);
		}

		return storeId;
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

	private IFrame instantiate(CFrame type, CIdentity storeId) {

		return customise(type.instantiate(getFunction()), storeId);
	}

	private IFrame customise(IFrame instance, CIdentity storeId) {

		return getCustomiser().onNewInstance(instance, storeId);
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
