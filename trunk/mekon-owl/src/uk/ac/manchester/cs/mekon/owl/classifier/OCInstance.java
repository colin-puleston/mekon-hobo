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

package uk.ac.manchester.cs.mekon.owl.classifier;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.classifier.frames.*;

/**
 * @author Colin Puleston
 */
class OCInstance {

	private OModel model;
	private OWLDataFactory dataFactory;

	private OCFrame rootFrame;
	private OWLClassExpression description;

	private class FrameDescriber {

		private OCFrame frame;

		private Set<OWLClassExpression> slotValueDescriptions
								= new HashSet<OWLClassExpression>();

		FrameDescriber(OCFrame frame) {

			this.frame = frame;
		}

		OWLClassExpression describe() {

			for (OCConceptSlot slot : frame.getConceptSlots()) {

				describeConceptSlotValues(slot);
			}

			for (OCNumberSlot slot : frame.getNumberSlots()) {

				describeNumberSlotValues(slot);
			}

			return getExpression();
		}

		private void describeConceptSlotValues(OCConceptSlot slot) {

			new ConceptSlotValuesDescriber(slot, slotValueDescriptions).describe();
		}

		private void describeNumberSlotValues(OCNumberSlot slot) {

			new NumberSlotValuesDescriber(slot, slotValueDescriptions).describe();
		}

		private OWLClassExpression getExpression() {

			OWLClass typeConcept = getConcept(frame);

			return slotValueDescriptions.isEmpty()
						? typeConcept
						: createCompoundExpression(typeConcept);
		}

		private OWLClassExpression createCompoundExpression(OWLClass typeConcept) {

			Set<OWLClassExpression> ops = new HashSet<OWLClassExpression>();

			ops.add(typeConcept);
			ops.addAll(slotValueDescriptions);

			return dataFactory.getOWLObjectIntersectionOf(ops);
		}
	}

	private abstract class SlotValuesDescriber<V> {

		private OCSlot<V> slot;
		private Set<OWLClassExpression> valueDescriptions;

		SlotValuesDescriber(
			OCSlot<V> slot,
			Set<OWLClassExpression> valueDescriptions) {

			this.slot = slot;
			this.valueDescriptions = valueDescriptions;
		}

		void describe() {

			if (!slot.mapsToOWLEntity()) {

				return;
			}

			OWLObjectProperty property = getProperty();
			Set<OWLClassExpression> fillers = getFillers();

			for (OWLClassExpression filler : fillers) {

				valueDescriptions.add(createHasValueConstruct(property, filler));
			}

			if (slot.closedWorldSemantics()) {

				valueDescriptions.add(createOnlyValuesConstruct(property, fillers));
			}
		}

		abstract OWLClassExpression getFillerOrNull(V value);

		private OWLObjectProperty getProperty() {

			return model.getAllObjectProperties().get(slot.getIRI());
		}

		private Set<OWLClassExpression> getFillers() {

			Set<OWLClassExpression> fillers = new HashSet<OWLClassExpression>();

			for (V value : slot.getValues()) {

				OWLClassExpression filler = getFillerOrNull(value);

				if (filler != null) {

					fillers.add(filler);
				}
			}

			return fillers;
		}
	}

	private class ConceptSlotValuesDescriber extends SlotValuesDescriber<OCFrame> {

		ConceptSlotValuesDescriber(
			OCConceptSlot slot,
			Set<OWLClassExpression> valueDescriptions) {

			super(slot, valueDescriptions);
		}

		OWLClassExpression getFillerOrNull(OCFrame value) {

			return value.mapsToOWLEntity() ? describeFrame(value) : null;
		}
	}

	private class NumberSlotValuesDescriber extends SlotValuesDescriber<INumber> {

		NumberSlotValuesDescriber(
			OCNumberSlot slot,
			Set<OWLClassExpression> valueDescriptions) {

			super(slot, valueDescriptions);
		}

		OWLClassExpression getFillerOrNull(INumber value) {

			return createNumberConstruct(value);
		}
	}

	OCInstance(OModel model, OCFrame rootFrame) {

		this.model = model;
		this.rootFrame = rootFrame;

		dataFactory = model.getDataFactory();

		description = describeFrame(rootFrame);
	}

	Set<OWLClass> classify() {

		OCMonitor.pollForPreClassify(model, description);
		Set<OWLClass> allConcepts = getEquivalentsOrSupers();
		OCMonitor.pollForClassified(model, description, allConcepts);

		return allConcepts;
	}

	private OWLClassExpression describeFrame(OCFrame frame) {

		return new FrameDescriber(frame).describe();
	}

	private OWLClassExpression createHasValueConstruct(
									OWLObjectProperty property,
									OWLClassExpression filler) {

		return dataFactory.getOWLObjectSomeValuesFrom(property, filler);
	}

	private OWLClassExpression createOnlyValuesConstruct(
									OWLObjectProperty property,
									Set<OWLClassExpression> fillers) {

		return dataFactory.getOWLObjectAllValuesFrom(property, getUnion(fillers));
	}

	private OWLClassExpression createNumberConstruct(INumber number) {

		if (model.numericPropertyDefined()) {

			OWLDataProperty property = model.getNumericProperty();
			OWLLiteral literal = toLiteral(number);

			return dataFactory.getOWLDataHasValue(property, literal);
		}

		return dataFactory.getOWLThing();
	}

	private OWLLiteral toLiteral(INumber number) {

		Class<? extends Number> type = number.getNumberType();

		if (type == Integer.class) {

			return dataFactory.getOWLLiteral(number.asInteger());
		}

		if (type == Long.class) {

			return dataFactory.getOWLLiteral(number.asLong());
		}

		if (type == Float.class) {

			return dataFactory.getOWLLiteral(number.asFloat());
		}

		if (type == Double.class) {

			return dataFactory.getOWLLiteral(number.asDouble());
		}

		throw new KModelException("Cannot handle number-type: " + type);
	}

	private Set<OWLClass> getEquivalentsOrSupers() {

		if (description instanceof OWLClass) {

			return Collections.singleton((OWLClass)description);
		}

		return checkRemoveRootFrameConcept(inferEquivalentsOrSupers());
	}

	private Set<OWLClass> inferEquivalentsOrSupers() {

		Set<OWLClass> types = model.getInferredEquivalents(description);

		return types.isEmpty() ? model.getInferredSupers(description, true) : types;
	}

	private Set<OWLClass> checkRemoveRootFrameConcept(Set<OWLClass> allConcepts) {

		OWLClass concept = getConcept(rootFrame);

		if (allConcepts.contains(concept)) {

			allConcepts = new HashSet<OWLClass>(allConcepts);
			allConcepts.remove(concept);
		}

		return allConcepts;
	}

	private OWLClassExpression getUnion(Set<OWLClassExpression> conjuncts) {

		if (conjuncts.isEmpty()) {

			return dataFactory.getOWLNothing();
		}

		return dataFactory.getOWLObjectUnionOf(conjuncts);
	}

	private OWLClass getConcept(OCFrame frame) {

		return getConcept(frame.getIRI());
	}

	private OWLClass getConcept(IRI iri) {

		return model.getAllConcepts().get(iri);
	}
}
