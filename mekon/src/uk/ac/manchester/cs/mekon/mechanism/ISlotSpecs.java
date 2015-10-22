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

package uk.ac.manchester.cs.mekon.mechanism;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Utility class that enables implementations of {@link IReasoner}
 * to maintain a set of specifications defining the set of slots
 * required for a specific instance-level frame. Existing
 * slot-specifications can be modified as further information is
 * acquired. Once the full set of specifications has been created,
 * they can be used for either initialising or updating the
 * slot-set for the relevant frame.
 *
 * @author Colin Puleston
 */
public class ISlotSpecs {

	private IEditor iEditor;

	private List<ISlotSpec> specs = new ArrayList<ISlotSpec>();
	private Map<CIdentity, ISlotSpec> specsBySlotId = new HashMap<CIdentity, ISlotSpec>();

	private Set<CFrame> absorbedFrameTypes = new HashSet<CFrame>();

	/**
	 * Constructor.
	 *
	 * @param iEditor Model-instantiation editor
	 */
	public ISlotSpecs(IEditor iEditor) {

		this.iEditor = iEditor;
	}

	/**
	 * Constructor that absorbs all slot-related definitions
	 * associated with the specified concept-level frame or any of
	 * it's ancestors. See {@link #absorb(CFrame)} for description of
	 * the absorption process.
	 *
	 * @param iEditor Model-instantiation editor
	 * @param frameType Concept-level frame whose slot-related
	 * definitions are to be absorbed
	 */
	public ISlotSpecs(IEditor iEditor, CFrame frameType) {

		this.iEditor = iEditor;

		absorb(frameType);
	}

	/**
	 * Absorbs all slot-related definitions associated with the
	 * specified concept-level frame or any of it's ancestors into the
	 * current set of slot-specifications, creating new specifications
	 * where necessary. Absorbed definitions may include:
	 * <ul>
	 *   <li>Concept-level slot definitions
	 *   <li>Instance-level slot default-values
	 * </ul>
	 * <p>
	 * The new slot-definition information will be used to update
	 * existing slot-specifications as follows:
	 * <ul>
	 *   <li>The most-specific of the cardinalities will be retained
	 *   (as defined by {@link CValue#subsumedBy(CValue)})
	 *   <li>The most-specific of the slot value-types will be retained
	 *   (as defined by
	 *   {@link CCardinality#moreRestrictiveThan(CCardinality)})
	 *   <li>The "active" status will be true if and only if the both
	 *   the existing status and the new status are true
	 *   <li>The "editability" status will be the "strongest" of the
	 *   existing and new statuses (see {@link CEditability#getStrongest})
	 * </ul>
	 *
	 * @param frameType Concept-level frame whose slot-related
	 * definitions are to be absorbed
	 */
	public void absorb(CFrame frameType) {

		for (CFrame ancType : frameType.getStructuredAncestors()) {

			absorbType(ancType);
		}

		absorbType(frameType);
	}

	/**
	 * Absorbs all slot-related definitions associated with the
	 * specified concept-level frames or any of it's ancestors, into
	 * the current set of slot-specifications, creating new
	 * specifications where necessary. See {@link #absorb(CFrame)} for
	 * description of the absorption process.
	 *
	 * @param frameTypes Concept-level frames whose slot-related
	 * definitions are to be absorbed
	 */
	public void absorbAll(List<CFrame> frameTypes) {

		for (CFrame frameType : frameTypes) {

			absorb(frameType);
		}
	}

	/**
	 * Initialises the slot-set, including value-types and "active" and
	 * "editability" statuses, and the fixed slot-values, on the specified
	 * instance-level frame, using the current set of slot-specifications.
	 *
	 * @param frame Instance-level frame to be initialised
	 */
	public void initialise(IFrame frame) {

		for (ISlotSpec spec : specs) {

			spec.checkAddSlot(frame);
		}

		update(frame, ISlotOps.VALUES);
	}

	/**
	 * Updates the slot-set, including value-types and "active" statuses,
	 * and/or the fixed slot-values, on the specified instance-level frame,
	 * using the current set of slot-specifications.
	 * <p>
	 * NOTE: Even if the required update operations (as specified via the
	 * relevant parameter) do not include slot-value updates, removals of
	 * (asserted) slot-values may still occur as a result of either slot
	 * removals or value-type updates.
	 *
	 * @param frame Instance-level frame to be updated
	 * @param ops Required update operations
	 * @return Types of update produced
	 */
	public ISlotOps update(IFrame frame, ISlotOps ops) {

		ISlotOps enactedOps = ISlotOps.NONE;

		if (ops.includesSlots()) {

			for (ISlot slot : frame.getSlots().asList()) {

				enactedOps = enactedOps.and(removeIfRedundant(frame, slot));
			}

			for (ISlotSpec spec : specs) {

				enactedOps = enactedOps.and(updateSlotsFor(frame, spec));
			}
		}

		if (ops.includesValues()) {

			for (ISlotSpec spec : specs) {

				enactedOps = enactedOps.and(updateSlotValuesFor(frame, spec));
			}
		}

		return enactedOps;
	}

