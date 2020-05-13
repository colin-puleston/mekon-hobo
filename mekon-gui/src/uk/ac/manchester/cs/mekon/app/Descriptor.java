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

import java.util.*;

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
	private boolean inferredValue;

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
		inferredValue = inferredValue();
	}

	ISlot getSlot() {

		return slot;
	}

	boolean structuredType() {

		return hasValueType(CFrame.class) && !instanceRefType();
	}

	boolean instanceRefType() {

		return instantiator.instanceRefValuedSlot(slot);
	}

	boolean hasValueType(Class<? extends CValue<?>> testType) {

		return testType.isAssignableFrom(valueType.getClass());
	}

	boolean hasValue() {

		return value != null;
	}

	IValue getValue() {

		return value;
	}

	boolean active() {

		if (structuredType()) {

			return anyUserEditability() || anyTerminalValues();
		}

		return userEditable();
	}

	boolean userEditable() {

		return editableSlot() && !inferredValue;
	}

	boolean anyUserEditability() {

		return userEditable() || anyNestedUserEditability();
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

	List<String> getValueDisjunctLabels() {

		if (hasValue()) {

			if (value instanceof CFrame) {

				return getFrameValueDisjunctLabels((CFrame)value);
			}

			if (value instanceof IFrame) {

				IFrame frame = (IFrame)value;

				if (!frame.getCategory().reference()) {

					CFrame frameType = frame.getType();

					if (frameType.asDisjuncts().size() > 1) {

						return getFrameValueDisjunctLabels(frameType);
					}
				}
			}

			return Collections.singletonList(getAtomicValueLabel(value));
		}

		return Collections.singletonList(getValueTypeLabel());
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

	private List<String> getFrameValueDisjunctLabels(CFrame frame) {

		List<String> labels = new ArrayList<String>();

		for (CFrame disjunct : frame.asDisjuncts()) {

			labels.add(getAtomicValueLabel(disjunct));
		}

		return labels;
	}

	private String getValueTypeLabel() {

		return getCustomiser().getTypeDisplayLabel(valueType);
	}

	private String getAtomicValueLabel(IValue atomicValue) {

		return getCustomiser().getValueDisplayLabel(atomicValue);
	}

	private boolean inferredValue() {

		if (!hasValue()) {

			return false;
		}

		return reasonerProvidedValue() || appProvidedValue();
	}

	private boolean reasonerProvidedValue() {

		return slot.getValues().getFixedValues().contains(value);
	}

	private boolean appProvidedValue() {

		return new AutoValueProvider(instantiator, slot).canProvide();
	}

	private boolean anyUserValues() {

		if (!hasValue()) {

			return false;
		}

		return userEditable() || anyNestedUserValues();
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

	private Object getStateMatcher() {

		return hasValue() ? value : NO_VALUE_MATCHER;
	}

	private boolean editableSlot() {

		return slot.getEditability().editable();
	}

	private Customiser getCustomiser() {

		return getController().getCustomiser();
	}

	private Controller getController() {

		return instantiator.getController();
	}
}
