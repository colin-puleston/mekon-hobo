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
 * Represents an instance-level model-frame. The frame can be
 * either of category {@link IFrameCategory#ASSERTION} or
 * category {@link IFrameCategory#QUERY}. Assertion-frames differ
 * from query-frames in the following ways:
 * <ul>
 *   <li>Assertion-frames cannot be instantiations of
 *   disjunction-frames (see {@link CFrameCategory#disjunction})
 *   <li>Dependent slots on query-frames (see {@link ISlot#dependent})
 *   are editable by the client (see {@link ISlot#editable}), which
 *   is not the case for dependent slots on assertion-frames
 *   <li>By default, all slots on query-frames can be given abstract
 *	 values (see {@link IValue#abstractValue}), whereas only those
 *	 slots on assertion-frames whose associated properties are defined
 *	 to be {@link CSlot#abstractAssertable} can. However, in either
 *	 case, this default behaviour can be overriden for individual
 *	 instance-level slots.
 * </ul>
 * Query-frames and assertion-frames cannot be mixed within a single
 * model-instantiation. Attempting to do so will result in an
 * exception.
 *
 * @author Colin Puleston
 */
public class IFrame implements IEntity, IValue {

	private CFrame type;
	private DynamicTypes inferredTypes = new DynamicTypes();
	private DynamicTypes suggestedTypes = new DynamicTypes();
	private IFrameCategory category;

	private ISlots slots = new ISlots();
	private ISlots referencingSlots = new ISlots();

	private Object mappedObject = null;
	private List<IFrameListener> listeners = new ArrayList<IFrameListener>();

	private class DynamicTypes {

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

			ISlot slot = new ISlot(IFrame.this, slotType);

			new DynamicUpdater(slot);
			IFrameSlotValueUpdateProcessor.checkAddTo(slot);

			slots.add(slot);
			pollListenersForSlotAdded(slot);

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

			slot.getValues().clearAllFixedAndAsserteds();
			slots.remove(slot);
			pollListenersForSlotRemoved(slot);
		}
	}

	private class DynamicUpdater implements KUpdateListener {

		private ISlotValues slotValues;
		private List<IValue> assertedValues;

		public void onUpdated() {

			List<IValue> latestAsserteds = slotValues.getAssertedValues();

			if (!latestAsserteds.equals(assertedValues)) {

				performDynamicUpdates();

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
	 * Re-sets the frame-category.
	 *
	 * @param category New category for frame
	 * @throws KAccessException if the frame is currently being
	 * referenced via the slots of another frame
	 */
	public void resetCategory(IFrameCategory category) {

		if (!referencingSlots.isEmpty()) {

			throw new KAccessException(
						"Attempting to change category "
						+ "of referenced frame " + this);
		}

		this.category = category;
	}

	/**
	 * Re-sets the frame-category to that of the specified
	 * template-frame.
	 *
	 * @param template Frame whose category is to be copied
	 * @throws KAccessException if the frame is currently being
	 * referenced via the slots of another frame
	 */
	public void alignCategory(IFrame template) {

		resetCategory(template.category);
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

		return new IFrameCopier().getCopy(this);
	}

	/**
	 * If auto-update is not enabled (see {@link IUpdating#autoUpdate}),
	 * then performs the default set of update operations on this frame.
	 * Otherwise does nothing.
	 */
	public void checkManualUpdate() {

		getIUpdating().checkManualUpdate(this);
	}

	/**
	 * If auto-update is not enabled (see {@link IUpdating#autoUpdate}),
	 * then performs all of the specified update operations on this frame.
	 * Otherwise, performs only those specified operations that are not
	 * default operations (see {@link IUpdating#getDefaultOps}).
	 *
	 * @param ops Update operations to be performed (where relevant)
	 */
	public void checkManualUpdate(Set<IUpdateOp> ops) {

		getIUpdating().checkManualUpdate(this, ops);
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

		if (other instanceof IFrame) {

			IFrame otherFrame = (IFrame)other;

			if (slots.isEmpty() && otherFrame.slots.isEmpty()) {

				return type.equals(otherFrame.type);
			}
		}

		return false;
	}

	/**
	 * Tests whether this frame and another one have identical types,
	 * identical inferred-types and matching slot-values (in matching
	 * order. For <code>IFrame</code>-valued slots, value-matching
	 * involves a recusive invocation of the same frame-matching
	 * operation. Otherwise value-matching is determinied via the
	 * standard <code>equals</code> methods on the value objects.
	 *
	 * @param other Frame to test for matching with this one
	 * @return true if frames match
	 */
	public boolean matches(IFrame other) {

		return equals(other) || new IFrameMatcher().match(this, other);
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
	 * Provides the frame-category.
	 *
	 * @return Frame-category.
	 */
	public IFrameCategory getCategory() {

		return category;
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
	 * Tests whether the frame/slot network emanating from this
	 * frame contains any cycles.
	 *
	 * @return True if any cycles detected
	 */
	public boolean leadsToCycle() {

		return new IFrameCycleTester(this).leadsToCycle();
	}

	IFrame(IFrame template) {

		this(template.type, template.category);
	}

	IFrame(CFrame type, IFrameCategory category) {

		this.type = type;
		this.category = category;
	}

	IFrameEditor createEditor() {

		return new Editor();
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

	private void validateAsReferencedFrame(IFrame referencingFrame) {

		referencingFrame.validateAsReferencingFrame();

		if (category != referencingFrame.category) {

			String thisCat = category.toString();
			String refCat = referencingFrame.category.toString();

			throw new KAccessException(
						"Cannot add frame: " + this
						+ " as slot-value on frame: " + referencingFrame
						+ " (attempting to mix assertion and query frames)");
		}
	}

	private void validateAsReferencingFrame() {

		if (disjunctionType() && category.assertion()) {

			throw new KAccessException(
						"Cannot add slot-values to assertion-frame "
						+ "with disjunction type: "
						+ this);
		}
	}

	private void performDynamicUpdates() {

		performDynamicUpdates(new ArrayList<IFrame>());
	}

	private void performDynamicUpdates(List<IFrame> visited) {

		if (visited.add(this)) {

			getIUpdating().checkAutoUpdate(this);

			for (ISlot slot : referencingSlots.asList()) {

				slot.getContainer().performDynamicUpdates(visited);
			}
		}
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
