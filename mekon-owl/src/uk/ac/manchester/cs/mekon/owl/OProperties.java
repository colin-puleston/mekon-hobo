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

package uk.ac.manchester.cs.mekon.owl;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

abstract class OProperties
					<E extends OWLPropertyExpression<?, E>,
					P extends OWLProperty<?, E>>
					extends OEntities<P> {

	private AssertedLinks assertedSupers = new AssertedSupers();
	private AssertedLinks assertedSubs = new AssertedSubs();
	private Links inferredSupers;
	private Links inferredSubs;

	private abstract class Links {

		abstract Set<P> get(P property, boolean directOnly);
	}

	private abstract class AssertedLinks extends Links{

		Set<P> get(P property, boolean directOnly) {

			return directOnly ? getDirect(property) : getAll(property);
		}

		Set<P> getAll(P property) {

			Set<P> all = new HashSet<P>();

			collectAll(all, property);

			return all;
		}

		abstract Set<P> getDirect(P property);

		private void collectAll(Set<P> all, P current) {

			for (P next : getDirect(current)) {

				if (all.add(next)) {

					collectAll(all, next);
				}
			}
		}
	}

	private class AssertedSupers extends AssertedLinks {

		Set<P> getDirect(P property) {

			return getAssertedSuperProperties(property);
		}
	}

	private class AssertedSubs extends AssertedLinks {

		Set<P> getDirect(P property) {

			return getAssertedSubProperties(property);
		}
	}

	private abstract class InferredLinks extends Links {

		Links resolve() {

			return inferenceSupported() ? this : getSubstitute();
		}

		abstract Links getSubstitute();

		private boolean inferenceSupported() {

			try {

				if (tryTestInference() != null) {

					return true;
				}
			}
			catch (UnsupportedOperationException e) {
			}

			return false;
		}

		private Object tryTestInference() throws UnsupportedOperationException {

			return get(getTop(), true);
		}
	}

	private class InferredSupers extends InferredLinks {

		Links getSubstitute() {

			return assertedSupers;
		}

		Set<P> get(P property, boolean directOnly) {

			return getInferredSuperProperties(property, directOnly);
		}
	}

	private class InferredSubs extends InferredLinks {

		Links getSubstitute() {

			return assertedSubs;
		}

		Set<P> get(P property, boolean directOnly) {

			return getInferredSubProperties(property, directOnly);
		}
	}

	OProperties(OModel model) {

		super(model);

		inferredSupers = new InferredSupers().resolve();
		inferredSubs = new InferredSubs().resolve();
	}

	Set<P> getAssertedSupers(P property) {

		return assertedSupers.getDirect(property);
	}

	Set<P> getAssertedSubs(P property) {

		return assertedSubs.getDirect(property);
	}

	Set<P> getInferredSupers(P property, boolean directOnly) {

		return inferredSupers.get(property, directOnly);
	}

	Set<P> getInferredSubs(P property, boolean directOnly) {

		return inferredSubs.get(property, directOnly);
	}

	abstract Class<P> getPropertyClass();

	abstract Set<P> getAssertedSuperProperties(P property);

	abstract Set<P> getAssertedSubProperties(P property);

	abstract Set<P> getInferredSuperProperties(P property, boolean directOnly);

	abstract Set<P> getInferredSubProperties(P property, boolean directOnly);

	Set<P> normaliseExprs(NodeSet<E> exprs) {

		return normaliseExprs(exprs.getFlattened());
	}

	Set<P> normaliseExprs(Set<E> exprs) {

		return normalise(extractProperties(exprs));
	}

	private Set<P> extractProperties(Set<E> exprs) {

		Set<P> properties = new HashSet<P>();
		Class<P> propClass = getPropertyClass();

		for (E expr : exprs) {

			if (propClass.isAssignableFrom(expr.getClass())) {

				properties.add(propClass.cast(expr));
			}
		}

		return properties;
	}
}
