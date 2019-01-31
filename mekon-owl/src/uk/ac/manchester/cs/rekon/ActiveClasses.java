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
class ActiveClasses {

	private SortedSet<ActiveClass> all = new TreeSet<ActiveClass>();
	private Set<ActiveClass> definitions = new HashSet<ActiveClass>();

	private ActiveClass classificationRoot = new ActiveClass();

	private EquivalentsFinder equivalentsFinder = new EquivalentsFinder();
	private SupersFinder supersFinder = new SupersFinder();
	private SubsFinder subsFinder = new SubsFinder();

	private class ClassClassifier {

		private Set<ActiveClass> visited = new HashSet<ActiveClass>();
		private Set<ActiveClass> subsumers = new HashSet<ActiveClass>();

		ClassClassifier(ActiveClass subject) {

			crawlFrom(classificationRoot, subject);
		}

		private boolean crawlFrom(ActiveClass current, ActiveClass subject) {

			boolean defnMatch = current.definitionMatch(subject);

			if (defnMatch || current.subsumes(subject)) {

				subsumers.add(current);

				if (defnMatch || subject.subsumes(current)) {

					current.addEquivalent(subject);
				}
				else {

					if (checkAsSub(current, subject)) {

						current.addSub(subject);
					}
				}

				return true;
			}

			return false;
		}

		private boolean crawlFromSubs(ActiveClass current, ActiveClass subject) {

			boolean subsumption = false;

			for (ActiveClass sub : current.getSubs()) {

				if (sub.hasDefinitions()) {

					subsumption |= crawlFromSub(sub, subject);
				}
			}

			return subsumption;
		}

		private boolean crawlFromSub(ActiveClass sub, ActiveClass subject) {

			return visited.add(sub) ? crawlFrom(sub, subject) : subsumers.contains(sub);
		}

		private boolean checkAsSub(ActiveClass current, ActiveClass subject) {

			if (subject.hasDefinitions()) {

				if (crawlFromSubs(current, subject)) {

					return false;
				}

				checkInsertNewSub(current, subject);

				return true;
			}

			return current != classificationRoot;
		}

		private void checkInsertNewSub(ActiveClass current, ActiveClass newSub) {

			for (ActiveClass sub : new HashSet<ActiveClass>(current.getSubs())) {

				if (newSub.subsumes(sub)) {

					current.removeSub(sub);
					newSub.addSub(sub);
				}
			}
		}
	}

	private abstract class DescriptionClassifier {

		Set<ClassName> findFor(Description desc, boolean purgeAncestors) {

			Set<ClassName> cs = new HashSet<ClassName>();

			for (ActiveClass c : getCandidateClasses()) {

				if (selectClass(desc, c)) {

					cs.add(c.getName());
				}
			}

			if (purgeAncestors) {

				purgeAncestors(cs);
			}

			return cs;
		}

		abstract Set<ActiveClass> getCandidateClasses();

		abstract boolean selectClass(Description desc, ActiveClass c);

		private void purgeAncestors(Set<ClassName> cs) {

			for (ClassName c : new HashSet<ClassName>(cs)) {

				cs.removeAll(c.getAncestors());
			}
		}
	}

	private class EquivalentsFinder extends DescriptionClassifier {

		Set<ActiveClass> getCandidateClasses() {

			return definitions;
		}

		boolean selectClass(Description desc, ActiveClass c) {

			return c.equivalentTo(desc);
		}
	}

	private class SupersFinder extends DescriptionClassifier {

		Set<ActiveClass> getCandidateClasses() {

			return definitions;
		}

		boolean selectClass(Description desc, ActiveClass c) {

			return c.subsumes(desc);
		}
	}

	private class SubsFinder extends DescriptionClassifier {

		Set<ActiveClass> getCandidateClasses() {

			return all;
		}

		boolean selectClass(Description desc, ActiveClass c) {

			return c.subsumedBy(desc);
		}
	}

	void addFor(ClassName name, Set<Description> defns, Set<Description> sups) {

		if (defns.isEmpty()) {

			if (!sups.isEmpty()) {

				add(name, new Description(name, sups));
			}
		}
		else {

			addDefinition(name, defns, sups);
		}
	}

	void classify(ActiveClass c) {

		new ClassClassifier(c);
	}

	Set<ActiveClass> getAll() {

		return all;
	}

	Set<ClassName> getEquivalents(Description desc) {

		return equivalentsFinder.findFor(desc, false);
	}

	Set<ClassName> getSupers(Description desc, boolean directOnly) {

		return supersFinder.findFor(desc, directOnly);
	}

	Set<ClassName> getSubs(Description desc, boolean directOnly) {

		return subsFinder.findFor(desc, directOnly);
	}

	private void addDefinition(ClassName name, Set<Description> defns, Set<Description> sups) {

		ActiveClass c = add(name, resolveProfile(defns, sups));

		c.addDefinitions(defns);
		definitions.add(c);
	}

	private ActiveClass add(ClassName name, Description prof) {

		ActiveClass c = new ActiveClass(name, prof);

		all.add(c);

		return c;
	}

	private Description resolveProfile(Set<Description> defns, Set<Description> sups) {

		Description combo = null;

		for (Description d : defns) {

			combo = combo == null ? d : combo.combineWith(d);
		}

		return sups.isEmpty() ? combo : combo.extend(sups);
	}
}
