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
import org.semanticweb.owlapi.vocab.*;

import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class OBValues {

	private OBFrames frames;
	private OBSlots slots;
	private OBNumbers numbers;

	private class ValueSpec {

		private OWLObject source;

		ValueSpec(OWLObject source) {

			this.source = validSource(source) ? source : null;
		}

		OBValue<?> checkCreate() {

			if (source instanceof OWLClass) {

				return checkCreate((OWLClass)source);
			}

			if (source instanceof OWLObjectIntersectionOf) {

				return checkCreate((OWLObjectIntersectionOf)source);
			}

			if (source instanceof OWLObjectUnionOf) {

				return checkCreate((OWLObjectUnionOf)source);
			}

			if (source instanceof OWLDataRange) {

				return checkCreate((OWLDataRange)source);
			}

			return null;
		}

		private OBValue<?> checkCreate(OWLClass source) {

			OBNumber number = numbers.checkExtractNumber(source);

			return number != null ? number : frames.get(source);
		}

		private OBFrame checkCreate(OWLObjectIntersectionOf source) {

			Set<OWLClassExpression> ops = OWLAPIVersion.getOperands(source);
			Set<OWLClass> namedOps = extractNamedConcepts(ops);

			if (namedOps.size() != 1) {

				return null;
			}

			OWLClass named = namedOps.iterator().next();

			if (ops.size() == 1) {

				return frames.get(named);
			}

			ops.remove(named);

			return createExtensionFrame(named, ops);
		}

		private OBFrame checkCreate(OWLObjectUnionOf source) {

			Set<OWLClassExpression> ops = OWLAPIVersion.getOperands(source);
			Set<OWLClass> namedOps = extractNamedConcepts(ops);

			if (namedOps.isEmpty()) {

				return null;
			}

			if (namedOps.size() == 1) {

				return frames.get(namedOps.iterator().next());
			}

			return createDisjunctionFrame(namedOps);
		}

		private OBValue<?> checkCreate(OWLDataRange source) {

			return stringDatatype(source)
					? OBString.SINGLETON
					: numbers.checkCreateNumber(source);
		}

		private OBFrame createExtensionFrame(
							OWLClass named,
							Set<OWLClassExpression> slotSources) {

			OBAtomicFrame base = frames.get(named);
			OBExtensionFrame frame = new OBExtensionFrame(base);

			for (OWLClassExpression slotSource : slotSources) {

				OBSlot slot = slots.checkCreateLooseSlot(slotSource);

				if (slot != null) {

					frame.addSlot(slot);
				}
			}

			return frame;
		}

		private OBFrame createDisjunctionFrame(Set<OWLClass> concepts) {

			OBDisjunctionFrame frame = new OBDisjunctionFrame();

			for (OWLClass concept : concepts) {

				frame.addDisjunct(frames.get(concept));
			}

			return frame;
		}

		private Set<OWLClass> extractNamedConcepts(Set<OWLClassExpression> ops) {

			Set<OWLClass> namedOps = new HashSet<OWLClass>();

			for (OWLClassExpression op : ops) {

				if (op instanceof OWLClass) {

					namedOps.add((OWLClass)op);
				}
			}

			return namedOps;
		}

		private boolean validSource(OWLObject source) {

			return source instanceof OWLClassExpression
					|| source instanceof OWLDataRange;
		}

		private boolean stringDatatype(OWLDataRange range) {

			return range instanceof OWLDatatype && stringDatatype(range.asOWLDatatype());
		}

		private boolean stringDatatype(OWLDatatype datatype) {

			return datatype.getBuiltInDatatype() == OWL2Datatype.XSD_STRING;
		}
	}

	OBValues(OModel model, OBFrames frames, OBSlots slots) {

		this.frames = frames;
		this.slots = slots;

		numbers = new OBNumbers(model);
	}

	OBValue<?> checkCreateValue(OWLObject source) {

		return new ValueSpec(source).checkCreate();
	}
}
