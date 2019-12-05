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

package uk.ac.manchester.cs.mekon.gui;

import java.util.*;

/**
 * @author Colin Puleston
 */
abstract class GCellDisplaySortedList<E>  {

	private Elements elements;

	private class SortedElementsComparator implements Comparator<E> {

		public int compare(E first, E second) {

			if (first.equals(second)) {

				return 0;
			}

			int c = compareOrdered(first, second);

			return c != 0 ? c : compareHashCodes(first, second);
		}
	}

	private class Elements {

		private List<E> list = new ArrayList<E>();

		void add(E element, int index) {

			list.add(index, element);
		}

		void remove(E element) {

			list.remove(element);
		}

		void clear() {

			list.clear();
		}

		Collection<E> view() {

			return list;
		}

		List<E> asList() {

			return list;
		}
	}

	private class SortedElements extends Elements {

		private Set<E> sorted = new TreeSet<E>(new SortedElementsComparator());
		private boolean upToDate = true;

		void add(E element, int index) {

			edit().add(element);
		}

		void remove(E element) {

			edit().remove(element);
		}

		void clear() {

			edit().clear();
		}

		Collection<E> view() {

			return sorted;
		}

		List<E> asList() {

			List<E> list = super.asList();

			if (!upToDate) {

				list.addAll(sorted);
				upToDate = true;
			}

			return list;
		}

		private Collection<E> edit() {

			super.asList().clear();
			upToDate = false;

			return sorted;
		}
	}

	GCellDisplaySortedList(boolean sort) {

		elements = sort ? new SortedElements() : new Elements();
	}

	void add(E element) {

		insert(element, elements.view().size());
	}

	void insert(E element, int index) {

		if (!elements.view().contains(element)) {

			elements.add(element, index);
		}
	}

	void remove(E element) {

		if (elements.view().contains(element)) {

			elements.remove(element);
		}
	}

	void clear() {

		if (!elements.view().isEmpty()) {

			elements.clear();
		}
	}

	int size() {

		return elements.view().size();
	}

	List<E> asList() {

		return elements.asList();
	}

	int compareOrdered(E first, E second) {

		return compareLabels(getLabel(first), getLabel(second));
	}

	abstract GCellDisplay getDisplay(E element);

	private int compareLabels(String first, String second) {

		int c = first.compareToIgnoreCase(second);

		return c != 0 ? c : first.compareTo(second);
	}

	private int compareHashCodes(E first, E second) {

		return first.hashCode() > second.hashCode() ? 1 : -1;
	}

	private String getLabel(E element) {

		return getDisplay(element).getText();
	}
}