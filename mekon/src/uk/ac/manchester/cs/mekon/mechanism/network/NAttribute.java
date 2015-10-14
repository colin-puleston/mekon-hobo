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

package uk.ac.manchester.cs.mekon.mechanism.network;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents an attribute attached to a node in the network-based
 * instance representation. Attributes can be of either link or
 * numeric type.
 *
 * @author Colin Puleston
 */
public abstract class NAttribute<V> {

	private CIdentity property;
	private List<V> values = new ArrayList<V>();

	private ISlot iSlot = null;

	private int hashCode = 0;

	/**
	 * Sets the property associated with the attribute.
	 *
	 * @param property Associated property
	 */
	public void setProperty(CIdentity property) {

		this.property = property;
	}

	/**
	 * Adds a value to the attribute.
	 *
	 * @param value Value to add
	 */
	public void addValue(V value) {

		values.add(value);
		hashCode = 0;
	}

	/**
	 * Adds a set of values to the attribute.
	 *
	 * @param values Values to add
	 */
	public void addValues(Collection<V> values) {

		this.values.addAll(values);
		hashCode = 0;
	}

	/**
	 * Removes a value from the attribute.
	 *
	 * @param value Value to remove
	 */
	public void removeValue(V value) {

		values.remove(value);
		hashCode = 0;
	}

	/**
	 * Removes a set of values from the attribute.
	 *
	 * @param values Values to remove
	 */
	public void removeValues(Collection<V> values) {

		this.values.removeAll(values);
		hashCode = 0;
	}

	/**
	 * Removes all values from the attribute.
	 */
	public void clearValues() {

		values.clear();
		hashCode = 0;
	}

	/**
	 */
	public String toString() {

		return getClass().getSimpleName() + ": " + property;
	}

	/**
	 * Tests if the other specified object is another
	 * <code>NAttribute</code> with the same associated property as
	 * this one, and with identical values.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if objects are equal
	 */
	public boolean equals(Object other) {

		if (getClass() == other.getClass()) {

			return equalsAttribute((NAttribute<?>)other);
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
	 * Provides the property associated with the attribute.
	 *
	 * @return Associated property
	 */
	public CIdentity getProperty() {

		return property;
	}

	/**
	 * Specifies whether this is a link attribute, rather than a
	 * numeric attribute.
	 *
	 * @return True if link attribute
	 */
	public abstract boolean link();

	/**
	 * Specifies whether this is a numeric attribute, rather than a
	 * link attribute.
	 *
	 * @return True if numeric attribute
	 */
	public boolean number() {

		return !link();
	}

	/**
	 * Casts attribute as a link.
	 *
	 * @return Attribute cast as link
	 * @throws KAccessException if not a link
	 */
	public NLink asLink() {

		throw new KAccessException("Not a link: " + this);
	}

	/**
	 * Casts attribute as a numeric.
	 *
	 * @return Attribute cast as numeric
	 * @throws KAccessException if not a numeric
	 */
	public NNumeric asNumber() {

		throw new KAccessException("Not a number: " + this);
	}

	/**
	 * Checks whether the attribute has any values.
	 *
	 * @return True if attribute has values
	 */
	public boolean hasValues() {

		return !values.isEmpty();
	}

	/**
	 * Provides all current values for the attribute.
	 *
	 * @return All current values
	 */
	public List<V> getValues() {

		return new ArrayList<V>(values);
	}

	/**
	 * Provides the corresponding concept-level slot for attributes
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

	NAttribute(CIdentity property, ISlot iSlot) {

		this.property = property;
		this.iSlot = iSlot;
	}

	private boolean equalsAttribute(NAttribute<?> other) {

		return property.equals(other.property) && values.equals(other.values);
	}

	private int calcHashCode() {

		return property.hashCode() + values.hashCode();
	}
}
