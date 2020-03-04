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
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceRefSelectionOptions extends EntitySelectionOptions<IFrame> {

	static private final String CREATE_LABEL = "Create new...";

	private Instantiator instantiator;
	private InstanceGroup instanceGroup;

	private CFrame type;

	private InstanceIdsList refIdOptionsList;

	private class CreateButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			checkInstantiateNewInstance(this);
		}

		CreateButton() {

			super(CREATE_LABEL);
		}
	}

	private class IdsSelectionListener extends GSelectionListener<CIdentity> {

		protected void onSelected(CIdentity refId) {

			onSelectedOption(createRef(refId));
		}

		protected void onDeselected(CIdentity refId) {
		}

		IdsSelectionListener() {

			refIdOptionsList.addSelectionListener(this);
		}
	}

	InstanceRefSelectionOptions(
		EntitySelector<IFrame> selector,
		Instantiator instantiator,
		CFrame type) {

		super(selector);

		this.instantiator = instantiator;
		this.type = type;

		instanceGroup = instantiator.getController().getInstanceGroup(type);
		refIdOptionsList = instanceGroup.createAssertionIdsList(type);

		new IdsSelectionListener();

		selector.getControlPanel().addExtraButton(new CreateButton());
	}

	JComponent createOptionsComponent() {

		return new GListPanel<CIdentity>(refIdOptionsList);
	}

	GCellDisplay getRefIdCellDisplay(CIdentity refId) {

		return refIdOptionsList.getCellDisplay(refId);
	}

	private void checkInstantiateNewInstance(JComponent parent) {

		CFrame type = createTypeDeterminator(parent).checkDetermineType();

		if (type != null) {

			CIdentity storeId = checkObtainStoreId(parent);

			if (storeId != null) {

				InstanceDialog dialog = displayNewInstanceDialog(parent, type, storeId);

				if (dialog.instantiationStored()) {

					onSelectedOption(createRef(dialog.getStoreId()));
				}
			}
		}
	}

	private InstantiationTypeDeterminator createTypeDeterminator(JComponent parent) {

		return new InstantiationTypeDeterminator(
						parent,
						instanceGroup,
						IFrameFunction.ASSERTION);
	}

	private CIdentity checkObtainStoreId(JComponent parent) {

		Controller controller = instanceGroup.getController();

		return new StoreIdSelections(parent, controller).checkObtainForAssertion(null);
	}

	private InstanceDialog displayNewInstanceDialog(
								JComponent parent,
								CFrame type,
								CIdentity storeId) {

		Instantiator instantiator = createNewInstantiator(type, storeId);

		return new InstanceDialog(parent, instantiator, storeId, false);
	}

	private Instantiator createNewInstantiator(CFrame type, CIdentity storeId) {

		return instanceGroup.createInstantiator(type, IFrameFunction.ASSERTION, storeId);
	}

	private IFrame createRef(CIdentity refId) {

		return instantiator.instantiateRef(type, refId);
	}
}