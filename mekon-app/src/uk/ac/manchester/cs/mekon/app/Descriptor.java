/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.app;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class Descriptor {

	static private final Object NO_VALUE_MATCHER = new String("NO-VALUE");

	static private final String NO_EFFECTIVE_VALUE_DEFAULT_LABEL = " - ";

	private Instantiator instantiator;

	private ISlot slot;
	private CValue<?> valueType;
	private IValue value;

	public boolean equals(Object other) {

		Descriptor d = (Descriptor)other;

		return slot.equals(d.slot)
				&& valueType.equals(d.valueType)
				&& getStateMatcher().equals(d.getStateMatcher());
	}

	public int hashCode() {

		return slot.hashCode() + valueType.hashCode() + getStateMatcher().hashCode();
	}

	Descriptor(Instantiator instantiator, ISlot slot, IValue value) {

		this.instantiator = instantiator;
		this.slot = slot;
		this.value = value;

		valueType = slot.getValueType();
	}

	ISlot getSlot() {

		return slot;
	}

	boolean aspectType() {

		return categoryCFrameType(false);
	}

	boolean instanceRefType() {

		return categoryCFrameType(true);
	}

	boolean valueType(Class<? extends CValue<?>> testType) {

		return testType.isAssignableFrom(valueType.getClass());
	}

	boolean hasValue() {

		return value != null;
	}

	IValue getValue() {

		return value;
	}

	boolean active() {

		if (aspectType()) {

			return anyUserEditability() || anyTerminalValues();
		}

		return editableSlot();
	}

	boolean anyUserEditability() {

		return editableSlot() || anyNestedUserEditability();
	}

	boolean anyEffectiveValues() {

		return anyUserValues() || anyTerminalValues();
	}

	String getIdentityLabel() {

		String label = slot.getType().getIdentity().getLabel();

		if (!slot.getType().getCardinality().singleValue()) {

			label += getIdentityLabelArraySuffix();
		}

		return label;
	}

	String getValueLabel() {

		return anyEffectiveValues() ? getActualValueLabel() : getNoEffectiveValueLabel();
	}

	private boolean categoryCFrameType(boolean aspectRef) {

		if (valueType(CFrame.class)) {

			return instantiator.instanceRefType((CFrame)valueType) == aspectRef;
		}

		return false;
	}

	private String getIdentityLabelArraySuffix() {

		return " [" + (getValueIndex() + 1) + "]";
	}

	private int getValueIndex() {

		ISlotValues values = slot.getValues();

		if (value == null) {

			return values.size();
		}

		return values.asList().indexOf(value);
	}

	private String getNoEffectiveValueLabel() {

		String description = getNoEffectiveValuesDescription();

		if (description.isEmpty()) {

			description = NO_EFFECTIVE_VALUE_DEFAULT_LABEL;
		}

		return "[" + description + "]";
	}

	private String getNoEffectiveValuesDescription() {

		return hasValue() ? getActualValueLabel() : getValueTypeLabel();
	}

	private String getValueTypeLabel() {

		return getCustomiser().getDisplayLabel(valueType);
	}

	private String getActualValueLabel() {

		return getCustomiser().getDisplayLabel(value);
	}

	private boolean anyUserValues() {

		if (!hasValue()) {

			return false;
		}

		return editableSlot() || anyNestedUserValues();
	}

	private boolean anyTerminalValues() {

		return currentTerminalValue() || anyNestedTerminalValues();
	}

	private boolean currentTerminalValue() {

		return hasValue() && ValuesTester.terminalValue(value);
	}

	private boolean anyNestedUserEditability() {

		return hasValue() && ValuesTester.anyNestedUserEditability(value);
	}

	private boolean anyNestedUserValues() {

		return hasValue() && ValuesTester.anyNestedUserValues(value);
	}

	private boolean anyNestedTerminalValues() {

		return hasValue() && ValuesTester.anyNestedTerminalValues(value);
	}

	private boolean editableSlot() {

		return slot.getEditability().editable();
	}

	private Object getStateMatcher() {

		return hasValue() ? value : NO_VALUE_MATCHER;
	}

	private Customiser getCustomiser() {

		return instantiator.getController().getCustomiser();
	}
}
