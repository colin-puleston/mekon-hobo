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
abstract class NameExpression extends Expression {

	private NestedNames nestedNames = new NestedNames();
	private NestedNameSubsumers nestedNameSubsumers = new NestedNameSubsumers();

	private abstract class NameReferences {

		NameSet refs = null;

		NameSet get() {

			if (refs == null) {

				refs = new NameSet();

				initialise();
			}

			return refs;
		}

		void reset() {

			refs = null;
		}

		abstract void addExtraNameRefs(Name name);

		abstract void addNestedRefs(NameExpression s);

		private void initialise() {

			for (Expression s : getSubExpressions()) {

				NameExpression ns = s.asNameExpression();

				if (ns != null) {

					addSubExpressionRefs(ns);
				}
			}
		}

		private void addSubExpressionRefs(NameExpression s) {

			Name name = s.getNameOrNull();

			if (name != null) {

				refs.add(name);
				addExtraNameRefs(name);
			}

			addNestedRefs(s);
		}
	}

	private class NestedNames extends NameReferences {

		void addExtraNameRefs(Name name) {
		}

		void addNestedRefs(NameExpression s) {

			refs.addAll(s.getNestedNames());
		}
	}

	private class NestedNameSubsumers extends NameReferences {

		private Set<Name> activeAncestors;

		void setActiveAncestors(Set<Name> activeAncestors) {

			this.activeAncestors = activeAncestors;
		}

		void addExtraNameRefs(Name name) {

			for (Name a : name.getAncestors()) {

				if (activeAncestors == null || activeAncestors.contains(a)) {

					refs.add(a);
				}
			}
		}

		void addNestedRefs(NameExpression s) {

			refs.addAll(s.getNestedNameSubsumers());
		}
	}

	void setActiveAncestors(Set<Name> activeAncestors) {

		nestedNameSubsumers.setActiveAncestors(activeAncestors);
	}

	void resetNameReferences() {

		nestedNames.reset();
		nestedNameSubsumers.reset();

		for (Expression s : getSubExpressions()) {

			NameExpression ns = s.asNameExpression();

			if (ns != null) {

				ns.resetNameReferences();
			}
		}
	}

	boolean subsumesAllNestedNames(NameExpression e) {

		return e.nestedNameSubsumers.get().containsAll(nestedNames.get());
	}

	NameSet getNestedNames() {

		return nestedNames.get();
	}

	NameSet getNestedNameSubsumers() {

		return nestedNameSubsumers.get();
	}

 	abstract Name getNameOrNull();

	abstract Set<? extends Expression> getSubExpressions();
}
