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
import org.semanticweb.owlapi.util.*;

import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class OSSlots {

	private OModel model;
	private OSFrames frames;
	private OSNumbers numbers;
	private OSEntityLabels labels;
	private boolean metaFrameSlotsEnabled = false;

	private SpecCreator specCreator = new SpecCreator();

	private abstract class SlotSpec extends OSSlotSpec {

		private OWLObjectProperty property;
		private OWLClassExpression filler;

		SlotSpec(
			OWLQuantifiedRestriction
				<?,
				OWLObjectPropertyExpression,
				OWLClassExpression> restriction) {

			property = toPropertyOrNull(restriction.getProperty());
			filler = restriction.getFiller();
		}

		boolean initialised() {

			return property != null;
		}

		OWLObjectProperty getProperty() {

			return property;
		}

		boolean singleValued() {

			return property.isFunctional(model.getAllOntologies());
		}

		String getLabel() {

			return labels.getLabel(property);
		}

		boolean metaFrameSlotsEnabled() {

			return metaFrameSlotsEnabled;
		}

		OSSlot create(OSFrame container) {

			if (filler instanceof OWLClass) {

				return createSimpleSlot((OWLClass)filler);
			}

			return createDisjunctionFrameSlot(container);
		}

		private OSSlot createSimpleSlot(OWLClass filler) {

			OSNumber number = numbers.checkExtractNumber(filler);

			return number != null
					? createNumberSlot(number)
					: createModelFrameSlot(filler);
		}

		private OSSlot createModelFrameSlot(OWLClass filler) {

			return new OSModelFrameSlot(this, frames.get(filler));
		}

		private OSSlot createDisjunctionFrameSlot(OSFrame container) {

			String valueTypeLabel = createDisjunctionValueTypeLabel(container);
			OSDisjunctionFrameSlot slot = new OSDisjunctionFrameSlot(
													this,
													valueTypeLabel);

			for (OWLClass subConcept : getSubConcepts(filler)) {

				slot.addValueTypeDisjunct(frames.get(subConcept));
			}

			return slot;
		}

		private OSSlot createNumberSlot(OSNumber valueType) {

			return new OSNumberSlot(this, valueType);
		}

		private String createDisjunctionValueTypeLabel(OSFrame container) {

			return container.getIdentity().getLabel() + ":" + getLabel();
		}

		private OWLObjectProperty toPropertyOrNull(OWLObjectPropertyExpression expr) {

			if (expr instanceof OWLObjectProperty) {

				return (OWLObjectProperty)expr;
			}

			return null;
		}
	}

	private class AllValuesFromSlotSpec extends SlotSpec {

		AllValuesFromSlotSpec(OWLObjectAllValuesFrom restriction) {

			super(restriction);
		}

		boolean valuedRequired() {

			return false;
		}
	}

	private class SomeValuesFromSlotSpec extends SlotSpec {

		SomeValuesFromSlotSpec(OWLObjectSomeValuesFrom restriction) {

			super(restriction);
		}

		boolean valuedRequired() {

			return true;
		}
	}

	private abstract class CardinalitySlotSpec extends SlotSpec {

		private int cardinality;

		CardinalitySlotSpec(OWLObjectCardinalityRestriction restriction) {

			super(restriction);

			cardinality = restriction.getCardinality();
		}

		boolean singleValued() {

			return super.singleValued() || (includesMax() && cardinality == 1);
		}

		boolean valuedRequired() {

			return includesMin() && cardinality != 0;
		}

		abstract boolean includesMin();

		abstract boolean includesMax();
	}

	private class ExactCardinalitySlotSpec extends CardinalitySlotSpec {

		ExactCardinalitySlotSpec(OWLObjectExactCardinality restriction) {

			super(restriction);
		}

		boolean includesMin() {

			return true;
		}

		boolean includesMax() {

			return true;
		}
	}

	private class MinCardinalitySlotSpec extends CardinalitySlotSpec {

		MinCardinalitySlotSpec(OWLObjectMinCardinality restriction) {

			super(restriction);
		}

		boolean includesMin() {

			return true;
		}

		boolean includesMax() {

			return false;
		}
	}

	private class MaxCardinalitySlotSpec extends CardinalitySlotSpec {

		MaxCardinalitySlotSpec(OWLObjectMaxCardinality restriction) {

			super(restriction);
		}

		boolean includesMin() {

			return false;
		}

		boolean includesMax() {

			return true;
		}
	}

	private class SpecCreator extends OWLObjectVisitorExAdapter<SlotSpec> {

		public SlotSpec visit(OWLObjectSomeValuesFrom e) {

			return new SomeValuesFromSlotSpec(e);
		}

		public SlotSpec visit(OWLObjectAllValuesFrom e) {

			return new AllValuesFromSlotSpec(e);
		}

		public SlotSpec visit(OWLObjectExactCardinality e) {

			return new ExactCardinalitySlotSpec(e);
		}

		public SlotSpec visit(OWLObjectMinCardinality e) {

			return new MinCardinalitySlotSpec(e);
		}

		public SlotSpec visit(OWLObjectMaxCardinality e) {

			return new MaxCardinalitySlotSpec(e);
		}

		protected SlotSpec getDefaultReturnValue(OWLObject e) {

			return null;
		}
	}

	OSSlots(OModel model, OSFrames frames, OSEntityLabels labels) {

		this.model = model;
		this.frames = frames;
		this.labels = labels;

		numbers = new OSNumbers(model);
	}

	void setMetaFrameSlotsEnabled(boolean value) {

		metaFrameSlotsEnabled = value;
	}

	void createAll(OSSubConceptAxioms subConceptOfs) {

		for (OWLSubClassOfAxiom subConceptOf : subConceptOfs.getAll()) {

			createSlots(subConceptOf);
		}
	}

	private void createSlots(OWLSubClassOfAxiom subConceptOf) {

		OWLClassExpression sub = subConceptOf.getSubClass();
		OWLClassExpression sup = subConceptOf.getSuperClass();

		if (sup instanceof OWLObjectIntersectionOf) {

			createSlots(sub, (OWLObjectIntersectionOf)sup);
		}

		checkCreateSlots(sub, sup);
	}

	private void createSlots(OWLClassExpression sub, OWLObjectIntersectionOf sups) {

		for (OWLClassExpression sup : sups.getOperands()) {

			checkCreateSlots(sub, sup);
		}
	}

	private void checkCreateSlots(OWLClassExpression sub, OWLClassExpression sup) {

		if (sub instanceof OWLClass) {

			checkCreateSlot((OWLClass)sub, sup);
		}
		else {

			for (OWLClass subSub : getSubConcepts(sub)) {

				checkCreateSlot(subSub, sup);
			}
		}
	}

	private void checkCreateSlot(OWLClass sub, OWLClassExpression sup) {

		SlotSpec spec = sup.accept(specCreator);

		if (spec != null && spec.initialised()) {

			OSFrame container = frames.get(sub);

			container.addSlot(spec.create(container));
		}
	}

	private Set<OWLClass> getSubConcepts(OWLClassExpression expression) {

		return model.getInferredSubs(expression, true);
	}
}
