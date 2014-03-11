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

/**
 * Map whose values are collections of some specific type.
 *
 * @author Colin Puleston
 */
public abstract class KCollectionMap<K, E> {

	private Map<K, Collection<E>> map = new HashMap<K, Collection<E>>();

	/**
	 * Adds an element to the collection mapped to the specified key,
	 * creating the collection if it doesn't yet exist.
	 *
	 * @param key Key for required collection
	 * @param element Element to be added to collection
	 */
	public void add(K key, E element) {

		resolveCollection(key).add(element);
	}

	/**
	 * Adds the specified elements to the collection mapped to the
	 * specified key, creating the collection if it doesn't yet exist.
	 *
	 * @param key Key for required collection
	 * @param elements Elements to be added to collection
	 */
	public void addAll(K key, Collection<? extends E> elements) {

		resolveCollection(key).addAll(elements);
	}

	/**
	 * Removes an element from the collection mapped to the specified key,
	 * if such a collection and element exist.
	 *
	 * @param key Key for required collection
	 * @param element Element to be removed from collection
	 */
	public void remove(K key, E element) {

		removeAll(key, Collections.<E>singleton(element));
	}

	/**
	 * Removes the specified elements from the collection mapped to the
	 * specified key, if such a collection and element exist.
	 *
	 * @param key Key for required collection
	 * @param elements Elements to be removed from collection
	 */
	public void removeAll(K key, Collection<? extends E> elements) {

		Collection<E> collection = map.get(key);

		if (collection != null) {

			collection.removeAll(elements);

			if (collection.isEmpty()) {

				map.remove(key);
			}
		}
	}

	/**
	 * Removes the specified collection from the map.
	 *
	 * @param key Key for required collection
	 */
	public void removeAll(K key) {

		map.remove(key);
	}

	/**
	 * Removes all collections from the map.
	 */
	public void clear() {

		map.clear();
	}

	/**
	 */
	public int hashCode() {

		return map.hashCode();
	}

	/**
	 * Provides the set of keys for which map currently contains
	 * collections.
	 *
	 * @return Current set of keys
	 */
	public Set<K> keySet() {

		return map.keySet();
	}

	/**
	 * Tests whether map contains collection for specified key.
	 *
	 * @param key Key to look for
	 * @return True required if collection found
	 */
	public boolean containsKey(K key) {

		return map.containsKey(key);
	}

	boolean equalMaps(KCollectionMap<?, ?> other) {

		return map.equals(other.map);
	}

	Collection<E> getCollection(K key) {

		Collection<E> collection = map.get(key);

		return collection != null ? collection : getEmptyCollection();
	}

	abstract Collection<E> createCollection();

	abstract Collection<E> getEmptyCollection();

	private Collection<E> resolveCollection(K key) {

		Collection<E> collection = map.get(key);

		if (collection == null) {

			collection = createCollection();
			map.put(key, collection);
		}

		return collection;
	}
}
