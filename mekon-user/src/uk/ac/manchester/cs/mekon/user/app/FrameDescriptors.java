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

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class FrameDescriptors {

	private List<Descriptor> allDescriptors = new ArrayList<Descriptor>();
	private List<SlotDescriptors> descriptorsBySlot = new ArrayList<SlotDescriptors>();

	private class Initialiser {

		private Instantiator instantiator;
		private IFrame frame;
		private boolean viewOnly;

		Initialiser(Instantiator instantiator, IFrame frame, boolean viewOnly) {

			this.instantiator = instantiator;
			this.frame = frame;
			this.viewOnly = viewOnly;

			populate();
		}

		private void populate() {

			for (ISlot slot : frame.getSlots().activesAsList()) {

				if (!hiddenSlot(slot)) {

					checkAddForSlot(slot);
				}
			}
		}

		private void checkAddForSlot(ISlot slot) {

			SlotDescriptors slotDescriptors = createSlotDescriptors(slot);

			if (slotDescriptors.anyDescriptors()) {

				allDescriptors.addAll(slotDescriptors.getDescriptors());
				descriptorsBySlot.add(slotDescriptors);
			}
		}

		private SlotDescriptors createSlotDescriptors(ISlot slot) {

			return new SlotDescriptors(instantiator, slot, viewOnly);
		}

		private boolean hiddenSlot(ISlot slot) {

			return instantiator.getController().getCustomiser().hiddenSlot(slot);
		}
	}

	FrameDescriptors(Instantiator instantiator, IFrame frame, boolean viewOnly) {

		new Initialiser(instantiator, frame, viewOnly);
	}

	boolean anyDescriptors() {

		return !descriptorsBySlot.isEmpty();
	}

	boolean equalDescriptors(FrameDescriptors other) {

		return allDescriptors.equals(other.allDescriptors);
	}

	List<SlotDescriptors> getDescriptorsBySlot() {

		return new ArrayList<SlotDescriptors>(descriptorsBySlot);
	}
}
