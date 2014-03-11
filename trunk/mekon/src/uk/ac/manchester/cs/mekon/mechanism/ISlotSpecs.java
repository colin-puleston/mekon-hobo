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
 * Utility class that enables implementations of
 * {@link IReasoner} to maintain a set of specifications
 * defining the set of slots required for a specific
 * instance-level frame. Existing slot-specifications can be
 * modified as further information is aquired. Once the full
 * set of specifications has been created, they can be used
 * for either initialising or updating the slot-set for the
 * relevant frame.
 *
 * @author Colin Puleston
 */
public class ISlotSpecs {

	private IEditor iEditor;

	private List<ISlotSpec> specs = new ArrayList<ISlotSpec>();
	private Map<CProperty, ISlotSpec> specsByProperty
					= new HashMap<CProperty, ISlotSpec>();

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
	 * Absorbs all slot-related definitions associated with the
	 * specified concept-level frame, and optionally it's
	 * ancestors, into the current set of slot-specifications,
	 * creating new specifications where necessary.
	 * <ul>
	 *   <li>Concept-level slot definitions
	 *   <li>Instance-level slot default-values
	 * </ul>
	 * The new slot-definition information will be used to update
	 * existing slot-specifications as follows:
	 * <ul>
	 *   <li>The most-specific of the cardinalities will be retained
	 *   (as defined by {@link CValue#subsumedBy(CValue)})
	 *   <li>The most-specific of the slot value-types will be retained
	 *   (as defined by
	 *   {@link CCardinality#moreRestrictiveThan(CCardinality)})
	 *   <li>The "active" status will true if either the existing or
	 *   the new status is true
	 *   <li>The "editable" status will be true if and only if both the
	 *   existing and the new status's are true
	 * </ul>
	 *
	 * @param frameType Concept-level frame whose slot-related
	 * definitions are to be absorbed
	 * @param includeAncestors True if slot-related definitions
	 * of ancestors are also to be absorbed
	 */
	public void absorb(CFrame frameType, boolean includeAncestors) {

		if (includeAncestors) {

			absorbAncestors(frameType);
		}

		absorb(frameType);
	}

	/**
	 * Absorbs all slot-related definitions associated with the
	 * specified concept-level frames, and optionally their
	 * ancestors into the current set of slot-specifications,
	 * creating new specifications where necessary. See
	 * {@link #absorb(CFrame, boolean)} for description of the
	 * absorption process.
	 *
	 * @param frameTypes Concept-level frames whose slot-related
	 * definitions are to be absorbed
	 * @param includeAncestors True if slot-related definitions
	 * of ancestors are also to be absorbed
	 */
	public void absorbAll(List<CFrame> frameTypes, boolean includeAncestors) {

		for (CFrame frameType : frameTypes) {

			absorb(frameType, includeAncestors);
		}
	}

	/**
	 * Initialises the specified instance-level frame using the
	 * current set of slot-specifications.
	 *
	 * @param frame Instance-level frame to be initialised
	 */
	public void initialiseSlots(IFrame frame) {

		for (ISlotSpec spec : specs) {

			spec.addSlot(frame);
		}
	}

	/**
	 * Updates the specified instance-level frame using the current
	 * set of slot-specifications.
	 *
	 * @param frame Instance-level frame to be updated
	 * @return True is any slot-value updates occured
	 */
	public boolean updateSlots(IFrame frame) {

		boolean valueUpdatesViaRemovals = removeRedundantSlots(frame);
		boolean valueUpdatesViaUpdates = updateRemainingSlots(frame);

		return valueUpdatesViaRemovals || valueUpdatesViaUpdates;
	}

	private void absorbAncestors(CFrame frameType) {

		for (CFrame ancType : frameType.getStructuredAncestors()) {

			absorb(ancType);
		}
	}

	private void absorb(CFrame frameType) {

		if (!absorbedFrameTypes.contains(frameType)) {

			absorbSlotTypes(frameType);
			absorbFixedValues(frameType);
		}
	}

	private void absorbSlotTypes(CFrame frameType) {

		for (CSlot slotType : frameType.getSlots().asList()) {

			getSpec(slotType.getProperty()).absorbType(slotType);
		}
	}

	private void absorbFixedValues(CFrame frameType) {

		CSlotValues slotValues = frameType.getSlotValues();

		for (CProperty property : slotValues.getSlotProperties()) {

			List<IValue> fixedValues = slotValues.getIValues(property);

			getSpec(property).absorbFixedValues(fixedValues);
		}
	}

	private boolean removeRedundantSlots(IFrame frame) {

		boolean removed = false;

		for (ISlot slot : frame.getSlots().asList()) {

			if (removeIfRedundant(frame, slot) && !removed) {

				removed = true;
			}
		}

		return removed;
	}

	private boolean updateRemainingSlots(IFrame frame) {

		boolean anyValueUpdates = false;

		for (ISlotSpec spec : specs) {

			if (updateSlotsFor(frame, spec)) {

				anyValueUpdates = true;
			}
		}

		return anyValueUpdates;
	}

	private boolean updateSlotsFor(IFrame frame, ISlotSpec spec) {

		ISlots slots = frame.getSlots();
		CProperty property = spec.getProperty();

		if (slots.containsSlotFor(property)) {

			ISlot slot = slots.getSlotFor(property);
			List<IValue> oldValues = slot.getValues().asList();

			spec.updateSlot(slot);

			return !slot.getValues().asList().equals(oldValues);
		}

		spec.addSlot(frame);

		return true;
	}

	private boolean removeIfRedundant(IFrame container, ISlot slot) {

		if (!specsByProperty.containsKey(slot.getType().getProperty())) {

			getFrameEditor(container).removeSlot(slot);

			return !slot.getValues().isEmpty();
		}

		return false;
	}

	private ISlotSpec getSpec(CProperty property) {

		ISlotSpec spec = specsByProperty.get(property);

		if (spec == null) {

			spec = new ISlotSpec(iEditor, property);

			specs.add(spec);
			specsByProperty.put(property, spec);
		}

		return spec;
	}

	private IFrameEditor getFrameEditor(IFrame frame) {

		return iEditor.getFrameEditor(frame);
	}
}
