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

package uk.ac.manchester.cs.mekon.owl.reason;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * @author Colin Puleston
 */
abstract class Renderer<FR extends OWLObject> {

	private OModel model;
	private OWLDataFactory dataFactory;
	private NumberRenderer defaultNumberRenderer;

	abstract class FrameRenderer {

		private ORFrame frame;

		FrameRenderer(ORFrame frame) {

			this.frame = frame;
		}

		abstract FR render(OWLClassExpression type);

		void renderSlots() {

			for (ORFrameSlot slot : frame.getFrameSlots()) {

				if (slot.mapsToOWLEntity()) {

					renderFrameSlotValues(slot);
				}
			}

			for (ORNumberSlot slot : frame.getNumberSlots()) {

				if (slot.mapsToOWLEntity()) {

					renderNumberSlotValues(slot);
				}
			}
		}

		void addHasValueConstructForFrame(OWLObjectProperty property, FR rendering) {

			addHasValueConstruct(property, toExpression(rendering));
		}

		void addOnlyValuesConstructForFrames(OWLObjectProperty property, Set<FR> renderings) {

			addOnlyValuesConstruct(property, toExpression(renderings));
		}

		abstract void addHasValueConstruct(
							OWLObjectProperty property,
							OWLClassExpression value);

		abstract void addOnlyValuesConstruct(
							OWLObjectProperty property,
							OWLClassExpression value);

		abstract void addValueConstruct(OWLClassExpression construct);

		abstract OWLClassExpression toExpression(FR rendering);

		abstract OWLClassExpression createUnion(Set<FR> renderings);

		private void renderFrameSlotValues(ORFrameSlot slot) {

			new FrameSlotValuesRenderer(this, slot).renderToFrame();
		}

		private void renderNumberSlotValues(ORNumberSlot slot) {

			getNumbersRenderer(slot).renderToFrame();
		}

		private SlotValuesRenderer<INumber, ?> getNumbersRenderer(ORNumberSlot slot) {

			return directNumberSlot(slot)
					? new DirectNumberSlotValuesRenderer(this, slot)
					: new IndirectNumberSlotValuesRenderer(this, slot);
		}

		private OWLClassExpression toExpression(Set<FR> valueRenderings) {

			if (valueRenderings.isEmpty()) {

				return dataFactory.getOWLNothing();
			}

			if (valueRenderings.size() == 1) {

				return toExpression(valueRenderings.iterator().next());
			}

			return createUnion(valueRenderings);
		}

		private boolean directNumberSlot(ORNumberSlot slot) {

			return model.getDataProperties().contains(slot.getIRI());
		}
	}

	private abstract class SlotValuesRenderer<V, IV> {

		private ORSlot<V> slot;

		SlotValuesRenderer(ORSlot<V> slot) {

			this.slot = slot;
		}

		void renderToFrame() {

			Set<IV> intermediates = getIntermediateValues(slot);

			for (IV intermediate : intermediates) {

				addHasValueConstruct(intermediate);
			}

			if (slot.closedWorldSemantics()) {

				addOnlyValuesConstruct(intermediates);
			}
		}

		abstract IV getIntermediateValueOrNull(V value);

		abstract void addHasValueConstruct(IV intermediate);

		abstract void addOnlyValuesConstruct(Set<IV> intermediates);

		OWLObjectProperty getObjectProperty() {

			return model.getObjectProperties().get(slot.getIRI());
		}

		OWLDataProperty getDataProperty() {

			return model.getDataProperties().get(slot.getIRI());
		}

		private Set<IV> getIntermediateValues(ORSlot<V> slot) {

			Set<IV> intermediates = new HashSet<IV>();

			for (V value : slot.getValues()) {

				IV intermediate = getIntermediateValueOrNull(value);

				if (intermediate != null) {

					intermediates.add(intermediate);
				}
			}

			return intermediates;
		}
	}

