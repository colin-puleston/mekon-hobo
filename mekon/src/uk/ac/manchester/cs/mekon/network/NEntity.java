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

package uk.ac.manchester.cs.mekon.network;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents an entity in the network-based instance representation
 *
 * @author Colin Puleston
 */
public abstract class NEntity {

	private int hashCode = 0;

	private References<CIdentity> typeDisjuncts = new References<CIdentity>();

	class References<V> extends ArrayList<V> {

		static private final long serialVersionUID = -1;

		public boolean add(V value) {

			onUpdate();

			return super.add(value);
		}

		public boolean addAll(Collection<? extends V> values) {

			onUpdate();

			return super.addAll(values);
		}

		public boolean remove(Object value) {

			onUpdate();

			return super.remove(value);
		}

		public boolean removeAll(Collection<?> values) {

			onUpdate();

			return super.removeAll(values);
		}

		public void clear() {

			onUpdate();

			super.clear();
		}

		private void onUpdate() {

			hashCode = 0;
		}
	}

	/**
	 * Sets an atomic type for the entity.
	 *
	 * @param type Atomic type for entity
	 */
	public void setType(CIdentity type) {

		typeDisjuncts.clear();
		typeDisjuncts.add(type);
	}

	/**
	 * Tests if the other specified object is another
	 * <code>NEntity</code> with the same type as this one
	 * (whether atomic or disjunction), and referencing recursively
	 * identical network structure.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if objects are equal
	 */
	public boolean equals(Object other) {

		if (other != null && getClass() == other.getClass()) {

			return equalsEntity((NEntity)other);
		}

		return false;
	}

	/**
	 * Provides hash-code for this object.
	 *
	 * @return Relelevant hash-code
	 */
	public int hashCode() {

		if (hashCode == 0) {

			hashCode = calcHashCode();
		}

		return hashCode;
	}

	/**
	 */
	public String toString() {

		return getClass().getSimpleName() + ": " + typeDisjuncts;
	}

	/**
	 * Specifies whether the entity type is atomic, rather than
	 * a disjunction.
	 *
	 * @return True entity type is atomic
	 */
	public boolean atomicType() {

		return typeDisjuncts.size() == 1;
	}

	/**
	 * Specifies whether the entity type is atomic and equal
	 * to the specified type.
	 *
	 * @param type Atomic type to test for
	 * @return True if entity type is as specified
	 */
	public boolean atomicType(CIdentity type) {

		return atomicType() && getType().equals(type);
	}

	/**
	 * Provides the atomic type of the entity, for relevant entities.
	 *
	 * @return Associated atomic type
	 * @throws KAccessException if entity type is disjunction
	 */
	public CIdentity getType() {

		if (atomicType()) {

			return typeDisjuncts.iterator().next();
		}

		throw new KAccessException("Does not have atomic type: " + this);
	}

	/**
	 * Provides all disjuncts of entity type. Where associated
	 * type is atomic, the returned set will consist of that single
	 * atomic type
	 *
	 * @return All disjuncts of entity type
	 */
	public List<CIdentity> getTypeDisjuncts() {

		return new ArrayList<CIdentity>(typeDisjuncts);
	}

	NEntity(CIdentity type) {

		typeDisjuncts.add(type);
	}

	NEntity(Collection<CIdentity> typeDisjuncts) {

		setTypeDisjuncts(typeDisjuncts);
	}

	void setTypeDisjuncts(Collection<CIdentity> disjuncts) {

		if (disjuncts.isEmpty()) {

			throw new KAccessException("Cannot have empty type-disjuncts set");
		}

		typeDisjuncts.clear();
		typeDisjuncts.addAll(disjuncts);
	}

	abstract References<?> getLateralReferences();

	private boolean equalsEntity(NEntity other) {

		return typeDisjuncts.equals(other.typeDisjuncts)
				&& getLateralReferences().equals(other.getLateralReferences());
	}

	private int calcHashCode() {

		return typeDisjuncts.hashCode() + getLateralReferences().hashCode();
	}
}
