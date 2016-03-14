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
	SINGLE_VALUE {

		boolean singleValued() {

			return true;
		}

		boolean conflictingAsserteds(IValue value1, IValue value2) {

			return true;
		}
	},

	/**
	 * Slot is multi-valued, but cannot have any values whose
	 * value-types duplicate or subsume one another.
	 */
	UNIQUE_TYPES {

		boolean singleValued() {

			return false;
		}

		boolean conflictingAsserteds(IValue value1, IValue value2) {

			return typeSubsumption(value1, value2) || typeSubsumption(value2, value1);
		}

		private boolean typeSubsumption(IValue testSubsumer, IValue testSubsumed) {

			return testSubsumer.getType().subsumes(testSubsumed.getType());
		}
	},

	/**
	 * Slot is multi-valued, with any combination of legal values
	 * permitted.
	 */
	REPEATABLE_TYPES {

		boolean singleValued() {

			return false;
		}

		boolean conflictingAsserteds(IValue value1, IValue value2) {

			return false;
		}
	};

	/**
	 * Specifes whether slot is single-valued - equal to
	 * {@link #SINGLE_VALUE}.
	 *
	 * @return True if single-valued
	 */
	public boolean singleValue() {

		return this == SINGLE_VALUE;
	}

	/**
	 * Specifes whether slot values must be of unique-types - equal
	 * to {@link #UNIQUE_TYPES} or, trivially, to {@link #SINGLE_VALUE}.
	 *
	 * @return True if unique-types
	 */
	public boolean uniqueTypes() {

		return this != REPEATABLE_TYPES;
	}

	/**
	 * Specifes whether this cardinality is more restrictive than
	 * the other specified cardinality (Note that the ordering of
	 * the cardinalities progresses from more to less restrictive).
	 *
	 * @param other Other cardinality to test against
	 * @return True if this is the more restrctive of the two
	 */
	public boolean moreRestrictiveThan(CCardinality other) {

		return ordinal() < other.ordinal();
	}

	/**
	 * Specifes whether this cardinality is less restrictive than
	 * the other specified cardinality (Note that the ordering of
	 * the cardinalities progresses from more to less restrictive).
	 *
	 * @param other Other cardinality to test against
	 * @return True if this is the less restrctive of the two
	 */
	public boolean lessRestrictiveThan(CCardinality other) {

		return ordinal() > other.ordinal();
	}

	/**
	 * Provides the more restrictive cardinality between this
	 * and the other specified cardinality.
	 *
	 * @param other Other cardinality to test against
	 * @return True if this is the more restrctive of the two
	 * @see #moreRestrictiveThan
	 */
	public CCardinality getMoreRestrictive(CCardinality other) {

		return moreRestrictiveThan(other) ? this : other;
	}

	/**
	 * Provides the less restrictive cardinality between this
	 * and the other specified cardinality.
	 *
	 * @param other Other cardinality to test against
	 * @return True if this is the less restrctive of the two
	 * @see #lessRestrictiveThan
	 */
	public CCardinality getLessRestrictive(CCardinality other) {

		return lessRestrictiveThan(other) ? this : other;
	}

	abstract boolean singleValued();

	abstract boolean conflictingAsserteds(IValue value1, IValue value2);
}
