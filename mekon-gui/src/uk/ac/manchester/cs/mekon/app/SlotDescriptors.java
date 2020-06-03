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
class SlotDescriptors {

	private ISlot slot;
	private boolean populatedMultiValueSlot;

	private List<Descriptor> descriptors = new ArrayList<Descriptor>();

	private class Initialiser {

		private Instantiator instantiator;
		private boolean viewOnly;

		Initialiser(Instantiator instantiator, boolean viewOnly) {

			this.instantiator = instantiator;
			this.viewOnly = viewOnly;

			populatedMultiValueSlot = multiValueSlot() && slotHasValues();

			addForAnyValues();
			checkAddForValueEntry();
		}

		private void addForAnyValues() {

			for (IValue value : slot.getValues().asList()) {

				Descriptor descriptor = createForValue(value);

				if (!viewOnly || descriptor.anyEffectiveValues()) {

					descriptors.add(descriptor);
				}
			}
		}

		private void checkAddForValueEntry() {

			if (!viewOnly && editableSlot() && (multiValueSlot() || !slotHasValues())) {

				descriptors.add(createForValue(null));
			}
		}

		private Descriptor createForValue(IValue value) {

			return new Descriptor(instantiator, slot, value);
		}

		private boolean multiValueSlot() {

			return !slot.getType().getCardinality().singleValue();
		}

		private boolean editableSlot() {

			return slot.getEditability().editable();
		}

		private boolean slotHasValues() {

			return !slot.getValues().isEmpty();
		}
	}

	public boolean equals(Object other) {

		return equalsSlotDescriptors((SlotDescriptors)other);
	}

	public int hashCode() {

		return populatedMultiValueSlot ? slot.hashCode() : descriptors.hashCode();
	}

	SlotDescriptors(Instantiator instantiator, ISlot slot, boolean viewOnly) {

		this.slot = slot;

		new Initialiser(instantiator, viewOnly);
	}

	ISlot getSlot() {

		return slot;
	}

	boolean populatedMultiValueSlot() {

		return populatedMultiValueSlot;
	}

	boolean anyDescriptors() {

		return !descriptors.isEmpty();
	}

	Descriptor getSingleDescriptor() {

		return descriptors.get(0);
	}

	List<Descriptor> getDescriptors() {

		return new ArrayList<Descriptor>(descriptors);
	}

	boolean containsDescriptor(Descriptor descriptor) {

		return descriptors.contains(descriptor);
	}

	boolean equalDescriptors(SlotDescriptors other) {

		return descriptors.equals(other.descriptors);
	}

	private boolean equalsSlotDescriptors(SlotDescriptors other) {

		if (slot.equals(other.slot)) {

			if (populatedMultiValueSlot) {

				return other.populatedMultiValueSlot;
			}

			return descriptors.equals(other.descriptors);
		}

		return false;
	}
}
