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
 * Base-class for classes that represent ordered lists whose
 * update-operations can be listened to. Provides standard and
 * some non-standard list-operations, plus methods for attaching
 * listeners to listen for the results of those operations.
 *
 * @author Colin Puleston
 */
public abstract class KList<V> {

	private List<V> values = new ArrayList<V>();
	private Set<V> valueFinder = new HashSet<V>();

	private List<KUpdateListener> updateListeners = new ArrayList<KUpdateListener>();
	private List<KValuesListener<V>> valuesListeners = new ArrayList<KValuesListener<V>>();

	/**
	 * Adds a general-update listener to the list.
	 *
	 * @param listener Listener to add
	 */
	public void addUpdateListener(KUpdateListener listener) {

		updateListeners.add(listener);
	}

	/**
	 * Removes a general-update listener to the list. If specified
	 * listener is not a currenly registered listener then does
	 * nothing.
	 *
	 * @param listener Listener to remove
	 */
	public void removeUpdateListener(KUpdateListener listener) {

		updateListeners.remove(listener);
	}

	/**
	 * Adds a listener for specific types of list-value updates.
	 *
	 * @param listener Listener to add
	 */
	public void addValuesListener(KValuesListener<V> listener) {

		valuesListeners.add(listener);
	}

	/**
	 * Removes a listener for specific types of list-value updates.
	 * If specified listener is not a currenly registered listener,
	 * does nothing.
	 *
	 * @param listener Listener to remove
	 */
	public void removeValuesListener(KValuesListener<V> listener) {

		valuesListeners.remove(listener);
	}

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>KList</code>
	 * containing the same ordered set of values
	 */
	public boolean equals(Object other) {

		if (other instanceof KList) {

			return values.equals(((KList)other).values);
		}

		return false;
	}

	/**
	 * Provides hash-code based on ordered set of values.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return values.hashCode();
	}

	/**
	 * Specifies the current size of the list.
	 *
	 * @return Size of list
	 */
	public int size() {

		return values.size();
	}

	/**
	 * Tests whether the list is currently empty.
	 *
	 * @return True if empty
	 */
	public boolean isEmpty() {

		return values.isEmpty();
	}

	/**
	 * Tests whether the list contains the specified value.
	 *
	 * @param value Value to look for
	 * @return True list contains required value
	 */
	public boolean contains(V value) {

		return valueFinder.contains(value);
	}

	/**
	 * Provides index within list of the specified value, if applicable.
	 *
	 * @param value Value whose index is required
	 * @return Index of value, or -1 if value not in list
	 */
	public int indexOf(V value) {

		return values.indexOf(value);
	}

	/**
	 * Provides contents of list as a <code>List</code> object.
	 *
	 * @return Ordered list contents
	 */
	public List<V> asList() {

		return new ArrayList<V>(values);
	}

	/**
	 * Provides contents of list as a <code>List</code> object with
	 * specified element-type, filtering out any elements not of the
	 * required type.
	 *
	 * @param <E> Generic version of elementType
	 * @param elementType Required element type
	 * @return Ordered elements of required type
	 */
	public <E>List<E> asList(Class<E> elementType) {

		List<E> list = new ArrayList<E>();

		for (V value : values) {

			if (elementType.isAssignableFrom(value.getClass())) {

				list.add(elementType.cast(value));
			}
		}

		return list;
	}

	/**
	 * Provides contents of list as a <code>Set</code> object.
	 *
	 * @return List contents
	 */
	public Set<V> asSet() {

		return new HashSet<V>(values);
	}

	/**
	 * Provides contents of list as a <code>Set</code> object with
	 * specified element-type, filtering out any elements not of the
	 * required type.
	 *
	 * @param <E> Generic version of elementType
	 * @param elementType Required element type
	 * @return Elements of required type
	 */
	public <E>Set<E> asSet(Class<E> elementType) {

		Set<E> set = new HashSet<E>();

		for (V value : values) {

			if (elementType.isAssignableFrom(value.getClass())) {

				set.add(elementType.cast(value));
			}
		}

		return set;
	}

	/**
	 * Constructor.
	 */
	protected KList() {
	}

	/**
	 * Constructor.
	 *
	 * @param values Values to be added to list
	 */
	protected KList(Collection<V> values) {

		addAllValues(values);
	}

	/**
	 * Adds specified value to the list, if not already present.
	 *
	 * @param value Value to add
	 * @return True if value was added
	 */
	protected boolean addValue(V value) {

		if (addNewValue(value)) {

			pollListenersForUpdate();

			return true;
		}

		return false;
	}

