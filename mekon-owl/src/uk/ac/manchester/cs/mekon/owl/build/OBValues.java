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

	static private final Set<OWL2Datatype> STRING_SOURCE_DATATYPES = new HashSet<OWL2Datatype>();

	static {

		STRING_SOURCE_DATATYPES.add(OWL2Datatype.XSD_STRING);
		STRING_SOURCE_DATATYPES.add(OWL2Datatype.RDF_PLAIN_LITERAL);
	}

	private OBFrames frames;
	private OBSlots slots;
	private OBNumbers numbers;

	OBValues(OModel model, OBFrames frames, OBSlots slots) {

		this.frames = frames;
		this.slots = slots;

		numbers = new OBNumbers(model);
	}

	OBValue<?> checkCreateValue(OWLObject source) {

		if (source instanceof OWLClass) {

			return checkCreateAtomicFrameOrNumber((OWLClass)source);
		}

		if (source instanceof OWLObjectIntersectionOf) {

			return checkCreateExtensionFrame((OWLObjectIntersectionOf)source);
		}

		if (source instanceof OWLObjectUnionOf) {

			return checkCreateDisjunctionFrame((OWLObjectUnionOf)source);
		}

		if (source instanceof OWLDataRange) {

			return checkCreateDataValue((OWLDataRange)source);
		}

		return null;
	}

	private OBValue<?> checkCreateAtomicFrameOrNumber(OWLClass source) {

		OBNumber number = numbers.checkExtractNumber(source);

		return number != null ? number : frames.getOrNull(source);
	}

	private OBFrame checkCreateExtensionFrame(OWLObjectIntersectionOf source) {

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

		Set<OBSlot> slots = createSlots(ops);

		return slots.isEmpty() ? null : new OBExtensionFrame(frames.get(named), slots);
	}

	private OBFrame checkCreateDisjunctionFrame(OWLObjectUnionOf source) {

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

	private OBValue<?> checkCreateDataValue(OWLDataRange source) {

		return stringDatatype(source)
				? OBString.SINGLETON
				: numbers.checkCreateNumber(source);
	}

	private OBFrame createDisjunctionFrame(Set<OWLClass> concepts) {

		OBDisjunctionFrame frame = new OBDisjunctionFrame();

		for (OWLClass concept : concepts) {

			frame.addDisjunct(frames.get(concept));
		}

		return frame;
	}

	private Set<OBSlot> createSlots(Set<OWLClassExpression> sources) {

		Set<OBSlot> createdSlots = new HashSet<OBSlot>();

		for (OWLClassExpression source : sources) {

			OBSlot slot = slots.checkCreateLooseSlot(source);

			if (slot != null) {

				createdSlots.add(slot);
			}
		}

		return createdSlots;
	}

	private Set<OWLClass> extractNamedConcepts(Set<OWLClassExpression> ops) {

		Set<OWLClass> namedOps = new HashSet<OWLClass>();

		for (OWLClassExpression op : ops) {

			if (op instanceof OWLClass) {

				OWLClass named = (OWLClass)op;

				if (frames.exists(named)) {

					namedOps.add(named);
				}
			}
		}

		return namedOps;
	}

	private boolean stringDatatype(OWLDataRange range) {

		return range instanceof OWLDatatype && stringDatatype(range.asOWLDatatype());
	}

	private boolean stringDatatype(OWLDatatype datatype) {

		return STRING_SOURCE_DATATYPES.contains(datatype.getBuiltInDatatype());
	}
}
