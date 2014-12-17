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

package uk.ac.manchester.cs.mekon.owl.reason.frames;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Main class in the pre-processable frames-based instance
 * representation.
 *
 * @author Colin Puleston
 */
public class ORFramesInstance {

	private OConceptFinder concepts;
	private OObjectPropertyFinder objectProperties;
	private ORSlotSemantics slotSemantics;
	private ORFrame rootFrame;

	private IFrameSlotBuilder iFrameSlotBuilder = new IFrameSlotBuilder();
	private CFrameSlotBuilder cFrameSlotBuilder = new CFrameSlotBuilder();
	private NumberSlotBuilder numberSlotBuilder = new NumberSlotBuilder();

	private Map<IFrame, ORFrame> framesByIFrame = new HashMap<IFrame, ORFrame>();

	private abstract class TypeSlotBuilder<V, S extends ORSlot<V>, IV> {

		void build(ORFrame oFrame, ISlot iSlot, List<IV> iValues) {

			S oSlot = createSlot(iSlot);

			for (IV iValue : iValues) {

				oSlot.addValue(getValue(iValue));
			}

			addSlot(oFrame, oSlot);
		}

		abstract V getValue(IV iValue);

		abstract S createSlot(ISlot iSlot, IRI iri);

		abstract void addSlot(ORFrame oFrame, S oSlot);

		private S createSlot(ISlot iSlot) {

			IRI iri = getIRIOrNull(iSlot);
			S slot = createSlot(iSlot, iri);

			if (iri != null) {

				slot.setClosedWorldSemantics(closedWorldSemantics(iri));
			}

			return slot;
		}

		private IRI getIRIOrNull(ISlot iSlot) {

			return objectProperties.getOrNull(iSlot.getType().getIdentity());
		}
	}

	private abstract class ConceptSlotBuilder<IV>
								extends
									TypeSlotBuilder<ORFrame, ORConceptSlot, IV> {

		ORConceptSlot createSlot(ISlot iSlot, IRI iri) {

			return new ORConceptSlot(iSlot, iri);
		}

		void addSlot(ORFrame oFrame, ORConceptSlot oSlot) {

			oFrame.addSlot(oSlot);
		}
	}

	private class IFrameSlotBuilder extends ConceptSlotBuilder<IFrame> {

		ORFrame getValue(IFrame iValue) {

			return getFrame(iValue);
		}
	}

	private class CFrameSlotBuilder extends ConceptSlotBuilder<CFrame> {

		ORFrame getValue(CFrame iValue) {

			return createFrame(iValue);
		}
	}

	private class NumberSlotBuilder
						extends
							TypeSlotBuilder<INumber, ORNumberSlot, INumber> {

		INumber getValue(INumber iValue) {

			return iValue;
		}

		ORNumberSlot createSlot(ISlot iSlot, IRI iri) {

			return new ORNumberSlot(iSlot, iri);
		}

		void addSlot(ORFrame oFrame, ORNumberSlot oSlot) {

			oFrame.addSlot(oSlot);
		}
	}

	private class SlotBuilder extends ISlotValuesVisitor {

		private ISlot iSlot;
		private ORFrame oFrame;

		protected void visit(CFrame valueType, List<IFrame> values) {

			iFrameSlotBuilder.build(oFrame, iSlot, values);
		}

		protected void visit(CNumber valueType, List<INumber> values) {

			numberSlotBuilder.build(oFrame, iSlot, values);
		}

		protected void visit(MFrame valueType, List<CFrame> values) {

			cFrameSlotBuilder.build(oFrame, iSlot, values);
		}

		SlotBuilder(ISlot iSlot, ORFrame oFrame) {

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
	public ORFramesInstance(
				OModel model,
				ORSlotSemantics slotSemantics,
				IFrame iFrame) {

		this.slotSemantics = slotSemantics;

		concepts = new OConceptFinder(model);
		objectProperties = new OObjectPropertyFinder(model);

		rootFrame = getFrame(iFrame);
	}

	/**
	 * Provides the root-frame in the pre-processable instance
	 * representation.
	 *
	 * @return Root-frame in instance representation
	 */
	public ORFrame getRootFrame() {

		return rootFrame;
	}

	private ORFrame getFrame(IFrame iFrame) {

		ORFrame frame = framesByIFrame.get(iFrame);

		if (frame == null) {

			frame = buildFrame(iFrame);
			framesByIFrame.put(iFrame, frame);
		}

		return frame;
	}

	private ORFrame buildFrame(IFrame iFrame) {

		ORFrame oFrame = createFrame(iFrame.getType());

		oFrame.setIFrame(iFrame);

		for (ISlot iSlot : iFrame.getSlots().asList()) {

			new SlotBuilder(iSlot, oFrame);
		}

		return oFrame;
	}

	private ORFrame createFrame(CFrame cFrame) {

		return cFrame.getCategory().disjunction()
				? createDisjunctionFrame(cFrame)
				: createModelFrame(cFrame);
	}

	private ORFrame createDisjunctionFrame(CFrame cFrame) {

		ORFrame oFrame = new ORFrame(cFrame, null);

		for (CFrame disjunct : cFrame.getSubs()) {

			IRI iri = concepts.getSubsumerOrNull(disjunct);

			if (iri != null) {

				oFrame.addTypeDisjunctIRI(iri);
			}
		}

		return oFrame;
	}

	private ORFrame createModelFrame(CFrame cFrame) {

		return new ORFrame(cFrame, concepts.getSubsumerOrNull(cFrame));
	}

	private boolean closedWorldSemantics(IRI propertyIRI) {

		return slotSemantics.getSemantics(propertyIRI) == ORSemantics.CLOSED_WORLD;
	}
}