	private ISlotSpecs(IEditor iEditor, List<CFrame> disjunctTypes) {

		this(iEditor, disjunctTypes.get(0));

		for (int i = 1 ; i < disjunctTypes.size() ; i++) {

			intersectWith(new ISlotSpecs(iEditor, disjunctTypes.get(i)));
		}
	}

	private void intersectWith(ISlotSpecs intersectee) {

		for (CIdentity slotId : new HashSet<CIdentity>(specsBySlotId.keySet())) {

			if (intersectee.specsBySlotId.containsKey(slotId)) {

				getSpec(slotId).intersectWith(intersectee.getSpec(slotId));
			}
			else {

				removeSpec(slotId);
			}
		}
	}

	private void absorbType(CFrame frameType) {

		if (absorbedFrameTypes.add(frameType)) {

			if (frameType.getCategory().disjunction()) {

				absorbDisjunctionType(frameType);
			}
			else {

				absorbSimpleType(frameType);
			}
		}
	}

	private void absorbDisjunctionType(CFrame frameType) {

		ISlotSpecs intersection = createIntersectionSpecs(frameType);

		for (CIdentity slotId : intersection.specsBySlotId.keySet()) {

			resolveSpec(slotId).absorbSpec(intersection.getSpec(slotId));
		}
	}

	private void absorbSimpleType(CFrame frameType) {

		absorbSlotTypes(frameType);
		absorbFixedValues(frameType);
	}

	private void absorbSlotTypes(CFrame frameType) {

		for (CSlot slotType : frameType.getSlots().asList()) {

			resolveSpec(slotType.getIdentity()).absorbType(slotType);
		}
	}

	private void absorbFixedValues(CFrame frameType) {

		CSlotValues slotValues = frameType.getSlotValues();

		for (CIdentity slotId : slotValues.getSlotIdentities()) {

			List<IValue> fixedValues = slotValues.getIValues(slotId);

			resolveSpec(slotId).absorbFixedValues(fixedValues);
		}
	}

	private ISlotSpecs createIntersectionSpecs(CFrame frameType) {

		return new ISlotSpecs(iEditor, frameType.asDisjuncts());
	}

	private ISlotOps removeIfRedundant(IFrame frame, ISlot slot) {

		if (!redundantSlot(slot)) {

			return ISlotOps.NONE;
		}

		getFrameEditor(frame).removeSlot(slot);

		if (slot.getValues().isEmpty()) {

			return ISlotOps.SLOTS;
		}

		return ISlotOps.SLOTS_AND_VALUES;
	}

	private boolean redundantSlot(ISlot slot) {

		return !specsBySlotId.containsKey(slot.getType().getIdentity());
	}

	private ISlotOps updateSlotsFor(IFrame frame, ISlotSpec spec) {

		ISlot slot = getSlotOrNull(frame, spec);

		return slot == null
				? spec.checkAddSlot(frame)
				: spec.checkUpdateOrRemoveSlot(slot);
	}

	private ISlotOps updateSlotValuesFor(IFrame frame, ISlotSpec spec) {

		ISlot slot = getSlotOrNull(frame, spec);

		return slot != null ? spec.checkUpdateSlotValues(slot) : ISlotOps.NONE;
	}

	private ISlot getSlotOrNull(IFrame frame, ISlotSpec spec) {

		return frame.getSlots().getOrNull(spec.getIdentity());
	}

	private ISlotSpec resolveSpec(CIdentity slotId) {

		ISlotSpec spec = getSpec(slotId);

		return spec != null ? spec : addSpec(slotId);
	}

	private ISlotSpec addSpec(CIdentity slotId) {

		ISlotSpec spec = new ISlotSpec(iEditor, slotId);

		specs.add(spec);
		specsBySlotId.put(slotId, spec);

		return spec;
	}

	private void removeSpec(CIdentity slotId) {

		specs.remove(specsBySlotId.remove(slotId));
	}

	private ISlotSpec getSpec(CIdentity slotId) {

		return specsBySlotId.get(slotId);
	}

	private IFrameEditor getFrameEditor(IFrame frame) {

		return iEditor.getFrameEditor(frame);
	}
}
