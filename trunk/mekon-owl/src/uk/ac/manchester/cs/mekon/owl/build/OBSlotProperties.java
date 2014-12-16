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

import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class OBSlotProperties {

	private OModel model;
	private OBProperties properties;
	private OBEntityLabels labels;

	private Map<OWLObjectProperty, OBSlotProperty> slotProperties
					= new HashMap<OWLObjectProperty, OBSlotProperty>();

	OBSlotProperties(
		OModel model,
		OBProperties properties,
		OBEntityLabels labels) {

		this.model = model;
		this.properties = properties;
		this.labels = labels;
	}

	void createAll() {

		for (OWLObjectProperty property : properties.getAll()) {

			createSlotProperty(property);
		}

		for (OWLObjectProperty property : properties.getAll()) {

			addSubsIfMirroredProperty(property);
		}
	}

	OBSlotProperty get(OWLObjectProperty property) {

		return slotProperties.get(property);
	}

	Collection<OBSlotProperty> getAll() {

		return slotProperties.values();
	}

	private void createSlotProperty(OWLObjectProperty property) {

		String label = labels.getLabel(property);
		boolean mirror = properties.mirrorAsFrame(property);
		OBSlotProperty slotProperty = new OBSlotProperty(property, label);

		if (properties.mirrorAsFrame(property)) {

			slotProperty.setMirrorAsFrame();
		}

		if (properties.abstractAssertable(property)) {

			slotProperty.setAbstractAssertable();
		}

		slotProperties.put(property, slotProperty);
	}

	private void addSubsIfMirroredProperty(OWLObjectProperty property) {

		OBSlotProperty slotProperty = slotProperties.get(property);

		if (slotProperty.mirrorAsFrame()) {

			addSubs(property, slotProperty);
		}
	}

	private void addSubs(
					OWLObjectProperty property,
					OBSlotProperty slotProperty) {

		for (OWLObjectProperty sub : model.getInferredSubs(property, true)) {

			slotProperty.addSubProperty(slotProperties.get(sub));
		}
	}

	private OWLObjectProperty getProperty(IRI iri) {

		return model.getDataFactory().getOWLObjectProperty(iri);
	}
}
