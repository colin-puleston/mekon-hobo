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

package uk.ac.manchester.cs.mekon.gui.explorer;

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

	final EntityIconsByLevel dataValues = new EntityIconsByLevel(
												DATA_VALUE_CLR,
												ENTITY_SIZE);

	final SlotIcons defaultEditAssertionsSlots = new DefaultSlotIcons();
	final SlotIcons nonEditAssertionsSlots = new NonEditSlotIcons();
	final SlotIcons fullEditAssertionsSlots = new FullEditSlotIcons();
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

	Icon get(IDataValue dataValue) {

		return dataValues.get(EntityLevel.INSTANCE);
	}

	Icon get(CDataValue<?> dataValue) {

		return dataValues.get(EntityLevel.CONCEPT);
	}

	Icon get(CSlot slot) {

		return getCSlotIcons(slot).get(slot.getSource());
	}

	Icon get(ISlot slot) {

		if (slot.getContainer().getCategory().disjunction()) {

			return null;
		}

		return getISlotIcons(slot).get(slot.getType().getSource());
	}

	Icon forInstanceRef() {

		return exposedFrames.get(CSource.EXTERNAL, EntityLevel.INSTANCE);
	}

	Icon forCSlotValues() {

		return defaultEditAssertionsSlots.get(CSource.EXTERNAL);
	}

	private Icon get(CFrame frame, EntityLevel level) {

		return getFrameIcons(frame).get(frame.getSource(), level);
	}

	private FrameIcons getFrameIcons(CFrame frame) {

		return frame.hidden() ? hiddenFrames : exposedFrames;
	}

	private SlotIcons getCSlotIcons(CSlot slot) {

		if (!slot.getActivation().active()) {

			return inactiveSlots;
		}

		return getSlotIconsForCEditability(slot.getEditability());
	}

	private SlotIcons getISlotIcons(ISlot slot) {

		return getSlotIconsForAssertionsEditability(slot.getEditability());
	}

	private SlotIcons getSlotIconsForCEditability(CEditability status) {

		return getSlotIconsForAssertionsEditability(status.forAssertions());
	}

	private SlotIcons getSlotIconsForAssertionsEditability(IEditability status) {

		switch (status) {

			case NONE:
				return nonEditAssertionsSlots;

			case CONCRETE_ONLY:
				return defaultEditAssertionsSlots;

			case FULL:
				return fullEditAssertionsSlots;
		}

		throw new Error("Unrecognised IEditability status: " + status);
	}
}


