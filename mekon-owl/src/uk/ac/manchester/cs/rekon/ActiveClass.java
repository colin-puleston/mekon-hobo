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

/**
 * @author Colin Puleston
 */
class ActiveClass implements Comparable<ActiveClass> {

	private ClassName name;

	private Description profile;
	private Set<Description> definitions = new HashSet<Description>();

	private Set<ActiveClass> equivalents = new HashSet<ActiveClass>();
	private Set<ActiveClass> supers = new HashSet<ActiveClass>();
	private Set<ActiveClass> subs = new HashSet<ActiveClass>();

	public int compareTo(ActiveClass other) {

		if (definitions.isEmpty()) {

			return 1;
		}

		if (other.definitions.isEmpty()) {

			return -1;
		}

		int c = minDefinitionSize() - other.minDefinitionSize();

		return c == 0 ? 1 : c;
	}

	public String toString() {

		return getClass().getSimpleName() + "(" + name.getEntity() + ")";
	}

	ActiveClass() {

		this(ClassName.THING, new Description(ClassName.THING));
	}

	ActiveClass(ClassName name, Description profile) {

		this.name = name;
		this.profile = profile;
	}

	void addDefinitions(Collection<Description> definitions) {

		this.definitions.addAll(definitions);
	}

	void setActiveNames(Set<Name> activeNames) {

		profile.setActiveNames(activeNames);

		for (Description d : definitions) {

			d.setActiveNames(activeNames);
		}
	}

	void setNameSubsumptions(NameSet updateds) {

		boolean updates = false;

		for (ActiveClass equ : equivalents) {

			if (name.addEquivalent(equ.name)) {

				updates |= true;
				updateds.add(equ.name);
			}
		}

		for (ActiveClass sup : supers) {

			if (name.addSuper(sup.name)) {

				updates |= true;
				updateds.add(sup.name);
			}
		}

		if (updates) {

			updateds.add(name);
		}
	}

	void resetNameReferences() {

		profile.resetNameReferences();

		for (Description d : definitions) {

			d.resetNameReferences();
		}
	}

	boolean removeReclassifiable(NameSet updateds) {

		if (profile.getNestedNameSubsumers().containsAny(updateds)) {

			removeFromClassification();

			return true;
		}

		return false;
	}

	void addEquivalent(ActiveClass equ) {

		addToEquivalents(equ);
		equ.addToEquivalents(this);
	}

	void addSub(ActiveClass sub) {

		subs.add(sub);

		if (!isRoot()) {

			sub.supers.add(this);
		}
	}

	void removeSub(ActiveClass sub) {

		subs.remove(sub);
		sub.supers.remove(this);
	}

	boolean isRoot() {

		return name == ClassName.THING;
	}

	ClassName getName() {

		return name;
	}

	Set<Name> getAllProfileNames() {

		return profile.getAllNames();
	}

	Set<ActiveClass> getEquivalents() {

		return equivalents;
	}

	Set<ActiveClass> getSupers() {

		return supers;
	}

	Set<ActiveClass> getSubs() {

		return subs;
	}

	boolean hasDefinitions() {

		return !definitions.isEmpty();
	}

	boolean definitionMatch(ActiveClass other) {

		for (Description d : definitions) {

			if (other.definitions.contains(d)) {

				return true;
			}
		}

		return false;
	}

	boolean equivalentTo(Description desc) {

		return definitions.contains(desc) || (subsumes(desc) && subsumedBy(desc));
	}

	boolean subsumes(ActiveClass other) {

		return other.isRoot() ? isRoot() : subsumes(other.profile);
	}

	boolean subsumes(Description desc) {

		if (isRoot()) {

			return true;
		}

		for (Description d : definitions) {

			if (d.subsumes(desc)) {

				return true;
			}
		}

		return false;
	}

	boolean subsumedBy(Description desc) {

		if (isRoot()) {

			return false;
		}

		for (Description d : definitions) {

			if (desc.subsumes(d)) {

				return true;
			}
		}

		return false;
	}

	void printHierarchy(String tabs) {

		System.out.println(tabs + name);

		for (ActiveClass sub : subs) {

			sub.printHierarchy(tabs + "  ");
		}
	}

	private void removeFromClassification() {

		removeEquivalents();
		removeFromHierarchy();
	}

	private void removeEquivalents() {

		for (ActiveClass equ : equivalents) {

			equ.equivalents.remove(this);
		}

		equivalents.clear();
	}

	private void removeFromHierarchy() {

		for (ActiveClass sup : supers) {

			sup.removeSub(this);

			for (ActiveClass sub : subs) {

				sup.addSub(sub);
			}
		}

		supers.clear();
		subs.clear();
	}

	private void addToEquivalents(ActiveClass equ) {

		equivalents.add(equ);

		for (ActiveClass e : equivalents) {

			e.equivalents.add(equ);
		}
	}

	private int minDefinitionSize() {

		int min = 0;

		for (Description d : definitions) {

			int s = d.getNestedNames().getSet().size();

			if (min == 0 || s < min) {

				min = s;
			}
		}

		return min;
	}
}
