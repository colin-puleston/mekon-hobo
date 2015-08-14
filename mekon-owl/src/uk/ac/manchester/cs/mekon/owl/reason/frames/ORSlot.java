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

package uk.ac.manchester.cs.mekon.owl.reason.frames;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents a slot in the pre-processable frames-based
 * instance representation.
 *
 * @author Colin Puleston
 */
public abstract class ORSlot<V> extends ORFramesEntity {

	private ISlot iSlot = null;

	private Set<V> values = new HashSet<V>();
	private boolean closedWorldSemantics = false;

	private int hashCode = 0;

	/**
	 * Adds a value to the slot.
	 *
	 * @param value Value to add
	 */
	public void addValue(V value) {

		values.add(value);
		hashCode = 0;
	}

	/**
	 * Adds a set of values to the slot.
	 *
	 * @param values Values to add
	 */
	public void addValues(Collection<V> values) {

		this.values.addAll(values);
		hashCode = 0;
	}

	/**
	 * Removes a value from the slot.
	 *
	 * @param value Value to remove
	 */
	public void removeValue(V value) {

		values.remove(value);
		hashCode = 0;
	}

	/**
	 * Removes a set of values from the slot.
	 *
	 * @param values Values to remove
	 */
	public void removeValues(Collection<V> values) {

		this.values.removeAll(values);
		hashCode = 0;
	}

	/**
	 * Removes all values from the slot.
	 */
	public void clearValues() {

		values.clear();
		hashCode = 0;
	}

	/**
	 * Sets the value of the "closed-world-semantics" attribute for
	 * the slot.
	 *
	 * @param closedWorldSemantics True if slot is to have
	 * closed-world-semantics
	 */
	public void setClosedWorldSemantics(boolean closedWorldSemantics) {

		this.closedWorldSemantics = closedWorldSemantics;
	}

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>ORSlot</code>
	 * of the same type, with the same identifier and identical
	 * values.
	 */
	public boolean equals(Object other) {

		if (getClass() == other.getClass()) {

			return equalsSlot((ORSlot)other);
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
	 * Specifies whether this is a frame--valued slot.
	 *
	 * @return True if frame--valued slot
	 */
	public abstract boolean frameSlot();

	/**
	 * Specifies whether this is a number-valued slot.
	 *
	 * @return True if number-valued slot
	 */
	public boolean numberSlot() {

		return !frameSlot();
	}

	/**
	 * Casts slot as a frame--valued slot.
	 *
	 * @return Slot cast as frame--valued slot
	 * @throws KAccessException if not a frame--valued slot
	 */
	public ORFrameSlot asFrameSlot() {

		throw new KAccessException("Not a concept-slot: " + getIdentifier());
	}

	/**
	 * Casts slot as a number-valued slot.
	 *
	 * @return Slot cast as number-valued slot
	 * @throws KAccessException if not a number-valued slot
	 */
	public ORNumberSlot asNumberSlot() {

		throw new KAccessException("Not a number-slot: " + getIdentifier());
	}

	/**
	 * Checks whether the slot has any values.
	 *
	 * @return True if slot has values
	 */
	public boolean hasValues() {

		return !values.isEmpty();
	}

	/**
	 * Provides all current values for the slot.
	 *
	 * @return All current values
	 */
	public Set<V> getValues() {

		return new HashSet<V>(values);
	}

	/**
	 * Provides the value of the "closed-world-semantics" attribute
	 * for the slot.
	 *
	 * @return True if slot is to have closed-world-semantics
	 */
	public boolean closedWorldSemantics() {

		return closedWorldSemantics;
	}

	/**
	 * Provides the corresponding concept-level slot for slots
	 * that have been directly derived from instance-level slots.
	 *
	 * @return Corresponding concept-level slot, or null if not
	 * applicable.
	 */
	public CSlot getCSlot() {

		return iSlot != null ? iSlot.getType() : null;
	}

	/**
	 * Provides the corresponding instance-level slot for slots
	 * that have been directly derived from such slots.
	 *
	 * @return Corresponding instance-level slot, or null if not
	 * applicable.
	 */
	public ISlot getISlot() {

		return iSlot;
	}

	ORSlot(IRI iri) {

		super(iri);
	}

	ORSlot(String identifier, IRI iri) {

		super(identifier, iri);
	}

	ORSlot(CIdentity id, ISlot iSlot, IRI iri) {

		super(id.getIdentifier(), iri);

		this.iSlot = iSlot;
	}

	private boolean equalsSlot(ORSlot<?> other) {

		return equalIdentifiers(other) && values.equals(other.values);
	}

	private int calcHashCode() {

		return getIdentifier().hashCode() + values.hashCode();
	}
}
