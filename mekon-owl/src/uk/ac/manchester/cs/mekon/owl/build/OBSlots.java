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
	private OBValues values = null;
	private OBProperties properties;
	private OBEntityLabels labels;
	private OBFrameSlotsPolicy defaultFrameSlotsPolicy
					= OBFrameSlotsPolicy.IFRAME_VALUED_ONLY;

	private SpecCreator specCreator = new SpecCreator();

	private abstract class SlotSpec extends OBSlotSpec {

		private OWLProperty<?, ?> property;
		private OBPropertyAttributes propertyAttributes;
		private OWLObject filler;

		SlotSpec(OWLQuantifiedRestriction<?, ?, ?> restriction) {

			property = getPropertyOrNull(restriction.getProperty());
			propertyAttributes = properties.getAttributes(property);
			filler = restriction.getFiller();
		}

		OWLProperty<?, ?> getProperty() {

			return property;
		}

		boolean singleValued() {

			return property.isFunctional(model.getAllOntologies());
		}

		String getLabel() {

			return labels.getLabel(property);
		}

		OBFrameSlotsPolicy getFrameSlotsPolicy() {

			OBFrameSlotsPolicy policy = propertyAttributes.getFrameSlotsPolicy();

			return policy != OBFrameSlotsPolicy.NONE
						? policy
						: defaultFrameSlotsPolicy;
		}

		OBPropertyAttributes getPropertyAttributes() {

			return propertyAttributes;
		}

		OBSlot checkCreate() {

			if (property != null) {

				OBValue<?> valueType = values.checkCreateValue(filler);

				if (valueType != null) {

					return new OBSlot(this, valueType);
				}
			}

			return null;
		}

		private OWLProperty<?, ?> getPropertyOrNull(OWLPropertyExpression<?, ?> expr) {

			return expr instanceof OWLProperty ? (OWLProperty<?, ?>)expr : null;
		}
	}

	private class AllValuesFromSlotSpec extends SlotSpec {

		AllValuesFromSlotSpec(OWLQuantifiedRestriction<?, ?, ?> restriction) {

			super(restriction);
		}

		boolean valuedRequired() {

			return false;
		}
	}

	private class SomeValuesFromSlotSpec extends SlotSpec {

		SomeValuesFromSlotSpec(OWLQuantifiedRestriction<?, ?, ?> restriction) {

			super(restriction);
		}

		boolean valuedRequired() {

			return true;
		}
	}

	private abstract class CardinalitySlotSpec extends SlotSpec {

		private int cardinality;

		CardinalitySlotSpec(OWLCardinalityRestriction<?, ?, ?> restriction) {

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

		ExactCardinalitySlotSpec(OWLCardinalityRestriction<?, ?, ?> restriction) {

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

		MinCardinalitySlotSpec(OWLCardinalityRestriction<?, ?, ?> restriction) {

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

		MaxCardinalitySlotSpec(OWLCardinalityRestriction<?, ?, ?> restriction) {

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

		public SlotSpec visit(OWLDataSomeValuesFrom e) {

			return new SomeValuesFromSlotSpec(e);
		}

		public SlotSpec visit(OWLDataAllValuesFrom e) {

			return new AllValuesFromSlotSpec(e);
		}

		public SlotSpec visit(OWLDataExactCardinality e) {

			return new ExactCardinalitySlotSpec(e);
		}

		public SlotSpec visit(OWLDataMinCardinality e) {

			return new MinCardinalitySlotSpec(e);
		}

		public SlotSpec visit(OWLDataMaxCardinality e) {

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
	}

	void setValues(OBValues values) {

		this.values = values;
	}

	void setDefaultFrameSlotsPolicy(OBFrameSlotsPolicy value) {

		defaultFrameSlotsPolicy = value;
	}

	void createAll(OBSubConceptAxioms subConceptOfs) {

		for (OWLSubClassOfAxiom subConceptOf : subConceptOfs.getAll()) {

			createSlots(subConceptOf);
		}
	}

	OBSlot checkCreateSlot(OWLClassExpression sup) {

		SlotSpec spec = sup.accept(specCreator);

		return spec != null ? spec.checkCreate() : null;
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

	private Set<OWLClass> getSubConcepts(OWLClassExpression expression) {

		return model.getInferredSubs(expression, true);
	}
}
