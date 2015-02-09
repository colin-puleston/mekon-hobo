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
 * Represents the editability from the client perspective of the
 * instantiations of a particular {@link CSlot}. Covers instance
 * editability status, as represented via {@link IEditability},
 * for instances of the slot on both assertions and queries.
 *
 * @author Colin Puleston
 */
public enum CEditability {

	/**
	 * Slot has {@link IEditability#CONCRETE_ONLY} editability on
	 * assertions, and {@link IEditability#FULL} editability on
	 * queries.
	 */
	DEFAULT(IEditability.CONCRETE_ONLY, IEditability.FULL),

	/**
	 * Slot has {@link IEditability#FULL} editability on both
	 * assertions and queries.
	 */
	FULL(IEditability.FULL, IEditability.FULL),

	/**
	 * Slot has {@link IEditability#NONE} editability on both
	 * assertions and queries.
	 */
	NONE(IEditability.NONE, IEditability.NONE),

	/**
	 * Slot has {@link IEditability#NONE} editability on assertions,
	 * and {@link IEditability#FULL} editability on queries.
	 */
	QUERY_ONLY(IEditability.NONE, IEditability.FULL);

	private IEditability assertionsStatus;
	private IEditability queriesStatus;

	/**
	 * Provides the {@link IEditability} status for slots on
	 * assertions.
	 *
	 * @return Editability status for slots on assertions
	 */
	public IEditability forAssertions() {

		return assertionsStatus;
	}

	/**
	 * Provides the {@link IEditability} status for slots on queries.
	 *
	 * @return Editability status for slots on queries
	 */
	public IEditability forQueries() {

		return queriesStatus;
	}

	/**
	 * Provides the {@link IEditability} status for slots on either
	 * assertions or queries.
	 *
	 * @param queries True if editability status for slots on queries,
	 * rather than assertions, is required
	 * @return Editability status for slots on queries
	 */
	public IEditability forInstances(boolean queries) {

		return queries ? queriesStatus : assertionsStatus;
	}

	/**
	 * Provides the "strongest" editability status between this and
	 * the other specified value. The strongest status is the one
	 * that will take precedence when two competing statuses are
	 * provided for a single slot. The enum values have been ordered
	 * so that status strength is equivalent to ordinal value, with
	 * {@link #DEFAULT} being the weakest, and {@link #QUERY_ONLY}
	 * the strongest.
	 *
	 * @param other Editability status to test against this one
	 * @return Strongest editability status
	 */
	public CEditability getStrongest(CEditability other) {

		return ordinal() > other.ordinal() ? this : other;
	}

	private CEditability(IEditability assertionsStatus, IEditability queriesStatus) {

		this.assertionsStatus = assertionsStatus;
		this.queriesStatus = queriesStatus;
	}
}
