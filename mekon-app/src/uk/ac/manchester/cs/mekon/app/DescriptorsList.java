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
	private IFrame aspect;

	private List<Descriptor> list = new ArrayList<Descriptor>();

	DescriptorsList(Instantiator instantiator, IFrame aspect) {

		this.instantiator = instantiator;
		this.aspect = aspect;

		populate();
	}

	void update() {

		list.clear();

		populate();
	}

	boolean isEmpty() {

		return list.isEmpty();
	}

	List<Descriptor> getList() {

		return new ArrayList<Descriptor>(list);
	}

	private void populate() {

		for (ISlot slot : aspect.getSlots().activesAsList()) {

			if (!hidden(slot) && (editable(slot) || hasValues(slot))) {

				addAllFor(slot);
			}
		}
	}

	private void addAllFor(ISlot slot) {

		for (IValue value : slot.getValues().asList()) {

			addFor(slot, value);
		}

		if (editable(slot) && (multiValued(slot) || !hasValues(slot))) {

			addFor(slot, null);
		}
	}

	private void addFor(ISlot slot, IValue value) {

		list.add(new Descriptor(instantiator, slot, value));
	}

	private boolean hidden(ISlot slot) {

		return getCustomiser().hiddenSlot(aspect, slot);
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
