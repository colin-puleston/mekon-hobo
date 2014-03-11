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
	private ISlotAttributes attributes;

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

		public void absorbActive(boolean otherActive) {

			attributes.absorbActive(otherActive);
		}

		public void absorbEditable(boolean otherEditable) {

			attributes.absorbEditable(otherEditable);
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
	 * Specifies whether the slot is "active" on the particular frame
	 * to which it is attached. If an instance-level slot is inactive
	 * then it will never have any current values. A slot that is
	 * inactive on a particular frame may be active on one or more
	 * descendant frames, which allows slots that are mapped to fields
	 * in the direct model to effectively only appear on objects that
	 * are mapped to frames at lower levels in the hierarchy.
	 *
	 * @return True if slot is active
	 */
	public boolean active() {

		return attributes.active();
	}

	/**
	 * Specifies whether the slot is "editable" on the particular frame
	 * to which it is attached. If an instance-level slot is not editable
	 * then any current values will be provided by the model rather than
	 * the client.
	 *
	 * @return True if slot is editable
	 */
	public boolean editable() {

		return attributes.editable();
	}

	CSlot(
		CFrame container,
		CProperty property,
		CCardinality cardinality,
		CValue<?> valueType) {

		this.container = container;
		this.property = property;
		this.cardinality = cardinality;

		attributes = new ISlotAttributes(valueType);
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

	ISlotAttributes getAttributes() {

		return attributes;
	}
}