	/**
	 * Inserts value at specified position in the list. If value is
	 * already present then moves it to the new position.
	 *
	 * @param value Value to add/move
	 * @param index Relevant index
	 * @return Previous index of moved value, or -1 if not previously
	 * present
	 * @throws KAccessException If specified index is invalid
	 */
	protected int insertValue(V value, int index) {

		int oldIndex = values.indexOf(value);

		if (oldIndex != -1) {

			values.remove(value);
		}

		if (index > values.size()) {

			throw new KAccessException("Invalid index: " + index);
		}

		values.add(index, value);

		if (oldIndex == -1) {

			valueFinder.add(value);
			pollListenersForAdded(value);
		}

		pollListenersForUpdate();

		return oldIndex;
	}

	/**
	 * Adds all specified values to the list, if not already present.
	 *
	 * @param values Values to add
	 * @return All values that were added
	 */
	protected List<V> addAllValues(Collection<V> values) {

		List<V> additions = new ArrayList<V>();

		for (V value : values) {

			if (addNewValue(value)) {

				additions.add(value);
			}
		}

		if (!additions.isEmpty()) {

			pollListenersForUpdate();
		}

		return additions;
	}

	/**
	 * Removes specified value from the list, if present.
	 *
	 * @param value Value to remove
	 * @return Index of removed value, or -1 if not present
	 */
	protected int removeValue(V value) {

		int index = removeOldValue(value);

		if (index != -1) {

			pollListenersForUpdate();
		}

		return index;
	}

	/**
	 * Removes value at specified index from the list.
	 *
	 * @param index Index of value to remove
	 * @throws KAccessException if illegal index
	 */
	protected void removeValue(int index) {

		if (index > values.size()) {

			throw new KAccessException("Illegal value-index: " + index);
		}

		removeValue(values.get(index));
	}

	/**
	 * Removes all values from the list.
	 */
	protected void clearValues() {

		if (!values.isEmpty()) {

			List<V> cleared = new ArrayList<V>(values);

			values.clear();
			valueFinder.clear();

			pollListenersForCleared(cleared);
			pollListenersForUpdate();
		}
	}

	/**
	 * Updates the list so that it contains each of the specified
	 * values, and only those values, making any required additions
	 * and deletions. Where relevant, will maintain the current list
	 * ordering in preference to the supplied list.
	 *
	 * @param latestValues Values that list is to contain
	 */
	protected void updateValues(List<V> latestValues) {

		boolean removals = removeOldValues(latestValues);
		boolean additions = addNewValues(latestValues);

		if (additions || removals) {

			pollListenersForUpdate();
		}
	}

	/**
	 * Re-orders the current set of values.
	 *
	 * @param reorderedValues List containing all current values
	 * ordered as required
	 * @throws KAccessException If provided list does not contain
	 * all current values and only current values
	 */
	protected void reorderValues(List<V> reorderedValues) {

		if (!currentValueSet(reorderedValues)) {

			throw new KAccessException(
						"Not a re-ordering of current value-set: "
						+ " Current values: " + values
						+ " Provided values: " + reorderedValues);
		}

		values.clear();
		values.addAll(reorderedValues);
	}

	private boolean addNewValues(List<V> latestValues) {

		boolean additions = false;
		List<V> previousValues = new ArrayList<V>(values);

		for (V value : latestValues) {

			if (!previousValues.contains(value)) {

				addNewValue(value);

				additions = true;
			}
		}

		return additions;
	}

	private boolean removeOldValues(List<V> latestValues) {

		boolean removals = false;

		for (V value : new ArrayList<V>(values)) {

			if (!latestValues.contains(value)) {

				removeOldValue(value);

				removals = true;
			}
		}

		return removals;
	}

	private boolean addNewValue(V value) {

		if (valueFinder.add(value)) {

			values.add(value);
			pollListenersForAdded(value);

			return true;
		}

		return false;
	}

	private int removeOldValue(V value) {

		if (valueFinder.remove(value)) {

			int index = values.indexOf(value);

			values.remove(value);
			pollListenersForRemoved(value);

			return index;
		}

		return -1;
	}

	private boolean currentValueSet(List<V> testValues) {

		return new HashSet<V>(testValues).equals(valueFinder);
	}

	private void pollListenersForUpdate() {

		for (KUpdateListener listener : copyListeners(updateListeners)) {

			listener.onUpdated();
		}
	}

	private void pollListenersForAdded(V value) {

		for (KValuesListener<V> listener : copyListeners(valuesListeners)) {

			listener.onAdded(value);
		}
	}

	private void pollListenersForRemoved(V value) {

		for (KValuesListener<V> listener : copyListeners(valuesListeners)) {

			listener.onRemoved(value);
		}
	}

	private void pollListenersForCleared(List<V> values) {

		for (KValuesListener<V> listener : copyListeners(valuesListeners)) {

			listener.onCleared(values);
		}
	}

	private <L>List<L> copyListeners(List<L> source) {

		return source.isEmpty() ? Collections.emptyList() : new ArrayList<L>(source);
	}
}
