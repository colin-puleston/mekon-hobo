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
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Responsible for providing the labels for the frames-based
 * entities that will be generated from named OWL entities.
 *
 * @author Colin Puleston
 */
public class OBEntityLabels {

	private OModel model;
	private OAnnotationReader annotationReader;

	/**
	 * Adds an annotation-property that will provide entity
	 * labels. If specified property is not present in the ontology
	 * then does nothing.
	 *
	 * @param annotationPropIRI IRI of relevant annotation-property
	 */
	public void addAnnotationProperty(IRI annotationPropIRI) {

		OEntities<OWLAnnotationProperty> props = model.getAnnotationProperties();

		if (props.contains(annotationPropIRI)) {

			annotationReader.addProperty(props.get(annotationPropIRI));
		}
	}

	/**
	 * Adds a set of annotation-properties that will provide entity
	 * labels.
	 *
	 * @param annotationPropIRIs IRIs of relevant annotation-properties
	 */
	public void addAnnotationProperties(List<IRI> annotationPropIRIs) {

		for (IRI iri : annotationPropIRIs) {

			addAnnotationProperty(iri);
		}
	}

	OBEntityLabels(OModel model) {

		this.model = model;

		annotationReader = new OAnnotationReader(model);
	}

	String getLabel(OWLEntity entity) {

		for (String label : annotationReader.getAllValues(entity)) {

			return label;
		}

		return OIdentity.createDefaultLabel(entity);
	}
}
