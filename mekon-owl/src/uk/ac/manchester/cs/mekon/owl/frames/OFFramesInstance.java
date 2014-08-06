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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Main class in the pre-processable frames-based instance
 * representation.
 *
 * @author Colin Puleston
 */
public class OFFramesInstance {

	private Set<IRI> conceptIRIs;
	private Set<IRI> objectPropertyIRIs;
	private OFSlotSemantics slotSemantics;
	private OFFrame rootFrame;

	private IFrameSlotBuilder iFrameSlotBuilder = new IFrameSlotBuilder();
	private CFrameSlotBuilder cFrameSlotBuilder = new CFrameSlotBuilder();
	private NumberSlotBuilder numberSlotBuilder = new NumberSlotBuilder();

	private Stack<IFrame> iFrameStack = new Stack<IFrame>();

	private abstract class TypeSlotBuilder<V, S extends OFSlot<V>, IV> {

		void build(OFFrame oFrame, ISlot iSlot, List<IV> iValues) {

			S oSlot = createSlot(iSlot);

			for (IV iValue : iValues) {

				oSlot.addValue(getValue(iValue));
			}

			addSlot(oFrame, oSlot);
		}

		abstract V getValue(IV iValue);

		abstract S createSlot(ISlot iSlot, IRI iri);

		abstract void addSlot(OFFrame oFrame, S oSlot);

		private S createSlot(ISlot iSlot) {

			IRI iri = getIRIOrNull(iSlot);
			S slot = createSlot(iSlot, iri);

			if (iri != null) {

				slot.setClosedWorldSemantics(closedWorldSemantics(iri));
			}

			return slot;
		}

		private IRI getIRIOrNull(ISlot iSlot) {

			return getObjectPropertyIRIOrNull(iSlot.getType().getProperty());
		}
	}

	private abstract class ConceptSlotBuilder<IV>
								extends
									TypeSlotBuilder<OFFrame, OFConceptSlot, IV> {

		OFConceptSlot createSlot(ISlot iSlot, IRI iri) {

			return new OFConceptSlot(iSlot, iri);
		}

		void addSlot(OFFrame oFrame, OFConceptSlot oSlot) {

			oFrame.addSlot(oSlot);
		}
	}

	private class IFrameSlotBuilder extends ConceptSlotBuilder<IFrame> {

		OFFrame getValue(IFrame iValue) {

			return buildFrame(iValue);
		}
	}

	private class CFrameSlotBuilder extends ConceptSlotBuilder<CFrame> {

		OFFrame getValue(CFrame iValue) {

			return createFrame(iValue);
		}
	}

	private class NumberSlotBuilder
						extends
							TypeSlotBuilder<INumber, OFNumberSlot, INumber> {

		INumber getValue(INumber iValue) {

			return iValue;
		}

		OFNumberSlot createSlot(ISlot iSlot, IRI iri) {

			return new OFNumberSlot(iSlot, iri);
		}

		void addSlot(OFFrame oFrame, OFNumberSlot oSlot) {

			oFrame.addSlot(oSlot);
		}
	}

	private class SlotBuilder extends ISlotValuesVisitor {

		private ISlot iSlot;
		private OFFrame oFrame;

		protected void visit(CFrame valueType, List<IFrame> values) {

			iFrameSlotBuilder.build(oFrame, iSlot, values);
		}

		protected void visit(CNumber valueType, List<INumber> values) {

			numberSlotBuilder.build(oFrame, iSlot, values);
		}

		protected void visit(MFrame valueType, List<CFrame> values) {

			cFrameSlotBuilder.build(oFrame, iSlot, values);
		}

		SlotBuilder(ISlot iSlot, OFFrame oFrame) {

			this.iSlot = iSlot;
			this.oFrame = oFrame;

			visit(iSlot);
		}
	}

	/**
	 * Constuctor.
	 *
	 * @param model Relevant model
	 * @param slotSemantics Semantics to be used in generating OWL
	 * constructs
	 * @param iFrame Instance-level frame from which instance is to be
	 * derived
	 */
	public OFFramesInstance(
				OModel model,
				OFSlotSemantics slotSemantics,
				IFrame iFrame) {

		this.slotSemantics = slotSemantics;

		conceptIRIs = getAllConceptIRIs(model);
		objectPropertyIRIs = getAllObjectPropertyIRIs(model);

		rootFrame = buildFrame(iFrame);
	}

	/**
	 * Provides the root-frame in the pre-processable instance
	 * representation
	 *
	 * @return Root-frame in instance representation
	 */
	public OFFrame getRootFrame() {

		return rootFrame;
	}

	private OFFrame buildFrame(IFrame iFrame) {

		checkForCycle(iFrame);
		iFrameStack.push(iFrame);

		OFFrame oFrame = createFrame(iFrame.getType());

		oFrame.setIFrame(iFrame);

		for (ISlot iSlot : iFrame.getSlots().asList()) {

			new SlotBuilder(iSlot, oFrame);
		}

		iFrameStack.pop();

		return oFrame;
	}

	private void checkForCycle(IFrame iFrame) {

		if (iFrameStack.contains(iFrame)) {

			throw new KModelException(
						"Cannot handle cyclic description involving: "
						+ iFrame);
		}
	}

	private OFFrame createFrame(CFrame cFrame) {

		return cFrame.getCategory().disjunction()
				? createDisjunctionFrame(cFrame)
				: createModelFrame(cFrame);
	}

	private OFFrame createDisjunctionFrame(CFrame cFrame) {

		OFFrame oFrame = new OFFrame(cFrame, null);

		for (CFrame disjunct : cFrame.getSubs()) {

			IRI iri = getNearestConceptIRIOrNull(disjunct);

			if (iri != null) {

				oFrame.addTypeDisjunctIRI(iri);
			}
		}

		return oFrame;
	}

	private OFFrame createModelFrame(CFrame cFrame) {

		return new OFFrame(cFrame, getNearestConceptIRIOrNull(cFrame));
	}

	private IRI getNearestConceptIRIOrNull(CFrame cFrame) {

		IRI iri = getConceptIRIOrNull(cFrame);

		if (iri == null) {

			for (CFrame sup : cFrame.getSupers()) {

				iri = getNearestConceptIRIOrNull(sup);

				if (iri != null) {

					break;
				}
			}
		}

		return iri;
	}

	private IRI getConceptIRIOrNull(CFrame frame) {

		return getIRIOrNull(frame, conceptIRIs);
	}

	private IRI getObjectPropertyIRIOrNull(CProperty property) {

		return getIRIOrNull(property, objectPropertyIRIs);
	}

	private IRI getIRIOrNull(CIdentified entity, Set<IRI> validIRIs) {

		IRI iri = O_IRIExtractor.extractIRI(entity);

		return iri != null && validIRIs.contains(iri) ? iri : null;
	}

	private Set<IRI> getAllConceptIRIs(OModel model) {

		return model.getConcepts().getAllIRIs();
	}

	private Set<IRI> getAllObjectPropertyIRIs(OModel model) {

		return model.getObjectProperties().getAllIRIs();
	}

	private boolean closedWorldSemantics(IRI propertyIRI) {

		return slotSemantics.getSemantics(propertyIRI) == OFSemantics.CLOSED_WORLD;
	}
}
