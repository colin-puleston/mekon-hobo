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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * Represents a concept-level model-slot.
 *
 * @author Colin Puleston
 */
public class CSlot implements CIdentified, CSourced, CAnnotatable {

	private CIdentity identity;
	private CFrame container;

	private CSource source = CSource.EXTERNAL;
	private CValue<?> valueType;
	private CCardinality cardinality;
	private CActivation activation = CActivation.ACTIVE;
	private CEditability editability = CEditability.DEFAULT;

	private class Editor implements CSlotEditor {

		public void setSource(CSource source) {

			CSlot.this.setSource(source);
		}

		public void resetLabel(String newLabel) {

			identity = identity.deriveIdentity(newLabel);
		}

		public void absorbCardinality(CCardinality otherCardinality) {

			otherCardinality = checkRestrictCardinality(otherCardinality);
			cardinality = cardinality.getMoreRestrictive(otherCardinality);
		}

		public void absorbValueType(CValue<?> otherValueType) {

			CValue<?> mergedType = valueType.update(otherValueType);

			if (mergedType == null) {

				throw new KModelException(
							"Incompatible value-types for: " + CSlot.this
							+ " (current type = " + valueType
							+ ", supplied type = " + otherValueType + ")");
			}

			valueType = mergedType;
		}

		public void absorbActivation(CActivation otherActivation) {

			activation = activation.getWeakest(otherActivation);
		}

		public void absorbAssertionsEditability(IEditability assertionsEditability) {

			editability = editability.withStrongestAssertionsStatus(assertionsEditability);
		}

		public void absorbQueriesEditability(IEditability queriesEditability) {

			editability = editability.withStrongestQueriesStatus(queriesEditability);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return FEntityDescriber.entityToString(this, this);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayLabel() {

		return identity.getLabel();
	}

	/**
	 * Provides the model with which the slot is associated.
	 *
	 * @return Model with which slot is associated
	 */
	public CModel getModel() {

		return container.getModel();
	}

	/**
	 * Provides the frame to which the slot is attached.
	 *
	 * @return Frame to which slot is attached
	 */
	public CFrame getContainer() {

		return container;
	}

	/**
	 * Provides the identity of the slot.
	 *
	 * @return Identity of slot
	 */
	public CIdentity getIdentity() {

		return identity;
	}

	/**
	 * Provides the source-type(s) of the slot definition.
	 *
	 * @return Source-type(s) of slot definition
	 */
	public CSource getSource() {

		return source;
	}

	/**
	 * Provides the value-type for the slot.
	 *
	 * @return value-type for slot
	 */
	public CValue<?> getValueType() {

		return valueType;
	}

	/**
	 * Provides the cardinality of the slot.
	 *
	 * @return Cardinality of slot
	 */
	public CCardinality getCardinality() {

		return cardinality;
	}

	/**
	 * Specifies the activation of instantiations of this slot.
	 *
	 * @return Activation of instantiations
	 */
	public CActivation getActivation() {

		return activation;
	}

	/**
	 * Specifies the editability of instantiations of this slot.
	 *
	 * @return Editability of instantiations
	 */
	public CEditability getEditability() {

		return editability;
	}

	/**
	 * Provides any annotations on the slot-set of which this slot
	 * is a member, which consists of all slots in the model with
	 * the same identity as this slot.
	 *
	 * @return Annotations on slot-set of which this slot is a member
	 */
	public CAnnotations getAnnotations() {

		return getModel().getSlotAnnotations(identity);
	}

	CSlot(
		CFrame container,
		CIdentity identity,
		CValue<?> valueType,
		CCardinality cardinality) {

		this.container = container;
		this.identity = identity;
		this.valueType = valueType;
		this.cardinality = checkRestrictCardinality(cardinality);
	}

	CSlot copy() {

		CSlot copy = new CSlot(container, identity, valueType, cardinality);

		copy.source = source;
		copy.activation = activation;
		copy.editability = editability;

		return copy;
	}

	CSlotEditor createEditor() {

		return new Editor();
	}

	void setSource(CSource source) {

		this.source = source;
	}

	boolean setValueType(CValue<?> valueType) {

		if (!valueType.equals(this.valueType)) {

			this.valueType = valueType;

			return true;
		}

		return false;
	}

	boolean setCardinality(CCardinality cardinality) {

		cardinality = checkRestrictCardinality(cardinality);

		if (cardinality != this.cardinality) {

			this.cardinality = cardinality;

			return true;
		}

		return false;
	}

	boolean setActivation(CActivation activation) {

		if (activation != this.activation) {

			this.activation = activation;

			return true;
		}

		return false;
	}

	boolean setEditability(CEditability editability) {

		if (editability != this.editability) {

			this.editability = editability;

			return true;
		}

		return false;
	}

	boolean setAllEditability(IEditability status) {

		return setEditability(editability.withAllStatus(status));
	}

	boolean setAssertionsEditability(IEditability status) {

		return setEditability(editability.withAssertionsStatus(status));
	}

	boolean setQueriesEditability(IEditability status) {

		return setEditability(editability.withQueriesStatus(status));
	}

	void remove() {

		container.getSlots().remove(this);
	}

	private CCardinality checkRestrictCardinality(CCardinality cardinality) {

		return valueType.checkRestrictCardinalityForValueType(cardinality);
	}
}
