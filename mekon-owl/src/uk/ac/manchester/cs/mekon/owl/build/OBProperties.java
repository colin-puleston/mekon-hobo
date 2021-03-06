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

import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Represents the set of OWL properties that will be used in
 * generating slots in the Frames Model (FM).
 *
 * @author Colin Puleston
 */
public class OBProperties
				extends
					OBEntities
						<OWLProperty,
						OBPropertyGroup,
						OBPropertyAttributes> {

	private Set<Handler> handlers = new HashSet<Handler>();

	private abstract class Handler {

		Handler() {

			handlers.add(this);
		}

		boolean handles(IRI iri) {

			return top(iri) || handlesNonTop(iri);
		}

		abstract boolean handlesNonTop(IRI iri);

		abstract OWLProperty getTop();

		abstract OWLProperty get(IRI iri);

		abstract Set<? extends OWLProperty> getAllInModel();

		abstract Set<? extends OWLProperty> getSubs(OWLProperty property);

		abstract Set<? extends OWLProperty> extractAll(OWLClassExpression expression);

		private boolean top(IRI iri) {

			return getTop().getIRI().equals(iri);
		}
	}

	private class ObjectPropertyHandler extends Handler {

		boolean handlesNonTop(IRI iri) {

			return getModelOntology().containsObjectPropertyInSignature(iri);
		}

		OWLProperty getTop() {

			return getDataFactory().getOWLTopObjectProperty();
		}

		OWLProperty get(IRI iri) {

			return getDataFactory().getOWLObjectProperty(iri);
		}

		Set<? extends OWLProperty> getAllInModel() {

			return getModel().getObjectProperties().getAll();
		}

		Set<? extends OWLProperty> getSubs(OWLProperty property) {

			return getModel().getInferredSubs((OWLObjectProperty)property, true);
		}

		Set<? extends OWLProperty> extractAll(OWLClassExpression expression) {

			return OWLAPIVersion.getObjectPropertiesInSignature(expression);
		}
	}

	private class DataPropertyHandler extends Handler {

		boolean handlesNonTop(IRI iri) {

			return getModelOntology().containsDataPropertyInSignature(iri);
		}

		OWLProperty getTop() {

			return getDataFactory().getOWLTopDataProperty();
		}

		OWLProperty get(IRI iri) {

			return getDataFactory().getOWLDataProperty(iri);
		}

		Set<? extends OWLProperty> getAllInModel() {

			return getModel().getDataProperties().getAll();
		}

		Set<? extends OWLProperty> getSubs(OWLProperty property) {

			return getModel().getInferredSubs((OWLDataProperty)property, true);
		}

		Set<? extends OWLProperty> extractAll(OWLClassExpression expression) {

			return OWLAPIVersion.getDataPropertiesInSignature(expression);
		}
	}

	OBProperties(OModel model) {

		super(model);

		new ObjectPropertyHandler();
		new DataPropertyHandler();
	}

	void addGroupEntity(
			OBPropertyGroup group,
			OWLProperty property,
			EntityLocation location) {

		add(property, group.getAttributes());
	}

	OBPropertyAttributes createAttributes()  {

		return new OBPropertyAttributes();
	}

	String getTypeName() {

		return "property";
	}

	boolean validEntity(IRI iri) {

		return getHandlerOrNull(iri) != null;
	}

	OWLProperty get(IRI iri) {

		return getHandler(iri).get(iri);
	}

	Set<OWLProperty> getAllInModel() {

		Set<OWLProperty> all = new HashSet<OWLProperty>();

		for (Handler handler : handlers) {

			all.addAll(handler.getAllInModel());
		}

		return all;
	}

	Set<OWLProperty> getSubs(OWLProperty property) {

		Handler handler = getHandler(property.getIRI());

		return new HashSet<OWLProperty>(handler.getSubs(property));
	}

	Set<OWLProperty> extractAll(OWLClassExpression expression) {

		Set<OWLProperty> all = new HashSet<OWLProperty>();

		for (Handler handler : handlers) {

			all.addAll(handler.extractAll(expression));
		}

		return all;
	}

	private Handler getHandler(IRI iri) {

		Handler handler = getHandlerOrNull(iri);

		if (handler != null) {

			return handler;
		}

		throw new Error("IRI does not correspond to a property: " + iri);
	}

	private Handler getHandlerOrNull(IRI iri) {

		for (Handler handler : handlers) {

			if (handler.handles(iri)) {

				return handler;
			}
		}

		return null;
	}
}
