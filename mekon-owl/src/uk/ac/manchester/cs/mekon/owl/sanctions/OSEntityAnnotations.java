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

package uk.ac.manchester.cs.mekon.owl.sanctions;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Responsible for copying annotations from OWL entities to
 * the relevant generated frames-based entities.
 *
 * @author Colin Puleston
 */
public class OSEntityAnnotations {

	private OModel model;
	private Set<OSEntityAnnotationType> types = new HashSet<OSEntityAnnotationType>();
	private Map<IRI, OAnnotationReader> readers = new HashMap<IRI, OAnnotationReader>();

	/**
	 * Adds a specification of a type of annotation to be copied.
	 *
	 * @param type Type of annotation to be copied
	 */
	public void addType(OSEntityAnnotationType type) {

		types.add(type);
	}

	/**
	 * Adds a set of specifications of types of annotation to be copied.
	 *
	 * @param types Types of annotations to be copied
	 */
	public void addTypes(Set<OSEntityAnnotationType> types) {

		this.types.addAll(types);
	}

	OSEntityAnnotations(OModel model) {

		this.model = model;
	}

	void addAnnotations(CBuilder builder, CEntity cEntity, OWLEntity owlEntity) {

		CAnnotationsEditor annoEd = builder.getAnnotationsEditor(cEntity.getAnnotations());

		for (OSEntityAnnotationType type : types) {

			OAnnotationReader reader = getReader(type.getAnnotationPropertyIRI());
			String value = reader.getValueOrNull(owlEntity);

			if (value != null) {

				annoEd.addAll(type.getFramesAnnotationId(), type.getValues(value));
			}
		}
	}

	private OAnnotationReader getReader(IRI iri) {

		OAnnotationReader reader = readers.get(iri);

		if (reader == null) {

			reader = createReader(iri);
			readers.put(iri, reader);
		}

		return reader;
	}

	private OAnnotationReader createReader(IRI iri) {

		return new OAnnotationReader(model, model.getAnnotationProperty(iri));
	}
}
