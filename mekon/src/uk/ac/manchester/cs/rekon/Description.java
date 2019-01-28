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

	private Name name;
	private Set<Expression> successors = new HashSet<Expression>();

	Description(Name name) {

		this(name, Collections.emptySet());
	}

	Description(Name name, Expression successor) {

		this(name, Collections.singleton(successor));
	}

	Description(Name name, Set<? extends Expression> successors) {

		this.name = name;
		this.successors.addAll(successors);
	}

	void addSuccessors(Set<? extends Expression> successors) {

		this.successors.addAll(successors);
	}

	NameExpression asNameExpression() {

		return this;
	}

	Description asDescription() {

		return this;
	}

	Name getName() {

		return name;
	}

	Set<Expression> getSuccessors() {

		return successors;
	}

	boolean structured() {

		return !successors.isEmpty();
	}

	Name getNameOrNull() {

		return name;
	}

	Set<? extends Expression> getSubExpressions() {

		return successors;
	}

	NameSet getCompulsoryNestedNames() {

		return getNestedNames();
	}

	boolean subsumesOther(Expression e) {

		Description d = e.asDescription();

		return d != null
				&& name.subsumes(d.name)
				&& possibleNestedSubsumption(d)
				&& successorSubsumptions(d);
	}

	boolean dependsOnAny(NameSet names) {

		return name.subsumedByAny(names) || getNestedNameSubsumers().containsAny(names);
	}

	void render(ExpressionRenderer r) {

		r.addLine(name.getEntityName());

		r = r.nextLevel();

		for (Expression s : successors) {

			s.render(r);
		}
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
}
