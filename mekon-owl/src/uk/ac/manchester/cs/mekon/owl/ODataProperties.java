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

class ODataProperties {

	private OModel model;
	private OEntities<OWLDataProperty> properties;

	ODataProperties(OModel model) {

		this.model = model;

		properties = findAll();
	}

	OEntities<OWLDataProperty> getAll() {

		return properties;
	}

	private OEntities<OWLDataProperty> findAll() {

		return new OEntities<OWLDataProperty>(
						"data-property",
						normalise(
							getMainOntology()
								.getDataPropertiesInSignature(true)));
	}

	private Set<OWLDataProperty> normalise(Set<OWLDataProperty> properties) {

		properties.remove(getDataFactory().getOWLTopDataProperty());
		properties.remove(getDataFactory().getOWLBottomDataProperty());

		return properties;
	}

	private OWLOntology getMainOntology() {

		return model.getMainOntology();
	}

	private OWLDataFactory getDataFactory() {

		return model.getDataFactory();
	}
}
