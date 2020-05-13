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
 * Manages the assignment of unique indexes to a set of entities.
 *
 * @author Colin Puleston
 */
public abstract class KIndexes<E> {

	private int maxIndex = -1;

	private List<Integer> freeIndexes = new ArrayList<Integer>();

	private Map<E, Integer> elementsToIndexes = new HashMap<E, Integer>();
	private Map<Integer, E> indexesToElements = new HashMap<Integer, E>();

	/**
	 * Assigns a unique index to an unspecified element.
	 *
	 * @return Assigned index
	 */
	public int assignIndex() {

		return freeIndexes.isEmpty() ? ++maxIndex : freeIndexes.remove(0);
	}

	/**
	 * Assigns a unique index to an element.
	 *
	 * @param element Element for which index is required
	 * @return Assigned index
	 */
	public int assignIndex(E element) {

		return addToMaps(element, assignIndex());
	}

	/**
	 * Assigns a specified index to an element. The specified index
	 * must not be currently assigned.
	 *
	 * @param element Element for which index is to be assigned
	 * @param index Index to be assigned
	 */
	public void assignIndex(E element, int index) {

		if (indexesToElements.containsKey(index)) {

			throw createException("Index already assigned: " + index);
		}

		addToMaps(element, index);

		if (index > maxIndex) {

			maxIndex = index;
		}
	}

	/**
	 * Either retrieves the currently assigned index for an element,
	 * or if no index currently assigned for that element, assigns
	 * it a unique index.
	 *
	 * @param element Element for which index is required
	 * @return Resulting assigned index
	 */
	public int ensureIndex(E element) {

		Integer index = elementsToIndexes.get(element);

		return index != null ? index : assignIndex(element);
	}

	/**
	 * Frees up a unique index that was previously assigned to a
	 * specific element.
	 *
	 * @param element Element for which index is no longer required
	 * @return Freed index
	 */
	public int freeIndex(E element) {

		Integer index = getIndex(element);

		elementsToIndexes.remove(element);
		indexesToElements.remove(index);

		freeIndex(index);

		return index;
	}

	/**
	 * Frees up a unique index that was previously assigned to an
	 * unspecified element.
	 *
	 * @param index Index to be freed
	 */
	public void freeIndex(int index) {

		freeIndexes.add(index);
	}

	/**
	 * Reinitialises the set of free unique indexes after a set
	 * of explicit assignments have been made via the {@link
	 * #assignIndex(Object, int)} method.
	 */
	public void reinitialiseFreeIndexes() {

		for (int i = 0 ; i < maxIndex ; i++) {

			if (!indexesToElements.containsKey(i)) {

				freeIndex(i);
			}
		}
	}

	/**
	 * Checks whether an element currently has an assigned index.
	 *
	 * @param element Element to check
	 * @return True if element currently has an assigned index
	 */
	public boolean hasIndex(E element) {

		return elementsToIndexes.containsKey(element);
	}

	/**
	 * Retrieves the currently assigned index for an element.
	 *
	 * @param element Element for which index is required
	 * @return Currently assigned index
	 */
	public int getIndex(E element) {

		Integer index = elementsToIndexes.get(element);

		if (index == null) {

			throw createException("No index for element: " + element);
		}

		return index;
	}

	/**
	 * Retrieves the currently assigned index for an element, if
	 * applicable.
	 *
	 * @param element Element for which index is required
	 * @return Currently assigned index, or null if no currently
	 * assigned index
	 */
	public Integer getIndexOrNull(E element) {

		return elementsToIndexes.get(element);
	}

	/**
	 * Retrieves the element to which an index is currently assigned.
	 *
	 * @param index Index for which element is required
	 * @return Element to which index is currently assigned
	 */
	public E getElement(int index) {

		E element = indexesToElements.get(index);

		if (element == null) {

			throw createException("No element for index: " + index);
		}

		return element;
	}

	/**
	 * Retrieves the entities to which the members of a set of indexes
	 * are currently assigned.
	 *
	 * @param indexes Indexes for which elements are required
	 * @return Elements to which indexs are currently assigned
	 */
	public List<E> getElements(List<Integer> indexes) {

		List<E> elements = new ArrayList<E>();

		for (Integer index : indexes) {

			E element = indexesToElements.get(index);

			if (element != null) {

				elements.add(element);
			}
		}

		return elements;
	}

	/**
	 * Creates a runtime-exception of the required type.
	 *
	 * @param message Message for exception
	 * @return Created exception of relevant type
	 */
	protected abstract KRuntimeException createException(String message);

	private int addToMaps(E element, int index) {

		if (elementsToIndexes.containsKey(element)) {

			throw createException("Index already assigned to element: " + element);
		}

		elementsToIndexes.put(element, index);
		indexesToElements.put(index, element);

		return index;
	}
}
