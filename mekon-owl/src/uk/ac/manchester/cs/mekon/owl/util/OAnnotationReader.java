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

package uk.ac.manchester.cs.mekon.owl.util;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.owl.*;

/**
 * Responsible for reading annotation-property values from OWL entities.
 *
 * @author Colin Puleston
 */
public class OAnnotationReader {

	private OModel model;
	private List<OWLAnnotationProperty> properties = new ArrayList<OWLAnnotationProperty>();

	/**
	 * Constructs object without any initially specified annotation
	 * properties.
	 *
	 * @param model Model containing entities whose annotation values
	 * are to be read
	 */
	public OAnnotationReader(OModel model) {

		this.model = model;
	}

	/**
	 * Constructs object to obtain values for a specified annotation
	 * property.
	 *
	 * @param model Model containing entities whose annotation values
	 * are to be read
	 * @param property Annotation property whose values are required
	 */
	public OAnnotationReader(OModel model, OWLAnnotationProperty property) {

		this(model);

		addProperty(property);
	}

	/**
	 * Constructs object to obtain values for any of the annotation
	 * properties from a specified set.
	 *
	 * @param model Model containing entities whose annotation values
	 * are to be read
	 * @param properties Annotation properties whose values are required
	 */
	public OAnnotationReader(OModel model, List<OWLAnnotationProperty> properties) {

		this(model);

		addProperties(properties);
	}

	/**
	 * Adds an annotation property whose values are required.
	 *
	 * @param property Annotation property whose values are required
	 */
	public void addProperty(OWLAnnotationProperty property) {

		properties.add(property);
	}

	/**
	 * Adds a set of annotation properties whose values are required.
	 *
	 * @param properties Annotation properties whose values are required
	 */
	public void addProperties(List<OWLAnnotationProperty> properties) {

		this.properties.addAll(properties);
	}

	/**
	 * Retrieves a value for the specified entity, of any member of
	 * the relevant set of annotation properties for which such a
	 * value exists.
	 *
	 * @param entity Entity whose annotation value is required
	 * @return Relevant value, or null if not found
	 */
	public String getValueOrNull(OWLEntity entity) {

		for (OWLAnnotationProperty property : properties) {

			String value = getValueOrNull(entity, property);

			if (value != null) {

				return value;
			}
		}

		return null;
	}

	private String getValueOrNull(OWLEntity entity, OWLAnnotationProperty property) {

		for (OWLAnnotation anno : getAnnotations(entity, property)) {

			if (anno.getProperty().equals(property)) {

				OWLAnnotationValue value = anno.getValue();

				if (value instanceof OWLLiteral) {

					return ((OWLLiteral)value).getLiteral();
				}
			}
		}

		return null;
	}

	private Set<OWLAnnotation> getAnnotations(
									OWLEntity entity,
									OWLAnnotationProperty property) {

		return OWLAPIVersion.getAnnotations(entity, model.getAllOntologies(), property);
	}
}
