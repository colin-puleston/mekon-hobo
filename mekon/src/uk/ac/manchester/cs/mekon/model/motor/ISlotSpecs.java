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

package uk.ac.manchester.cs.mekon.model.motor;

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

	private IFrame frame;
	private IEditor iEditor;

	private Set<CFrame> absorbedFrameTypes = new HashSet<CFrame>();

	private SpecSet specs;

	private class SpecSet {

		private List<ISlotSpec> specs = new ArrayList<ISlotSpec>();
		private Map<CIdentity, ISlotSpec> bySlotId = new HashMap<CIdentity, ISlotSpec>();

		SpecSet(CFrame frameType) {

			absorb(frameType);
		}

		void absorb(CFrame frameType) {

			List<CFrame> structuredAncTypes = frameType.getStructuredAncestors();

			for (int i = structuredAncTypes.size() - 1 ; i >= 0 ; i--) {

				absorbType(structuredAncTypes.get(i));
			}

			absorbType(frameType);
		}

		void initialiseSlots() {

			for (ISlotSpec spec : specs) {

				spec.checkAddSlot(frame);
			}
		}

		void initialiseSlotValues() {

			for (ISlotSpec spec : specs) {

				updateSlotValuesFor(spec);
			}
		}

		ISlotOps updateSlots() {

			ISlotOps enactedOps = ISlotOps.NONE;

			for (ISlot slot : frame.getSlots().asList()) {

				enactedOps = enactedOps.and(removeIfRedundant(slot));
			}

			for (ISlotSpec spec : specs) {

				enactedOps = enactedOps.and(updateSlotsFor(spec));
			}

			return enactedOps;
		}

		ISlotOps updateSlotValues() {

			ISlotOps enactedOps = ISlotOps.NONE;

			for (ISlotSpec spec : specs) {

				enactedOps = enactedOps.and(updateSlotValuesFor(spec));
			}

			return enactedOps;
		}

		private SpecSet(List<CFrame> disjunctTypes) {

			this(disjunctTypes.get(0));

			for (int i = 1 ; i < disjunctTypes.size() ; i++) {

				intersectWith(new SpecSet(disjunctTypes.get(i)));
			}
		}

		private void intersectWith(SpecSet intersectee) {

			for (CIdentity slotId : new HashSet<CIdentity>(bySlotId.keySet())) {

				if (intersectee.bySlotId.containsKey(slotId)) {

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

					absorbAtomicType(frameType);
				}
			}
		}

		private void absorbDisjunctionType(CFrame frameType) {

			SpecSet intersection = createIntersectionSpecs(frameType);

			for (CIdentity slotId : intersection.bySlotId.keySet()) {

				resolveSpec(slotId).absorbSpec(intersection.getSpec(slotId));
			}
		}

		private void absorbAtomicType(CFrame frameType) {

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

				absorbFixedValues(slotValues, slotId);
			}
		}

		private void absorbFixedValues(CSlotValues slotValues, CIdentity slotId) {

			resolveSpec(slotId).absorbFixedValues(getFixedValues(slotValues, slotId));
		}

		private List<IValue> getFixedValues(CSlotValues slotValues, CIdentity slotId) {

			return slotValues.getIValues(slotId, frame.getFunction());
		}

		private SpecSet createIntersectionSpecs(CFrame frameType) {

			return new SpecSet(frameType.asDisjuncts());
		}

		private ISlotOps removeIfRedundant(ISlot slot) {

			if (!redundantSlot(slot)) {

				return ISlotOps.NONE;
			}

			getFrameEditor().removeSlot(slot);

			if (slot.getValues().isEmpty()) {

				return ISlotOps.SLOTS;
			}

			return ISlotOps.SLOTS_AND_VALUES;
		}

		private boolean redundantSlot(ISlot slot) {

			return !bySlotId.containsKey(slot.getType().getIdentity());
		}

		private ISlotOps updateSlotsFor(ISlotSpec spec) {

			ISlot slot = getSlotOrNull(spec);

			return slot == null
					? spec.checkAddSlot(frame)
					: spec.checkUpdateOrRemoveSlot(slot);
		}

		private ISlotOps updateSlotValuesFor(ISlotSpec spec) {

			ISlot slot = getSlotOrNull(spec);

			return slot != null ? spec.checkUpdateSlotValues(slot) : ISlotOps.NONE;
		}

		private ISlot getSlotOrNull(ISlotSpec spec) {

			return frame.getSlots().getOrNull(spec.getIdentity());
		}

		private ISlotSpec resolveSpec(CIdentity slotId) {

			ISlotSpec spec = getSpec(slotId);

			return spec != null ? spec : addSpec(slotId);
		}

		private ISlotSpec addSpec(CIdentity slotId) {

			ISlotSpec spec = new ISlotSpec(iEditor, slotId);

			specs.add(spec);
			bySlotId.put(slotId, spec);

			return spec;
		}

		private void removeSpec(CIdentity slotId) {

			specs.remove(bySlotId.remove(slotId));
		}

		private ISlotSpec getSpec(CIdentity slotId) {

			return bySlotId.get(slotId);
		}
	}

	/**
	 * Constructs object for initialising or updating the slots on
	 * the specified instance-level frame. Invokes {@link
	 * #absorb(CFrame)} method on the concept-level frame representing
	 * the frame-type.
	 *
	 * @param frame Instance-level frame whose slots are to be
	 * initialised or updated
	 * @param iEditor Model-instantiation editor
	 */
	public ISlotSpecs(IFrame frame, IEditor iEditor) {

		this.frame = frame;
		this.iEditor = iEditor;

		specs = new SpecSet(frame.getType());
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
	 *   <li>The most-specific of the slot value-types will be retained
	 *   (as defined by {@link CValue#subsumedBy(CValue)})
	 *   <li>The most-specific of the cardinalities will be retained
	 *   (as defined by
	 *   {@link CCardinality#moreRestrictiveThan(CCardinality)})
	 *   <li>The activation will be the "strongest" of the existing
	 *	 and new activations (see {@link CActivation#getStrongest})
	 *   <li>The editability will be the "strongest" of the existing
	 *	 and new editabilities (see {@link CEditability#getStrongest})
	 * </ul>
	 *
	 * @param frameType Concept-level frame whose slot-related
	 * definitions are to be absorbed
	 */
	public void absorb(CFrame frameType) {

		specs.absorb(frameType);
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
	 * Initialises the slot-set, including value-types, editabilities,
	 * and activations on the instance-level frame, using the current
	 * set of slot-specifications. Optionally, will also add any initial
	 * fixed slot-values included in the slot-specifications.
	 *
	 * @param initSlotValues True if fixed slot-values are to be added
	 */
	public void initialise(boolean initSlotValues) {

		specs.initialiseSlots();

		if (initSlotValues) {

			specs.initialiseSlotValues();
		}
	}

	/**
	 * Updates the slot-set, including value-types, editabilities,
	 * activations and fixed slot-values, on the instance-level frame,
	 * using the current set of slot-specifications.
	 * <p>
	 * NOTE: Even if the required update operations (as specified via the
	 * relevant parameter) do not include slot-value updates, removals of
	 * asserted slot-values may still occur as a result of either slot
	 * removals or value-type updates.
	 *
	 * @param ops Required update operations
	 * @return Types of update produced
	 */
	public ISlotOps update(ISlotOps ops) {

		ISlotOps enactedOps = ISlotOps.NONE;

		if (ops.includesSlots()) {

			enactedOps = enactedOps.and(specs.updateSlots());
		}

		if (ops.includesValues()) {

			enactedOps = enactedOps.and(specs.updateSlotValues());
		}

		return enactedOps;
	}

	private IFrameEditor getFrameEditor() {

		return iEditor.getFrameEditor(frame);
	}
}
