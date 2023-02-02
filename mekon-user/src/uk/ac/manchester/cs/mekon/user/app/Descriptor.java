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

package uk.ac.manchester.cs.mekon.user.app;

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
	private IValue value = null;
	private boolean hasInferredValue = false;

	public boolean equals(Object other) {

		Descriptor d = (Descriptor)other;

		return slot.equals(d.slot)
				&& valueType.equals(d.valueType)
				&& getStateMatcher().equals(d.getStateMatcher());
	}

	public int hashCode() {

		return slot.hashCode() + valueType.hashCode() + getStateMatcher().hashCode();
	}

	Descriptor(Instantiator instantiator, ISlot slot) {

		this.instantiator = instantiator;
		this.slot = slot;

		valueType = slot.getValueType();
	}

	Descriptor(Instantiator instantiator, ISlot slot, IValue value) {

		this(instantiator, slot);

		this.value = value;

		hasInferredValue = slot.getValues().getFixedValues().contains(value);
	}

	ISlot getSlot() {

		return slot;
	}

	boolean instanceGroupLink() {

		return instantiator.instanceGroupLinkSlot(slot);
	}

	boolean hasValueType(Class<? extends CValue<?>> testType) {

		return testType.isAssignableFrom(valueType.getClass());
	}

	boolean hasValue() {

		return value != null;
	}

	boolean hasStructuredValue() {

		IFrame frame = checkForIFrameValue(IFrameCategory.ATOMIC);

		return frame != null && !frame.getSlots().isEmpty();
	}

	boolean hasInstanceRefValue() {

		return checkForIFrameValue(IFrameCategory.REFERENCE) != null;
	}

	boolean hasURLValue() {

		return valueType == CString.URL_VALUE;
	}

	IValue getValue() {

		return value;
	}

	boolean active() {

		if (hasStructuredValue()) {

			return anyUserEditability() || anyTerminalValues();
		}

		return userEditable();
	}

	boolean userEditable() {

		return exposedSlot() && editableSlot() && !hasInferredValue;
	}

	boolean anyUserEditability() {

		return userEditable() || anyNestedUserEditability();
	}

	boolean anyEffectiveValues() {

		return anyUserValues() || anyTerminalValues();
	}

	String getIdentityLabel(boolean arrayIndicesOnly) {

		if (arrayIndicesOnly && multiValuedSlot()) {

			if (slot.getValues().isEmpty()) {

				return DescriptorLabels.forArrayHeader(slot);
			}

			return DescriptorLabels.forArrayElement(getValueIndex() + 1);
		}

		return DescriptorLabels.forSimple(slot);
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

		return Collections.singletonList(getTypeLabel(valueType));
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

			labels.add(getTypeLabel(disjunct));
		}

		return labels;
	}

	private String getTypeLabel(CValue<?> type) {

		return getCustomiser().getTypeDisplayLabel(type);
	}

	private String getAtomicValueLabel(IValue atomicValue) {

		return getCustomiser().getValueDisplayLabel(atomicValue);
	}

	private IFrame checkForIFrameValue(IFrameCategory category) {

		if (value instanceof IFrame) {

			IFrame frame = (IFrame)value;

			if (frame.getCategory() == category) {

				return frame;
			}
		}

		return null;
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

	private boolean exposedSlot() {

		return slot.getType().getActivation().activeExposed();
	}

	private boolean editableSlot() {

		return slot.getEditability().editable();
	}

	private boolean multiValuedSlot() {

		return !slot.getType().getCardinality().singleValue();
	}

	private Customiser getCustomiser() {

		return instantiator.getCustomiser();
	}
}
