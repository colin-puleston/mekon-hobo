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
class ClassDefinition implements Comparable<ClassDefinition> {

	private ClassName name;
	private Description definition;

	private Set<ClassDefinition> equivalents = new HashSet<ClassDefinition>();

	private Set<ClassDefinition> supers = new HashSet<ClassDefinition>();
	private Set<ClassDefinition> subs = new HashSet<ClassDefinition>();

	private abstract class SubsumptionCrawler<S> {

		private Set<ClassDefinition> visited = new HashSet<ClassDefinition>();
		private Set<ClassDefinition> subsumers = new HashSet<ClassDefinition>();

		void crawl(S subject) {

			crawlFrom(ClassDefinition.this, subject);
		}

		abstract Description toDescription(S subject);

		abstract boolean subsumption(ClassDefinition current, S subject);

		abstract void processSubsumption(ClassDefinition current, S subject, boolean equiv);

		boolean crawlFromSubs(ClassDefinition current, S subject) {

			boolean subsumption = false;

			for (ClassDefinition sub : current.subs) {

				subsumption |= crawlFromSub(sub, subject);
			}

			return subsumption;
		}

		private boolean crawlFrom(ClassDefinition current, S subject) {

			boolean equiv = equivalence(current, subject);

			if (equiv || subsumption(current, subject)) {

				subsumers.add(current);
				processSubsumption(current, subject, equiv);

				return true;
			}

			return false;
		}

		private boolean equivalence(ClassDefinition current, S subject) {

			return toDescription(subject) == current.definition;
		}

		private boolean crawlFromSub(ClassDefinition sub, S subject) {

			return visited.add(sub) ? crawlFrom(sub, subject) : subsumers.contains(sub);
		}
	}

	private class Absorber extends SubsumptionCrawler<ClassDefinition> {

		Absorber(ClassDefinition defn) {

			crawl(defn);
		}

		Description toDescription(ClassDefinition subject) {

			return subject.definition;
		}

		boolean subsumption(ClassDefinition current, ClassDefinition subject) {

			return current.subsumes(subject);
		}

		void processSubsumption(ClassDefinition current, ClassDefinition subject, boolean equiv) {

			if (equiv) {

				current.addEquivalent(subject);
			}
			else {

				if (!crawlFromSubs(current, subject)) {

					checkInsertNewSub(current, subject);
					current.addSub(subject);
				}
			}
		}

		private void checkInsertNewSub(ClassDefinition current, ClassDefinition newSub) {

			for (ClassDefinition sub : new HashSet<ClassDefinition>(current.subs)) {

				if (newSub.subsumes(sub)) {

					current.removeSub(sub);
					newSub.addSub(sub);
				}
			}
		}
	}

	private abstract class DescriptionChecker extends SubsumptionCrawler<Description> {

		Description toDescription(Description subject) {

			return subject;
		}

		boolean subsumption(ClassDefinition current, Description subject) {

			return current.subsumes(subject);
		}
	}

	private class SupersInferrer extends DescriptionChecker {

		private NameSet updateds;

		SupersInferrer(Description desc, NameSet updateds) {

			this.updateds = updateds;

			crawl(desc);
		}

		void processSubsumption(ClassDefinition current, Description subject, boolean equiv) {

			ClassName sn = (ClassName)subject.getName();

			if (sn != current.name) {

				if (equiv || (!crawlFromSubs(current, subject) && !current.isRoot())) {

					sn.addSuper(current.name);
					updateds.add(sn);
				}
			}
		}
	}

	private abstract class NamesFinder extends DescriptionChecker {

		final Set<ClassName> names = new HashSet<ClassName>();

		Set<ClassName> findAll(Description desc) {

			crawl(desc);

			return names;
		}
	}

	private class EquivalentsFinder extends NamesFinder {

		void processSubsumption(ClassDefinition current, Description subject, boolean equiv) {

			if (equiv) {

				names.add(current.name);

				for (ClassDefinition equ : current.equivalents) {

					names.add(equ.name);
				}
			}
			else {

				crawlFromSubs(current, subject);
			}
		}
	}

	private class SupersFinder extends NamesFinder {

		private boolean directOnly;

		SupersFinder(boolean directOnly) {

			this.directOnly = directOnly;
		}

		void processSubsumption(ClassDefinition current, Description subject, boolean equiv) {

			if (!equiv) {

				if (!crawlFromSubs(current, subject) || !directOnly) {

					if (!current.isRoot()) {

						names.add(current.name);
					}
				}
			}
		}
	}

	public int compareTo(ClassDefinition d) {

		int c = nestedNameCount() - d.nestedNameCount();

		return c == 0 ? 1 : c;
	}

	ClassDefinition() {

		this(null, null);
	}

	ClassDefinition(ClassName name, Description definition) {

		this.name = name;
		this.definition = definition;
	}

	void absorb(ClassDefinition defn) {

		new Absorber(defn);
	}

	void inferSubsumptionsFromClassDefinition(NameSet updateds) {

		if (equivalents.isEmpty() && supers.isEmpty()) {

			return;
		}

		updateds.add(name);

		for (ClassDefinition equ : equivalents) {

			name.addEquivalent(equ.name);
			updateds.add(equ.name);
		}

		for (ClassDefinition sup : supers) {

			name.addSuper(sup.name);
		}
	}

	void inferSupersFromClassDescription(Description desc, NameSet updateds) {

		new SupersInferrer(desc, updateds);
	}

	boolean removeReclassifiable(NameSet updateds) {

		if (definition.dependsOnAny(updateds)) {

			remove();

			return true;
		}

		return false;
	}

	Description getDefinition() {

		return definition;
	}

	Set<ClassName> getEquivalents(Description desc) {

		return new EquivalentsFinder().findAll(desc);
	}

	Set<ClassName> getSupers(Description desc, boolean directOnly) {

		return new SupersFinder(directOnly).findAll(desc);
	}

	void printHierarchy(String tabs) {

		System.out.println(tabs + name);

		for (ClassDefinition sub : subs) {

			sub.printHierarchy(tabs + "  ");
		}
	}

	private void remove() {

		removeEquivalents();
		removeFromHierarchy();
	}

	private void removeEquivalents() {

		for (ClassDefinition equ : equivalents) {

			equ.equivalents.remove(this);
		}

		equivalents.clear();
	}

	private void removeFromHierarchy() {

		for (ClassDefinition sup : supers) {

			for (ClassDefinition sub : subs) {

				sup.addSub(sub);
			}
		}

		supers.clear();
		subs.clear();
	}

	private void addEquivalent(ClassDefinition equ) {

		addToEquivalents(equ);
		equ.addToEquivalents(this);
	}

	private void addToEquivalents(ClassDefinition equ) {

		equivalents.add(equ);

		for (ClassDefinition e : equivalents) {

			e.equivalents.add(equ);
		}
	}

	private void addSub(ClassDefinition sub) {

		subs.add(sub);

		if (!isRoot()) {

			sub.supers.add(this);
		}
	}

	private void removeSub(ClassDefinition sub) {

		subs.remove(sub);
		sub.supers.remove(this);
	}

	private boolean subsumes(ClassDefinition defn) {

		return subsumes(defn.definition);
	}

	private boolean subsumes(Description desc) {

		return isRoot() || definition.subsumes(desc);
	}

	private boolean isRoot() {

		return name == null;
	}

	private int nestedNameCount() {

		return definition.getNestedNames().getSet().size();
	}
}
