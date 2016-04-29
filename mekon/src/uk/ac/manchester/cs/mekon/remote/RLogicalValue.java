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
 * Represents a logical value, which can either be an atomic entity,
 * or a disjunction of atomic entities.
 *
 * @author Colin Puleston
 */
public abstract class RLogicalValue<E, ES, S extends RLogicalValueSpec<E, ES, ?>> {

	private List<E> disjuncts = new ArrayList<E>();

	/**
	 */
	public String toString() {

		return getClass().getSimpleName() + "(" + disjunctsToString() + ")";
	}

	/**
	 * Specifies whether the entity is atomic, rather than a
	 * disjunction.
	 *
	 * @return True if entity is atomic
	 */
	public boolean atomic() {

		return disjuncts.size() == 1;
	}

	/**
	 * Provides the single atomic entity, if applicable.
	 *
	 * @return Atomic entity
	 * @throws KAccessException if entity is disjunction
	 */
	public E getAtomic() {

		if (atomic()) {

			return disjuncts.get(0);
		}

		throw new KAccessException("Not an atomic entity: " + this);
	}

	/**
	 * Provides all disjuncts. Where entity is atomic, the returned
	 * set will consist of that single atomic entity.
	 *
	 * @return All disjuncts
	 */
	public List<E> getDisjuncts() {

		return new ArrayList<E>(disjuncts);
	}

	RLogicalValue(E entity) {

		disjuncts.add(entity);
	}

	RLogicalValue(Collection<E> disjuncts) {

		if (disjuncts.isEmpty()) {

			throw new KAccessException("Cannot have empty disjuncts set");
		}

		this.disjuncts.addAll(disjuncts);
	}

	S toSpec() {

		S spec = createSpec();

		for (E disjunct : disjuncts) {

			spec.addDisjunct(entityToSpec(disjunct));
		}

		return spec;
	}

	abstract S createSpec();

	abstract ES entityToSpec(E entity);

	private String disjunctsToString() {

		StringBuilder s = new StringBuilder();
		boolean first = true;

		for (E disjunct : disjuncts) {

			if (first) {

				first = false;
			}
			else {

				s.append(" OR ");
			}

			s.append(disjunct);
		}

		return s.toString();
	}
}
