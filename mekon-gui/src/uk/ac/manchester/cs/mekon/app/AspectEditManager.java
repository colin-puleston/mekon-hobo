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

/**
 * @author Colin Puleston
 */
class AspectEditManager  {

	private AspectWindow parentWindow;
	private ISlot slot;
	private IFrame aspect;

	private DescriptorsList descriptors;

	AspectEditManager(AspectWindow parentWindow, ISlot slot, IFrame aspect) {

		this.parentWindow = parentWindow;
		this.slot = slot;
		this.aspect = aspect;

		update();
	}

	void invokeEdit() {

		Descriptor proxy = descriptors.getProxyDescriptorOrNull();

		if (proxy != null) {

			invokeProxyDescriptorEdit(proxy);
		}
		else {

			invokeDescriptorsTableEdit(descriptors);
		}
	}

	void update() {

		descriptors = new DescriptorsList(parentWindow.getInstantiator(), aspect);
	}

	DescriptorsList getDescriptors() {

		return descriptors;
	}

	private void invokeProxyDescriptorEdit(Descriptor proxy) {

		new DescriptorDisplay(parentWindow, proxy).checkEdit();
	}

	private void invokeDescriptorsTableEdit(DescriptorsList descriptors) {

		EditStatus status = displayAspectDialog(descriptors);

		if (status == EditStatus.CLEARED || emptyAspect()) {

			clearAspect();
		}
	}

	private EditStatus displayAspectDialog(DescriptorsList descriptors) {

		return createAspectDialog().display(descriptors);
	}

	private AspectDialog createAspectDialog() {

		return new AspectDialog(parentWindow, this, slot);
	}

	private boolean emptyAspect() {

		return !ValuesTester.anyNestedUserValues(aspect);
	}

	private void clearAspect() {

		if (slot.getEditability().editable()) {

			slot.getValuesEditor().remove(aspect);
		}
		else {

			clearUserValues(aspect);
		}
	}

	private void clearUserValues(ISlot fromSlot) {

		if (fromSlot.getEditability().editable()) {

			fromSlot.getValuesEditor().clear();
		}
		else {

			if (fromSlot.getValueType() instanceof CFrame) {

				for (IValue value : fromSlot.getValues().asList()) {

					clearUserValues((IFrame)value);
				}
			}
		}
	}

	private void clearUserValues(IFrame fromFrame) {

		for (ISlot slot : fromFrame.getSlots().activesAsList()) {

			clearUserValues(slot);
		}
	}
}
