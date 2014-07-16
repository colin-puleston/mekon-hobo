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

class OObjectPropertyLinks {

	private OModel model;
	private OEntities<OWLObjectProperty> properties;

	private AssertedSupers assertedSupers = new AssertedSupers();
	private AssertedSubs assertedSubs = new AssertedSubs();

	private abstract class AssertedLinks {

		Set<OWLObjectProperty> getLinked(OWLObjectProperty property, boolean directOnly) {

			return directOnly ? getAllNext(property) : getAll(property);
		}

		abstract Set<OWLObjectProperty> getAllNext(OWLObjectProperty property);

		private Set<OWLObjectProperty> getAll(OWLObjectProperty property) {

			Set<OWLObjectProperty> all = new HashSet<OWLObjectProperty>();

			collectAll(all, property);

			return all;
		}

		private void collectAll(Set<OWLObjectProperty> all, OWLObjectProperty current) {

			for (OWLObjectProperty next : getAllNext(current)) {

				if (all.add(next)) {

					collectAll(all, next);
				}
			}
		}
	}

	private class AssertedSupers extends AssertedLinks {

		Set<OWLObjectProperty> getAllNext(OWLObjectProperty property) {

			return normalise(property.getSuperProperties(getAllOntologies()));
		}
	}

	private class AssertedSubs extends AssertedLinks {

		Set<OWLObjectProperty> getAllNext(OWLObjectProperty property) {

			return normalise(property.getSubProperties(getAllOntologies()));
		}
	}

	OObjectPropertyLinks(OModel model, OEntities<OWLObjectProperty> properties) {

		this.model = model;
		this.properties = properties;
	}

	OEntities<OWLObjectProperty> getAll() {

		return properties;
	}

	Set<OWLObjectProperty> getAssertedSupers(OWLObjectProperty property, boolean directOnly) {

		return assertedSupers.getLinked(property, directOnly);
	}

	Set<OWLObjectProperty> getAssertedSubs(OWLObjectProperty property, boolean directOnly) {

		return assertedSubs.getLinked(property, directOnly);
	}

	Set<OWLObjectProperty> getInferredSupers(
								OWLObjectProperty property,
								boolean directOnly) {

		return normalise(getReasoner().getSuperObjectProperties(property, directOnly));
	}

	Set<OWLObjectProperty> getInferredSubs(
								OWLObjectProperty property,
								boolean directOnly) {

		return normalise(getReasoner().getSubObjectProperties(property, directOnly));
	}

	private Set<OWLObjectProperty> normalise(NodeSet<OWLObjectPropertyExpression> exprs) {

		return normalise(exprs.getFlattened());
	}

	private Set<OWLObjectProperty> normalise(Set<OWLObjectPropertyExpression> exprs) {

		return model.normaliseObjectProperties(extractProperties(exprs));
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

	private Set<OWLOntology> getAllOntologies() {

		return model.getAllOntologies();
	}

	private OWLReasoner getReasoner() {

		return model.getReasoner();
	}
}
