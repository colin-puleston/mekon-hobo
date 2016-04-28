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

package uk.ac.manchester.cs.mekon.remote;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;

/**
 * Represents a concept, which can either be an atomic concept,
 * or a disjunction of atomic concepts.
 *
 * @author Colin Puleston
 */
public class RConcept {

	private List<RIdentity> disjunctIds = new ArrayList<RIdentity>();

	/**
	 */
	public String toString() {

		return getClass().getSimpleName() + "(" + toInnerString() + ")";
	}

	/**
	 * Specifies whether the concept is atomic, rather than a
	 * disjunction.
	 *
	 * @return True if concept is atomic
	 */
	public boolean atomic() {

		return disjunctIds.size() == 1;
	}

	/**
	 * Provides the identity of the atomic concept, if applicable.
	 *
	 * @return Identity of atomic concept
	 * @throws KAccessException if concept is disjunction
	 */
	public RIdentity getAtomicId() {

		if (atomic()) {

			return disjunctIds.get(0);
		}

		throw new KAccessException("Not an atomic concept: " + this);
	}

	/**
	 * Provides identities of all disjuncts. Where concept is atomic,
	 * the returned set will consist of that single atomic concept.
	 *
	 * @return Identities of all disjuncts
	 */
	public List<RIdentity> getDisjunctIds() {

		return new ArrayList<RIdentity>(disjunctIds);
	}

	RConcept(RIdentity conceptId) {

		disjunctIds.add(conceptId);
	}

	RConcept(Collection<RIdentity> disjunctIds) {

		if (disjunctIds.isEmpty()) {

			throw new KAccessException("Cannot have empty disjuncts set");
		}

		this.disjunctIds.addAll(disjunctIds);
	}

	RConceptSpec toSpec() {

		RConceptSpec spec = new RConceptSpec();

		for (RIdentity disjunctId : disjunctIds) {

			spec.addDisjunctId(disjunctId.toSpec());
		}

		return spec;
	}

	String toInnerString() {

		StringBuilder s = new StringBuilder();
		boolean first = true;

		for (RIdentity disjunctId : disjunctIds) {

			if (first) {

				first = false;
			}
			else {

				s.append(" OR ");
			}

			s.append(disjunctId.toInnerString());
		}

		return s.toString();
	}
}
