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
import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * Represents an instance-level model-slot.
 *
 * @author Colin Puleston
 */
public class ISlot implements IEntity {

	private CSlot type;
	private IFrame container;
	private ISlotValues values = new ISlotValues(this);
	private List<ISlotListener> listeners = new ArrayList<ISlotListener>();

	private class Editor implements ISlotEditor {

		public boolean setValueType(CValue<?> valueType) {

			if (type.setValueType(valueType)) {

				values.removeInvalidValues();
				pollListenersForUpdatedValueType();

				return true;
			}

			return false;
		}

		public boolean setCardinality(CCardinality cardinality) {

			if (type.setCardinality(cardinality)) {

				pollListenersForUpdatedCardinality();

				return true;
			}

			return false;
		}

		public boolean setActivation(CActivation activation) {

			if (type.setActivation(activation)) {

				pollListenersForUpdatedActiveStatus();

				return true;
			}

			return false;
		}

		public boolean setEditability(CEditability editability) {

			if (type.setEditability(editability)) {

				pollListenersForUpdatedEditability();

				return true;
			}

			return false;
		}

		public boolean setAssertedValues(List<IValue> assertedValues) {

			return values.update(assertedValues, true);
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

		return type.getValueType();
	}

	/**
	 * Specifies the editability status for the slot.
	 * <p>
	 * NOTE: If the slot is a special "disjuncts-slots" attached to
	 * a disjunction-frame (see {@link IFrame} for details), then
	 * editability will always be {@link IEditability#CONCRETE_ONLY})
	 * on both assertions and queries, despite the editability status
	 * of the slot-type being {@link CEditability#DEFAULT}, which will
	 * always be the case for disjuncts-slots.
	 *
	 * @return Editability status for slot
	 */
	public IEditability getEditability() {

		return type.getEditability().forInstances(querySlot());
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

		checkExternalValuesEditorAccess(
			type.getActivation().active(),
			"inactive");

		checkExternalValuesEditorAccess(
			getEditability().editable(),
			"non-editable");

		return new ISlotValuesEditor(values, false);
	}

	ISlot(CSlot type, IFrame container) {

		this.type = type;
		this.container = container;
	}

	ISlotEditor createEditor() {

		return new Editor();
	}

	ISlotValuesEditor getValuesEditorInternal() {

		return new ISlotValuesEditor(values, true);
	}

	private boolean querySlot() {

		return container.getFunction().query();
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

	private void pollListenersForUpdatedCardinality() {

		for (ISlotListener listener : copyListeners()) {

			listener.onUpdatedCardinality(type.getCardinality());
		}
	}

	private void pollListenersForUpdatedActiveStatus() {

		for (ISlotListener listener : copyListeners()) {

			listener.onUpdatedActivation(type.getActivation());
		}
	}

	private void pollListenersForUpdatedEditability() {

		for (ISlotListener listener : copyListeners()) {

			listener.onUpdatedEditability(type.getEditability());
		}
	}

	private List<ISlotListener> copyListeners() {

		return new ArrayList<ISlotListener>(listeners);
	}
}
