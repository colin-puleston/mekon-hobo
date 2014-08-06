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

package uk.ac.manchester.cs.mekon.owl.frames.preprocess;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.classifier.*;
import uk.ac.manchester.cs.mekon.owl.frames.*;

/**
 * Pre-processer that modifies intermediate instance
 * representations, by substituting any concept-valued "source"
 * slot that has a particular identifier, with one or more
 * "target" slots. Each target-slot is specified via a IRI and
 * a value-type, as represented by the relevant {@link CFrame}
 * object. If a target-slot does not already exist on the parent
 * frame then it will be created. All values from the original
 * source-slot whose value-type is subsumed by the value-type
 * specified for one of the target-slots, will be moved to the
 * relevant target-slot. If this process results in the
 * source-slot being left with no values then it will be removed
 * from the parent frame.
 *
 * @author Colin Puleston
 */
public class OFConceptSlotSubstituter implements OFPreProcessor {

	private CIdentity sourceSlotId;
	private Map<CFrame, IRI> targetSlotsByValueType = new HashMap<CFrame, IRI>();

	private class Substituter {

		private OModel model;

		Substituter(OModel model) {

			this.model = model;
		}

		void process(OFFrame frame) {

			Set<OFConceptSlot> postSubstitutionSlots = new HashSet<OFConceptSlot>();

			for (OFConceptSlot slot : frame.getConceptSlots()) {

				postSubstitutionSlots.addAll(checkSubstitute(frame, slot));
			}

			for (OFConceptSlot slot : postSubstitutionSlots) {

				process(slot);
			}
		}

		private void process(OFConceptSlot slot) {

			for (OFFrame value : slot.getValues()) {

				process(value);
			}
		}

		private Set<OFConceptSlot> checkSubstitute(OFFrame container, OFConceptSlot slot) {

			return isSourceSlot(slot)
					? substitute(container, slot)
					: Collections.<OFConceptSlot>singleton(slot);
		}

		private Set<OFConceptSlot> substitute(OFFrame container, OFConceptSlot sourceSlot) {

			Set<OFConceptSlot> postSubstitutionSlots = new HashSet<OFConceptSlot>();

			for (CFrame valueType : targetSlotsByValueType.keySet()) {

				OFConceptSlot targetSlot = resolveTargetSlot(container, valueType);

				moveSlotValues(sourceSlot, targetSlot, valueType);
				postSubstitutionSlots.add(targetSlot);
			}

			if (sourceSlot.getValues().isEmpty()) {

				container.removeSlot(sourceSlot);
			}
			else {

				postSubstitutionSlots.add(sourceSlot);
			}

			return postSubstitutionSlots;
		}

		private void moveSlotValues(
						OFConceptSlot sourceSlot,
						OFConceptSlot targetSlot,
						CFrame moveValueType) {

			for (OFFrame value : sourceSlot.getValues()) {

				if (value.mapsToOWLEntity()) {

					CFrame valueType = value.getCFrame();

					if (valueType != null && moveValueType.subsumes(valueType)) {

						sourceSlot.removeValue(value);
						targetSlot.addValue(value);
					}
				}
			}
		}

		private OFConceptSlot resolveTargetSlot(OFFrame container, CFrame valueType) {

			IRI iri = targetSlotsByValueType.get(valueType);
			OFConceptSlot targetSlot = getTargetSlotOrNull(container, iri);

			if (targetSlot == null) {

				targetSlot = new OFConceptSlot(iri);

				container.addSlot(targetSlot);
			}

			return targetSlot;
		}

		private OFConceptSlot getTargetSlotOrNull(OFFrame container, IRI iri) {

			for (OFConceptSlot slot : container.getConceptSlots()) {

				if (slot.mapsToOWLEntity() && slot.getIRI().equals(iri)) {

					return slot;
				}
			}

			return null;
		}

		private boolean isSourceSlot(OFConceptSlot slot) {

			return slot.getIdentifier().equals(sourceSlotId.getIdentifier());
		}
	}

	/**
	 * Constructor.
	 *
	 * @param sourceSlotId Identifier for source-slots
	 */
	public OFConceptSlotSubstituter(CIdentity sourceSlotId) {

		this.sourceSlotId = sourceSlotId;
	}

	/**
	 * Defines a target-slot.
	 *
	 * @param slotIRI IRI for target-slot
	 * @param valueType Value-type frame that determines which
	 * values will be moved to the target-slot
	 */
	public void defineTargetSlot(IRI slotIRI, CFrame valueType) {

		targetSlotsByValueType.put(valueType, slotIRI);
	}

	/**
	 * {@inheritDoc}
	 */
	public void process(OModel model, OFFrame rootFrame) {

		new Substituter(model).process(rootFrame);
	}
}
