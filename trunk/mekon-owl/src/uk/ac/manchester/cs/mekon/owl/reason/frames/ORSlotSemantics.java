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

package uk.ac.manchester.cs.mekon.owl.reason.frames;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.owl.*;

/**
 * Defines the semantics for all slots that the reasoning operations
 * will handle. Options for a specific slot are {@link
 * ORSemantics#OPEN_WORLD} and {@link ORSemantics#CLOSED_WORLD}. The
 * semantics are specified via a general default value plus a set of
 * exception properties. The default semantics will apply to all slots
 * other than those whose associated property, or any of its
 * super-properties, is specified as an exception property. For slots
 * in this latter category the alternative option will apply.
 *
 * @author Colin Puleston
 */
public class ORSlotSemantics {

	private OModel model;
	private ORSemantics defaultSemantics = ORSemantics.OPEN_WORLD;
	private Set<String> exceptionPropertyURIs = new HashSet<String>();

	/**
	 * Constructor.
	 *
	 * @param model Model to which semantics are to apply
	 */
	public ORSlotSemantics(OModel model) {

		this.model = model;
	}

	/**
	 * Sets the default semantics. If not set will default to
	 * {@link ORSemantics#OPEN_WORLD}.
	 *
	 * @param defaultSemantics Value of default semantics
	 */
	public void setDefaultSemantics(ORSemantics defaultSemantics) {

		this.defaultSemantics = defaultSemantics;
	}

	/**
	 * Adds an exception property.
	 *
	 * @param exceptionPropertyURI URI of exception property to add
	 */
	public void addExceptionProperty(String exceptionPropertyURI) {

		exceptionPropertyURIs.add(exceptionPropertyURI);
	}

	/**
	 * Removes an exception property.
	 *
	 * @param exceptionPropertyURI URI of exception property to remove
	 */
	public void removeExceptionProperty(String exceptionPropertyURI) {

		exceptionPropertyURIs.remove(exceptionPropertyURI);
	}

	/**
	 * Removes all exception properties.
	 */
	public void clearExceptionProperties() {

		exceptionPropertyURIs.clear();
	}

	/**
	 * Provides the semantics for the specified property.
	 *
	 * @param propertyIRI URI of property for which semantics are
	 * required
	 */
	public ORSemantics getSemantics(IRI propertyIRI) {

		return hasNonDefaultSemantics(propertyIRI)
					? defaultSemantics.getOpposite()
					: defaultSemantics;
	}

	private boolean hasNonDefaultSemantics(IRI propertyIRI) {

		return isExceptionPropertyIRI(propertyIRI)
				|| hasExceptionSuperProperty(propertyIRI);
	}

	private boolean hasExceptionSuperProperty(IRI propertyIRI) {

		for (OWLProperty superProp : getSuperProperties(propertyIRI)) {

			if (isExceptionPropertyIRI(superProp.getIRI())) {

				return true;
			}
		}

		return false;
	}

	private boolean isExceptionPropertyIRI(IRI propertyIRI) {

		return exceptionPropertyURIs.contains(propertyIRI.toURI().toASCIIString());
	}

	private Set<? extends OWLProperty> getSuperProperties(IRI propertyIRI) {

		return dataProperty(propertyIRI)
				? getSuperDataProperties(propertyIRI)
				: getSuperObjectProperties(propertyIRI);
	}

	private boolean dataProperty(IRI iri) {

		return model.getDataProperties().contains(iri);
	}

	private Set<OWLObjectProperty> getSuperObjectProperties(IRI propertyIRI) {

		return model.getInferredSupers(getObjectProperty(propertyIRI), false);
	}

	private Set<OWLDataProperty> getSuperDataProperties(IRI propertyIRI) {

		return model.getInferredSupers(getDataProperty(propertyIRI), false);
	}

	private OWLObjectProperty getObjectProperty(IRI iri) {

		return model.getObjectProperties().get(iri);
	}

	private OWLDataProperty getDataProperty(IRI iri) {

		return model.getDataProperties().get(iri);
	}
}
