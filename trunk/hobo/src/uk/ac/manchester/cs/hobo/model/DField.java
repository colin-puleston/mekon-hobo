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

package uk.ac.manchester.cs.hobo.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Represents a field in the Object Model (OM). The field will be
 * bound to a specific {@link ISlot} object in the frames-based
 * representation.
 *
 * @author Colin Puleston
 */
public abstract class DField<V> implements DFieldView<V> {

	private DModel model;
	private DValueType<V> valueType;
	private ISlot slot = null;

	private Map<KValuesListener<V>, SlotValuesListener> slotValuesListeners
						= new HashMap<KValuesListener<V>, SlotValuesListener>();

	private class SlotValuesListener implements KValuesListener<IValue> {

		private KValuesListener<V> fieldListener;

		public void onAdded(IValue value) {

			if (convertibleToFieldValue(value)) {

				fieldListener.onAdded(toFieldValue(value));
			}
		}

		public void onRemoved(IValue value) {

			if (convertibleToFieldValue(value)) {

				fieldListener.onRemoved(toFieldValue(value));
			}
		}

		public void onCleared(List<IValue> values) {

			fieldListener.onCleared(toFieldValues(values));
		}

		SlotValuesListener(KValuesListener<V> fieldListener) {

			this.fieldListener = fieldListener;

			slotValuesListeners.put(fieldListener, this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addUpdateListener(KUpdateListener listener) {

		getSlotValues().addUpdateListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeUpdateListener(KUpdateListener listener) {

		getSlotValues().removeUpdateListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addValuesListener(KValuesListener<V> listener) {

		getSlotValues().addValuesListener(new SlotValuesListener(listener));
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeValuesListener(KValuesListener<V> listener) {

		SlotValuesListener slotListener = slotValuesListeners.get(listener);

		if (slotListener != null) {

			getSlotValues().removeValuesListener(slotListener);
		}
	}

	/**
	 * Provides the model with which the field is associated.
	 *
	 * @return Model with which field is associated
	 */
	public DModel getModel() {

		return model;
	}

	/**
	 * Specifies whether the field is "editable". If the field is not
	 * editable then any current values will be provided by the model
	 * rather than the client.
	 *
	 * @return True if field is editable
	 */
	public boolean editable() {

		return getSlot().editable();
	}

	/**
	 * Removes all values from the field.
	 */
	public void clear() {

		getSlotValuesEditor().clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasValue(V value) {

		return getSlot().getValues().contains(toSlotValue(value));
	}

	/**
	 * {@inheritDoc}
	 */
	public ISlot getSlot() {

		if (slot == null) {

			throw new Error("Slot has not been set");
		}

		return slot;
	}

	DField(DModel model, DValueType<V> valueType) {

		this.model = model;
		this.valueType = valueType;
	}

	void setSlot(ISlot slot) {

		this.slot = slot;
	}

	boolean add(V value) {

		return getSlotValuesEditor().add(toSlotValue(value));
	}

	List<V> addAll(Collection<? extends V> values) {

		return toFieldValues(getSlotValuesEditor().addAll(toSlotValues(values)));
	}

	boolean remove(V value) {

		return getSlotValuesEditor().remove(toSlotValue(value));
	}

	void remove(int index) {

		getSlotValuesEditor().remove(index);
	}

	List<V> removeAll(Collection<? extends V> values) {

		return toFieldValues(getSlotValuesEditor().removeAll(toSlotValues(values)));
	}

	void update(Collection<? extends V> latestValues) {

		getSlotValuesEditor().update(toSlotValues(latestValues));
	}

	abstract DFieldViewer<V, ?> createViewer();

	CValue<?> getSlotValueType() {

		return valueType.getSlotValueType();
	}

	abstract CCardinality getCardinality();

	List<V> getAll() {

		return toFieldValues(getSlot().getValues().asList());
	}

	private List<IValue> toSlotValues(Collection<? extends V> values) {

		List<IValue> slotValues = new ArrayList<IValue>();

		for (V value : values) {

			slotValues.add(toSlotValue(value));
		}

		return slotValues;
	}

	private IValue toSlotValue(V value) {

		return valueType.toSlotValue(value);
	}

	private List<V> toFieldValues(List<IValue> slotValues) {

		List<V> values = new ArrayList<V>();

		for (IValue slotValue : slotValues) {

			if (convertibleToFieldValue(slotValue)) {

				values.add(toFieldValue(slotValue));
			}
		}

		return values;
	}

	private V toFieldValue(IValue slotValue) {

		return valueType.toFieldValue(slotValue);
	}

	private boolean convertibleToFieldValue(IValue slotValue) {

		return valueType.convertibleToFieldValue(slotValue);
	}

	private ISlotValues getSlotValues() {

		return getSlot().getValues();
	}

	private ISlotValuesEditor getSlotValuesEditor() {

		return model.getISlotValuesEditor(getSlot());
	}
}
