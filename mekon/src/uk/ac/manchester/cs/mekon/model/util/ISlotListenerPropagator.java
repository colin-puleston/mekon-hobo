/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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

package uk.ac.manchester.cs.mekon.model.util;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Responsible for propagating a specified listener to
 * dynamically selected {@link ISlotValues} objects that
 * lie on recursive instance-level slot-frame-slot chains
 * originating from one-or-more specified "seed-slots".
 * Selection of "target-slots" to which the listener is
 * to be added is based on criteria provided by specific
 * implementations of the abstract {@link #targetSlot}
 * method. The listener will be added to all possible
 * target-slots, including both those that already exist
 * when a seed-slot is specified and those that come into
 * existence subsequently. Propogation will occur along
 * each slot-frame-slot chain as far as the first encounted
 * non-target-slot.
 *
 * @author Colin Puleston
 */
public abstract class ISlotListenerPropagator<L>
						implements
							KValuesListener<IValue> {

	private L listener;

	/**
	 */
	public void onAdded(IValue value) {

		if (value instanceof IFrame) {

			checkPropagate((IFrame)value);
		}
	}

	/**
	 */
	public void onRemoved(IValue value) {

		removeAll(value);
	}

	/**
	 */
	public void onCleared(List<IValue> values) {

		removeAll(values);
	}

	/**
	 * Enables the provision of a seed-frame from which the
	 * listener will be propagated.
	 *
	 * @param seedFrame Relevant seed-frame
	 */
	public void propagateFrom(IFrame seedFrame) {

		checkPropagate(seedFrame);
	}

	/**
	 * Enables the provision of a seed-slot from which the
	 * listener will be propagated.
	 *
	 * @param seedSlot Relevant seed-slot
	 */
	public void propagateFrom(ISlot seedSlot) {

		checkPropagate(seedSlot);
	}

	/**
	 * Specifies whether a particular slot is to be a target for
	 * the listener.
	 *
	 * @param slot Slot to test
	 * @return True if target slot
	 */
	protected abstract boolean targetSlot(ISlot slot);

	ISlotListenerPropagator(L listener) {

		this.listener = listener;
	}

	abstract void addListener(ISlotValues values, L listener);

	abstract void removeListener(ISlotValues values, L listener);

	private void checkPropagate(IFrame frame) {

		for (ISlot slot : getTargetSlots(frame)) {

			checkPropagate(slot);
		}
	}

	private void checkPropagate(ISlot slot) {

		initialiseSlot(slot);

		if (slot.getValueType() instanceof CFrame) {

			checkPropagate(slot.getValues().asList());
		}
	}

	private void checkPropagate(List<IValue> values) {

		for (IValue value : values) {

			checkPropagate((IFrame)value);
		}
	}

	private void removeAll(IFrame frame) {

		for (ISlot slot : getTargetSlots(frame)) {

			removeAll(slot);
		}
	}

	private void removeAll(ISlot slot) {

		ISlotValues values = slot.getValues();

		removeListener(values, listener);
		removeAll(values.asList());
	}

	private void removeAll(List<IValue> values) {

		for (IValue value : values) {

			removeAll(value);
		}
	}

	private void removeAll(IValue value) {

		if (value instanceof IFrame) {

			removeAll((IFrame)value);
		}
	}

	private void initialiseSlot(ISlot slot) {

		ISlotValues values = slot.getValues();

		values.addValuesListener(this);
		addListener(values, listener);
	}

	private List<ISlot> getTargetSlots(IFrame frame) {

		List<ISlot> targetSlots = new ArrayList<ISlot>();

		for (ISlot slot : frame.getSlots().asList()) {

			if (targetSlot(slot)) {

				targetSlots.add(slot);
			}
		}

		return targetSlots;
	}
}
