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
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Responsible for copying annotations from OWL entities to the
 * relevant generated frames-based entities.
 *
 * @author Colin Puleston
 */
public class OBAnnotations {

	static private final String FRAME_DEFINITION_ID = "OWL-Definition";

	private OModel model;
	private boolean annotateFramesWithDefinitions = false;

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

	void setAnnotateFramesWithDefinitions(boolean value) {

		annotateFramesWithDefinitions = value;
	}

	void checkAnnotate(CBuilder builder, CEntity cEntity, OWLEntity owlEntity) {

		CAnnotationsEditor cEditor = getCEditor(builder, cEntity);

		addInclusions(cEditor, owlEntity);

		if (annotateFramesWithDefinitions
			&& owlEntity instanceof OWLClass) {

			addCFrameDefinitions(cEditor, (OWLClass)owlEntity);
		}
	}

	private void addInclusions(CAnnotationsEditor cEditor, OWLEntity owlEntity) {

		for (OBAnnotationInclusion inclusion : inclusions) {

			inclusion.checkAdd(model, owlEntity, cEditor);
		}
	}

	private void addCFrameDefinitions(CAnnotationsEditor cEditor, OWLClass owlConcept) {

		OLabelRenderer labels = new OLabelRenderer(model);

		labels.setAllowCarriageReturns(false);

		for (OWLClassExpression equiv : getEquivalents(owlConcept)) {

			cEditor.add(FRAME_DEFINITION_ID, labels.render(equiv));
		}
	}

	private Set<OWLClassExpression> getEquivalents(OWLClass owlConcept) {

		Set<OWLClassExpression> equivs = new HashSet<OWLClassExpression>();

		for (OWLOntology ontology : model.getAllOntologies()) {

			for (OWLClassAxiom axiom : ontology.getAxioms(owlConcept)) {

				if (axiom instanceof OWLEquivalentClassesAxiom) {

					OWLEquivalentClassesAxiom eqAxiom
						= (OWLEquivalentClassesAxiom)axiom;

					equivs.addAll(eqAxiom.getClassExpressionsMinus(owlConcept));
				}
			}
		}

		return equivs;
	}

	private CAnnotationsEditor getCEditor(CBuilder builder, CEntity cEntity) {

		return builder.getAnnotationsEditor(cEntity.getAnnotations());
	}
}
