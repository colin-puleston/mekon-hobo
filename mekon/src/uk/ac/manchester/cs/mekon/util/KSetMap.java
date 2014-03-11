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
 * Map whose values are sets.
 *
 * @author Colin Puleston
 */
public class KSetMap<K, E> extends KCollectionMap<K, E> {

	/**
	 */
	public boolean equals(Object other) {

		return other instanceof KSetMap && equalMaps((KSetMap<?, ?>)other);
	}

	/**
	 * Provides a copy of the set for the specified key.
	 *
	 * @param key Key for which set is required
	 * @return Copy of relevant set, or null if no set for key
	 */
	public Set<E> getSet(K key) {

		return new HashSet<E>(getCollection(key));
	}

	/**
	 * Provides the union of all sets for the specified keys.
	 *
	 * @param keys Keys for sets whose union is required
	 * @return Relevant union
	 */
	public Set<E> getAll(Set<K> keys) {

		Set<E> all = new HashSet<E>();

		for (K key : keys) {

			all.addAll(getSet(key));
		}

		return all;
	}

	Collection<E> createCollection() {

		return new HashSet<E>();
	}

	Collection<E> getEmptyCollection() {

		return Collections.<E>emptySet();
	}
}
