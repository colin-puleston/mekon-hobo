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

/**
 * Represents an instance-level frame.
 * <p>
 * Each such instance-level frame represents an instantiation of a
 * specific concept-level frame, which will be the frame-type of
 * the instance-level frame.
 * <p>
 * There are three categories of instance-level frame, as provided via
 * an appropriate {@link IFrameCategory} value. The possible categories
 * are:
 * <ul>
 *   <li><i>Atomic</i> Represents a standard instantiation, with the
 *   current slot-set, slot-constraints and fixed slot-values being
 *   determined via dynamic reasoning dependent on the frame-type (i.e.
 *   the concept-level frame of which the frame is an instantiation),
 *   in combination with the current set of slot-values
 *   <li><i>Disjunction</i> Represents a disjunction of instance-level
 *   frames of the relevant type, providing only a single slot, the
 *   "disjuncts" slot, whose value-type is the frame-type, and whose
 *   values (which can not themselves be of disjunction category)
 *   represent the disjuncts
 *   <li><i>Reference</i> Represents a reference to a specific existing
 *   instantiation of the relevant frame-type, typically located in some
 *   database, with the referencing instantiation providing only an
 *   identifier for the referenced instantiation (and hence providing no
 *   slots itself)
 * </ul>
 * The frame also has a function of either {@link IFrameFunction#ASSERTION}
 * or {@link IFrameFunction#QUERY}, with the two differeing in the following
 * ways:
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
public abstract class IFrame implements IEntity, IValue {

	static private final CIdentifiedsLocal<CFrame> NO_TYPES = new CIdentifiedsLocal<CFrame>();

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

	private ISlots referencingSlots = new ISlots();

	private Object mappedObject = null;
	private List<IFrameListener> listeners = new ArrayList<IFrameListener>();

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
	 * IUpdating#autoUpdate}). If the frame is not of atomic category,
	 * will do nothing.
	 * <p>
	 * NOTE: Even if the default update operations do not include
	 * slot-value updates, removals of (asserted) slot-values may still
	 * occur as a result of either slot removals or value-type updates.
	 *
	 * @return Types of update produced
	 */
	public Set<IUpdateOp> update() {

		return Collections.emptySet();
	}

	/**
	 * Performs all of the specified update operations on this frame,
	 * whether or not auto-update is enabled (see {@link #update(Set)}
	 * for further details). If the frames is not of atomic category,
	 * will do nothing.
	 *
	 * @param ops Update operations to be performed
	 * @return Types of update produced
	 */
	public Set<IUpdateOp> update(IUpdateOp... ops) {

		return Collections.emptySet();
	}

	/**
	 * Performs all of the specified update operations on this frame,
	 * whether or not auto-update is enabled (see {@link
	 * IUpdating#autoUpdate}). If the frames is not of atomic category,
	 * will do nothing.
	 * <p>
	 * NOTE: Even if the specified update operations do not include
	 * slot-value updates, removals of (asserted) slot-values may still
	 * occur as a result of either slot removals or value-type updates.
	 *
	 * @param ops Update operations to be performed
	 * @return Types of update produced
	 */
	public Set<IUpdateOp> update(Set<IUpdateOp> ops) {

		return Collections.emptySet();
	}

	/**
	 * If auto-update is not enabled (see {@link IUpdating#autoUpdate}),
	 * then performs the default set of update operations on this frame.
	 * Otherwise does nothing. If the frames is not of atomic category,
	 * will do nothing.
	 * <p>
	 * NOTE: Even if the default update operations do not include
	 * slot-value updates, removals of (asserted) slot-values may still
	 * occur as a result of either slot removals or value-type updates.
	 *
	 * @return Types of update produced
	 */
	public Set<IUpdateOp> checkManualUpdate() {

		return Collections.emptySet();
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

		return Collections.emptySet();
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

		return other.getClass() == getClass() && subsumes((IFrame)other, true);
	}

	/**
	 * For atomic-frames provides a hash-code based on the frame-type
	 * if the frame has no slots, or just the default <code>Object</code>
	 * hash-code, otherwise. For disjunction-frames provides a hash-code
	 * based on those of the current set of disjuncts. For reference-frames
	 * provides a hash-code based on the identity of referenced instance.
	 */
	public int hashCode() {

		if (reference()) {

			return getReferenceId().hashCode();
		}

		return noSlots() ? type.hashCode() : super.hashCode();
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
	public abstract String getDisplayLabel();

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

		return false;
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

		return other instanceof IFrame && disjunctsSubsumeDisjuncts((IFrame)other);
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

		return other instanceof IFrame && equalsStructure((IFrame)other);
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

		return other instanceof IFrame && subsumesStructure((IFrame)other);
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
	public abstract IFrameCategory getCategory();

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
	 * slot-values of atomic-frames are updated.
	 *
	 * @return True if auto-update is enabled
	 */
	public boolean autoUpdateEnabled() {

		return false;
	}

	/**
	 * Provides the set of concept-level frames representing the
	 * currently inferred types for the frame.
	 *
	 * @return Relevant set of concept-level frames
	 */
	public CIdentifieds<CFrame> getInferredTypes() {

		return NO_TYPES;
	}

	/**
	 * Provides the set of concept-level frames representing the
	 * currently suggested types for the frame.
	 *
	 * @return Relevant set of concept-level frames
	 */
	public CIdentifieds<CFrame> getSuggestedTypes() {

		return NO_TYPES;
	}

	/**
	 * Provides the object that represents the set of slots currently
	 * attached to the frame.
	 *
	 * @return Object representing current set of slots
	 */
	public abstract ISlots getSlots();

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
	 * Provides the identity of the referenced instance for frames
	 * of category {@link IFrameCategory#REFERENCE}.
	 *
	 * @return Identity of referenced instance
	 * @throws KAccessException if this is not a reference-frame
	 */
	public CIdentity getReferenceId() {

		throw createCategoryRetrievalException("referenced-identity");
	}

	/**
	 * Provides the identities of all instances referenced via
	 * {@link IFrameCategory#REFERENCE} frames contained within the
	 * frame/slot network emanating from this frame.
	 *
	 * @return Identities of all referenced instances
	 */
	public List<CIdentity> getAllReferenceIds() {

		List<CIdentity> referenceIds = new ArrayList<CIdentity>();

		collectReferenceIds(referenceIds);

		return referenceIds;
	}

	/**
	 * Provides the special disjuncts-slot for frames of category
	 * {@link IFrameCategory#DISJUNCTION}.
	 *
	 * @return Object representing current set of referencing-slots
	 * @throws KAccessException if this is not a disjunction-frame
	 */
	public ISlot getDisjunctsSlot() {

		throw createCategoryRetrievalException("disjuncts-slot");
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

	void completeInitialInstantiation() {

		completeInstantiation(false);
	}

	Set<IUpdateOp> completeReinstantiation(boolean possibleModelUpdates) {

		completeInstantiation(true);

		return Collections.<IUpdateOp>emptySet();
	}

	ISlot addSlotInternal(CSlot slotType) {

		throw createCategoryAdditionException("slot");
	}

	ISlot addSlotInternal(ISlot slot) {

		throw createCategoryAdditionException("slot");
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

	IFrameEditor createEditor() {

		throw createCategoryRetrievalException("editor");
	}

	abstract IFrame copyEmpty(boolean freeInstance);

	abstract void autoUpdate(Set<IFrame> visited);

	boolean updateInferredTypes(List<CFrame> updateds) {

		return false;
	}

	void autoUpdateReferencingFrames(Set<IFrame> visited) {

		for (ISlot slot : referencingSlots.asList()) {

			slot.getContainer().autoUpdate(visited);
		}
	}

	boolean freeInstance() {

		return freeInstance;
	}

	Object getMappedObject() {

		return mappedObject;
	}

	boolean equalsLocalStructure(IFrame other) {

		return subsumesLocalStructure(other, true);
	}

	boolean subsumesLocalStructure(IFrame other) {

		return subsumesLocalStructure(other, false);
	}

	int localHashCode() {

		return reference() ? getReferenceId().hashCode() : type.hashCode();
	}

	void collectReferenceIds(List<CIdentity> referenceIds) {

		for (ISlot slot : getSlots().asList()) {

			if (slot.getValueType() instanceof CFrame) {

				for (IValue value : slot.getValues().asList()) {

					((IFrame)value).collectReferenceIds(referenceIds);
				}
			}
		}
	}

	abstract String describeLocally();

	List<IFrameListener> copyListeners() {

		return new ArrayList<IFrameListener>(listeners);
	}

	private void completeInstantiation(boolean reinstantiation) {

		type = type.toNormalisedInstanceType();

		if (!freeInstance) {

			type.pollListenersForInstantiated(this, reinstantiation);
		}
	}

	private CFrame toExtension(Set<IFrame> visited) {

		CExtender extender = new CExtender(type);

		for (ISlot slot : getSlots().asList()) {

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

	private boolean disjunctsSubsumeDisjuncts(IFrame other) {

		if (other == this) {

			return true;
		}

		for (IFrame disjunct : other.asDisjuncts()) {

			if (!anyDisjunctSubsumesDisjunct(disjunct)) {

				return false;
			}
		}

		return true;
	}

	private boolean anyDisjunctSubsumesDisjunct(IFrame otherDisjunct) {

		if (otherDisjunct == this) {

			return true;
		}

		for (IFrame disjunct : asDisjuncts()) {

			if (disjunct.subsumes(otherDisjunct, false)) {

				return true;
			}
		}

		return false;
	}

	private boolean subsumes(IFrame other, boolean equalityOnly) {

		if (other == this) {

			return true;
		}

		return noSlots() && other.noSlots() && subsumesLocalStructure(other, equalityOnly);
	}

	private boolean subsumesLocalStructure(IFrame other, boolean equalityOnly) {

		if (reference()) {

			return other.reference() && getReferenceId().equals(other.getReferenceId());
		}

		if (equalityOnly) {

			return !other.reference() && type.equals(other.type);
		}

		return type.subsumes(other.type);
	}

	private boolean equalsStructure(IFrame other) {

		return new IStructureEqualityTester().match(this, other);
	}

	private boolean subsumesStructure(IFrame other) {

		return new IStructureSubsumptionTester().match(this, other);
	}

	private boolean reference() {

		return getCategory().reference();
	}

	private boolean noSlots() {

		return getSlots().isEmpty();
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

		if (type.getCategory().disjunction() && function.assertion()) {

			throw new KAccessException(
						"Cannot add slot-values to assertion-frame "
						+ "with disjunction type: " + this);
		}
	}

	private KAccessException createCategoryAdditionException(String entityName) {

		return createCategoryException("Cannot add " + entityName + " to");
	}

	private KAccessException createCategoryRetrievalException(String entityName) {

		return createCategoryException("Cannot retrieve " + entityName + " from");
	}

	private KAccessException createCategoryException(String msgPrefix) {

		return createCategoryException(msgPrefix + " " + getCategory() + " frame: " + this);
	}
}
