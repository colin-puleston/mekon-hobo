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
import org.semanticweb.owlapi.util.*;

import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class OBSlots {

	private OModel model;
	private OBFrames frames;
	private OBNumbers numbers;
	private OBProperties properties;
	private OBEntityLabels labels;
	private OBFrameSlotsPolicy defaultFrameSlotsPolicy
					= OBFrameSlotsPolicy.IFRAME_VALUED_ONLY;

	private SpecCreator specCreator = new SpecCreator();

	private abstract class SlotSpec extends OBSlotSpec {

		private OWLObjectProperty property;
		private OWLClassExpression filler;
		private OBPropertyAttributes propertyAttributes;

		SlotSpec(
			OWLQuantifiedRestriction
				<?,
				OWLObjectPropertyExpression,
				OWLClassExpression> restriction) {

			property = toPropertyOrNull(restriction.getProperty());
			filler = restriction.getFiller();
			propertyAttributes = properties.getAttributes(property);
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

		OBFrameSlotsPolicy getFrameSlotsPolicy() {

			OBFrameSlotsPolicy p = propertyAttributes.getFrameSlotsPolicy();

			return p != OBFrameSlotsPolicy.NONE ? p : defaultFrameSlotsPolicy;
		}

		OBPropertyAttributes getPropertyAttributes() {

			return propertyAttributes;
		}

		OBSlot create() {

			if (filler instanceof OWLClass) {

				return createSimpleSlot((OWLClass)filler);
			}

			if (filler instanceof OWLObjectIntersectionOf) {

				OBSlot slot = createExtensionFrameSlotOrNull();

				if (slot != null) {

					return slot;
				}
			}

			return createDisjunctionFrameSlot();
		}

		private OBSlot createSimpleSlot(OWLClass filler) {

			OBNumber number = numbers.checkExtractNumber(filler);

			return number != null
					? createNumberSlot(number)
					: createModelFrameSlot(filler);
		}

		private OBSlot createModelFrameSlot(OWLClass filler) {

			return new OBModelFrameSlot(this, frames.get(filler));
		}

		private OBSlot createExtensionFrameSlotOrNull() {

			if (filler instanceof OWLObjectIntersectionOf) {

				OWLObjectIntersectionOf inter = (OWLObjectIntersectionOf)filler;
				Set<OWLClassExpression> ops = inter.getOperands();
				OWLClass named = getSoleNamedClassOrNull(ops);

				if (named != null) {

					ops.remove(named);

					return createExtensionFrameSlot(named, ops);
				}
			}

			return null;
		}

		private OBSlot createExtensionFrameSlot(
							OWLClass named,
							Set<OWLClassExpression> ops) {

			OBFrame valueTypeBase = frames.get(named);
			OBExtensionFrameSlot slot = new OBExtensionFrameSlot(this, valueTypeBase);

			for (OWLClassExpression op : ops) {

				OBSlot valueTypeSlot = checkCreateSlot(op);

				if (valueTypeSlot != null) {

					slot.addValueTypeSlot(valueTypeSlot);
				}
			}

			return slot;
		}

		private OBSlot createDisjunctionFrameSlot() {

			OBDisjunctionFrameSlot slot = new OBDisjunctionFrameSlot(this);

			for (OWLClass subConcept : getSubConcepts(filler)) {

				slot.addValueTypeDisjunct(frames.get(subConcept));
			}

			return slot;
		}

		private OBSlot createNumberSlot(OBNumber valueType) {

			return new OBNumberSlot(this, valueType);
		}

		private OWLClass getSoleNamedClassOrNull(Set<OWLClassExpression> ops) {

			OWLClass named = null;

			for (OWLClassExpression op : ops) {

				if (op instanceof OWLClass) {

					if (named != null) {

						return null;
					}

					named = (OWLClass)op;
				}
			}

			return named;
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

	OBSlots(
		OModel model,
		OBFrames frames,
		OBProperties properties,
		OBEntityLabels labels) {

		this.model = model;
		this.frames = frames;
		this.properties = properties;
		this.labels = labels;

		numbers = new OBNumbers(model);
	}

	void setDefaultFrameSlotsPolicy(OBFrameSlotsPolicy value) {

		defaultFrameSlotsPolicy = value;
	}

	void createAll(OBSubConceptAxioms subConceptOfs) {

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

		createSlots(sub, sup);
	}

	private void createSlots(OWLClassExpression sub, OWLObjectIntersectionOf sups) {

		for (OWLClassExpression sup : sups.getOperands()) {

			createSlots(sub, sup);
		}
	}

	private void createSlots(OWLClassExpression sub, OWLClassExpression sup) {

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

		OBSlot slot = checkCreateSlot(sup);

		if (slot != null) {

			frames.get(sub).addSlot(slot);
		}
	}

	private OBSlot checkCreateSlot(OWLClassExpression sup) {

		SlotSpec spec = sup.accept(specCreator);

		return spec != null && spec.initialised() ? spec.create() : null;
	}

	private Set<OWLClass> getSubConcepts(OWLClassExpression expression) {

		return model.getInferredSubs(expression, true);
	}
}
