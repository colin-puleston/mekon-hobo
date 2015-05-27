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

import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class OBValues {

	private OModel model;
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

				return createFromClassSource();
			}

			if (source instanceof OWLClassExpression) {

				return checkCreateExpressionFrame();
			}

			if (source instanceof OWLDataRange) {

				return checkCreateNumber();
			}

			return null;
		}

		private OBValue<?> createFromClassSource() {

			OWLClass classSource = (OWLClass)source;
			OBNumber number = numbers.checkExtractNumber(classSource);

			return number != null ? number : frames.get(classSource);
		}

		private OBFrame checkCreateExpressionFrame() {

			OBFrame frame = checkCreateExtensionFrame();

			return frame != null ? frame : checkCreateDisjunctionFrame();
		}

		private OBFrame checkCreateExtensionFrame() {

			if (source instanceof OWLObjectIntersectionOf) {

				OWLObjectIntersectionOf intSource = (OWLObjectIntersectionOf)source;
				Set<OWLClassExpression> ops = intSource.getOperands();
				OWLClass named = getSoleNamedClassOrNull(ops);

				if (named != null) {

					ops.remove(named);

					return createExtensionFrame(named, ops);
				}
			}

			return null;
		}

		private OBFrame createExtensionFrame(OWLClass named, Set<OWLClassExpression> ops) {

			OBAtomicFrame base = frames.get(named);
			OBExtensionFrame frame = new OBExtensionFrame(base);

			for (OWLClassExpression op : ops) {

				OBSlot slot = slots.checkCreateSlot(op);

				if (slot != null) {

					frame.addSlot(slot);
				}
			}

			return frame;
		}

		private OBFrame checkCreateDisjunctionFrame() {

			OWLClassExpression exprSource = (OWLClassExpression)source;
			Set<OWLClass> concepts = getSubConcepts(exprSource);

			return concepts.isEmpty() ? null : createDisjunctionFrame(concepts);
		}

		private OBFrame createDisjunctionFrame(Set<OWLClass> concepts) {

			OBDisjunctionFrame frame = new OBDisjunctionFrame();

			for (OWLClass concept : concepts) {

				frame.addDisjunct(frames.get(concept));
			}

			return frame;
		}

		private OBNumber checkCreateNumber() {

			return numbers.checkCreateNumber((OWLDataRange)source);
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

		private boolean validSource(OWLObject source) {

			return source instanceof OWLClassExpression
					|| source instanceof OWLDataRange;
		}
	}

	OBValues(OModel model, OBFrames frames, OBSlots slots) {

		this.model = model;
		this.frames = frames;
		this.slots = slots;

		numbers = new OBNumbers(model);
	}

	OBValue<?> checkCreateValue(OWLObject source) {

		return new ValueSpec(source).checkCreate();
	}

	private Set<OWLClass> getSubConcepts(OWLClassExpression expression) {

		return model.getInferredSubs(expression, true);
	}
}
