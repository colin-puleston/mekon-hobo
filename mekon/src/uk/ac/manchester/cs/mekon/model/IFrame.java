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
import uk.ac.manchester.cs.mekon.model.util.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Represents an instance-level atomic-frame.
 * <p>
 * The frame has a category of either {@link IFrameCategory#ATOMIC}
 * or {@link IFrameCategory#DISJUNCTION}. The latter category being a
 * special case where the frame will always have exactly one slot,
 * which will be a special "disjuncts-slot" whose value-type is the
 * same as the type of the disjunction-frame, and whose values, which
 * must be of category {@link IFrameCategory#ATOMIC}, represent the
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

	/**
	 * Creates an instance-level frame of category {@link
	 * IFrameCategory#DISJUNCTION}, taking the frame-type to be the
	 * closest possible common subsumer of the types of the specified
	 * set of disjunct-frames, and adding each of those disjuncts a
	 * value for the disjuncts-slot.
	 *
	 * @param disjuncts Set of disjunct-frames, to be used in determining
	 * the type of the frame, and to be set as disjuncts-slot values
	 * @return Resulting instance-level disjunction-frame
	 */
	static public IFrame createDisjunction(Collection<IFrame> disjuncts) {

		CFrame type = getDefaultDisjunctionType(disjuncts);
		IFrame disjunction = type.instantiateDisjunction();
		ISlot disjunctsSlot = disjunction.getDisjunctsSlot();

		disjunctsSlot.getValuesEditor().addAll(disjuncts);

 		return disjunction;
	}

	static private CFrame getDefaultDisjunctionType(Collection<IFrame> disjuncts) {

		return getCommonFrameTypeSubsumer(getFrameTypes(disjuncts));
	}

	static private List<CFrame> getFrameTypes(Collection<IFrame> frames) {

		List<CFrame> types = new ArrayList<CFrame>();

		for (IFrame frame : frames) {

			types.add(frame.getType());
		}

		return types;
	}

	static private CFrame getCommonFrameTypeSubsumer(List<CFrame> types) {

		return new CFrameSubsumers(CVisibility.EXPOSED, types).getSingleCommon();
	}

	private CFrame type;
	private IFrameFunction function;
	private boolean freeInstance;

	private DynamicTypes inferredTypes = new DynamicTypes();
	private DynamicTypes suggestedTypes = new DynamicTypes();

	private ISlots slots = new ISlots();
	private ISlots referencingSlots = new ISlots();

	private Object mappedObject = null;
	private List<IFrameListener> listeners = new ArrayList<IFrameListener>();

	private boolean autoUpdateEnabled = true;
	private boolean autoUpdating = false;

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
						CValue<?> valueType,
						CCardinality cardinality,
						CActivation activation,
						CEditability editability,
						Collection<CAnnotations> annotationSources) {

			CFrame atomicType = type.getAtomicFrame();
			CSlot slotType = new CSlot(atomicType, identity, valueType, cardinality);
			CAnnotations annotations = CAnnotations.combineAll(slotType, annotationSources);

			slotType.setSource(source);
			slotType.setActivation(activation);
			slotType.setEditability(editability);
			slotType.setAnnotations(annotations);

			return addSlot(slotType);
		}

		public void removeSlot(ISlot slot) {

			slot.getValues().clearAllFixedAndAssertedValues();
			slots.remove(slot);
			pollListenersForSlotRemoved(slot);
		}

		public void setAutoUpdateEnabled(boolean enabled) {

			IFrame.this.setAutoUpdateEnabled(enabled);
		}
	}

	private class AutoUpdater implements KValuesListener<IValue> {

		private ISlotValues slotValues;

		public void onAdded(IValue value) {

			checkAutoUpdates();
		}

		public void onRemoved(IValue value) {

			checkAutoUpdates();
		}

		public void onCleared(List<IValue> values) {

			checkAutoUpdates();
		}

		AutoUpdater(ISlot slot) {

			slotValues = slot.getValues();
			slotValues.addValuesListener(this);
		}

		private void checkAutoUpdates() {

			if (autoUpdateEnabled && !autoUpdating) {

				autoUpdating = true;
				performAutoUpdates();
				autoUpdating = false;
			}
		}
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
	 * Creates a representation of the <code>IFrame/ISlot</code> network
	 * emanating from this frame, as a {@link CFrame} object of category
	 * {@link CFrameCategory#ABSTRACT_EXTENSION}.
	 *
	 * @return Resulting representation of network
	 * @throws KAccessException If network is not a tree, and hence not
	 * convertible
	 */
	public CFrame toExtension() {

		return toExtension(new HashSet<IFrame>());
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
	public Set<IUpdateOp> update() {

		return getIUpdating().update(this);
	}

	/**
	 * Performs all of the specified update operations on this frame,
	 * whether or not auto-update is enabled (see {@link #update(Set)}
	 * for further details).
	 *
	 * @param ops Update operations to be performed
	 * @return Types of update produced
	 */
	public Set<IUpdateOp> update(IUpdateOp... ops) {

		return update(new HashSet<IUpdateOp>(Arrays.asList(ops)));
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
	 * Tests for equality between this and another specified object,
	 * which will be the case if and only if the other object is the same
	 * object as this one, or is another <code>IFrame</code> with the
	 * same type as this one, and the type being such that there are no
	 * attached slots.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if objects are equal
	 */
	public boolean equals(Object other) {

		if (other == this) {

			return true;
		}

		if (other instanceof IFrame) {

			return equalsFrame((IFrame)other);
		}

		return false;
	}

	/**
	 * Provides a hash-code based on the frame-type if the frame has
	 * any slots, or just be the default <code>Object</code> hash-code,
	 * otherwise.
	 */
	public int hashCode() {

		return slots.isEmpty() ? type.hashCode() : super.hashCode();
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
	 * Tests whether this value-entity subsumes another specified
	 * value-entity, which will be the case if and only if the other
	 * value-entity is another <code>IFrame</code> object that has
	 * no slots, and whose type if subsumed by the type of this one.
	 *
	 * @param other Other value-entity to test for subsumption
	 * @return True if this value-entity subsumes other value-entity
	 */
	public boolean subsumes(IValue other) {

		return other instanceof IFrame && subsumesFrame((IFrame)other);
	}

	/**
	 * Tests whether this value-entity currently has a structure that
	 * is equivalent to another value-entity, which will be the case if
	 * and only if the other value-entity is another <code>IFrame</code>
	 * object, and both the type and the oredered set of current
	 * slot-values of this frame are identical to those of the other
	 * frame. For <code>IFrame</code>-valued slots, value-match testing
	 * involves a recursive invocation of the same frame-match testing
	 * operation. Otherwise value-matching is determinied via the
	 * standard <code>equals</code> methods on the value objects.
	 *
	 * @param other Other value-entity to test for structure-matching
	 * with this one
	 * @return true if structures match
	 */
	public boolean equalsStructure(IValue other) {

		return testStructure(other, true);
	}

	/**
	 * Tests whether this value-entity currently has a structure that
	 * subsumes that of another value-entity, which will be the case if
	 * and only if the other value-entity is another <code>IFrame</code>
	 * object, and the both the type and the current slot-values of this
	 * frame subsume those of the other frame. For
	 * <code>IFrame</code>-valued slots, value-subsumption testing involves
	 * a recursive invocation of the same frame-subsumption testing
	 * operation. Otherwise value-matching is determinied via the standard
	 * {@link CValue#subsumes} method on the value-type objects.
	 *
	 * @param other Other value-entity to test for structure-subsumption
	 * with this one
	 * @return True if structure-subsumption holds
	 */
	public boolean subsumesStructure(IValue other) {

		return testStructure(other, false);
	}

	/**
	 * Calculates an integer-value based on the current recursive
	 * structure of the frame, suitable for use as a hash-code value for
	 * any wrapper-class that is to use the {@link #equalsStructure}
	 * method in it's implementation of the general {@link Object#equals}
	 * method.
	 *
	 * @return Suitable structure-based hash-code value
	 */
	public int structuralHashCode() {

		return new IStructuralHashCode(this).getCode();
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

	IFrame(CFrame type, IFrameFunction function, boolean freeInstance) {

		this.type = type;
		this.function = function;
		this.freeInstance = freeInstance;
	}

	IFrameEditor createEditor() {

		return new Editor();
	}

	void normaliseType() {

		type = type.toNormalisedInstanceType();
	}

	IFrame copyEmpty(boolean freeInstance) {

		return new IFrame(type, function, freeInstance);
	}

	boolean updateInferredTypes(List<CFrame> updateds) {

		return inferredTypes.update(updateds);
	}

	ISlot addSlotInternal(CSlot slotType) {

		return addSlotInternal(new ISlot(slotType, this));
	}

	ISlot addSlotInternal(ISlot slot) {

		slots.add(slot);
		IFrameSlotValueUpdateProcessor.checkAddTo(slot);

		new AutoUpdater(slot);

		return slot;
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

	Set<IUpdateOp> reinitialise() {

		return getIUpdating().reinitialise(this);
	}

	void completeInstantiation(boolean reinstantiation) {

		type.pollListenersForInstantiated(this, reinstantiation);
	}

	boolean freeInstance() {

		return freeInstance;
	}

	Object getMappedObject() {

		return mappedObject;
	}

	void autoUpdateThis() {

		IUpdating updating = getIUpdating();

		while (updating.checkAutoUpdate(this).contains(IUpdateOp.SLOT_VALUES));
	}

	private CFrame toExtension(Set<IFrame> visited) {

		CExtender extender = new CExtender(type);

		for (ISlot slot : slots.asList()) {

			CIdentity slotId = slot.getType().getIdentity();

			for (IValue value : slot.getValues().asList()) {

				extender.addSlotValue(slotId, toExtensionSlotValue(slot, value, visited));
			}
		}

		return extender.extend();
	}

	private CValue<?> toExtensionSlotValue(ISlot slot, IValue value, Set<IFrame> visited) {

		if (value instanceof IFrame) {

			return ((IFrame)value).toExtensionSlotValue(visited);
		}

		if (value instanceof INumber) {

			return ((INumber)value).normaliseValueTypeTo((CNumber)slot.getValueType());
		}

		return value.getType();
	}

	private CValue<?> toExtensionSlotValue(Set<IFrame> visited) {

		if (visited.add(this)) {

			return toExtension(visited);
		}

		throw new KAccessException(
					"Cannot convert IFrame/ISlot-network "
					+ "to extension-CFrame: "
					+ "Cycle detected at: " + this);

	}

	private boolean testStructure(IValue other, boolean equality) {

		return other instanceof IFrame && testStructure((IFrame)other, equality);
	}

	private boolean testStructure(IFrame other, boolean equality) {

		if (other instanceof IFrame) {

			return equals(other) || getStructureTester(equality).match(this, other);
		}

		return false;
	}

	private IStructureTester getStructureTester(boolean equality) {

		return equality
				? new IStructureEqualityTester()
				: new IStructureSubsumptionTester();
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

		performAutoUpdates(new HashSet<IFrame>());
	}

	private void performAutoUpdates(Set<IFrame> visited) {

		if (visited.add(this)) {

			autoUpdateThis();

			for (ISlot slot : referencingSlots.asList()) {

				slot.getContainer().performAutoUpdates(visited);
			}
		}
	}

	private boolean equalsFrame(IFrame other) {

		if (slots.isEmpty() && other.slots.isEmpty()) {

			return type.equals(other.type);
		}

		return false;
	}

	private boolean subsumesFrame(IFrame other) {

		if (slots.isEmpty() && other.slots.isEmpty()) {

			return type.subsumes(other.type);
		}

		return false;
	}

	private boolean disjunctionType() {

		return type.getCategory().disjunction();
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
