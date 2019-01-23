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

	private ReferencedNames referencedNames = new ReferencedNames();
	private ReferencedNameSubsumers referencedNameSubsumers = new ReferencedNameSubsumers();

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

		abstract void addSubExpressionRefs(NameExpression ne);

		private void initialise() {

			Name name = getNameOrNull();

			if (name != null) {

				refs.add(name);
				addExtraNameRefs(name);
			}

			for (Expression e : getSubExpressions()) {

				NameExpression ne = e.asNameExpression();

				if (ne != null) {

					addSubExpressionRefs(ne);
				}
			}
		}
	}

	private class ReferencedNames extends NameReferences {

		void addExtraNameRefs(Name name) {
		}

		void addSubExpressionRefs(NameExpression ne) {

			refs.addAll(ne.getReferencedNames());
		}
	}

	private class ReferencedNameSubsumers extends NameReferences {

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

		void addSubExpressionRefs(NameExpression ne) {

			refs.addAll(ne.getReferencedNameSubsumers());
		}
	}

	void setActiveAncestors(Set<Name> activeAncestors) {

		referencedNameSubsumers.setActiveAncestors(activeAncestors);
	}

	void resetNameReferences() {

		referencedNames.reset();
		referencedNameSubsumers.reset();

		for (Expression e : getSubExpressions()) {

			NameExpression ne = e.asNameExpression();

			if (ne != null) {

				ne.resetNameReferences();
			}
		}
	}

	boolean possiblySubsumes(NameExpression e) {

		return e.referencedNameSubsumers.get().containsAll(referencedNames.get());
	}

	NameSet getReferencedNames() {

		return referencedNames.get();
	}

	NameSet getReferencedNameSubsumers() {

		return referencedNameSubsumers.get();
	}

 	abstract Name getNameOrNull();

	abstract Set<? extends Expression> getSubExpressions();
}
