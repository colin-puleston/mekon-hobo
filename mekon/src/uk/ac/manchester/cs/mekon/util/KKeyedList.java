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

package uk.ac.manchester.cs.mekon.util;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;

/**
 * Abstract class whose extensions represent ordered lists,
 * with element value-types that enable each value to be
 * mapped to an associated "key" object, and where the key
 * object can be retrieved via the value object itself.
 * <p>
 * See {@link KList} for details of additional list-operations
 * and associated listening mechanisms.
 *
 * @author Colin Puleston
 */
public abstract class KKeyedList<K, V> extends KList<V> {

	private Map<K, V> finder = new HashMap<K, V>();

	private class FinderUpdater implements KValuesListener<V> {

		public void onAdded(V value) {

			finder.put(getKey(value), value);
		}

		public void onRemoved(V value) {

			finder.remove(getKey(value));
		}

		public void onCleared(List<V> values) {

			finder.clear();
		}
	}

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>KList</code>
	 * containing the same ordered set of values
	 */
	public boolean equals(Object other) {

		if (other instanceof KKeyedList && super.equals(other)) {

			return finder.equals(((KKeyedList)other).finder);
		}

		return false;
	}

	/**
	 * Provides hash-code based on ordered set of values.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return super.hashCode() + finder.hashCode();
	}

	/**
	 * Tests whether list contains value with the specified key.
	 *
	 * @param key Key to look for
	 * @return True if list contains value for specified key
	 */
	public boolean containsKey(K key) {

		return finder.containsKey(key);
	}

	/**
	 * Tests whether list contains specified value.
	 *
	 * @param value Value to look for
	 * @return True if list contains specified value
	 */
	public boolean containsValue(V value) {

		return finder.containsValue(value);
	}

	/**
	 * Provides all keys as a <code>List</code> object.
	 *
	 * @return Ordered list for list
	 */
	public List<K> keysAsList() {

		List<K> keys = new ArrayList<K>();

		for (V value : asList()) {

			keys.add(getKey(value));
		}

		return keys;
	}

	/**
	 * Provides all keys as a <code>Set</code> object.
	 *
	 * @return Keys for list
	 */
	public Set<K> keysAsSet() {

		return finder.keySet();
	}

	/**
	 * Retrieves the value with the specified key.
	 *
	 * @param key Key for which value is required
	 * @return Required value
	 * @throws KAccessException If list does not contain value for
	 * specified key
	 */
	public V get(K key) {

		V value = getOrNull(key);

		if (value == null) {

			throw new KAccessException("Cannot find value for: " + key);
		}

		return value;
	}

	/**
	 * Retrieves the value with the specified key.
	 *
	 * @param key Key for which value is required
	 * @return Required value, or null if list does not contain
	 * value for specified key
	 */
	public V getOrNull(K key) {

		return finder.get(key);
	}

	/**
	 * Retrieves all values with the specified keys.
	 *
	 * @param keys Keys for which values are required
	 * @return Required values
	 * @throws KAccessException If list does not contain value for
	 * any of the specified keys
	 */
	public List<V> getAll(List<K> keys) {

		List<V> values = new ArrayList<V>();

		for (K key : keys) {

			values.add(get(key));
		}

		return values;
	}

	/**
	 * Constructor.
	 */
	protected KKeyedList() {

		addValuesListener(new FinderUpdater());
	}

	/**
	 * Constructor.
	 *
	 * @param values Values to be added to list
	 */
	protected KKeyedList(Collection<V> values) {

		this();

		addAllValues(values);
	}

	/**
	 * Method whose implementations will provide the key by which
	 * the specified value will be accessed.
	 *
	 * @param value Value whose key is required
	 * @return Key for specified value
	 */
	protected abstract K getKey(V value);
}
