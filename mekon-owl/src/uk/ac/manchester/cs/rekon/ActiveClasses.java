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

	private ActiveClass root = new ActiveClass();
	private SortedSet<ActiveClass> all = new TreeSet<ActiveClass>();

	private abstract class SubsumptionCrawler<S> {

		private Set<ActiveClass> visited = new HashSet<ActiveClass>();
		private Set<ActiveClass> subsumers = new HashSet<ActiveClass>();

		void crawl(S subject) {

			crawlFrom(root, subject);
		}

		abstract boolean sameDefinitions(ActiveClass c, S subject);

		abstract boolean subsumption(ActiveClass c, S subject);

		abstract boolean subsumption(S subject, ActiveClass c);

		abstract void processSubsumption(ActiveClass current, S subject, boolean equiv);

		boolean crawlFromSubs(ActiveClass current, S subject) {

			boolean subsumption = false;

			for (ActiveClass sub : current.getSubs()) {

				if (sub.hasDefinition()) {

					subsumption |= crawlFromSub(sub, subject);
				}
			}

			return subsumption;
		}

		private boolean crawlFrom(ActiveClass current, S subject) {

			boolean sameDefns = sameDefinitions(current, subject);

			if (sameDefns || subsumption(current, subject)) {

				boolean equiv = sameDefns || subsumption(subject, current);

				subsumers.add(current);
				processSubsumption(current, subject, equiv);

				return true;
			}

			return false;
		}

		private boolean crawlFromSub(ActiveClass sub, S subject) {

			return visited.add(sub) ? crawlFrom(sub, subject) : subsumers.contains(sub);
		}
	}

	private class Absorber extends SubsumptionCrawler<ActiveClass> {

		Absorber(ActiveClass defn) {

			crawl(defn);
		}

		boolean sameDefinitions(ActiveClass c1, ActiveClass c2) {

			return c1.sameDefinitions(c2);
		}

		boolean subsumption(ActiveClass c1, ActiveClass c2) {

			return c1.subsumes(c2);
		}

		void processSubsumption(ActiveClass current, ActiveClass subject, boolean equiv) {

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

		private void checkInsertNewSub(ActiveClass current, ActiveClass newSub) {

			for (ActiveClass sub : new HashSet<ActiveClass>(current.getSubs())) {

				if (newSub.subsumes(sub)) {

					current.removeSub(sub);
					newSub.addSub(sub);
				}
			}
		}
	}

	private abstract class DescriptionClassifier extends SubsumptionCrawler<Description> {

		final Set<ClassName> names = new HashSet<ClassName>();

		Set<ClassName> findAll(Description d) {

			crawl(d);

			return names;
		}

		boolean sameDefinitions(ActiveClass c, Description subject) {

			return c.hasDefinition(subject);
		}

		boolean subsumption(ActiveClass c, Description subject) {

			return c.subsumes(subject);
		}

		boolean subsumption(Description subject, ActiveClass c) {

			return c.subsumedBy(subject);
		}
	}

	private class EquivalentsFinder extends DescriptionClassifier {

		void processSubsumption(ActiveClass current, Description subject, boolean equiv) {

			if (equiv) {

				names.add(current.getName());

				for (ActiveClass equ : current.getEquivalents()) {

					names.add(equ.getName());
				}
			}
			else {

				crawlFromSubs(current, subject);
			}
		}
	}

	private class SupersFinder extends DescriptionClassifier {

		private boolean directOnly;

		SupersFinder(boolean directOnly) {

			this.directOnly = directOnly;
		}

		void processSubsumption(ActiveClass current, Description subject, boolean equiv) {

			if (!equiv) {

				if (!crawlFromSubs(current, subject) || !directOnly) {

					if (!current.isRoot()) {

						names.add(current.getName());
					}
				}
			}
		}
	}

	void addFor(ClassName name, Set<Description> defns, Set<Description> sups) {

		if (defns.isEmpty()) {

			if (!sups.isEmpty()) {

				add(name, null, new Description(name, sups));
			}
		}
		else {

			for (Description defn : defns) {

				add(name, defn, resolveProfile(defn, sups));
			}
		}
	}

	void absorbIntoHierarchy(ActiveClass c) {

		new Absorber(c);
	}

	Set<ActiveClass> getAll() {

		return all;
	}

	Set<ClassName> getEquivalents(Description desc) {

		return new EquivalentsFinder().findAll(desc);
	}

	Set<ClassName> getSupers(Description desc, boolean directOnly) {

		return new SupersFinder(directOnly).findAll(desc);
	}

	private Description resolveProfile(Description defn, Set<Description> sups) {

		return sups.isEmpty() ? defn : defn.extend(sups);
	}

	private void add(ClassName name, Description defn, Description prof) {

		all.add(new ActiveClass(name, defn, prof));
	}
}
