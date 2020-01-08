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

	private ISlot slot;
	private CValue<?> valueType;
	private IValue currentValue;

	public boolean equals(Object other) {

		Descriptor d = (Descriptor)other;

		return slot.equals(d.slot)
				&& valueType.equals(d.valueType)
				&& getStateMatcher().equals(d.getStateMatcher());
	}

	public int hashCode() {

		return slot.hashCode() + valueType.hashCode() + getStateMatcher().hashCode();
	}

	Descriptor(ISlot slot, IValue currentValue) {

		this.slot = slot;
		this.currentValue = currentValue;

		valueType = slot.getValueType();
	}

	void setNewValue(IValue value) {

		if (isCurrentValue()) {

			removeCurrentValue();
		}

		slot.getValuesEditor().add(value);
	}

	void removeCurrentValue() {

		slot.getValuesEditor().remove(currentValue);
	}

	ISlot getSlot() {

		return slot;
	}

	IValue getCurrentValue() {

		return currentValue;
	}

	boolean anyUserEditability() {

		return editableSlot() || anyNestedUserEditability();
	}

	boolean anyEffectiveValues() {

		return anyUserValues() || anyTerminalValues();
	}

	boolean anyUserValues() {

		if (!isCurrentValue()) {

			return false;
		}

		return editableSlot() || anyNestedUserValues();
	}

	boolean anyTerminalValues() {

		return currentTerminalValue() || anyNestedTerminalValues();
	}

	private boolean currentTerminalValue() {

		return isCurrentValue() && ValuesTester.terminalValue(currentValue);
	}

	private boolean anyNestedUserEditability() {

		return isCurrentValue() && ValuesTester.anyNestedUserEditability(currentValue);
	}

	private boolean anyNestedUserValues() {

		return isCurrentValue() && ValuesTester.anyNestedUserValues(currentValue);
	}

	private boolean anyNestedTerminalValues() {

		return isCurrentValue() && ValuesTester.anyNestedTerminalValues(currentValue);
	}

	private boolean editableSlot() {

		return slot.getEditability().editable();
	}

	private Object getStateMatcher() {

		return isCurrentValue() ? currentValue : NO_VALUE_MATCHER;
	}

	private boolean isCurrentValue() {

		return currentValue != null;
	}
}