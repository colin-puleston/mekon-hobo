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

class OObjectProperties extends OEntities<OWLObjectProperty> {

	private AssertedLinks assertedSupers = new AssertedSupers();
	private AssertedLinks assertedSubs = new AssertedSubs();
	private Links inferredSupers;
	private Links inferredSubs;

	private abstract class Links {

		abstract Set<OWLObjectProperty> get(
										OWLObjectProperty property,
										boolean directOnly);
	}

	private abstract class AssertedLinks extends Links{

		Set<OWLObjectProperty> get(
								OWLObjectProperty property,
								boolean directOnly) {

			return directOnly ? getDirect(property) : getAll(property);
		}

		Set<OWLObjectProperty> getAll(OWLObjectProperty property) {

			Set<OWLObjectProperty> all = new HashSet<OWLObjectProperty>();

			collectAll(all, property);

			return all;
		}

		abstract Set<OWLObjectProperty> getDirect(OWLObjectProperty property);

		private void collectAll(Set<OWLObjectProperty> all, OWLObjectProperty current) {

			for (OWLObjectProperty next : getDirect(current)) {

				if (all.add(next)) {

					collectAll(all, next);
				}
			}
		}
	}

	private class AssertedSupers extends AssertedLinks {

		Set<OWLObjectProperty> getDirect(OWLObjectProperty property) {

			return normaliseExprs(property.getSuperProperties(getAllOntologies()));
		}
	}

	private class AssertedSubs extends AssertedLinks {

		Set<OWLObjectProperty> getDirect(OWLObjectProperty property) {

			return normaliseExprs(property.getSubProperties(getAllOntologies()));
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

		Set<OWLObjectProperty> get(OWLObjectProperty property, boolean directOnly) {

			return normaliseExprs(
						getReasoner()
							.getSuperObjectProperties(
								property,
								directOnly));
		}
	}

	private class InferredSubs extends InferredLinks {

		Links getSubstitute() {

			return assertedSubs;
		}

		Set<OWLObjectProperty> get(OWLObjectProperty property, boolean directOnly) {

			return normaliseExprs(
						getReasoner()
							.getSubObjectProperties(
								property,
								directOnly));
		}
	}

	OObjectProperties(OModel model) {

		super(model);

		inferredSupers = new InferredSupers().resolve();
		inferredSubs = new InferredSubs().resolve();
	}

	Set<OWLObjectProperty> getAssertedSupers(OWLObjectProperty property) {

		return assertedSupers.getDirect(property);
	}

	Set<OWLObjectProperty> getAssertedSubs(OWLObjectProperty property) {

		return assertedSubs.getDirect(property);
	}

	Set<OWLObjectProperty> getInferredSupers(
								OWLObjectProperty property,
								boolean directOnly) {

		return inferredSupers.get(property, directOnly);
	}

	Set<OWLObjectProperty> getInferredSubs(
								OWLObjectProperty property,
								boolean directOnly) {

		return inferredSubs.get(property, directOnly);
	}

	String getEntityTypeName() {

		return "object-property";
	}

	Set<OWLObjectProperty> findAll() {

		return getMainOntology().getObjectPropertiesInSignature(true);
	}

	OWLObjectProperty getTop() {

		return getDataFactory().getOWLTopObjectProperty();
	}

	OWLObjectProperty getBottom() {

		return getDataFactory().getOWLBottomObjectProperty();
	}

	private Set<OWLObjectProperty> normaliseExprs(NodeSet<OWLObjectPropertyExpression> exprs) {

		return normaliseExprs(exprs.getFlattened());
	}

	private Set<OWLObjectProperty> normaliseExprs(Set<OWLObjectPropertyExpression> exprs) {

		return normalise(extractProperties(exprs));
	}

	private Set<OWLObjectProperty> extractProperties(Set<OWLObjectPropertyExpression> exprs) {

		Set<OWLObjectProperty> properties = new HashSet<OWLObjectProperty>();

		for (OWLObjectPropertyExpression expr : exprs) {

			if (expr instanceof OWLObjectProperty) {

				properties.add((OWLObjectProperty)expr);
			}
		}

		return properties;
	}
}
