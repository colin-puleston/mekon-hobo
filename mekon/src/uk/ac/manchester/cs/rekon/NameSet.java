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

package uk.ac.manchester.cs.rekon;

import java.util.*;

import org.semanticweb.owlapi.model.*;

/**
 * @author Colin Puleston
 */
class NameSet {

	private Set<Name> names = new HashSet<Name>();
	private int[] refs = null;

	public String toString() {

		return getClass().getSimpleName() + "(" + names + ")";
	}

	void add(Name name) {

		names.add(name);
		refs = null;
	}

	void addAll(NameSet set) {

		names.addAll(set.names);
		refs = null;
	}

	void removeAll(NameSet set) {

		names.removeAll(set.names);
		refs = null;
	}

	void clear() {

		names.clear();
		refs = null;
	}

	Set<Name> getSet() {

		return names;
	}

	Set<Name> copySet() {

		return new HashSet<Name>(names);
	}

	boolean isEmpty() {

		return names.isEmpty();
	}

	boolean contains(Name name) {

		return containsRef(ensureRefs(), name.getRef());
	}

	boolean containsAll(NameSet set) {

		return containsAllRefs(ensureRefs(), set.ensureRefs());
	}

	boolean containsAny(NameSet set) {

		return containsAnyRefs(ensureRefs(), set.ensureRefs());
	}

	private boolean containsRef(int[] refs, int testRef) {

		for (int i = 0 ; i < refs.length ; i++) {

			if (refs[i] == testRef) {

				return true;
			}
		}

		return false;
	}

	private boolean containsAllRefs(int[] refs, int[] testRefs) {

		int r = 0;

		for (int tr = 0 ; tr < testRefs.length ; tr++) {

			boolean found = false;

			for (; r < refs.length ; r++) {

				if (refs[r] == testRefs[tr]) {

					found = true;
					break;
				}
			}

			if (!found) {

				return false;
			}
		}

		return true;
	}

	private boolean containsAnyRefs(int[] refs, int[] testRefs) {

		int r = 0;

		for (int tr = 0 ; tr < testRefs.length ; tr++) {

			for (; r < refs.length ; r++) {

				if (refs[r] == testRefs[tr]) {

					return true;
				}
			}
		}

		return false;
	}

	private int[] ensureRefs() {

		if (refs == null) {

			refs = new int[names.size()];
			int i = 0;

			for (Integer ref : sortRefs()) {

				refs[i++] = ref;
			}
		}

		return refs;
	}

	private SortedSet<Integer> sortRefs() {

		SortedSet<Integer> sRefs = new TreeSet<Integer>();

		for (Name n : names) {

			sRefs.add(n.getRef());
		}

		return sRefs;
	}
}
