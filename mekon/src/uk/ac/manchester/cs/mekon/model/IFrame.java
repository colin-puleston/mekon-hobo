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

package uk.ac.manchester.cs.mekon.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Represents an instance-level atomic-frame.
 * <p>
 * The frame has a category of either {@link IFrameCategory#ATOMIC}
 * or {@link IFrameCategory#DISJUNCTION}. The latter category being a
 * special case where the frame will always have exactly one slot,
 * which will be a special "disjuncts-slot" whose value-type is the
 * same as the type of the disjunction-frame, and whose values, which
 * must be of category  {@link IFrameCategory#ATOMIC}, represent the
 * disjuncts for the disjunction.
 * <p>
 * The frame also has a function of either {@link
 * IFrameFunction#ASSERTION} or {@link IFrameFunction#QUERY}, with the
 * two differeing in the following ways:
 * <ul>
 *   <li>Query-frames can be instantiations of concept-level
 *	 disjunction-frames (see {@link CFrameCategory#disjunction}),
 *	 whereas assertion-frames cannot
 *   <li>Query-frames can be disjunction-instantiations of concept-level
 *	 frames (see {@link IFrameCategory#disjunction}), whereas
 *	 assertion-frames cannot
 *   <li>Slots on query-frames and assertion-frames may, and generally
 *	 will, have different editabilty criteria (see {@link
 *	 CSlot#getEditability}), specifying whether or not the slot is
 *	 editable by the client and whether it can be given abstract values
 *	 (see {@link IValue#abstractValue}). In general slots on query-frames
 *	 are likely to be allowed abstract values, whereas those on
 *	 assertion-frames are not, and slots on query-frames are more likely
 *	 to be editable by the client than on assertion-frames
 * </ul>
 * <p>
 * Query-frames and assertion-frames cannot be mixed within a single
 * model-instantiation. Attempting to do so will result in an exception
 * being thrown.
 *
 * @author Colin Puleston
 */
public class IFrame implements IEntity, IValue {

	static private class DynamicTypes {

		private CIdentifiedsLocal<CFrame> types = new CIdentifiedsLocal<CFrame>();

		boolean update(List<CFrame> updates) {

			if (typesMatch(updates)) {

				return false;
			}

			removeOldTypes(updates);
			addNewTypes(updates);

			return true;
		}

		CIdentifieds<CFrame> getTypes() {

			return types;
		}

		private void removeOldTypes(List<CFrame> updates) {

			for (CFrame type : types.asList()) {

				if (!updates.contains(type)) {

					types.remove(type);
				}
			}
		}

		private void addNewTypes(List<CFrame> updates) {

			for (CFrame type : updates) {

				if (!types.contains(type)) {

					types.add(type);
				}
			}
		}

		private boolean typesMatch(List<CFrame> testTypes) {

			return types.asSet().equals(new HashSet<CFrame>(testTypes));
		}
	}

	private CFrame type;
	private DynamicTypes inferredTypes = new DynamicTypes();
	private DynamicTypes suggestedTypes = new DynamicTypes();
	private IFrameFunction function;

	private ISlots slots = new ISlots();
	private ISlots referencingSlots = new ISlots();

	private Object mappedObject = null;
	private List<IFrameListener> listeners = new ArrayList<IFrameListener>();

	private boolean autoUpdateEnabled = true;

	private class Editor implements IFrameEditor {

		public boolean updateInferredTypes(List<CFrame> updateds) {

			if (inferredTypes.update(updateds)) {

				pollListenersForUpdatedInferredTypes();

				return true;
			}

			return false;
		}

		public boolean updateSuggestedTypes(List<CFrame> updateds) {

			if (suggestedTypes.update(updateds)) {

				pollListenersForUpdatedSuggestedTypes();

				return true;
			}

			return false;
		}

		public ISlot addSlot(CSlot slotType) {

			ISlot slot = addSlotInternal(slotType);

			pollListenersForSlotAdded(slot);

			return slot;
		}

		public ISlot addSlot(
						CIdentity identity,
						CSource source,
						CCardinality cardinality,
						CValue<?> valueType,
						boolean active,
						CEditability editability) {

			CSlot slotType = new CSlot(type, identity, cardinality, valueType);

			slotType.setSource(source);
			slotType.setActive(active);
			slotType.setEditability(editability);

			return addSlot(slotType);
		}

		public void removeSlot(ISlot slot) {

			slot.getValues().clearAllFixedAndAsserteds();
			slots.remove(slot);
			pollListenersForSlotRemoved(slot);
		}

		public void setAutoUpdateEnabled(boolean enabled) {

			IFrame.this.setAutoUpdateEnabled(enabled);
		}
	}

	private class AutoUpdater implements KUpdateListener {

		private ISlotValues slotValues;
		private List<IValue> assertedValues;

		private boolean updating = false;

		public void onUpdated() {

			if (autoUpdateEnabled && !updating) {

				List<IValue> latestAsserteds = slotValues.getAssertedValues();

				if (!latestAsserteds.equals(assertedValues)) {

					assertedValues = latestAsserteds;

					updating = true;
					performAutoUpdates();
					updating = false;
				}
			}
		}

		AutoUpdater(ISlot slot) {

			slotValues = slot.getValues();
			assertedValues = slotValues.getAssertedValues();

			slotValues.addUpdateListener(this);
		}
	}

	/**
	 * Re-sets the frame-function.
	 *
	 * @param function New function for frame
	 * @throws KAccessException if the frame is currently being
	 * referenced via the slots of another frame
	 */
	public void resetFunction(IFrameFunction function) {

		if (!referencingSlots.isEmpty()) {

			throw new KAccessException(
						"Attempting to change function "
						+ "of referenced frame " + this);
		}

		this.function = function;
	}

	/**
	 * Re-sets the frame-function to that of the specified
	 * template-frame.
	 *
	 * @param template Frame whose function is to be copied
	 * @throws KAccessException if the frame is currently being
	 * referenced via the slots of another frame
	 */
	public void alignFunction(IFrame template) {

		resetFunction(template.function);
	}

	/**
	 * Adds a frame-listener.
	 *
	 * @param listener Listener to add
	 */
	public void addListener(IFrameListener listener) {

		listeners.add(listener);
	}

	/**
	 * Creates a deep copy of this frame and all recursively
	 * referenced frames.
	 *
	 * @return Copy of this frame
	 */
	public IFrame copy() {

		return new IFrameCopier().copy(this);
	}

	/**
	 * If auto-update is not enabled (see {@link IUpdating#autoUpdate}),
	 * then performs the default set of update operations on this frame.
	 * Otherwise does nothing.
	 * <p>
	 * NOTE: Even if the default update operations do not include
	 * slot-value updates, removals of (asserted) slot-values may still
	 * occur as a result of either slot removals or value-type updates.
	 *
	 * @return Types of update produced
	 */
	public Set<IUpdateOp> update() {

		return getIUpdating().update(this);
	}

	/**
	 * Performs all of the specified update operations on this frame,
	 * whether or not auto-update is enabled (see {@link
	 * IUpdating#autoUpdate}).
	 * <p>
	 * NOTE: Even if the specified update operations do not include
	 * slot-value updates, removals of (asserted) slot-values may still
	 * occur as a result of either slot removals or value-type updates.
	 *
	 * @param ops Update operations to be performed
	 * @return Types of update produced
	 */
	public Set<IUpdateOp> update(Set<IUpdateOp> ops) {

		return getIUpdating().update(this, ops);
	}

	/**
	 * Performs the default set of update operations on this frame,
	 * whether or not auto-update is enabled (see {@link
	 * IUpdating#autoUpdate}).
	 * <p>
	 * NOTE: Even if the default update operations do not include
	 * slot-value updates, removals of (asserted) slot-values may still
	 * occur as a result of either slot removals or value-type updates.
	 *
	 * @return Types of update produced
	 */
	public Set<IUpdateOp> checkManualUpdate() {

		return getIUpdating().checkManualUpdate(this);
	}

	/**
	 * If auto-update is not enabled (see {@link IUpdating#autoUpdate}),
	 * then performs all of the specified update operations on this frame.
	 * Otherwise, performs only those specified operations that are not
	 * default operations (see {@link IUpdating#getDefaultOps}).
	 * <p>
	 * NOTE: Even if the specified update operations do not include
	 * slot-value updates, removals of (asserted) slot-values may still
	 * occur as a result of either slot removals or value-type updates.
	 *
	 * @param ops Update operations to be performed (where relevant)
	 * @return Types of update produced
	 */
	public Set<IUpdateOp> checkManualUpdate(Set<IUpdateOp> ops) {

		return getIUpdating().checkManualUpdate(this, ops);
	}

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is the same object as this one,
	 * or is another <code>IFrame</code> with the same type as this
	 * one, and the type being such that there are no attached slots
	 */
	public boolean equals(Object other) {

		if (other == this) {

			return true;
		}

		return other instanceof IFrame && equalsFrame((IFrame)other);
	}

	/**
	 */
	public int hashCode() {

		return slots.isEmpty() ? type.hashCode() : super.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean subsumes(IFrame other) {

		for (IFrame disjunct : asDisjunctsSet()) {

			if (!other.hasDisjunct(disjunct)) {

				return false;
			}
		}

		return true;
	}

	/**
	 * Tests whether the type and the current slot-values (in matching
	 * order) of this frame are identical to those of another frame.
	 * For <code>IFrame</code>-valued slots, value-match testing
	 * involves a recursive invocation of the same frame-match testing
	 * operation. Otherwise value-matching is determinied via the
	 * standard <code>equals</code> methods on the value objects.
	 *
	 * @param other Frame to test for structure-matching against this
	 * one
	 * @return true if structures match
	 */
	public boolean matchesStructure(IFrame other) {

		return equals(other)
				|| new IFrameStructureMatcher().match(this, other);
	}

	/**
	 * Tests whether the type and the current slot-values of this
	 * frame subsume those of another frame. For
	 * <code>IFrame</code>-valued slots, value-subsumption testing
	 * involves a recursive invocation of the same frame-subsumption
	 * testing operation. Otherwise value-matching is determinied via
	 * the standard {@link CValue#subsumes} method on the value-type
	 * objects.
	 *
	 * @param other Frame to test for structure-subsumption by this
	 * one
	 * @return true if this frames structure subsumes that of other
	 * frame
	 */
	public boolean subsumesStructure(IFrame other) {

		return equals(other)
				|| new IFrameStructureSubsumptionTester().match(this, other);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return new IFrameDescriber(this).describe();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayLabel() {

		return type.getDisplayLabel();
	}

	/**
	 * Provides the concept-level frame of which this frame is an
	 * instance.
	 *
	 * @return Relevant concept-level frame
	 */
	public CFrame getType() {

		return type;
	}

	/**
	 * Provides the frame-category.
	 *
	 * @return Frame-category.
	 */
	public IFrameCategory getCategory() {

		return IFrameCategory.ATOMIC;
	}

	/**
	 * Provides the frame-function.
	 *
	 * @return Frame-function.
	 */
	public IFrameFunction getFunction() {

		return function;
	}

	/**
	 * Stipulates that this frame is abstract if and only if the
	 * concept-level frame representing it's type is a {@link
	 * CFrameCategory#disjunction} frame.
	 *
	 * @return True if type-frame is a disjunction
	 */
	public boolean abstractValue() {

		return disjunctionType();
	}

	/**
	 * Provides the auto-update-enabled status for the frame, which
	 * determines whether or not any automatic updates can occur when
	 * slot-values are updated.
	 *
	 * @return True if auto-update is enabled
	 */
	public boolean autoUpdateEnabled() {

		return autoUpdateEnabled;
	}

	/**
	 * Provides the set of concept-level frames representing the
	 * currently inferred types for the frame.
	 *
	 * @return Relevant set of concept-level frames
	 */
	public CIdentifieds<CFrame> getInferredTypes() {

		return inferredTypes.getTypes();
	}

	/**
	 * Provides the set of concept-level frames representing the
	 * currently suggested types for the frame.
	 *
	 * @return Relevant set of concept-level frames
	 */
	public CIdentifieds<CFrame> getSuggestedTypes() {

		return suggestedTypes.getTypes();
	}

	/**
	 * Provides the object that represents the set of slots currently
	 * attached to the frame.
	 *
	 * @return Object representing current set of slots
	 */
	public ISlots getSlots() {

		return slots;
	}

	/**
	 * Provides the object that represents the current set of slots
	 * for which the frame provides a current value.
	 *
	 * @return Object representing current set of referencing-slots
	 */
	public ISlots getReferencingSlots() {

		return referencingSlots;
	}

	/**
	 * Provides the special disjuncts-slot for frames of category
	 * {@link IFrameCategory#DISJUNCTION}.
	 *
	 * @return Object representing current set of referencing-slots
	 * @throws KAccessException if this is not a disjunction-frame
	 */
	public ISlot getDisjunctsSlot() {

		throw new KAccessException(
					"Cannot retrieve disjuncts-slot "
					+ "from atomic frame: " + this);
	}

	/**
	 * Provides a representation of the frame as a set of disjuncts,
	 * which if the frame is of category {@link
	 * IFrameCategory#DISJUNCTION}, will be the values of the
	 * disjuncts-slot, otherwise it will just be a set consisting
	 * only of the frame itself.
	 *
	 * @return Representation of frame as set of disjuncts
	 */
	public List<IFrame> asDisjuncts() {

		return Collections.<IFrame>singletonList(this);
	}

	/**
	 * Provides the simplest possible representation of the frame,
	 * which if the frame is of category {@link
	 * IFrameCategory#DISJUNCTION} and the disjuncts-slot contains
	 * exactly one value, will be that value, otherwise it will just
	 * be the frame itself (disjunction or otherwise).
	 *
	 * @return Normalised version of frame
	 */
	public IFrame normalise() {

		return this;
	}

	/**
	 * Tests whether the frame/slot network emanating from this
	 * frame contains any cycles.
	 *
	 * @return True if any cycles detected
	 */
	public boolean leadsToCycle() {

		return new IFrameCycleTester(this).leadsToCycle();
	}

	IFrame(CFrame type, IFrameFunction function) {

		this.type = type;
		this.function = function;
	}

	IFrameEditor createEditor() {

		return new Editor();
	}

	boolean updateInferredTypes(List<CFrame> updateds) {

		return inferredTypes.update(updateds);
	}

	ISlot addSlotFree(CSlot slotType) {

		return addSlotInternal(slotType, true);
	}

	ISlot addSlotInternal(CSlot slotType) {

		return addSlotInternal(slotType, false);
	}

	void addReferencingSlot(ISlot slot) {

		validateAsReferencedFrame(slot.getContainer());
		referencingSlots.add(slot);
	}

	void removeReferencingSlot(ISlot slot) {

		referencingSlots.remove(slot);
	}

	void setMappedObject(Object mappedObject) {

		this.mappedObject = mappedObject;
	}

	void setAutoUpdateEnabled(boolean enabled) {

		autoUpdateEnabled = enabled;
	}

	void completeInstantiation(boolean freeInstance) {

		type.pollListenersForInstantiated(this, freeInstance);
	}

	Object getMappedObject() {

		return mappedObject;
	}

	private ISlot addSlotInternal(CSlot slotType, boolean free) {

		ISlot slot = new ISlot(slotType, this);

		slots.add(slot);
		IFrameSlotValueUpdateProcessor.checkAddTo(slot);

		if (!free) {

			new AutoUpdater(slot);
		}

		return slot;
	}

	private void validateAsReferencedFrame(IFrame referencer) {

		referencer.validateAsReferencingFrame();

		if (function != referencer.function) {

			throw new KAccessException(
						"Cannot add " + function + " frame: " + this
						+ ", as slot-value on " + referencer.function
						+ " frame: " + referencer);
		}
	}

	private void validateAsReferencingFrame() {

		if (disjunctionType() && function.assertion()) {

			throw new KAccessException(
						"Cannot add slot-values to assertion-frame "
						+ "with disjunction type: "
						+ this);
		}
	}

	private void performAutoUpdates() {

		performAutoUpdates(new ArrayList<IFrame>());
	}

	private void performAutoUpdates(List<IFrame> visited) {

		if (visited.add(this)) {

			autoUpdateThis();

			for (ISlot slot : referencingSlots.asList()) {

				slot.getContainer().performAutoUpdates(visited);
			}
		}
	}

	private void autoUpdateThis() {

		IUpdating updating = getIUpdating();

		while (updating.checkAutoUpdate(this).contains(IUpdateOp.SLOT_VALUES));
	}

	private boolean equalsFrame(IFrame other) {

		Set<IFrame> disjuncts = asDisjunctsSet();
		Set<IFrame> otherDisjuncts = other.asDisjunctsSet();

		if (disjuncts.size() != otherDisjuncts.size()) {

			return false;
		}

		if (disjuncts.size() != 1) {

			return disjuncts.equals(otherDisjuncts);
		}

		IFrame disjunct = disjuncts.iterator().next();
		IFrame otherDisjunct = otherDisjuncts.iterator().next();

		return disjunct.atomicFrameEqualsAtomicFrame(otherDisjunct);
	}

	private boolean hasDisjunct(IFrame atom) {

		for (IFrame disjunct : asDisjunctsSet()) {

			if (disjunct.atomicFrameEqualsAtomicFrame(atom)) {

				return true;
			}
		}

		return false;
	}

	private boolean atomicFrameEqualsAtomicFrame(IFrame other) {

		if (other == this) {

			return true;
		}

		if (slots.isEmpty() && other.slots.isEmpty()) {

			return type.equals(other.type);
		}

		return false;
	}

	private boolean disjunctionType() {

		return type.getCategory().disjunction();
	}

	private Set<IFrame> asDisjunctsSet() {

		return new HashSet<IFrame>(asDisjuncts());
	}

	private void pollListenersForUpdatedInferredTypes() {

		for (IFrameListener listener : copyListeners()) {

			listener.onUpdatedInferredTypes(inferredTypes.getTypes());
		}
	}

	private void pollListenersForUpdatedSuggestedTypes() {

		for (IFrameListener listener : copyListeners()) {

			listener.onUpdatedSuggestedTypes(suggestedTypes.getTypes());
		}
	}

	private void pollListenersForSlotAdded(ISlot slot) {

		for (IFrameListener listener : copyListeners()) {

			listener.onSlotAdded(slot);
		}
	}

	private void pollListenersForSlotRemoved(ISlot slot) {

		for (IFrameListener listener : copyListeners()) {

			listener.onSlotRemoved(slot);
		}
	}

	private List<IFrameListener> copyListeners() {

		return new ArrayList<IFrameListener>(listeners);
	}

	private IUpdating getIUpdating() {

		return getModel().getIUpdating();
	}

	private CModel getModel() {

		return type.getModel();
	}
}
