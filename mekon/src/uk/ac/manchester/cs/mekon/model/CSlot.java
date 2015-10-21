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
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * Represents a concept-level model-slot.
 *
 * @author Colin Puleston
 */
public class CSlot implements CIdentified, CSourced {

	private CIdentity identity;
	private CFrame container;

	private CSource source = CSource.EXTERNAL;
	private CCardinality cardinality;
	private CValue<?> valueType;
	private boolean active = true;
	private CEditability editability = CEditability.DEFAULT;

	private CAnnotations annotations = new CAnnotations(this);

	private class Editor implements CSlotEditor {

		public void setSource(CSource source) {

			CSlot.this.setSource(source);
		}

		public void resetLabel(String newLabel) {

			identity = identity.deriveIdentity(newLabel);
		}

		public void absorbCardinality(CCardinality otherCardinality) {

			cardinality = cardinality.getMoreRestrictive(otherCardinality);
		}

		public void absorbValueType(CValue<?> otherValueType) {

			CValue<?> mergedType = valueType.mergeWith(otherValueType);

			if (mergedType == null) {

				throw new KModelException(
							"Incompatible value-types for: " + CSlot.this
							+ " (current type = " + valueType
							+ ", supplied type = " + otherValueType + ")");
			}

			valueType = mergedType;
		}

		public void absorbActive(boolean otherActive) {

			active &= otherActive;
		}

		public void absorbEditability(CEditability otherEditability) {

			editability = editability.getStrongest(otherEditability);
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
	 * @throws KAccessException if this is the root-slot
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
	 * Provides the cardinality of the slot.
	 *
	 * @return Cardinality of slot
	 */
	public CCardinality getCardinality() {

		return cardinality;
	}

	/**
	 * Provides any annotations on the slot.
	 *
	 * @return Annotations on slot
	 */
	public CAnnotations getAnnotations() {

		return annotations;
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
	 * Specifies whether instantiations of this slot will be "active"
	 * on the particular frames to which they are attached. If a slot
	 * is inactive then it will never have any current values.
	 *
	 * @return True if instantiations of slot will be active
	 */
	public boolean active() {

		return active;
	}

	/**
	 * Specifies the editability status for instantiations of this slot.
	 *
	 * @return Editability status for instantiations
	 */
	public CEditability getEditability() {

		return editability;
	}

	CSlot(
		CFrame container,
		CIdentity identity,
		CCardinality cardinality,
		CValue<?> valueType) {

		this.container = container;
		this.identity = identity;
		this.cardinality = cardinality;
		this.valueType = valueType;
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

	boolean setActive(boolean active) {

		if (active != this.active) {

			this.active = active;

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

	void remove() {

		container.getSlots().remove(this);
	}
}
