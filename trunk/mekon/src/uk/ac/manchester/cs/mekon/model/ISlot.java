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

/**
 * Represents an instance-level model-slot.
 *
 * @author Colin Puleston
 */
public class ISlot implements IEntity {

	private CSlot type;
	private IFrame container;
	private FSlotAttributes attributes;
	private ISlotValues values;
	private ISlotValuesEditor valuesEditor;
	private List<ISlotListener> listeners = new ArrayList<ISlotListener>();

	private class Editor implements ISlotEditor {

		public void setActive(boolean active) {

			attributes.setActive(active);
		}

		public void setDependent(boolean editable) {

			attributes.setDependent(editable);
		}

		public boolean setValueType(CValue<?> valueType) {

			if (!valueType.equals(getValueType())) {

				attributes.setValueType(valueType);

				values.removeInvalidValues();
				pollListenersForUpdatedValueType();

				return true;
			}

			return false;
		}

		public boolean setFixedValues(List<IValue> fixedValues) {

			return values.updateFixedValues(fixedValues);
		}
	}

	/**
	 * Adds a slot-listener.
	 *
	 * @param listener Listener to add
	 */
	public void addListener(ISlotListener listener) {

		listeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return FEntityDescriber.entityToString(this, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayLabel() {

		return type.getDisplayLabel();
	}

	/**
	 * Provides the concept-level slot of which this slot is an
	 * instance.
	 *
	 * @return Relevant concept-level slot
	 */
	public CSlot getType() {

		return type;
	}

	/**
	 * Provides the frame to which the slot is attached.
	 *
	 * @return Frame to which slot is attached
	 */
	public IFrame getContainer() {

		return container;
	}

	/**
	 * Provides the current value-type for the slot.
	 *
	 * @return Current value-type for slot
	 */
	public CValue<?> getValueType() {

		return attributes.getValueType();
	}

	/**
	 * Specifies whether the slot is "active" on the particular frame
	 * to which it is attached. If a slot is inactive then it will
	 * never have any current values. A slot that is inactive on a
	 * particular frame may be active on one or more descendant frames.
	 *
	 * @return True if slot is active
	 */
	public boolean active() {

		return attributes.active();
	}

	/**
	 * Specifies whether the values for the slot can be entirely
	 * determined by the model, based on the current values of other
	 * slots in the model-instantiation.
	 *
	 * @return True if all slot-values can be entirely determined by
	 * the model
	 */
	public boolean dependent() {

		return attributes.dependent();
	}

	/**
	 * Specifies whether the slot-values can be edited by the client.
	 * This will always be the case if the container-frame of the slot
	 * is of category {@link IFrameCategory#QUERY}. Otherwise it will
	 * only be the case for non-{@link #dependent} slots.
	 *
	 * @return True if slot is currently editable by client
	 */
	public boolean editable() {

		return querySlot() || !dependent();
	}

	/**
	 * Specifies whether the slot can be given abstract values. This
	 * will be the case if and only if the container-frame of the slot
	 * is of category {@link IFrameCategory#QUERY}, or the associated
	 * concept-level slot of which this slot is an instance is
	 * {@link CSlot#abstractAssertable}.
	 *
	 * @return True if abstract values allowed
	 */
	public boolean abstractValuesAllowed() {

		return querySlot() || type.abstractAssertable();
	}

	/**
	 * Provides the object for accessing the slot-values.
	 *
	 * @return Value-access object
	 */
	public ISlotValues getValues() {

		return values;
	}

	/**
	 * Provides the object for editing the slot-values.
	 *
	 * @return Value-edit object
	 * @throws KAccessException if slot is inactive or not editable
	 */
	public ISlotValuesEditor getValuesEditor() {

		checkExternalValuesEditorAccess(active(), "inactive");
		checkExternalValuesEditorAccess(editable(), "non-editable");

		return getValuesEditorInternal();
	}

	ISlot(IFrame container, CSlot type) {

		this(container, type, type.getAttributes());
	}

	ISlot(IFrame container, ISlot template) {

		this(container, template.type, template.attributes);
	}

	ISlotEditor createEditor() {

		return new Editor();
	}

	ISlotValuesEditor getValuesEditorInternal() {

		return valuesEditor;
	}

	private ISlot(IFrame container, CSlot type, FSlotAttributes attributes) {

		this.type = type;
		this.container = container;
		this.attributes = attributes.copy();

		values = type.getCardinality().createSlotValues(this);
		valuesEditor = new ISlotValuesEditor(values);
	}

	private CModel getModel() {

		return container.getType().getModel();
	}

	private boolean querySlot() {

		return container.getCategory().query();
	}

	private void checkExternalValuesEditorAccess(
						boolean legalAccess,
						String accessProblem) {

		if (!legalAccess) {

			throw new KAccessException(
						"Cannot edit values for "
						+ accessProblem
						+ " slot: "
						+ this);
		}
	}

	private void pollListenersForUpdatedValueType() {

		for (ISlotListener listener : copyListeners()) {

			listener.onUpdatedValueType(getValueType());
		}
	}

	private List<ISlotListener> copyListeners() {

		return new ArrayList<ISlotListener>(listeners);
	}
}
