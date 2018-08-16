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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Represents a specification of a set of annotations to be copied
 * from any relevant OWL entities to the corresponding frames-based
 * entities that have been generated.
 *
 * @author Colin Puleston
 */
public class OBAnnotationInclusion {

	private IRI annotationPropertyIRI;
	private String framesAnnotationId;

	private String valueSeparators = null;
	private Map<String, String> valueSubstitutions = new HashMap<String, String>();

	private OAnnotationReader reader = null;

	/**
	 * Constructor.
	 *
	 * @param annotationPropertyIRI IRI of OWL annotation property
	 * @param framesAnnotationId Identifier for annotations in
	 * Frames Model (FM)
	 */
	public OBAnnotationInclusion(IRI annotationPropertyIRI, String framesAnnotationId) {

		this.annotationPropertyIRI = annotationPropertyIRI;
		this.framesAnnotationId = framesAnnotationId;
	}

	/**
	 * Defines one or more characters that are to act as
	 * value-list separators. When value-separators are specified,
	 * all annotation-value strings derived from the OWL entities
	 * will be split accordingly in order to provide multiple
	 * annotation-values for the corresponding frames-based entities.
	 *
	 * @param valueSeparators String containing set of individual
	 * characters, each of which can act as a list-value separator
	 */
	public void setValueSeparators(String valueSeparators) {

		this.valueSeparators = valueSeparators;
	}

	/**
	 * Defines a potential value for the OWL annotations that if
	 * found will be replaced by another specified value when the
	 * annotation on the frames-based entity is created.
	 *
	 * @param owlValue Annotation-value derived from OWL entity
	 * @param framesValue Annotation-value to be substituted for
	 * frames-based entity
	 */
	public void addValueSubstitution(String owlValue, String framesValue) {

		valueSubstitutions.put(owlValue, framesValue);
	}

	void checkAdd(OModel model, OWLEntity owlEntity, CAnnotationsEditor editor) {

		for (String owlValue : getOWLValues(model, owlEntity)) {

			for (String value : owlValueToList(owlValue)) {

				editor.add(framesAnnotationId, resolveValue(value));
			}
		}
	}

	private String resolveValue(String owlValue) {

		String substitute = valueSubstitutions.get(owlValue);

		return substitute != null ? substitute : owlValue;
	}

	private List<String> getOWLValues(OModel model, OWLEntity owlEntity) {

		return getAnnotationReader(model).getAllValues(owlEntity);
	}

	private List<String> owlValueToList(String owlValue) {

		return valueSeparators != null
				? Arrays.asList(owlValue.split(valueSeparators))
				: Collections.singletonList(owlValue);
	}

	private OAnnotationReader getAnnotationReader(OModel model) {

		if (reader == null) {

			reader = createAnnotationReader(model);
		}

		return reader;
	}

	private OAnnotationReader createAnnotationReader(OModel model) {

		return new OAnnotationReader(model, getAnnotationProperty(model));
	}

	private OWLAnnotationProperty getAnnotationProperty(OModel model) {

		return model.getAnnotationProperty(annotationPropertyIRI);
	}
}
