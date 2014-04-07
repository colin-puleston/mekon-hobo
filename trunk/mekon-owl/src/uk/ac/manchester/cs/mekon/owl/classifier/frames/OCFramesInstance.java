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

package uk.ac.manchester.cs.mekon.owl.classifier.frames;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;
import uk.ac.manchester.cs.mekon.owl.classifier.semantics.*;

/**
 * Main class in the pre-processable frames-based instance
 * representation.
 *
 * @author Colin Puleston
 */
public class OCFramesInstance {

	private Set<IRI> conceptIRIs;
	private Set<IRI> objectPropertyIRIs;
	private OCSlotSemantics slotSemantics;
	private OCFrame rootFrame;

	private IFrameSlotBuilder iFrameSlotBuilder = new IFrameSlotBuilder();
	private CFrameSlotBuilder cFrameSlotBuilder = new CFrameSlotBuilder();
	private NumberSlotBuilder numberSlotBuilder = new NumberSlotBuilder();

	private Stack<IFrame> iFrameStack = new Stack<IFrame>();

	private abstract class TypeSlotBuilder<V, S extends OCSlot<V>, IV> {

		void build(OCFrame oFrame, ISlot iSlot, List<IV> iValues) {

			S oSlot = createSlot(iSlot);

			for (IV iValue : iValues) {

				oSlot.addValue(getValue(iValue));
			}

			addSlot(oFrame, oSlot);
		}

		abstract V getValue(IV iValue);

		abstract S createSlot(ISlot iSlot, IRI iri);

		abstract void addSlot(OCFrame oFrame, S oSlot);

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
									TypeSlotBuilder<OCFrame, OCConceptSlot, IV> {

		OCConceptSlot createSlot(ISlot iSlot, IRI iri) {

			return new OCConceptSlot(iSlot, iri);
		}

		void addSlot(OCFrame oFrame, OCConceptSlot oSlot) {

			oFrame.addSlot(oSlot);
		}
	}

	private class IFrameSlotBuilder extends ConceptSlotBuilder<IFrame> {

		OCFrame getValue(IFrame iValue) {

			return buildFrame(iValue);
		}
	}

	private class CFrameSlotBuilder extends ConceptSlotBuilder<CFrame> {

		OCFrame getValue(CFrame iValue) {

			return createFrame(iValue);
		}
	}

	private class NumberSlotBuilder
						extends
							TypeSlotBuilder<INumber, OCNumberSlot, INumber> {

		INumber getValue(INumber iValue) {

			return iValue;
		}

		OCNumberSlot createSlot(ISlot iSlot, IRI iri) {

			return new OCNumberSlot(iSlot, iri);
		}

		void addSlot(OCFrame oFrame, OCNumberSlot oSlot) {

			oFrame.addSlot(oSlot);
		}
	}

	private class SlotBuilder extends ISlotValuesVisitor {

		private ISlot iSlot;
		private OCFrame oFrame;

		protected void visit(CFrame valueType, List<IFrame> values) {

			iFrameSlotBuilder.build(oFrame, iSlot, values);
		}

		protected void visit(CNumber valueType, List<INumber> values) {

			numberSlotBuilder.build(oFrame, iSlot, values);
		}

		protected void visit(MFrame valueType, List<CFrame> values) {

			cFrameSlotBuilder.build(oFrame, iSlot, values);
		}

		SlotBuilder(ISlot iSlot, OCFrame oFrame) {

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
	public OCFramesInstance(
				OModel model,
				OCSlotSemantics slotSemantics,
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
	public OCFrame getRootFrame() {

		return rootFrame;
	}

	private OCFrame buildFrame(IFrame iFrame) {

		checkForCycle(iFrame);
		iFrameStack.push(iFrame);

		OCFrame oFrame = createFrame(iFrame.getType());

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

	private OCFrame createFrame(CFrame cFrame) {

		return cFrame.disjunction()
				? createDisjunctionFrame(cFrame)
				: createModelFrame(cFrame);
	}

	private OCFrame createDisjunctionFrame(CFrame cFrame) {

		OCFrame oFrame = new OCFrame(cFrame, null);

		for (CFrame disjunct : cFrame.getSubs()) {

			IRI iri = getNearestConceptIRIOrNull(disjunct);

			if (iri != null) {

				oFrame.addTypeDisjunctIRI(iri);
			}
		}

		return oFrame;
	}

	private OCFrame createModelFrame(CFrame cFrame) {

		return new OCFrame(cFrame, getNearestConceptIRIOrNull(cFrame));
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

		return model.getAllConcepts().getAllIRIs();
	}

	private Set<IRI> getAllObjectPropertyIRIs(OModel model) {

		return model.getAllObjectProperties().getAllIRIs();
	}

	private boolean closedWorldSemantics(IRI propertyIRI) {

		return slotSemantics.getSemantics(propertyIRI) == OCSemantics.CLOSED_WORLD;
	}
}
