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
 * Represents an feature attached to a node in the network-based
 * instance representation. Features can be of either link or
 * numeric type.
 *
 * @author Colin Puleston
 */
public abstract class NFeature<V> extends NEntity {

	private References<V> values = new References<V>();
	private ISlot iSlot = null;

	/**
	 * Adds a value to the feature.
	 *
	 * @param value Value to add
	 */
	public void addValue(V value) {

		values.add(value);
	}

	/**
	 * Adds a set of values to the feature.
	 *
	 * @param values Values to add
	 */
	public void addValues(Collection<V> values) {

		this.values.addAll(values);
	}

	/**
	 * Removes a value from the feature.
	 *
	 * @param value Value to remove
	 */
	public void removeValue(V value) {

		values.remove(value);
	}

	/**
	 * Removes a set of values from the feature.
	 *
	 * @param values Values to remove
	 */
	public void removeValues(Collection<V> values) {

		this.values.removeAll(values);
	}

	/**
	 * Removes all values from the feature.
	 */
	public void clearValues() {

		values.clear();
	}

	/**
	 * Specifies whether this is a link feature, rather than a
	 * numeric feature.
	 *
	 * @return True if link feature
	 */
	public abstract boolean link();

	/**
	 * Specifies whether this is a numeric feature, rather than a
	 * link feature.
	 *
	 * @return True if numeric feature
	 */
	public boolean number() {

		return !link();
	}

	/**
	 * Casts feature as a link.
	 *
	 * @return Feature cast as link
	 * @throws KAccessException if not a link
	 */
	public NLink asLink() {

		throw new KAccessException("Not a link: " + this);
	}

	/**
	 * Casts feature as a numeric.
	 *
	 * @return Feature cast as numeric
	 * @throws KAccessException if not a numeric
	 */
	public NNumeric asNumeric() {

		throw new KAccessException("Not a numeric: " + this);
	}

	/**
	 * Checks whether the feature has any values.
	 *
	 * @return True if feature has values
	 */
	public boolean hasValues() {

		return !values.isEmpty();
	}

	/**
	 * Provides all current values for the feature.
	 *
	 * @return All current values
	 */
	public List<V> getValues() {

		return new ArrayList<V>(values);
	}

	/**
	 * Provides the corresponding concept-level slot for features
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

	NFeature(CIdentity type, ISlot iSlot) {

		super(type);

		this.iSlot = iSlot;
	}

	References<?> getLateralReferences() {

		return values;
	}
}
