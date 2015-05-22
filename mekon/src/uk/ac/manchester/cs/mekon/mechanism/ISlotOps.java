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

package uk.ac.manchester.cs.mekon.mechanism;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents the types of update operation connected with
 * instance-level slots that can be performed as the result of
 * reasoning.
 *
 * @author Colin Puleston
 */
public enum ISlotOps {

	/**
	 * No slot-related updates.
	 */
	NONE(),

	/**
	 * Updates of slot-sets on relevant frames, and/or value-types
	 * and other attributes for specific slots.
	 */
	SLOTS(IUpdateOp.SLOTS),

	/**
	 * Updates of fixed values for relevant slots.
	 */
	VALUES(IUpdateOp.SLOT_VALUES),

	/**
	 * Both {@link #SLOTS} and {@link #VALUES}.
	 */
	SLOTS_AND_VALUES(IUpdateOp.SLOTS, IUpdateOp.SLOT_VALUES);

	/**
	 * Provides the option matching the specified requirements.
	 *
	 * @param slots True if option is to include slot-updates
	 * @param values True if option is to include value-updates
	 * @return Specified option
	 */
	static public ISlotOps get(boolean slots, boolean values) {

		ISlotOps ops = ISlotOps.NONE;

		if (slots) {

			ops = ops.and(SLOTS);
		}

		if (values) {

			ops = ops.and(VALUES);
		}

		return ops;
	}

	/**
	 * Combines this option with the other specified option.
	 *
	 * @param other Option with which to combine
	 * @return Combined option
	 */
	public ISlotOps and(ISlotOps other) {

		if (this == other) {

			return this;
		}

		if (this == NONE) {

			return other;
		}

		if (other == NONE) {

			return this;
		}

		return SLOTS_AND_VALUES;
	}

	/**
	 * Specifies whether this option includes slot-updates, which will
	 * be the case for {@link #SLOTS} or {@link #SLOTS_AND_VALUES}.
	 *
	 * @return True if option includes slot-updates
	 */
	public boolean includesSlots() {

		return this == SLOTS || this == SLOTS_AND_VALUES;
	}

	/**
	 * Specifies whether this option includes value-updates, which will
	 * be the case for {@link #VALUES} or {@link #SLOTS_AND_VALUES}.
	 *
	 * @return True if option includes value-updates
	 */
	public boolean includesValues() {

		return this == VALUES || this == SLOTS_AND_VALUES;
	}

	/**
	 * Represents the option as a set of (between 0 and 2) {@link IUpdateOp}
	 * values of the relevant types.
	 *
	 * @return Option as set of update-ops
	 */
	public Set<IUpdateOp> asUpdateOps() {

		return updateOps;
	}

	private Set<IUpdateOp> updateOps = new HashSet<IUpdateOp>();

	private ISlotOps(IUpdateOp... updateOps) {

		this.updateOps.addAll(Arrays.asList(updateOps));
	}
}

