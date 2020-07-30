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

package uk.ac.manchester.cs.mekon.gui.app;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class Controller {

	private Store store;
	private Customiser customiser;

	private Set<InstanceGroup> instanceGroups = new HashSet<InstanceGroup>();

	Controller(Store store, Customiser customiser) {

		this.store = store;
		this.customiser = customiser;
	}

	InstanceGroup addInstanceGroup(InstanceGroupSpec spec) {

		InstanceGroup group = spec.createGroup(this);

		instanceGroups.add(group);

		return group;
	}

	Store getStore() {

		return store;
	}

	Customiser getCustomiser() {

		return customiser;
	}

	boolean instanceGroupType(CFrame type) {

		return getInstanceGroupOrNull(type) != null;
	}

	InstanceGroup getInstanceGroup(CFrame type) {

		InstanceGroup group = getInstanceGroupOrNull(type);

		if (group == null) {

			throw new Error("Cannot find instance-group for type: " + type);
		}

		return group;
	}

	boolean anyEditableSlots(IFrame instance) {

		for (ISlot slot : instance.getSlots().asList()) {

			if (editableSlot(slot)) {

				return true;
			}
		}

		return false;
	}

	private InstanceGroup getInstanceGroupOrNull(CFrame type) {

		for (InstanceGroup group : instanceGroups) {

			if (group.includesInstancesOfType(type)) {

				return group;
			}
		}

		return null;
	}

	private boolean editableSlot(ISlot slot) {

		return !customiser.hiddenSlot(slot)
				&& slot.getType().getActivation().active()
				&& slot.getEditability().editable();
	}
}
