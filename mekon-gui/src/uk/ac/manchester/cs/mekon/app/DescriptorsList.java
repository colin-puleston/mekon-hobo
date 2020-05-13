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
class DescriptorsList {

	private Instantiator instantiator;
	private IFrame container;

	private List<Descriptor> list = new ArrayList<Descriptor>();

	DescriptorsList(Instantiator instantiator, IFrame container, boolean viewOnly) {

		this.instantiator = instantiator;
		this.container = container;

		populate(viewOnly);
	}

	void update(boolean viewOnly) {

		list.clear();

		populate(viewOnly);
	}

	boolean isEmpty() {

		return list.isEmpty();
	}

	List<Descriptor> getList() {

		return new ArrayList<Descriptor>(list);
	}

	private void populate(boolean viewOnly) {

		for (ISlot slot : container.getSlots().activesAsList()) {

			if (!hidden(slot) && (editable(slot) || hasValues(slot))) {

				addAllFor(slot, viewOnly);
			}
		}
	}

	private void addAllFor(ISlot slot, boolean viewOnly) {

		for (IValue value : slot.getValues().asList()) {

			Descriptor descriptor = createFor(slot, value);

			if (!viewOnly || descriptor.anyEffectiveValues()) {

				list.add(descriptor);
			}
		}

		if (!viewOnly && editable(slot) && (multiValued(slot) || !hasValues(slot))) {

			list.add(createFor(slot, null));
		}
	}

	private Descriptor createFor(ISlot slot, IValue value) {

		return new Descriptor(instantiator, slot, value);
	}

	private boolean hidden(ISlot slot) {

		return getCustomiser().hiddenSlot(slot);
	}

	private boolean multiValued(ISlot slot) {

		return !slot.getType().getCardinality().singleValue();
	}

	private boolean editable(ISlot slot) {

		return slot.getEditability().editable();
	}

	private boolean hasValues(ISlot slot) {

		return !slot.getValues().isEmpty();
	}

	private Customiser getCustomiser() {

		return instantiator.getController().getCustomiser();
	}
}
