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
 * Represents an instance-level model-frame. The frame is defined
 * as either:
 * <ul>
 *   <li><i>Concrete-instance:</i> Represents a specific concrete
 *   instantiation
 *   <li><i>Query-instance:</i> Represents a set of possible
 *   instantiations
 * </ul>
 * Concrete-instances differ from query-instances in the following
 * ways:
 * <ul>
 *   <li>Concrete-instances cannot be instantiations of
 *   disjunction-frames (see {@link CFrame#disjunction})
 *   <li>Slots on concrete-instances cannot have abstract values
 *   (see {@link IValue#abstractValue})
 *   <li>Derived-values slots on query-instances (see {@link
 *   ISlot#derivedValues}) are editable by the client (see {@link
 *   ISlot#editable}), which is not the case for derived-values
 *   slots on concrete-instances
 * </ul>
 * Query-instances and concrete-instances cannot be mixed within a
 * single model-instantiation. Attempting to add a query-instance
 * as a slot-value on a concrete-instance will result in an
 * exception. However, a concrete-instance can be added as a
 * slot-value on a query-instance, since it will then automatically
 * become a query-instance (unless it is already a slot-value on a
 * concrete-instance, in which case an exception will result).
 *
 * @author Colin Puleston
 */
public class IFrame implements IEntity, IValue {

	private CFrame type;
	private CIdentifiedsLocal<CFrame> inferredTypes = new CIdentifiedsLocal<CFrame>();
	private boolean queryInstance;

	private ISlots slots = new ISlots();
	private ISlots referencingSlots = new ISlots();

	private Object mappedObject = null;
	private List<IFrameListener> listeners = new ArrayList<IFrameListener>();

	private class Editor implements IFrameEditor {

		public void updateInferredTypes(List<CFrame> inferredTypes) {

			removeOldInferredTypes(inferredTypes);
			addNewInferredTypes(inferredTypes);

			pollListenersForUpdatedInferredTypes();
		}

		public ISlot addSlot(CSlot slotType) {

			ISlot slot = new ISlot(IFrame.this, slotType);

			new DynamicUpdater(slot);
			IFrameSlotValueUpdateProcessor.checkAddTo(slot);

			slots.add(slot);

			return slot;
		}

		public ISlot addSlot(
						CProperty property,
						CSource source,
						CCardinality cardinality,
						CValue<?> valueType) {

			CSlot slotType = new CSlot(type, property, cardinality, valueType);

			slotType.setSource(source);

			return addSlot(slotType);
		}

		public void removeSlot(ISlot slot) {

			slots.remove(slot);
		}
	}

	private class DynamicUpdater implements KUpdateListener {

		private ISlotValues slotValues;
		private List<IValue> assertedValues;

		public void onUpdated() {

			List<IValue> latestAsserteds = slotValues.getAssertedValues();

			if (!latestAsserteds.equals(assertedValues)) {

				performDynamicUpdate();

				assertedValues = latestAsserteds;
			}
		}

		DynamicUpdater(ISlot slot) {

			slotValues = slot.getValues();
			assertedValues = slotValues.getAssertedValues();

			slotValues.addUpdateListener(this);
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
	 * If the {@link CModel#autoUpdate} facility is not enabled, then
	 * invokes the mechanism for dynamically updating the set of
	 * slots associated with this frame, based on the current state of
	 * the frame. Otherwise does nothing.
	 */
	public void checkManualUpdate() {

		checkUpdate(false);
	}

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is the same object as this one,
	 * or is another <code>IFrame</code> with the same type as this
	 * one, and the type being such that there are no attached slots.
	 */
	public boolean equals(Object other) {

		if (other == this) {

			return true;
		}

		if (other instanceof IFrame) {

			IFrame otherFrame = (IFrame)other;

			if (slots.isEmpty() && otherFrame.slots.isEmpty()) {

				return type.equals(otherFrame.type);
			}
		}

		return false;
	}

	/**
	 * Provides hash-code based on type value.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return type.hashCode();
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
	 * Specifies whether this frame is a query-instance.
	 *
	 * @return True if query-instance
	 */
	public boolean queryInstance() {

		return queryInstance;
	}

	/**
	 * Stipulates that this frame is abstract if and only if the
	 * concept-level frame representing it's type is a {@link
	 * CFrame#disjunction} frame.
	 *
	 * @return True if type-frame is a disjunction
	 */
	public boolean abstractValue() {

		return type.disjunction();
	}

	/**
	 * Provides the set of concept-level frames representing the
	 * currently inferred types for the frame.
	 *
	 * @return Relevant set of concept-level frames
	 */
	public CIdentifieds<CFrame> getInferredTypes() {

		return inferredTypes;
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

	IFrame(CFrame type, boolean queryInstance) {

		this.type = type;
		this.queryInstance = queryInstance;
	}

	IFrameEditor createEditor() {

		return new Editor();
	}

	void addReferencingSlot(ISlot slot) {

		checkReferencingFrame(slot.getContainer());
		referencingSlots.add(slot);
	}

	void removeReferencingSlot(ISlot slot) {

		referencingSlots.remove(slot);
	}

	void setMappedObject(Object mappedObject) {

		this.mappedObject = mappedObject;
	}

	<T>T getMappedObject(Class<T> expectedType) {

		if (mappedObject == null) {

			return null;
		}

		Class<?> mappedType = mappedObject.getClass();

		if (expectedType.isAssignableFrom(mappedType)) {

			return expectedType.cast(mappedObject);
		}

		throw new KAccessException(
					"Mapped-object not of expected type for: " + this
					+ ", expected type: " + expectedType
					+ " , found type: " + mappedType);
	}

	private void checkReferencingFrame(IFrame referencingFrame) {

		referencingFrame.validateAsReferencingFrame();

		if (queryInstance != referencingFrame.queryInstance) {

			if (queryInstance) {

				throwReferencingFrameException(
					referencingFrame,
					"Cannot add query-instance frame "
					+ "as slot-value for concrete-instance");
			}

			if (!referencingSlots.isEmpty()) {

				throwReferencingFrameException(
					referencingFrame,
					"Cannot use frame as slot-value "
					+ "for both concrete and query-instances");
			}

			ensureOnlyQueryInstancesReferenced();
		}
	}

	private void ensureOnlyQueryInstancesReferenced() {

		if (!queryInstance) {

			queryInstance = true;

			for (ISlot slot : slots.asList()) {

				ensureOnlyQueryInstancesReferencedFrom(slot);
			}
		}
	}

	private void ensureOnlyQueryInstancesReferencedFrom(ISlot slot) {

		if (slot.getValueType() instanceof CFrame) {

			for (IValue value : slot.getValues().asList()) {

				((IFrame)value).ensureOnlyQueryInstancesReferenced();
			}
		}
	}

	private void validateAsReferencingFrame() {

		if (!queryInstance && !type.disjunction()) {

			throw new KAccessException(
						"Cannot add slot-values to "
						+ "concrete-instance of disjunction-frame: "
						+ this);
		}
	}

	private void throwReferencingFrameException(
					IFrame referencingFrame,
					String extraMsg) {

		throw new KAccessException(
					"Cannot add frame: " + this
					+ " as slot-value on frame: " + referencingFrame
					+ ": " + extraMsg);
	}

	private void performDynamicUpdate() {

		while (updateAllBackwardsFromThis(new ArrayList<IFrame>()));
	}

	private boolean updateAllBackwardsFromThis(List<IFrame> visited) {

		boolean anyValueUpdates = false;

		if (!visited.contains(this)) {

			visited.add(this);

			if (checkUpdate(true)) {

				anyValueUpdates = true;
			}

			if (updateAllBackwardsFromReferencers(visited)) {

				anyValueUpdates = true;
			}
		}

		return anyValueUpdates;
	}

	private boolean updateAllBackwardsFromReferencers(List<IFrame> visited) {

		boolean anyValueUpdates = false;

		for (ISlot slot : referencingSlots.asList()) {

			if (slot.getContainer().updateAllBackwardsFromThis(visited)) {

				anyValueUpdates = true;
			}
		}

		return anyValueUpdates;
	}

	private void removeOldInferredTypes(List<CFrame> updatedInferredTypes) {

		for (CFrame inferredType : inferredTypes.asList()) {

			if (!updatedInferredTypes.contains(inferredType)) {

				inferredTypes.remove(inferredType);
			}
		}
	}

	private void addNewInferredTypes(List<CFrame> updatedInferredTypes) {

		for (CFrame inferredType : updatedInferredTypes) {

			if (!inferredTypes.contains(inferredType)) {

				inferredTypes.add(inferredType);
			}
		}
	}

	private boolean checkUpdate(boolean autoUpdate) {

		return type.checkUpdateInstance(this, autoUpdate);
	}

	private void pollListenersForUpdatedInferredTypes() {

		for (IFrameListener listener : copyListeners()) {

			listener.onUpdatedInferredTypes(inferredTypes);
		}
	}

	private List<IFrameListener> copyListeners() {

		return new ArrayList<IFrameListener>(listeners);
	}
}
