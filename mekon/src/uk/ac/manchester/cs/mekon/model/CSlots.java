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

package uk.ac.manchester.cs.mekon.model;

import java.util.*;

/**
 * Represents ordered list of concept-level model-slots.
 * <p>
 * See ancestor classes for details of list-operations and
 * associated listening mechanisms.
 *
 * @author Colin Puleston
 */
public class CSlots extends FSlots<CSlot> {

	static final CSlots INERT_INSTANCE = new CSlots() {

		void add(CSlot slot) {

			onAttemptedUpdate();
		}

		void addAll(Collection<CSlot> slots) {

			onAttemptedUpdate();
		}

		void remove(CSlot slot) {

			onAttemptedUpdate();
		}

		private void onAttemptedUpdate() {

			throw new Error("Illegal updating of inert object!");
		}
	};

	CSlot getCSlot(CSlot slot) {

		return slot;
	}

	void validateAll(CFrame container) {

		for (CSlot slot : asList()) {

			new CSlotValueTypeValidator(slot).checkNotInvalidFor(container);
		}
	}
}
