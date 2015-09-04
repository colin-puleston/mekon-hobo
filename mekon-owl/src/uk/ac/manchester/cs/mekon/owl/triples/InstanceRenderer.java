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
abstract class InstanceRenderer {

	private int frameCount = 0;

	private class NumberSlotValuesRenderer {

		private OT_URI frameNode;
		private OT_URI slotPredicate;

		NumberSlotValuesRenderer(OT_URI frameNode, ORNumberSlot slot) {

			this.frameNode = frameNode;

			slotPredicate = renderEntityType(slot);

			for (INumber value : slot.getValues()) {

				renderValue(value);
			}
		}

		private void renderValue(INumber value) {

			if (value.indefinite()) {

				renderRange(value.getType());
			}
			else {

				renderTriple(renderDefiniteNumber(value));
			}
		}

		private void renderRange(CNumber range) {

			if (range.hasMin()) {

				OTNumber min = renderDefiniteNumber(range.getMin());

				renderTriple(renderNumberMin(min));
			}

			if (range.hasMax()) {

				OTNumber max = renderDefiniteNumber(range.getMax());

				renderTriple(renderNumberMax(max));
			}
		}

		private void renderTriple(OTValue value) {

			InstanceRenderer.this.renderTriple(frameNode, slotPredicate, value);
		}
	}

	OT_URI renderFrame(ORFrame frame) {

		OT_URI frameNode = renderFrame(frameCount++);

		checkRenderType(frame, frameNode);
		renderSlotValues(frame, frameNode);

		return frameNode;
	}

	abstract OT_URI renderFrame(int index);

	abstract OT_URI renderURI(String uri);

	abstract OTNumber renderNumber(Integer number);

	abstract OTNumber renderNumber(Long number);

	abstract OTNumber renderNumber(Float number);

	abstract OTNumber renderNumber(Double number);

	abstract OTValue renderNumberMin(OTNumber value);

	abstract OTValue renderNumberMax(OTNumber value);

	abstract void renderTriple(OT_URI subject, OT_URI predicate, OTValue object);

	abstract void renderUnion(OT_URI subject, OT_URI predicate, Set<OTValue> objects);

	private void checkRenderType(ORFrame frame, OT_URI frameNode) {

		OT_URI typePredicate = renderURI(RDFConstants.RDF_TYPE);

		if (frame.disjunctionType()) {

			renderUnion(frameNode, typePredicate, renderFrameTypeDisjuncts(frame));
		}
		else if (frame.mapsToOWLEntity()) {

			renderTriple(frameNode, typePredicate, renderEntityType(frame));
		}
	}

	private void renderSlotValues(ORFrame frame, OT_URI frameNode) {

		for (ORFrameSlot slot : frame.getFrameSlots()) {

			if (slot.mapsToOWLEntity()) {

				renderFrameSlotValues(frameNode, slot);
			}
		}

		for (ORNumberSlot slot : frame.getNumberSlots()) {

			if (slot.mapsToOWLEntity()) {

				new NumberSlotValuesRenderer(frameNode, slot);
			}
		}
	}

	private void renderFrameSlotValues(OT_URI frameNode, ORFrameSlot slot) {

		OT_URI slotPredicate = renderEntityType(slot);

		for (ORFrame value : slot.getValues()) {

			renderTriple(frameNode, slotPredicate, renderFrame(value));
		}
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

	private OTNumber renderDefiniteNumber(INumber number) {

		if (number.hasNumberType(Integer.class)) {

			return renderNumber(number.asInteger());
		}

		if (number.hasNumberType(Long.class)) {

			return renderNumber(number.asLong());
		}

		if (number.hasNumberType(Float.class)) {

			return renderNumber(number.asFloat());
		}

		if (number.hasNumberType(Double.class)) {

			return renderNumber(number.asDouble());
		}

		throw new Error("Unexpected number-type: " + number.getNumberType());
	}

	private OT_URI renderURI(IRI iri) {

		return renderURI(iri.toString());
	}
}
