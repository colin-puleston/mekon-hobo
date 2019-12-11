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

	private Customiser customiser;
	private List<Descriptor> list = new ArrayList<Descriptor>();

	DescriptorsList(Instantiator instantiator, IFrame aspect) {

		customiser = instantiator.getController().getCustomiser();

		for (ISlot slot : aspect.getSlots().activesAsList()) {

			if (!customiser.hiddenSlot(aspect, slot)) {

				addAllFor(slot);
			}
		}
	}

	boolean isEmpty() {

		return list.isEmpty();
	}

	List<Descriptor> getList() {

		return list;
	}

	private void addAllFor(ISlot slot) {

		ISlotValues values = slot.getValues();

		for (IValue value : values.asList()) {

			addFor(slot, value);
		}

		if (values.isEmpty() || (editable(slot) && !singleValued(slot))) {

			addFor(slot, null);
		}
	}

	private void addFor(ISlot slot, IValue value) {

		list.add(new Descriptor(slot, value));
	}

	private boolean singleValued(ISlot slot) {

		return slot.getType().getCardinality().singleValue();
	}

	private boolean editable(ISlot slot) {

		return slot.getEditability().editable();
	}
}
