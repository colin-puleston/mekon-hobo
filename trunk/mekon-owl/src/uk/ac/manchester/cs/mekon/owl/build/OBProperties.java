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

package uk.ac.manchester.cs.mekon.owl.build;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.owl.*;

/**
 * Represents the set of OWL properties that will be used in
 * generating slots in the Frames Model (FM).
 *
 * @author Colin Puleston
 */
public class OBProperties
				extends
					OBEntities
						<OWLObjectProperty,
						OBPropertyInclusions,
						OBPropertyAttributes> {

	OBProperties(OModel model) {

		super(model);
	}

	void addGroupEntity(
			OBPropertyInclusions group,
			OWLObjectProperty property,
			boolean isRoot) {

		add(property, group.getAttributes());
	}

	OBPropertyAttributes createAttributes()  {

		return new OBPropertyAttributes();
	}

	String getTypeName() {

		return "property";
	}

	boolean isValidEntity(IRI iri) {

		return getMainOntology().containsObjectPropertyInSignature(iri, true);
	}

	OWLObjectProperty get(IRI iri) {

		return getDataFactory().getOWLObjectProperty(iri);
	}

	Set<OWLObjectProperty> getAllInModel() {

		return getModel().getObjectProperties().getAll();
	}

	Set<OWLObjectProperty> getDescendants(OWLObjectProperty property) {

		return getModel().getInferredSubs(property, false);
	}

	Set<OWLObjectProperty> extractAll(OWLClassExpression expression) {

		return expression.getObjectPropertiesInSignature();
	}
}
