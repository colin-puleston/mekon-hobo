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
	private NumberRenderer numberRenderer;

	abstract class FrameRenderer {

		private ORFrame frame;

		FrameRenderer(ORFrame frame) {

			this.frame = frame;
		}

		abstract FR render(OWLClassExpression type);

		void renderSlots() {

			for (ORConceptSlot slot : frame.getConceptSlots()) {

				if (slot.mapsToOWLEntity()) {

					renderConceptSlotValues(slot);
				}
			}

			for (ORNumberSlot slot : frame.getNumberSlots()) {

				if (slot.mapsToOWLEntity()) {

					renderNumberSlotValues(slot);
				}
			}
		}

		void addHasValueForFrame(OWLObjectProperty property, FR rendering) {

			addHasValueForExpr(property, toExpression(rendering));
		}

		void addOnlyValuesForFrames(OWLObjectProperty property, Set<FR> renderings) {

			addOnlyValuesForExpr(property, toExpression(renderings));
		}

		abstract void addHasValueForExpr(
							OWLObjectProperty property,
							OWLClassExpression expr);

		abstract void addOnlyValuesForExpr(
							OWLObjectProperty property,
							OWLClassExpression expr);

		abstract void addExpr(OWLClassExpression expr);

		abstract OWLClassExpression toExpression(FR rendering);

		abstract OWLClassExpression createUnion(Set<FR> renderings);

		private void renderConceptSlotValues(ORConceptSlot slot) {

			new ConceptSlotValuesRenderer(this, slot).renderToFrame();
		}

		private void renderNumberSlotValues(ORNumberSlot slot) {

			if (directNumberSlot(slot)) {

				renderDirectNumberSlotValues(slot);
			}
			else {

				renderIndirectNumberSlotValues(slot);
			}
		}

		private void renderDirectNumberSlotValues(ORNumberSlot slot) {

			if (slot.hasValues()) {

				OWLDataProperty property = getDataProperty(slot);
				INumber value = slot.getValues().iterator().next();

				addExpr(numberRenderer.render(property, value));
			}
		}

		private void renderIndirectNumberSlotValues(ORNumberSlot slot) {

			new IndirectNumberSlotValuesRenderer(this, slot).renderToFrame();
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

		private OWLDataProperty getDataProperty(ORNumberSlot slot) {

			return model.getDataProperties().get(slot.getIRI());
		}
	}

	private abstract class ObjectPropertyBasedSlotValuesRenderer<V, VR> {

		private ORSlot<V> slot;
		private OWLObjectProperty property;

		ObjectPropertyBasedSlotValuesRenderer(ORSlot<V> slot) {

			this.slot = slot;

			property = getObjectProperty();
		}

		void renderToFrame() {

			Set<VR> valueRenderings = renderValues(slot);

			for (VR valueRendering : valueRenderings) {

				addHasValue(property, valueRendering);
			}

			if (slot.closedWorldSemantics()) {

				addOnlyValues(property, valueRenderings);
			}
		}

		abstract Set<VR> renderValues(ORSlot<V> slot);

		abstract void addHasValue(OWLObjectProperty property, VR valueRendering);

		abstract void addOnlyValues(OWLObjectProperty property, Set<VR> valueRenderings);

		private OWLObjectProperty getObjectProperty() {

			return model.getObjectProperties().get(slot.getIRI());
		}
	}

	private class ConceptSlotValuesRenderer
					extends
						ObjectPropertyBasedSlotValuesRenderer<ORFrame, FR> {

		private FrameRenderer frameRenderer;

		ConceptSlotValuesRenderer(
			FrameRenderer frameRenderer,
			ORSlot<ORFrame> slot) {

			super(slot);

			this.frameRenderer = frameRenderer;
		}

		Set<FR> renderValues(ORSlot<ORFrame> slot) {

			Set<FR> renderings = new HashSet<FR>();

			for (ORFrame value : slot.getValues()) {

				FR rendering = renderValueOrNull(value);

				if (rendering != null) {

					renderings.add(rendering);
				}
			}

			return renderings;
		}

		void addHasValue(OWLObjectProperty property, FR valueRendering) {

			frameRenderer.addHasValueForFrame(property, valueRendering);
		}

		void addOnlyValues(OWLObjectProperty property, Set<FR> valueRenderings) {

			frameRenderer.addOnlyValuesForFrames(property, valueRenderings);
		}

		private FR renderValueOrNull(ORFrame value) {

			if (value.mapsToOWLEntity()) {

				return renderFrame(value);
			}

			if (value.disjunctionType()) {

				return renderFrame(value, createUnion(value));
			}

			return null;
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
						ObjectPropertyBasedSlotValuesRenderer<INumber, OWLClassExpression> {

		private FrameRenderer frameRenderer;

		IndirectNumberSlotValuesRenderer(
			FrameRenderer frameRenderer,
			ORSlot<INumber> slot) {

			super(slot);

			this.frameRenderer = frameRenderer;
		}

		Set<OWLClassExpression> renderValues(ORSlot<INumber> slot) {

			Set<OWLClassExpression> renderings = new HashSet<OWLClassExpression>();

			if (slot.hasValues()) {

				INumber value = slot.getValues().iterator().next();

				renderings.add(numberRenderer.render(value));
			}

			return renderings;
		}

		void addHasValue(OWLObjectProperty property, OWLClassExpression valueRendering) {

			frameRenderer.addHasValueForExpr(property, valueRendering);
		}

		void addOnlyValues(OWLObjectProperty property, Set<OWLClassExpression> valueRenderings) {

			frameRenderer.addOnlyValuesForExpr(property, createUnion(valueRenderings));
		}

		private OWLClassExpression createUnion(Set<OWLClassExpression> exprs) {

			return dataFactory.getOWLObjectUnionOf(exprs);
		}
	}

	Renderer(OModel model) {

		this.model = model;

		dataFactory = model.getDataFactory();
		numberRenderer = new NumberRenderer(model);
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
