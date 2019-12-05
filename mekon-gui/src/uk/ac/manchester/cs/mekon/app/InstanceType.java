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
class InstanceType {

	private Controller controller;
	private Store store;

	private CFrame type;

	private List<CIdentity> instanceIds;
	private InstanceIdsList instanceIdsList;

	InstanceType(Controller controller, CFrame type) {

		this.controller = controller;
		this.type = type;

		store = controller.getStore();
		instanceIdsList = new InstanceIdsList(false);

		updateInstanceIds();
	}

	Controller getController() {

		return controller;
	}

	CFrame getType() {

		return type;
	}

	List<CIdentity> getInstanceIds() {

		return instanceIds;
	}

	InstanceIdsList getInstanceIdsList() {

		return instanceIdsList;
	}

	boolean checkAddInstance(IFrame instance, CIdentity id) {

		if (store.checkAdd(instance, id)) {

			updateInstanceIds();

			return true;
		}

		return false;
	}

	void checkRemoveInstance(CIdentity id) {

		if (store.checkRemove(id)) {

			updateInstanceIds();
		}
	}

	Instantiator createAssertionInstantiator(CIdentity id) {

		IFrame instance = instantiate(IFrameFunction.ASSERTION);

		controller.getCustomiser().onNewInstance(instance, id);

		return createInstantiator(instance);
	}

	Instantiator createQueryInstantiator() {

		return createInstantiator(instantiate(IFrameFunction.QUERY));
	}

	Instantiator createInstantiator(IFrame instantiation) {

		return new Instantiator(this, instantiation);
	}

	private IFrame instantiate(IFrameFunction function) {

		return type.instantiate(function);
	}

	private void updateInstanceIds() {

		instanceIds = store.getInstanceIds(type);
		instanceIdsList.update(instanceIds);
	}
}
