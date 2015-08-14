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

package uk.ac.manchester.cs.mekon.owl.reason.preprocess;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * Pre-processer that modifies intermediate instance
 * representations, by substituting any frame--valued "source"
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
public class ORFrameSlotSubstituter extends ORVisitingPreProcessor {

	private CIdentity sourceSlotId;
	private Map<CFrame, IRI> targetSlotsByValueType = new HashMap<CFrame, IRI>();

	/**
	 * Constructor.
	 *
	 * @param sourceSlotId Identifier for source-slots
	 */
	public ORFrameSlotSubstituter(CIdentity sourceSlotId) {

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
	protected void visit(OModel model, ORFrame frame) {

		for (ORFrameSlot slot : frame.getFrameSlots()) {

			if (isSourceSlot(slot)) {

				substitute(frame, slot);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void visit(OModel model, ORFrameSlot slot) {
	}

	/**
	 * {@inheritDoc}
	 */
	protected void visit(OModel model, ORNumberSlot slot) {
	}

	private void substitute(ORFrame container, ORFrameSlot sourceSlot) {

		for (CFrame valueType : targetSlotsByValueType.keySet()) {

			ORFrameSlot targetSlot = resolveTargetSlot(container, valueType);

			moveSlotValues(sourceSlot, targetSlot, valueType);
		}

		if (sourceSlot.getValues().isEmpty()) {

			container.removeSlot(sourceSlot);
		}
	}

	private boolean isSourceSlot(ORFrameSlot slot) {

		return slot.getIdentifier().equals(sourceSlotId.getIdentifier());
	}

	private ORFrameSlot resolveTargetSlot(ORFrame container, CFrame valueType) {

		IRI iri = targetSlotsByValueType.get(valueType);
		ORFrameSlot targetSlot = getTargetSlotOrNull(container, iri);

		if (targetSlot == null) {

			targetSlot = new ORFrameSlot(iri);

			container.addSlot(targetSlot);
		}

		return targetSlot;
	}

	private ORFrameSlot getTargetSlotOrNull(ORFrame container, IRI iri) {

		for (ORFrameSlot slot : container.getFrameSlots()) {

			if (slot.mapsToOWLEntity() && slot.getIRI().equals(iri)) {

				return slot;
			}
		}

		return null;
	}

	private void moveSlotValues(
					ORFrameSlot sourceSlot,
					ORFrameSlot targetSlot,
					CFrame moveValueType) {

		for (ORFrame value : sourceSlot.getValues()) {

			if (value.mapsToOWLEntity()) {

				CFrame valueType = value.getCFrame();

				if (valueType != null && moveValueType.subsumes(valueType)) {

					sourceSlot.removeValue(value);
					targetSlot.addValue(value);
				}
			}
		}
	}
}
