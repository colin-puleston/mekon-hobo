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

import uk.ac.manchester.cs.mekon_util.*;

/**
 * Abstract class whose extensions represent ordered lists,
 * with element value-types that enable each value to be
 * mapped to an associated {@link CIdentity} object, where
 * that identity object can be retrieved via the value object
 * itself.
 * <p>
 * The "keys" for the version of {@link KKeyedList} that this
 * class extends will be the "identifier" values from the
 * identities to which the values are mapped (see
 * {@link CIdentity#getIdentifier()}).
 * <p>
 * See ancestor classes for details of additional
 * list-operations and associated listening mechanisms.
 *
 * @author Colin Puleston
 */
public abstract class FIdentifiables<V> extends KKeyedList<String, V> {

	/**
	 * Tests whether list contains value with the specified
	 * identity.
	 *
	 * @param identity Identity to look for
	 * @return True if list contains value for specified identity
	 */
	public boolean containsValueFor(CIdentity identity) {

		return containsKey(identity.getIdentifier());
	}

	/**
	 * Retrieves the value with the specified identity.
	 *
	 * @param identity Identity for which value is required
	 * @return Required value
	 * @throws KAccessException If list does not contain value for
	 * specified identity
	 */
	public V get(CIdentity identity) {

		return get(identity.getIdentifier());
	}

	/**
	 * Retrieves the value with the specified identity.
	 *
	 * @param identity Identity for which value is required
	 * @return Required value, or null if list does not contain
	 * value for specified identity
	 */
	public V getOrNull(CIdentity identity) {

		return getOrNull(identity.getIdentifier());
	}

	/**
	 * Retrieves all values with the specified identities.
	 *
	 * @param identities Keys for which values are required
	 * @return Required values
	 * @throws KAccessException If list does not contain value for
	 * any of the specified identities
	 */
	public List<V> getForIdentities(List<CIdentity> identities) {

		List<V> values = new ArrayList<V>();

		for (CIdentity identity : identities) {

			values.add(get(identity));
		}

		return values;
	}

	/**
	 * Constructor.
	 *
	 */
	protected FIdentifiables() {
	}

	/**
	 * Constructor.
	 *
	 * @param values Values to be added to list
	 */
	protected FIdentifiables(Collection<V> values) {

		addAllValues(values);
	}

	/**
	 * Provides the key by which the specified value will be
	 * accessed, which will be the "identifier" value from the
	 * identity associated with that value.
	 *
	 * @param value Value whose key is required
	 * @return Key for specified value
	 */
	protected String getKey(V value) {

		return getIdentity(value).getIdentifier();
	}

	/**
	 * Method whose implementations will provide the identity
	 * by which the specified value will be accessed.
	 *
	 * @param value Value whose identity is required
	 * @return Identity for specified value
	 */
	protected abstract CIdentity getIdentity(V value);
}
