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

package uk.ac.manchester.cs.mekon.owl.frames;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
abstract class OFFrameRenderer<FR extends OWLObject> {

	private OModel model;
	private OWLDataFactory dataFactory;
	private OFNumberRenderer numberRenderer;

	abstract class FrameRenderer {

		private OFFrame frame;

		FrameRenderer(OFFrame frame) {

			this.frame = frame;
		}

		abstract FR render(OWLClassExpression type);

		void renderSlots() {

			for (OFConceptSlot slot : frame.getConceptSlots()) {

				if (slot.mapsToOWLEntity()) {

					renderConceptSlotValues(slot);
				}
			}

			for (OFNumberSlot slot : frame.getNumberSlots()) {

				if (slot.mapsToOWLEntity()) {

					renderNumberSlotValues(slot);
				}
			}
		}

		void addHasValueForFrame(OWLObjectProperty property, FR rendering) {

			addHasValueForExpr(property, toExpression(rendering));
		}

		void addHasValueForExpr(OWLObjectProperty property, OWLClassExpression expr) {

			addValueExpression(
				dataFactory
					.getOWLObjectSomeValuesFrom(
						property,
						expr));
		}

		void addOnlyValuesForFrames(OWLObjectProperty property, Set<FR> renderings) {

			addOnlyValuesForExpr(property, toExpression(renderings));
		}

		void addOnlyValuesForExpr(OWLObjectProperty property, OWLClassExpression expr) {

			addValueExpression(
				dataFactory
					.getOWLObjectAllValuesFrom(
						property,
						expr));
		}

		abstract void addValueExpression(OWLClassExpression expr);

		abstract OWLClassExpression toExpression(FR rendering);

		abstract OWLClassExpression createUnion(Set<FR> renderings);

		private void renderConceptSlotValues(OFConceptSlot slot) {

			new ConceptSlotValuesRenderer(this, slot).renderToFrame();
		}

		private void renderNumberSlotValues(OFNumberSlot slot) {

			new NumberSlotValuesRenderer(this, slot).renderToFrame();
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
	}

	private abstract class SlotValuesRenderer<V, VR> {

		private FrameRenderer frameRenderer;
		private OFSlot<V> slot;
		private OWLObjectProperty property;

		SlotValuesRenderer(FrameRenderer frameRenderer, OFSlot<V> slot) {

			this.frameRenderer = frameRenderer;
			this.slot = slot;

			property = getProperty();
		}

		void renderToFrame() {

			Set<VR> valueRenderings = renderValues(slot);

			for (VR valueRendering : valueRenderings) {

				addHasValue(valueRendering);
			}

			if (slot.closedWorldSemantics()) {

				addOnlyValues(valueRenderings);
			}
		}

		abstract Set<VR> renderValues(OFSlot<V> slot);

		abstract void addHasValue(VR valueRendering);

		abstract void addOnlyValues(Set<VR> valueRenderings);

		void addHasValueForFrame(FR rendering) {

			frameRenderer.addHasValueForFrame(property, rendering);
		}

		void addHasValueForExpr(OWLClassExpression expr) {

			frameRenderer.addHasValueForExpr(property, expr);
		}

		void addOnlyValuesForFrames(Set<FR> renderings) {

			frameRenderer.addOnlyValuesForFrames(property, renderings);
		}

		void addOnlyValuesForExpr(OWLClassExpression expr) {

			frameRenderer.addOnlyValuesForExpr(property, expr);
		}

		private OWLObjectProperty getProperty() {

			return model.getObjectProperties().get(slot.getIRI());
		}
	}

	private class ConceptSlotValuesRenderer
					extends
						SlotValuesRenderer<OFFrame, FR> {

		ConceptSlotValuesRenderer(
			FrameRenderer frameRenderer,
			OFSlot<OFFrame> slot) {

			super(frameRenderer, slot);
		}

		Set<FR> renderValues(OFSlot<OFFrame> slot) {

			Set<FR> renderings = new HashSet<FR>();

			for (OFFrame value : slot.getValues()) {

				FR rendering = renderValueOrNull(value);

				if (rendering != null) {

					renderings.add(rendering);
				}
			}

			return renderings;
		}

		void addHasValue(FR valueRendering) {

			addHasValueForFrame(valueRendering);
		}

		void addOnlyValues(Set<FR> valueRenderings) {

			addOnlyValuesForFrames(valueRenderings);
		}

		private FR renderValueOrNull(OFFrame value) {

			if (value.mapsToOWLEntity()) {

				return renderFrame(value);
			}

			if (value.disjunctionType()) {

				return renderFrame(value, createUnion(value));
			}

			return null;
		}

		private OWLObjectUnionOf createUnion(OFFrame disjunction) {

			Set<OWLClass> ops = new HashSet<OWLClass>();

			for (IRI iri : disjunction.getTypeDisjunctIRIs()) {

				ops.add(getConcept(iri));
			}

			return dataFactory.getOWLObjectUnionOf(ops);
		}
	}

	private class NumberSlotValuesRenderer
					extends
						SlotValuesRenderer<INumber, OWLClassExpression> {

		NumberSlotValuesRenderer(
			FrameRenderer frameRenderer,
			OFSlot<INumber> slot) {

			super(frameRenderer, slot);
		}

		Set<OWLClassExpression> renderValues(OFSlot<INumber> slot) {

			Set<OWLClassExpression> renderings = new HashSet<OWLClassExpression>();

			if (slot.hasValues()) {

				INumber value = slot.getValues().iterator().next();

				renderings.add(numberRenderer.render(value));
			}

			return renderings;
		}

		void addHasValue(OWLClassExpression valueRendering) {

			addHasValueForExpr(valueRendering);
		}

		void addOnlyValues(Set<OWLClassExpression> valueRenderings) {

			addOnlyValuesForExpr(createUnion(valueRenderings));
		}

		private OWLClassExpression createUnion(Set<OWLClassExpression> exprs) {

			return dataFactory.getOWLObjectUnionOf(exprs);
		}
	}

	OFFrameRenderer(OModel model) {

		this.model = model;

		dataFactory = model.getDataFactory();
		numberRenderer = new OFNumberRenderer(model);
	}

	FR renderFrame(OFFrame frame) {

		return renderFrame(frame, getConcept(frame));
	}

	abstract FrameRenderer createFrameRenderer(OFFrame frame);

	private FR renderFrame(OFFrame frame, OWLClassExpression type) {

		return createFrameRenderer(frame).render(type);
	}

	private OWLClass getConcept(OFFrame frame) {

		return getConcept(frame.getIRI());
	}

	private OWLClass getConcept(IRI iri) {

		return model.getConcepts().get(iri);
	}
}