	private class FrameSlotValuesRenderer
					extends
						SlotValuesRenderer<ORFrame, FR> {

		private FrameRenderer frameRenderer;
		private OWLObjectProperty property;

		FrameSlotValuesRenderer(
			FrameRenderer frameRenderer,
			ORSlot<ORFrame> slot) {

			super(slot);

			this.frameRenderer = frameRenderer;

			property = getObjectProperty();
		}

		FR getIntermediateValueOrNull(ORFrame value) {

			if (value.mapsToOWLEntity()) {

				return renderFrame(value);
			}

			if (value.disjunctionType()) {

				return renderFrame(value, createUnion(value));
			}

			return null;
		}

		void addHasValueConstruct(FR intermediate) {

			frameRenderer.addHasValueConstructForFrame(property, intermediate);
		}

		void addOnlyValuesConstruct(Set<FR> intermediates) {

			frameRenderer.addOnlyValuesConstructForFrames(property, intermediates);
		}

		private OWLObjectUnionOf createUnion(ORFrame disjunction) {

			Set<OWLClass> ops = new HashSet<OWLClass>();

			for (IRI iri : disjunction.getTypeDisjunctIRIs()) {

				ops.add(getConcept(iri));
			}

			return dataFactory.getOWLObjectUnionOf(ops);
		}
	}

	private class IndirectNumberSlotValuesRenderer
					extends
						SlotValuesRenderer<INumber, OWLClassExpression> {

		private FrameRenderer frameRenderer;
		private OWLObjectProperty property;

		IndirectNumberSlotValuesRenderer(
			FrameRenderer frameRenderer,
			ORSlot<INumber> slot) {

			super(slot);

			this.frameRenderer = frameRenderer;

			property = getObjectProperty();
		}

		OWLClassExpression getIntermediateValueOrNull(INumber value) {

			return defaultNumberRenderer.renderHasValue(value);
		}

		void addHasValueConstruct(OWLClassExpression intermediate) {

			frameRenderer.addHasValueConstruct(property, intermediate);
		}

		void addOnlyValuesConstruct(Set<OWLClassExpression> intermediates) {

			frameRenderer.addOnlyValuesConstruct(property, createUnion(intermediates));
		}

		private OWLClassExpression createUnion(Set<OWLClassExpression> exprs) {

			return dataFactory.getOWLObjectUnionOf(exprs);
		}
	}

	private class DirectNumberSlotValuesRenderer
					extends
						SlotValuesRenderer<INumber, INumber> {

		private FrameRenderer frameRenderer;
		private NumberRenderer numberRenderer;

		DirectNumberSlotValuesRenderer(
			FrameRenderer frameRenderer,
			ORSlot<INumber> slot) {

			super(slot);

			this.frameRenderer = frameRenderer;

			numberRenderer = new NumberRenderer(model, getDataProperty());
		}

		INumber getIntermediateValueOrNull(INumber value) {

			return value;
		}

		void addHasValueConstruct(INumber intermediate) {

			frameRenderer.addValueConstruct(numberRenderer.renderHasValue(intermediate));
		}

		void addOnlyValuesConstruct(Set<INumber> intermediates) {

			frameRenderer.addValueConstruct(numberRenderer.renderOnlyValues(intermediates));
		}
	}

	Renderer(OModel model) {

		this.model = model;

		dataFactory = model.getDataFactory();
		defaultNumberRenderer = new NumberRenderer(model);
	}

	FR renderFrame(ORFrame frame) {

		return renderFrame(frame, getConcept(frame));
	}

	abstract FrameRenderer createFrameRenderer(ORFrame frame);

	private FR renderFrame(ORFrame frame, OWLClassExpression type) {

		return createFrameRenderer(frame).render(type);
	}

	private OWLClass getConcept(ORFrame frame) {

		return getConcept(frame.getIRI());
	}

	private OWLClass getConcept(IRI iri) {

		return model.getConcepts().get(iri);
	}
}
