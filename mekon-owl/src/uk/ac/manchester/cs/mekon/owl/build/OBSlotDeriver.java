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

/**
 * @author Colin Puleston
 */
abstract class OBSlotDeriver {

	private OModel model;
	private OBSlots slots;
	private OBConcepts concepts;
	private OBProperties properties;

	OBSlotDeriver(
		OModel model,
		OBSlots slots,
		OBConcepts concepts,
		OBProperties properties) {

		this.model = model;
		this.slots = slots;
		this.concepts = concepts;
		this.properties = properties;
	}

	void createSlots(
			OWLClassExpression frameExpression,
			OWLClassExpression slotExpression) {

		for (OWLClass frameConcept : toFrameConcepts(frameExpression)) {

			slots.checkCreateSlot(frameConcept, slotExpression);
		}
	}

	void createAllValuesSlots(
			OWLClassExpression frameExpression,
			OWLProperty<?, ?> property,
			OWLObject range) {

		for (OWLClass frameConcept : toFrameConcepts(frameExpression)) {

			slots.checkCreateAllValuesSlot(frameConcept, property, range);
		}
	}

	<A extends OWLAxiom>Set<A> getTypeAxioms(AxiomType<A> type) {

		return model.getModelOntology().getAxioms(type, true);
	}

	boolean modelExpression(OWLClassExpression expression) {

		return concepts.containsAllInSignature(expression)
				&& properties.containsAllInSignature(expression);
	}

	boolean modelProperty(OWLProperty<?, ?> property) {

		return properties.contains(property);
	}

	private Set<OWLClass> toFrameConcepts(OWLClassExpression frameExpression) {

		Set<OWLClass> concepts = new HashSet<OWLClass>();

		if (frameExpression instanceof OWLClass) {

			concepts.add((OWLClass)frameExpression);
		}
		else {

			concepts.addAll(getSubConcepts(frameExpression));
		}

		return concepts;
	}

	private Set<OWLClass> getSubConcepts(OWLClassExpression expression) {

		return model.getInferredSubs(expression, true);
	}
}
