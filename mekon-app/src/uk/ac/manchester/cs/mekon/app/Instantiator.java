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

/**
 * @author Colin Puleston
 */
class Instantiator {

	private InstanceGroup instanceGroup;

	private CIdentity storeId;
	private IFrame instance;

	Instantiator(InstanceGroup instanceGroup, CIdentity storeId, IFrame instance) {

		this.instanceGroup = instanceGroup;
		this.storeId = storeId;
		this.instance = instance;
	}

	Controller getController() {

		return instanceGroup.getController();
	}

	InstanceGroup getInstanceGroup() {

		return instanceGroup;
	}

	CIdentity getStoreId() {

		return storeId;
	}

	IFrame getInstance() {

		return instance;
	}

	IFrame instantiate(CFrame type) {

		IFrame instance = type.instantiate(getFunction());

		if (!queryInstance()) {

			checkInitialiseSlots(instance);
		}

		return instance;
	}

	IFrame instantiateRef(CFrame type, CIdentity refId) {

		return type.instantiate(refId, getFunction());
	}

	boolean instanceRefValuedSlot(ISlot slot) {

		if (!slot.getEditability().abstractEditable()) {

			CValue<?> valueType = slot.getValueType();

			if (valueType instanceof CFrame) {

				return getController().instanceGroupType((CFrame)valueType);
			}
		}

		return false;
	}

	IFrameFunction getFunction() {

		return instance.getFunction();
	}

	boolean queryInstance() {

		return getFunction().query();
	}

	private void checkInitialiseSlots(IFrame instance) {

		for (ISlot slot : instance.getSlots().asList()) {

			checkInitialiseSlot(slot);
		}
	}

	private void checkInitialiseSlot(ISlot slot) {

		new AutoValueProvider(this, slot).checkProvide();
	}
}
