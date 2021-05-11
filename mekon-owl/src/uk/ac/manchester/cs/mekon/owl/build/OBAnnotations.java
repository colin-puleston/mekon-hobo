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

	static private final String FRAME_OWL_DEFINITION_ID = "OWL-definition";
	static private final String FRAME_MEKON_DEFINITION_ID = "MEKON-definition";

	private OModel model;

	private OBValues values;
	private OBEntityLabels entityLabels;
	private OLabelRenderer expressionLabels;

	private boolean annotateFramesWithOWLDefinitions = false;
	private boolean annotateFramesWithMekonDefinitions = false;

	private Set<OBAnnotationInclusion> inclusions
				= new HashSet<OBAnnotationInclusion>();

	private class ConceptAnnotationsWriter {

		private CBuilder builder;
		private CFrame frame;
		private CAnnotationsEditor editor;

		ConceptAnnotationsWriter(CBuilder builder, CFrame frame, CAnnotationsEditor editor) {

			this.builder = builder;
			this.frame = frame;
			this.editor = editor;
		}

		void checkAnnotate(OWLClass owlConcept) {

			addInclusions(editor, owlConcept);

			if (annotateWithDefinitions()) {

				addDefinitions(owlConcept);
			}
		}

		private void addDefinitions(OWLClass owlConcept) {

			for (OWLClassExpression owlDefn : getEquivalents(owlConcept)) {

				if (annotateFramesWithOWLDefinitions) {

					addOWLDefinition(owlDefn);
				}

				if (annotateFramesWithMekonDefinitions) {

					checkAddMekonDefinition(owlDefn);
				}
			}
		}

		private void addOWLDefinition(OWLClassExpression owlDefn) {

			editor.add(FRAME_OWL_DEFINITION_ID, expressionLabels.render(owlDefn));
		}

		private void checkAddMekonDefinition(OWLClassExpression owlDefn) {

			OBValue<?> defnBldr = values.checkCreateValue(owlDefn);

			if (defnBldr != null && defnBldr instanceof OBExtensionFrame) {

				CFrame defn = createMekonDefinition((OBExtensionFrame)defnBldr);

				editor.add(FRAME_MEKON_DEFINITION_ID, defn);
			}
		}

		private CFrame createMekonDefinition(OBExtensionFrame defnBldr) {

			return defnBldr.ensureCFrame(builder, OBAnnotations.this);
		}

		private boolean annotateWithDefinitions() {

			return annotateFramesWithOWLDefinitions
					|| annotateFramesWithMekonDefinitions;
		}
	}

	private class PropertyAnnotationsWriter {

		private CAnnotationsEditor editor;

		PropertyAnnotationsWriter(CAnnotationsEditor editor) {

			this.editor = editor;
		}

		void checkAnnotate(OWLProperty owlProperty) {

			addAllInclusions(owlProperty);
		}

		private void addAllInclusions(OWLProperty owlProperty) {

			addInclusions(editor, owlProperty);

			for (OWLProperty owlSuperProperty : getSuperProperties(owlProperty)) {

				addAllInclusions(owlSuperProperty);
			}
		}
	}

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

	/**
	 * Sets the attribute that determines whether or not all frames
	 * that are built will be annotated with any OWL equivalent-classes
	 * that have been asserted or inferred.
	 *
	 * @param value Required value of attribute
	 */
	public void setAnnotateFramesWithOWLDefinitions(boolean value) {

		annotateFramesWithOWLDefinitions = value;
	}

	/**
	 * Sets the attribute that determines whether or not all frames
	 * that are built will be annotated with frames-model versions
	 * of any OWL equivalent-classes that have been asserted or inferred,
	 * and which can be represented within the frames-model.
	 *
	 * @param value Required value of attribute
	 */
	public void setAnnotateFramesWithMekonDefinitions(boolean value) {

		annotateFramesWithMekonDefinitions = value;
	}

	OBAnnotations(
		OModel model,
		OBFrames frames,
		OBSlots slots,
		OBEntityLabels entityLabels) {

		this.model = model;
		this.entityLabels = entityLabels;

		values = new OBValues(model, frames, slots);
		expressionLabels = new OLabelRenderer(model);

		expressionLabels.setAllowCarriageReturns(false);
	}

	void checkAnnotateFrame(CBuilder builder, CFrame frame, OWLEntity owlEntity) {

		CAnnotationsEditor editor = builder.getFrameAnnotationsEditor(frame);

		if (owlEntity instanceof OWLClass) {

			new ConceptAnnotationsWriter(builder, frame, editor).checkAnnotate((OWLClass)owlEntity);
		}
		else {

			new PropertyAnnotationsWriter(editor).checkAnnotate((OWLProperty)owlEntity);
		}
	}

	void checkAnnotateSlotSet(CBuilder builder, OWLProperty owlProperty) {

		CIdentity slotId = toSlotId(owlProperty);
		CAnnotationsEditor editor = builder.getSlotAnnotationsEditor(slotId);

		new PropertyAnnotationsWriter(editor).checkAnnotate(owlProperty);
	}

	private CIdentity toSlotId(OWLProperty owlProperty) {

		return new OIdentity(owlProperty, entityLabels.getLabel(owlProperty));
	}

	private void addInclusions(CAnnotationsEditor editor, OWLEntity owlEntity) {

		for (OBAnnotationInclusion inclusion : inclusions) {

			inclusion.checkAdd(model, owlEntity, editor);
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
}
