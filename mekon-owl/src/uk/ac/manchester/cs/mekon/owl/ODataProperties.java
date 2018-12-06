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

import uk.ac.manchester.cs.mekon.owl.util.*;

class ODataProperties
			extends
				OProperties<OWLDataPropertyExpression, OWLDataProperty> {

	public boolean contains(IRI iri) {

		return getModelOntology().containsDataPropertyInSignature(iri);
	}

	ODataProperties(OModel model) {

		super(model);
	}

	String getEntityTypeName() {

		return "data-property";
	}

	OWLDataProperty getTop() {

		return getDataFactory().getOWLTopDataProperty();
	}

	OWLDataProperty getBottom() {

		return getDataFactory().getOWLBottomDataProperty();
	}

	OWLDataProperty getContained(IRI iri) {

		return getDataFactory().getOWLDataProperty(iri);
	}

	Set<OWLDataProperty> getAllPreNormalise() {

		return OWLAPIVersion.getDataPropertiesInSignature(getModelOntology());
	}

	Class<OWLDataProperty> getPropertyClass() {

		return OWLDataProperty.class;
	}

	Set<OWLDataProperty> getAssertedSuperProperties(OWLDataProperty property) {

		return normaliseExprs(
					OWLAPIVersion.getSuperProperties(
						property,
						getAllOntologies()));
	}

	Set<OWLDataProperty> getAssertedSubProperties(OWLDataProperty property) {

		return normaliseExprs(
					OWLAPIVersion.getSubProperties(
							property,
							getAllOntologies()));
	}

	Set<OWLDataProperty> getInferredSuperProperties(
							OWLDataProperty property,
							boolean directOnly) {

		return normalise(getReasoner().getSuperDataProperties(property, directOnly));
	}

	Set<OWLDataProperty> getInferredSubProperties(
							OWLDataProperty property,
							boolean directOnly) {

		return normalise(getReasoner().getSubDataProperties(property, directOnly));
	}
}
