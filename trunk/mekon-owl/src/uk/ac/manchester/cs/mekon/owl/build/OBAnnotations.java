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
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * Responsible for copying annotations from OWL entities to the
 * relevant generated frames-based entities.
 *
 * @author Colin Puleston
 */
public class OBAnnotations {

	private OModel model;

	private Set<OBAnnotationInclusion> inclusions
				= new HashSet<OBAnnotationInclusion>();

	/**
	 * Adds a specification of a set of annotations to be included.
	 *
	 * @param inclusion Specification of set of annotations to be
	 * included
	 */
	public void addInclusion(OBAnnotationInclusion inclusion) {

		inclusions.add(inclusion);
	}

	/**
	 * Adds a specifications of sets of annotations to be included.
	 *
	 * @param inclusions Specifications of sets of annotations to be
	 * included
	 */
	public void addInclusions(Set<OBAnnotationInclusion> inclusions) {

		this.inclusions.addAll(inclusions);
	}

	OBAnnotations(OModel model) {

		this.model = model;
	}

	void checkAdd(CBuilder builder, CEntity cEntity, OWLEntity owlEntity) {

		CAnnotationsEditor editor = getEditor(builder, cEntity);

		for (OBAnnotationInclusion inclusion : inclusions) {

			inclusion.checkAdd(model, owlEntity, editor);
		}
	}

	private CAnnotationsEditor getEditor(CBuilder builder, CEntity cEntity) {

		return builder.getAnnotationsEditor(cEntity.getAnnotations());
	}
}