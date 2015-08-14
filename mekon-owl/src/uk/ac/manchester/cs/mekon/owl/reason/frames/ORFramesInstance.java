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
	private OPropertyFinder properties;
	private ORSlotSemantics slotSemantics;
	private ORFrame rootFrame;

	private IFrameSlotConverter iFrameSlotConverter = new IFrameSlotConverter();
	private CFrameSlotConverter cFrameSlotConverter = new CFrameSlotConverter();
	private NumberSlotConverter numberSlotConverter = new NumberSlotConverter();

	private Map<IFrame, ORFrame> framesByIFrame = new HashMap<IFrame, ORFrame>();

	private abstract class TypeSlotConverter<V, S extends ORSlot<V>, IV> {

		void build(ORFrame oFrame, ISlot iSlot, List<IV> iValues) {

			build(oFrame, iSlot.getType().getIdentity(), iSlot, iValues);
		}

		void build(ORFrame oFrame, CIdentity slotId, List<IV> iValues) {

			build(oFrame, slotId, null, iValues);
		}

		abstract V getValue(IV iValue);

		abstract S createSlot(CIdentity id, ISlot iSlot, IRI iri);

		abstract void addSlot(ORFrame oFrame, S oSlot);

		private void build(
						ORFrame oFrame,
						CIdentity id,
						ISlot iSlot,
						List<IV> iValues) {

			S oSlot = createSlot(id, iSlot);

			for (IV iValue : iValues) {

				oSlot.addValue(getValue(iValue));
			}

			addSlot(oFrame, oSlot);
		}

		private S createSlot(CIdentity id, ISlot iSlot) {

			IRI iri = properties.getOrNull(id);
			S slot = createSlot(id, iSlot, iri);

			if (iri != null) {

				slot.setClosedWorldSemantics(closedWorldSemantics(iri));
			}

			return slot;
		}
	}

	private abstract class FrameSlotConverter<IV>
								extends
									TypeSlotConverter<ORFrame, ORFrameSlot, IV> {

		ORFrameSlot createSlot(CIdentity id, ISlot iSlot, IRI iri) {

			return new ORFrameSlot(id, iSlot, iri);
		}

		void addSlot(ORFrame oFrame, ORFrameSlot oSlot) {

			oFrame.addSlot(oSlot);
		}
	}

	private class IFrameSlotConverter extends FrameSlotConverter<IFrame> {

		ORFrame getValue(IFrame iValue) {

			return getFrame(iValue);
		}
	}

	private class CFrameSlotConverter extends FrameSlotConverter<CFrame> {

		ORFrame getValue(CFrame iValue) {

			return createFrame(iValue);
		}
	}

	private class NumberSlotConverter
						extends
							TypeSlotConverter<INumber, ORNumberSlot, INumber> {

		INumber getValue(INumber iValue) {

			return iValue;
		}

		ORNumberSlot createSlot(CIdentity id, ISlot iSlot, IRI iri) {

			return new ORNumberSlot(id, iSlot, iri);
		}

		void addSlot(ORFrame oFrame, ORNumberSlot oSlot) {

			oFrame.addSlot(oSlot);
		}
	}

	private class ISlotConverter extends ISlotValuesVisitor {

		private ISlot iSlot;
		private ORFrame oFrame;

		protected void visit(CFrame valueType, List<IFrame> values) {

			iFrameSlotConverter.build(oFrame, iSlot, values);
		}

		protected void visit(CNumber valueType, List<INumber> values) {

			numberSlotConverter.build(oFrame, iSlot, values);
		}

		protected void visit(MFrame valueType, List<CFrame> values) {

			cFrameSlotConverter.build(oFrame, iSlot, values);
		}

		ISlotConverter(ISlot iSlot, ORFrame oFrame) {

			this.iSlot = iSlot;
			this.oFrame = oFrame;

			visit(iSlot);
		}
	}

	private class CSlotValuesConverter extends CValueVisitor {

		private CSlotValues cSlotValues;
		private CIdentity slotId;
		private ORFrame oFrame;

		protected void visit(CFrame value) {

			cFrameSlotConverter.build(oFrame, slotId, getValues(CFrame.class));
		}

		protected void visit(CNumber value) {

			numberSlotConverter.build(oFrame, slotId, getCNumberValuesAsINumbers());
		}

		protected void visit(MFrame value) {

			cFrameSlotConverter.build(oFrame, slotId, getMFrameValuesAsCFrames());
		}

		CSlotValuesConverter(CSlotValues cSlotValues, CIdentity slotId, ORFrame oFrame) {

			this.cSlotValues = cSlotValues;
			this.slotId = slotId;
			this.oFrame = oFrame;

			visit(cSlotValues.getValues(slotId).get(0));
		}

		private List<CFrame> getMFrameValuesAsCFrames() {

			List<CFrame> cFrames = new ArrayList<CFrame>();

			for (MFrame mFrame : getValues(MFrame.class)) {

				cFrames.add(mFrame.getRootCFrame());
			}

			return cFrames;
		}

		private List<INumber> getCNumberValuesAsINumbers() {

			List<INumber> iNumbers = new ArrayList<INumber>();

			for (CNumber cNumber : getValues(CNumber.class)) {

				iNumbers.add(cNumber.asINumber());
			}

			return iNumbers;
		}

		private <V extends CValue<?>>List<V> getValues(Class<V> valueClass) {

			return cSlotValues.getValues(slotId, valueClass);
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
		properties = new OPropertyFinder(model);

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

			new ISlotConverter(iSlot, oFrame);
		}

		return oFrame;
	}

	private ORFrame createFrame(CFrame cFrame) {

		CFrameCategory category = cFrame.getCategory();

		if (category.disjunction()) {

			return createDisjunctionFrame(cFrame);
		}

		if (category.extension()) {

			return createExtensionFrame(cFrame);
		}

		return createSimpleFrame(cFrame);
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

	private ORFrame createExtensionFrame(CFrame cFrame) {

		ORFrame oFrame = createSimpleFrame(cFrame);
		CSlotValues slotValues = cFrame.getSlotValues();

		for (CIdentity slotId : slotValues.getSlotIdentities()) {

			new CSlotValuesConverter(slotValues, slotId, oFrame);
		}

		return oFrame;
	}

	private ORFrame createSimpleFrame(CFrame cFrame) {

		return new ORFrame(cFrame, concepts.getSubsumerOrNull(cFrame));
	}

	private boolean closedWorldSemantics(IRI propertyIRI) {

		return slotSemantics.getSemantics(propertyIRI) == ORSemantics.CLOSED_WORLD;
	}
}
