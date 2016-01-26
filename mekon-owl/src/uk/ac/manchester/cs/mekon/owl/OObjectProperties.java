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

class OObjectProperties
			extends
				OProperties<OWLObjectPropertyExpression, OWLObjectProperty> {

	OObjectProperties(OModel model) {

		super(model);
	}

	String getEntityTypeName() {

		return "object-property";
	}

	Set<OWLObjectProperty> findAll() {

		return getModelOntology().getObjectPropertiesInSignature(true);
	}

	OWLObjectProperty getTop() {

		return getDataFactory().getOWLTopObjectProperty();
	}

	OWLObjectProperty getBottom() {

		return getDataFactory().getOWLBottomObjectProperty();
	}

	Class<OWLObjectProperty> getPropertyClass() {

		return OWLObjectProperty.class;
	}

	Set<OWLObjectProperty> getAssertedSuperProperties(OWLObjectProperty property) {

		return normaliseExprs(property.getSuperProperties(getAllOntologies()));
	}

	Set<OWLObjectProperty> getAssertedSubProperties(OWLObjectProperty property) {

		return normaliseExprs(property.getSubProperties(getAllOntologies()));
	}

	Set<OWLObjectProperty> getInferredSuperProperties(
								OWLObjectProperty property,
								boolean directOnly) {

		return normaliseExprs(getReasoner().getSuperObjectProperties(property, directOnly));
	}

	Set<OWLObjectProperty> getInferredSubProperties(
								OWLObjectProperty property,
								boolean directOnly) {

		return normaliseExprs(getReasoner().getSubObjectProperties(property, directOnly));
	}
}
