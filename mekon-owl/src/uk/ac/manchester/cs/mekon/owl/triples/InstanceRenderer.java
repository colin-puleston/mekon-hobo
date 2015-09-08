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

package uk.ac.manchester.cs.mekon.owl.triples;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.rdf.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * @author Colin Puleston
 */
abstract class InstanceRenderer<FN extends OTValue> {

	private FrameSlotValuesRenderer frameValuesRenderer = new FrameSlotValuesRenderer();
	private NumberSlotValuesRenderer numberValuesRenderer = new NumberSlotValuesRenderer();

	private int frameCount = 0;

	private abstract class SlotValuesRenderer<V> {

		void render(FN frameNode, Set<? extends ORSlot<V>> slots) {

			for (ORSlot<V> slot : slots) {

				if (slot.mapsToOWLEntity() && !slot.getValues().isEmpty()) {

					renderValues(frameNode, slot);
				}
			}
		}

		abstract void renderValue(FN frameNode, OT_URI predicate, V value);

		private void renderValues(FN frameNode, ORSlot<V> slot) {

			OT_URI predicate = renderEntityType(slot);

			for (V value : slot.getValues()) {

				renderValue(frameNode, predicate, value);
			}
		}
	}

	private class FrameSlotValuesRenderer extends SlotValuesRenderer<ORFrame> {

		void renderValue(FN frameNode, OT_URI predicate, ORFrame value) {

			renderTriple(frameNode, predicate, renderFrame(value));
		}
	}

	private class NumberSlotValuesRenderer extends SlotValuesRenderer<INumber> {

		void renderValue(FN frameNode, OT_URI predicate, INumber value) {

			if (value.indefinite()) {

				renderRange(frameNode, predicate, value.getType());
			}
			else {

				renderTriple(frameNode, predicate, renderDefiniteNumber(value));
			}
		}

		private void renderRange(FN frameNode, OT_URI predicate, CNumber range) {

			if (range.hasMin()) {

				renderTriple(frameNode, predicate, renderMin(range.getMin()));
			}

			if (range.hasMax()) {

				renderTriple(frameNode, predicate, renderMax(range.getMax()));
			}
		}

		private OTValue renderMin(INumber min) {

			return renderNumberMin(renderDefiniteNumber(min));
		}

		private OTValue renderMax(INumber max) {

			return renderNumberMax(renderDefiniteNumber(max));
		}
	}

	FN renderFrame(ORFrame frame) {

		FN frameNode = renderFrame(frameCount++);

		checkRenderType(frame, frameNode);
		renderSlotValues(frame, frameNode);

		return frameNode;
	}

	abstract FN renderFrame(int index);

	abstract OTValue renderNumberMin(OTNumber value);

	abstract OTValue renderNumberMax(OTNumber value);

	abstract void renderTriple(FN subject, OT_URI predicate, OTValue object);

	abstract void renderUnion(FN subject, OT_URI predicate, Set<OTValue> objects);

	OT_URI renderURI(String uri) {

		return new OT_URI(uri);
	}

	OTNumber renderDefiniteNumber(INumber number) {

		return new OTNumber(number.asTypeNumber());
	}

	private void checkRenderType(ORFrame frame, FN frameNode) {

		OT_URI typePredicate = renderURI(RDFConstants.RDF_TYPE);

		if (frame.disjunctionType()) {

			renderUnion(frameNode, typePredicate, renderFrameTypeDisjuncts(frame));
		}
		else if (frame.mapsToOWLEntity()) {

			renderTriple(frameNode, typePredicate, renderEntityType(frame));
		}
	}

	private void renderSlotValues(ORFrame frame, FN frameNode) {

		frameValuesRenderer.render(frameNode, frame.getFrameSlots());
		numberValuesRenderer.render(frameNode, frame.getNumberSlots());
	}

	private Set<OTValue> renderFrameTypeDisjuncts(ORFrame frame) {

		Set<OTValue> objects = new HashSet<OTValue>();

		for (IRI iri : frame.getTypeDisjunctIRIs()) {

			objects.add(renderURI(iri));
		}

		return objects;
	}

	private OT_URI renderEntityType(ORFramesEntity entity) {

		return renderURI(entity.getIRI());
	}

	private OT_URI renderURI(IRI iri) {

		return renderURI(iri.toString());
	}
}
