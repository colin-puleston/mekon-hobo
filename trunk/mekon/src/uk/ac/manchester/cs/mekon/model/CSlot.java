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

import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * Represents a concept-level model-slot.
 *
 * @author Colin Puleston
 */
public class CSlot implements CSourced {

	private CFrame container;
	private CProperty property;
	private CSource source = CSource.INDIRECT;
	private CCardinality cardinality;
	private CAnnotations annotations = new CAnnotations(this);
	private FSlotAttributes attributes;

	private class Editor implements CSlotEditor {

		public void setSource(CSource source) {

			CSlot.this.source = source;
		}

		public void absorbCardinality(CCardinality otherCardinality) {

			cardinality = cardinality.getMoreRestrictive(otherCardinality);
		}

		public void absorbValueType(CValue<?> otherValueType) {

			attributes.absorbValueType(CSlot.this, otherValueType);
		}

		public void absorbActive(boolean value) {

			attributes.absorbActive(value);
		}

		public void absorbDependent(boolean value) {

			attributes.absorbDependent(value);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return FEntityDescriber.entityToString(this, property);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayLabel() {

		return property.getDisplayLabel();
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
	 */
	public CProperty getProperty() {

		return property;
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

		return attributes.getValueType();
	}

	/**
	 * Specifies the default "active" status for any instantiations
	 * of this slot (see {@link ISlot#active}).
	 *
	 * @return True if instantiations of slot will by default by active
	 */
	public boolean active() {

		return attributes.active();
	}

	/**
	 * Specifies the default "dependent" status for any instantiations
	 * of this slot (see {@link ISlot#active}).
	 *
	 * @return True if instantiations of slot will by default be dependent
	 */
	public boolean dependent() {

		return attributes.dependent();
	}

	CSlot(
		CFrame container,
		CProperty property,
		CCardinality cardinality,
		CValue<?> valueType) {

		this.container = container;
		this.property = property;
		this.cardinality = cardinality;

		attributes = new FSlotAttributes(valueType);
	}

	CSlotEditor createEditor() {

		return new Editor();
	}

	void setSource(CSource source) {

		this.source = source;
	}

	void remove() {

		container.getSlots().remove(this);
	}

	FSlotAttributes getAttributes() {

		return attributes;
	}
}
