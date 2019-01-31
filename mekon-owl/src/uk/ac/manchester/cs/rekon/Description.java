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
class Description extends NameExpression {

	private Set<Name> names = new HashSet<Name>();
	private Set<Expression> successors = new HashSet<Expression>();

	Description(Name name) {

		this(Collections.singleton(name));
	}

	Description(Name name, Expression successor) {

		this(Collections.singleton(name), successor);
	}

	Description(Name name, Set<? extends Expression> successors) {

		this(Collections.singleton(name), successors);
	}

	Description(Collection<Name> names) {

		this(names, Collections.emptySet());
	}

	Description(Collection<Name> names, Expression successor) {

		this(names, Collections.singleton(successor));
	}

	Description(Collection<Name> names, Set<? extends Expression> successors) {

		this.names.addAll(names);
		this.successors.addAll(successors);
	}

	Description combineWith(Description other) {

		Description e = new Description(names, successors);

		e.names.addAll(other.names);
		e.successors.addAll(other.successors);

		return e;
	}

	Description extend(Set<? extends Expression> extraSuccessors) {

		Description e = new Description(names, successors);

		e.successors.addAll(extraSuccessors);

		return e;
	}

	NameExpression asNameExpression() {

		return this;
	}

	Description asDescription() {

		return this;
	}

	Set<Name> getNames() {

		return names;
	}

	Set<Expression> getSuccessors() {

		return successors;
	}

	boolean structured() {

		return !successors.isEmpty();
	}

	Set<Name> getDirectNames() {

		return names;
	}

	NameSet getCompulsoryNestedNames() {

		return getNestedNames();
	}

	Set<? extends Expression> getSubExpressions() {

		return successors;
	}

	boolean subsumesOther(Expression e) {

		Description d = e.asDescription();

		return d != null
				&& nameSubsumptions(d)
				&& possibleNestedSubsumption(d)
				&& successorSubsumptions(d);
	}

	void render(ExpressionRenderer r) {

		r.addLine(namesToString());

		r = r.nextLevel();

		for (Expression s : successors) {

			s.render(r);
		}
	}

	private boolean nameSubsumptions(Description d) {

		for (Name n : names) {

			if (!nameSubsumption(d, n)) {

				return false;
			}
		}

		return true;
	}

	private boolean nameSubsumption(Description d, Name n) {

		for (Name dn : d.names) {

			if (n.subsumes(dn)) {

				return true;
			}
		}

		return false;
	}

	private boolean successorSubsumptions(Description d) {

		for (Expression s : successors) {

			if (!successorSubsumption(d, s)) {

				return false;
			}
		}

		return true;
	}

	private boolean successorSubsumption(Description d, Expression s) {

		for (Expression ds : d.successors) {

			if (s.subsumes(ds)) {

				return true;
			}
		}

		return false;
	}

	private String namesToString() {

		if (names.isEmpty()) {

			return ClassName.THING.toString();
		}

		if (names.size() == 1) {

			return names.iterator().next().getEntityName();
		}

		List<String> l = new ArrayList<String>();

		for (Name n : names) {

			l.add(n.getEntityName());
		}

		return l.toString();
	}
}
