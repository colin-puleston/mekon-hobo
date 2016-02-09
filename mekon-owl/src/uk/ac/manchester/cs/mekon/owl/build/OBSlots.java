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
	private OBValues values;
	private OBConcepts concepts;
	private OBProperties properties;
	private OBEntityLabels labels;
	private OBFrameSlotsPolicy defaultFrameSlotsPolicy
					= OBFrameSlotsPolicy.IFRAME_VALUED_ONLY;

	private SpecCreator specCreator = new SpecCreator();

	private abstract class SlotSpec extends OBSlotSpec {

		private OWLProperty<?, ?> property;
		private OWLObject range;

		private OBPropertyAttributes propertyAttributes;

		SlotSpec(OWLQuantifiedRestriction<?, ?, ?> restriction) {

			property = getPropertyOrNull(restriction.getProperty());

			if (property != null) {

				initialise(property, restriction.getFiller());
			}
		}

		SlotSpec(OWLProperty<?, ?> property, OWLObject range) {

			initialise(property, range);
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

			if (policy == OBFrameSlotsPolicy.NONE) {

				return defaultFrameSlotsPolicy;
			}

			return policy;
		}

		OBPropertyAttributes getPropertyAttributes() {

			return propertyAttributes;
		}

		OBSlot checkCreate() {

			if (property != null) {

				OBValue<?> valueType = checkCreateValueType();

				if (valueType != null) {

					return new OBSlot(this, valueType);
				}
			}

			return null;
		}

		private void initialise(OWLProperty<?, ?> property, OWLObject range) {

			this.property = property;
			this.range = range;

			propertyAttributes = properties.getAttributes(property);
		}

		private OBValue<?> checkCreateValueType() {

			return values.checkCreateValue(range);
		}

		private OWLProperty<?, ?> getPropertyOrNull(OWLPropertyExpression<?, ?> expr) {

			if (expr instanceof OWLProperty) {

				OWLProperty<?, ?> property = (OWLProperty<?, ?>)expr;

				if (properties.contains(property)) {

					return property;
				}
			}

			return null;
		}
	}

	private class AllValuesFromSlotSpec extends SlotSpec {

		AllValuesFromSlotSpec(OWLQuantifiedRestriction<?, ?, ?> restriction) {

			super(restriction);
		}

		AllValuesFromSlotSpec(OWLProperty<?, ?> property, OWLObject range) {

			super(property, range);
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
		OBConcepts concepts,
		OBProperties properties,
		OBEntityLabels labels) {

		this.model = model;
		this.frames = frames;
		this.concepts = concepts;
		this.properties = properties;
		this.labels = labels;

		values = new OBValues(model, frames, this);
	}

	void setDefaultFrameSlotsPolicy(OBFrameSlotsPolicy value) {

		defaultFrameSlotsPolicy = value;
	}

	void createAll() {

		new OBRestrictionSlotDeriver(model, this, concepts, properties).createAll();
		new OBDomainRangePairSlotDeriver(model, this, concepts, properties).createAll();
	}

	void checkCreateSlot(OWLClass frameConcept, OWLClassExpression slotExpression) {

		checkAddSlot(frameConcept, checkCreateLooseSlot(slotExpression));
	}

	void checkCreateAllValuesSlot(
			OWLClass frameConcept,
			OWLProperty<?, ?> property,
			OWLObject range) {

		checkAddSlot(frameConcept, checkCreateLooseAllValuesSlot(property, range));
	}

	OBSlot checkCreateLooseSlot(OWLClassExpression slotExpression) {

		SlotSpec spec = slotExpression.accept(specCreator);

		return spec != null ? spec.checkCreate() : null;
	}

	private OBSlot checkCreateLooseAllValuesSlot(
						OWLProperty<?, ?> property,
						OWLObject range) {

		return new AllValuesFromSlotSpec(property, range).checkCreate();
	}

	private void checkAddSlot(OWLClass frameConcept, OBSlot slot) {

		if (slot != null) {

			frames.get(frameConcept).addSlot(slot);
		}
	}
}
