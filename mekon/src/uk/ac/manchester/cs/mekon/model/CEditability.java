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
 * Represents the editability from the client perspective of the
 * instantiations of a particular {@link CSlot}. Covers instance
 * editability status, as represented via {@link IEditability},
 * for instances of the slot on both assertions and queries.
 *
 * @author Colin Puleston
 */
public class CEditability {

	static private List<CEditability> editabilities = new ArrayList<CEditability>();

	static private CEditability get(IEditability assertionsStatus, IEditability queriesStatus) {

		for (CEditability ed : editabilities) {

			if (ed.hasStatuses(assertionsStatus, queriesStatus)) {

				return ed;
			}
		}

		return new CEditability(assertionsStatus, queriesStatus);
	}

	/**
	 * Represents the default editability where, for normal slots,
	 * the slot has {@link IEditability#CONCRETE_ONLY} editability
	 * on assertions, and {@link IEditability#FULL} editability on
	 * queries (see {@link ISlot#getEditability} for a discussion
	 * of the exceptional case of "disjuncts-slots").
	 */
	static public final CEditability DEFAULT = get(IEditability.CONCRETE_ONLY, IEditability.FULL);

	private IEditability assertionsStatus;
	private IEditability queriesStatus;

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>IEditability</code>
	 * with identical "assertions" and "queries" statuses to this one
	 */
	public boolean equals(Object other) {

		if (other == this) {

			return true;
		}

		return other instanceof CEditability && equalsEditability((CEditability)other);
	}

	/**
	 * Provides hash-code based on combination of "assertions" and
	 * "queries" statuses.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return assertionsStatus.hashCode() + queriesStatus.hashCode();
	}

	/**
	 * Provides string representaion combining of "assertions" and
	 * "queries" statuses.
	 *
	 * @return String representation of this object
	 */
	public String toString() {

		return CEditability.class.getSimpleName()
				+ "(" + assertionsStatus + "(Assertions)"
				+ "," + queriesStatus + "(Queries))";
	}

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
	 * Derives the "strongest" editability status between this and
	 * the other specified value. This will be a {@link CEditability}
	 * whose assertions-status and queries-status will each be the
	 * strongest for the two values (see {@link
	 * IEditability#getAssertionsStrongest} and {@link
	 * IEditability#getQueriesStrongest}).
	 *
	 * @param other Editability status to test against this one
	 * @return Strongest editability status
	 */
	public CEditability getStrongest(CEditability other) {

		return get(
				assertionsStatus.getAssertionsStrongest(other.assertionsStatus),
				queriesStatus.getQueriesStrongest(other.queriesStatus));
	}

	CEditability withAllStatus(IEditability status) {

		return get(status, status);
	}

	CEditability withAssertionsStatus(IEditability status) {

		return get(status, queriesStatus);
	}

	CEditability withQueriesStatus(IEditability status) {

		return get(assertionsStatus, status);
	}

	CEditability withStrongestAssertionsStatus(IEditability status) {

		return get(assertionsStatus.getAssertionsStrongest(status), queriesStatus);
	}

	CEditability withStrongestQueriesStatus(IEditability status) {

		return get(assertionsStatus, queriesStatus.getQueriesStrongest(status));
	}

	private CEditability(IEditability assertionsStatus, IEditability queriesStatus) {

		this.assertionsStatus = assertionsStatus;
		this.queriesStatus = queriesStatus;

		editabilities.add(this);
	}

	private boolean equalsEditability(CEditability other) {

		return hasStatuses(other.assertionsStatus, other.queriesStatus);
	}

	private boolean hasStatuses(IEditability assertions, IEditability queries) {

		return assertionsStatus.equals(assertions) && queriesStatus.equals(queries);
	}
}
