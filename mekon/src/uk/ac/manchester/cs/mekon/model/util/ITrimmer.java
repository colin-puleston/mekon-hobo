/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.mekon.model.util;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Responsible for trimming any superfluous structure from an
 * <code>IFrame/ISlot</code> network, by recursively removing any
 * frame-values that are deemed to be superfluous. A superfluous
 * frame-value is one that (a) has attached slots, (b) none of whose
 * attached slots have non-superfluous values and (c) is specified
 * as being {@link #trimmable}.
 *
 * @author Colin Puleston
 */
public abstract class ITrimmer {

	/**
	 * Provides a trimmed copy of the frame/slot network rooted at the
	 * specified frame.
	 *
	 * @param rootFrame Root-frame of network to be copied and trimmed
	 * @return Root-frame of resulting network
	 */
	public IFrame copyTrimmed(IFrame rootFrame) {

		IFrame rootFrameCopy = rootFrame.copy();

		trimFrom(rootFrameCopy);

		return rootFrameCopy;
	}

	/**
	 * Trims the frame/slot network rooted at the specified frame.
	 *
	 * @param rootFrame Root-frame of network to be trimmed
	 */
	public void trim(IFrame rootFrame) {

		trimFrom(rootFrame);
	}

	/**
	 * Method that determines whether a frame-value is considered
	 * superfluous if it has no non-superfluous successor values.
	 *
	 * @param type Type of frame-value
	 * @return true if trimmable type
	 */
	protected abstract boolean trimmable(CFrame type);

	private boolean trimFrom(IFrame frame) {

		boolean empty = true;

		for (ISlot slot : frame.getSlots().activesAsList()) {

			if (!trimFrom(slot)) {

				empty = false;
			}
		}

		return empty;
	}

	private boolean trimFrom(ISlot slot) {

		boolean empty = true;
		ISlotValuesEditor valuesEd = slot.getValuesEditor();

		for (IValue value : slot.getValues().asList()) {

			if (trimFrom(value)) {

				valuesEd.remove(value);
			}
			else {

				empty = false;
			}
		}

		return empty;
	}

	private boolean trimFrom(IValue value) {

		if (value instanceof IFrame) {

			IFrame frame = (IFrame)value;

			if (frame.getSlots().isEmpty()) {

				return false;
			}

			return trimmable(frame.getType()) && trimFrom(frame);
		}

		return false;
	}
}
