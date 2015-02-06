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

package uk.ac.manchester.cs.mekon.gui;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class EntityIcons implements EntityIconConstants {

	static private final EntityIcons singleton = new EntityIcons();

	static EntityIcons get() {

		return singleton;
	}

	final FrameIcons exposedFrames = new FrameIcons(false);
	final FrameIcons hiddenFrames = new FrameIcons(true);

	final EntityIconsByLevel numbers = new EntityIconsByLevel(NUMBER_CLR, ENTITY_SIZE);

	final SlotIcons defaultSlots = new DefaultSlotIcons();
	final SlotIcons nonEditSlots = new NonEditSlotIcons();
	final SlotIcons queryOnlyEditSlots = new QueryOnlyEditSlotIcons();
	final SlotIcons fullEditSlots = new FullEditSlotIcons();
	final SlotIcons inactiveSlots = new InactiveSlotIcons();

	Icon get(IFrame frame) {

		return get(frame.getType(), EntityLevel.INSTANCE);
	}

	Icon get(CFrame frame) {

		return get(frame, EntityLevel.CONCEPT);
	}

	Icon get(MFrame frame) {

		return get(frame.getRootCFrame(), EntityLevel.META);
	}

	Icon get(INumber number) {

		return numbers.get(EntityLevel.INSTANCE);
	}

	Icon get(CNumber number) {

		return numbers.get(EntityLevel.CONCEPT);
	}

	Icon get(CSlot slot) {

		return getCSlotIcons(slot).get(slot.getSource());
	}

	Icon get(ISlot slot) {

		return getISlotIcons(slot).get(slot.getType().getSource());
	}

	Icon forCSlotValues() {

		return defaultSlots.get(CSource.EXTERNAL);
	}

	private Icon get(CFrame frame, EntityLevel level) {

		return getFrameIcons(frame).get(frame.getSource(), level);
	}

	private FrameIcons getFrameIcons(CFrame frame) {

		return frame.hidden() ? hiddenFrames : exposedFrames;
	}

	private SlotIcons getCSlotIcons(CSlot slot) {

		if (!slot.active()) {

			return inactiveSlots;
		}

		switch (slot.getEditability()) {

			case DEFAULT:
				return defaultSlots;

			case NONE:
				return nonEditSlots;

			case QUERY_ONLY:
				return queryOnlyEditSlots;

			case FULL:
				return fullEditSlots;
		}

		throw new Error(
					"Unrecognised CEditability value: "
					+ slot.getEditability());
	}

	private SlotIcons getISlotIcons(ISlot slot) {

		switch (slot.getEditability()) {

			case NONE:
				return nonEditSlots;

			case CONCRETE_ONLY:
				return defaultSlots;

			case FULL:
				return fullEditSlots;
		}

		throw new Error(
					"Unrecognised IEditability value: "
					+ slot.getEditability());
	}
}


