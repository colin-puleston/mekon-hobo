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

package uk.ac.manchester.cs.mekon.gui.util;

import java.util.*;

/**
 * @author Colin Puleston
 */
abstract class GCellDisplaySortedList<E>  {

	private Collection<E> elements;
	private List<E> list = null;
	private Map<E, Integer> indices = null;

	private class SortedElementsComparator implements Comparator<E> {

		public int compare(E first, E second) {

			if (first.equals(second)) {

				return 0;
			}

			int c = compareOrdered(first, second);

			return c != 0 ? c : compareHashCodes(first, second);
		}
	}

	GCellDisplaySortedList(boolean ordered) {

		elements = ordered ? createSortedSet() : new ArrayList<E>();
	}

	void add(E element) {

		if (!elements.contains(element)) {

			elements.add(element);

			onEdited();
		}
	}

	void remove(E element) {

		if (elements.remove(element)) {

			onEdited();
		}
	}

	void clear() {

		if (!elements.isEmpty()) {

			elements.clear();
			onEdited();
		}
	}

	List<E> asList() {

		checkReadable();

		return list;
	}

	int compareOrdered(E first, E second) {

		return compareLabels(getLabel(first), getLabel(second));
	}

	abstract GCellDisplay getDisplay(E element);

	private SortedSet<E> createSortedSet() {

		return new TreeSet<E>(new SortedElementsComparator());
	}

	private void onEdited() {

		if (list != null) {

			list = null;
			indices = null;
		}
	}

	private void checkReadable() {

		if (list == null) {

			list = new ArrayList<E>();
			indices = new HashMap<E, Integer>();

			int i = 0;

			for (E element : elements) {

				list.add(element);
				indices.put(element, i++);
			}
		}
	}

	private int compareLabels(String first, String second) {

		int c = first.compareToIgnoreCase(second);

		return c != 0 ? c : first.compareTo(second);
	}

	private int compareHashCodes(E first, E second) {

		return first.hashCode() > second.hashCode() ? 1 : -1;
	}

	private String getLabel(E element) {

		return getDisplay(element).getLabel();
	}
}