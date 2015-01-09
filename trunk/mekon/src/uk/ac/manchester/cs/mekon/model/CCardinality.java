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

/**
 * Represents a notion of cardinality for a slot, incorporating
 * both single/multi-valuedness and value-type repeatability.
 *
 * @author Colin Puleston
 */
public enum CCardinality {

	/**
	 * Slot is single-valued.
	 */
	SINGLETON(true, true) {

		ISlotValues createSlotValues(ISlot slot) {

			return new SingletonISlotValues(slot);
		}
	},

	/**
	 * Slot is multi-valued, but cannot have any values whose
	 * value-types duplicate or subsume one another.
	 */
	UNIQUE_TYPES(false, true) {

		ISlotValues createSlotValues(ISlot slot) {

			return new UniqueTypesISlotValues(slot);
		}
	},

	/**
	 * Slot is multi-valued, with any combination of leagal values
	 * permitted.
	 */
	FREE(false, false) {

		ISlotValues createSlotValues(ISlot slot) {

			return new FreeISlotValues(slot);
		}
	};

	private boolean singleValue;
	private boolean uniqueTypes;

	/**
	 * Specifes whether slot is single-valued - equal to
	 * {@link #SINGLETON}.
	 *
	 * @return True if single-valued
	 */
	public boolean singleValue() {

		return singleValue;
	}

	/**
	 * Specifes whether slot values must be of unique-types - equal
	 * to {@link #UNIQUE_TYPES} or (trivially) to {@link #SINGLETON}.
	 *
	 * @return True if unique-types
	 */
	public boolean uniqueTypes() {

		return uniqueTypes;
	}

	/**
	 * Specifes whether this cardinality is more restrictive than
	 * the other specified cardinality (Note that the ordering of
	 * the cardinalities progresses from more to less restrictive).
	 *
	 * @return True if this is the more restrctive of the two
	 */
	public boolean moreRestrictiveThan(CCardinality other) {

		return ordinal() < other.ordinal();
	}

	/**
	 * Provides the more restrictive cardinality between this
	 * and the other specified cardinality.
	 *
	 * @return True if this is the more restrctive of the two
	 * @see #moreRestrictiveThan
	 */
	public CCardinality getMoreRestrictive(CCardinality other) {

		return moreRestrictiveThan(other) ? this : other;
	}

	abstract ISlotValues createSlotValues(ISlot slot);

	private CCardinality(boolean singleValue, boolean uniqueTypes) {

		this.singleValue = singleValue;
		this.uniqueTypes = uniqueTypes;
	}
}
