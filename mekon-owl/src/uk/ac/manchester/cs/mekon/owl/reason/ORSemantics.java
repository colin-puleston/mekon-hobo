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

package uk.ac.manchester.cs.mekon.owl.reason;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.owl.*;

/**
 * Defines the open/closed-world semantics to be embodied by the OWL
 * constructs that will be created and used by the reasoning mechanisms.
 * Semantics are defined on a per-property basis, with the available
 * options being defined via the {@link ORSemanticWorld} enum. A
 * general default value will be defined, plus optionally a set of
 * "exception" properties. The default semantics will apply to all
 * properties other than the specifed exception properties, and any
 * super-properties of the exception properties.
 *
 * @author Colin Puleston
 */
public class ORSemantics {

	private ORSemanticWorld defaultWorld = ORSemanticWorld.OPEN;
	private Set<String> exceptionPropertyURIs = new HashSet<String>();

	private class PropertyWorldResolver {

		private OModel model;
		private IRI propertyIRI;

		PropertyWorldResolver(OModel model, IRI propertyIRI) {

			this.model = model;
			this.propertyIRI = propertyIRI;
		}

		ORSemanticWorld resolve() {

			return hasNonDefaultSemantics()
						? defaultWorld.getOpposite()
						: defaultWorld;
		}

		private boolean hasNonDefaultSemantics() {

			return exceptionProperty() || hasExceptionSuperProperty();
		}

		private boolean hasExceptionSuperProperty() {

			for (OWLProperty<?, ?> superProp : getSuperProperties()) {

				if (exceptionProperty(superProp.getIRI())) {

					return true;
				}
			}

			return false;
		}

		private boolean exceptionProperty() {

			return exceptionProperty(propertyIRI);
		}

		private boolean exceptionProperty(IRI iri) {

			return exceptionPropertyURIs.contains(iriAsString(iri));
		}

		private Set<? extends OWLProperty<?, ?>> getSuperProperties() {

			return dataProperty()
					? getSuperDataProperties()
					: getSuperObjectProperties();
		}

		private boolean dataProperty() {

			return model.getDataProperties().contains(propertyIRI);
		}

		private Set<OWLObjectProperty> getSuperObjectProperties() {

			return model.getInferredSupers(getObjectProperty(), false);
		}

		private Set<OWLDataProperty> getSuperDataProperties() {

			return model.getInferredSupers(getDataProperty(), false);
		}

		private OWLObjectProperty getObjectProperty() {

			return model.getObjectProperties().get(propertyIRI);
		}

		private OWLDataProperty getDataProperty() {

			return model.getDataProperties().get(propertyIRI);
		}

		private String iriAsString(IRI iri) {

			return iri.toURI().toASCIIString();
		}
	}

	/**
	 * Sets the default open/closed-world semantics. If not set will
	 * default to {@link ORSemanticWorld#OPEN}.
	 *
	 * @param defaultWorld Value of default semantics
	 */
	public void setDefaultWorld(ORSemanticWorld defaultWorld) {

		this.defaultWorld = defaultWorld;
	}

	/**
	 * Adds an exception property.
	 *
	 * @param exceptionPropertyURI URI of exception property to add
	 */
	public void addExceptionProperty(String exceptionPropertyURI) {

		exceptionPropertyURIs.add(exceptionPropertyURI);
	}

	ORSemanticWorld getWorld(OModel model, IRI propertyIRI) {

		return new PropertyWorldResolver(model, propertyIRI).resolve();
	}
}
