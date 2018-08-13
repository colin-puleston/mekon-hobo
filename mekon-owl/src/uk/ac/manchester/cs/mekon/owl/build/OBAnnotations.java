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

	void checkAnnotateFrame(CBuilder builder, CFrame cFrame, OWLEntity owlEntity) {

		CAnnotationsEditor cEditor = getCEditor(builder, cFrame);

		if (owlEntity instanceof OWLClass) {

			addInclusions(cEditor, owlEntity);

			if (annotateFramesWithDefinitions) {

				addCFrameDefinitions(cEditor, (OWLClass)owlEntity);
			}
		}
		else {

			addPropertyInclusions(cEditor, (OWLProperty)owlEntity);
		}
	}

	void checkAnnotateSlot(CBuilder builder, CSlot cSlot, OWLProperty owlProperty) {

		addPropertyInclusions(getCEditor(builder, cSlot), owlProperty);
	}

	private void addPropertyInclusions(CAnnotationsEditor cEditor, OWLProperty owlProperty) {

		addInclusions(cEditor, owlProperty);

		for (OWLProperty owlSuperProperty : getSuperProperties(owlProperty)) {

			addPropertyInclusions(cEditor, owlSuperProperty);
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

			for (OWLClassAxiom axiom : OWLAPIVersion.getAxioms(ontology, owlConcept)) {

				if (axiom instanceof OWLEquivalentClassesAxiom) {

					OWLEquivalentClassesAxiom eqAxiom
						= (OWLEquivalentClassesAxiom)axiom;

					equivs.addAll(eqAxiom.getClassExpressionsMinus(owlConcept));
				}
			}
		}

		return equivs;
	}

	private Set<? extends OWLProperty> getSuperProperties(OWLProperty owlProperty) {

		return owlProperty instanceof OWLObjectProperty
					? model.getInferredSupers((OWLObjectProperty)owlProperty, true)
					: model.getInferredSupers((OWLDataProperty)owlProperty, true);
	}

	private CAnnotationsEditor getCEditor(CBuilder builder, CAnnotatable cEntity) {

		return builder.getAnnotationsEditor(cEntity.getAnnotations());
	}
}
