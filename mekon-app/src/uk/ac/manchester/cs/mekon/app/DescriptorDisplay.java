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

import java.awt.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
abstract class DescriptorDisplay {

	private AspectWindow aspectWindow;

	private Window rootWindow;
	private Instantiator instantiator;

	private Descriptor descriptor;

	private ISlot slot;
	private IValue currentValue;

	private TypeHandler typeHandler;

	private abstract class TypeHandler {

		private CValue<?> valueType;

		TypeHandler(CValue<?> valueType) {

			this.valueType = valueType;
		}

		String getIdentityLabel() {

			return slot.getType().getIdentity().getLabel();
		}

		String getValueLabel() {

			return anyEffectiveValues()
					? getCurrentValueLabel()
					: getNoEffectiveValueLabel();
		}

		abstract boolean active();

		abstract void performAction();

		private String getNoEffectiveValueLabel() {

			return "[" + getNoEffectiveValuesDescription() + "]";
		}

		private String getNoEffectiveValuesDescription() {

			return isCurrentValue() ? getCurrentValueLabel() : getValueTypeLabel();
		}

		private String getValueTypeLabel() {

			return getCustomiser().getDisplayLabel(valueType);
		}

		private String getCurrentValueLabel() {

			return getCustomiser().getDisplayLabel(currentValue);
		}
	}

	private abstract class CFrameTypeHandler extends TypeHandler {

		CFrameTypeHandler(CFrame valueType) {

			super(valueType);
		}

		boolean active() {

			return anyUserEditability() || anyTerminalValues();
		}

		void performAction() {

			IFrame aspect = null;
			DescriptorsList descriptors = null;

			if (currentValue != null) {

				aspect = (IFrame)currentValue;
				descriptors = new DescriptorsList(instantiator, aspect);
			}

			if (aspect == null || descriptors.isEmpty()) {

				aspect = getNewAspectOrNull();

				if (aspect == null) {

					return;
				}

				descriptor.setNewValue(aspect);
				descriptors = new DescriptorsList(instantiator, aspect);
			}

			onAspectActionPerformed(aspect, descriptors);
		}

		abstract IFrame getNewAspectOrNull();
	}

	private class FixedCFrameTypeHandler extends CFrameTypeHandler {

		private CFrame valueType;

		FixedCFrameTypeHandler(CFrame valueType) {

			super(valueType);

			this.valueType = valueType;
		}

		IFrame getNewAspectOrNull() {

			return instantiator.instantiate(valueType);
		}
	}

	private class SelectableCFrameTypeHandler extends CFrameTypeHandler {

		private CFrame valueType;

		SelectableCFrameTypeHandler(CFrame valueType) {

			super(valueType);

			this.valueType = valueType;
		}

		IFrame getNewAspectOrNull() {

			FrameSelector selector = createSelector();

			if (selector.display() == EditStatus.EDITED) {

				return instantiator.instantiate(selector.getSelection());
			}

			return null;
		}

		private FrameSelector createSelector() {

			return new FrameSelector(rootWindow, valueType, false, false);
		}
	}

	private abstract class SimpleTypeHandler<S> extends TypeHandler {

		SimpleTypeHandler(CValue<?> valueType) {

			super(valueType);
		}

		boolean active() {

			return editableSlot();
		}

		void performAction() {

			Selector<S> selector = createValueSelector(isCurrentValue());

			switch (selector.display()) {

				case EDITED:
					addSelectedValue(selector.getSelection());
					break;

				case CLEARED:
					descriptor.removeCurrentValue();
					break;
			}
		}

		abstract Selector<S> createValueSelector(boolean clearRequired);

		abstract IValue selectionToValue(S selection);

		private void addSelectedValue(S selection) {

			descriptor.setNewValue(selectionToValue(selection));
		}
	}

	private class MFrameTypeHandler extends SimpleTypeHandler<CFrame> {

		private CFrame rootCFrame;

		MFrameTypeHandler(MFrame valueType) {

			super(valueType);

			rootCFrame = valueType.getRootCFrame();
		}

		FrameSelector createValueSelector(boolean clearRequired) {

			boolean multiSelect = abstractEditableSlot();

			return new FrameSelector(rootWindow, rootCFrame, multiSelect, clearRequired);
		}

		CFrame selectionToValue(CFrame selection) {

			return selection;
		}
	}

	private class InstanceRefTypeHandler extends SimpleTypeHandler<IFrame> {

		private CFrame valueType;

		InstanceRefTypeHandler(CFrame valueType) {

			super(valueType);

			this.valueType = valueType;
		}

		InstanceRefSelector createValueSelector(boolean clearRequired) {

			boolean multiSelect = abstractEditableSlot();

			return new InstanceRefSelector(aspectWindow, valueType, multiSelect, clearRequired);
		}

		IFrame selectionToValue(IFrame selection) {

			return selection;
		}
	}

	private abstract class DataTypeHandler<V extends IValue> extends SimpleTypeHandler<V> {

		DataTypeHandler(CValue<?> valueType) {

			super(valueType);
		}

		V selectionToValue(V selection) {

			return selection;
		}
	}

	private class CNumberTypeHandler extends DataTypeHandler<INumber> {

		private CNumber valueType;

		CNumberTypeHandler(CNumber valueType) {

			super(valueType);

			this.valueType = valueType;
		}

		INumberSelector createValueSelector(boolean clearRequired) {

			if (abstractEditableSlot()) {

				return new IndefiniteINumberSelector(rootWindow, valueType, clearRequired);
			}

			return new DefiniteINumberSelector(rootWindow, valueType, clearRequired);
		}
	}

	private class CStringTypeHandler extends DataTypeHandler<IString> {

		CStringTypeHandler() {

			super(CString.SINGLETON);
		}

		IStringSelector createValueSelector(boolean clearRequired) {

			return new IStringSelector(rootWindow, clearRequired);
		}
	}

	private class TypeHandlerCreator extends CValueVisitor {

		protected void visit(CFrame value) {

			typeHandler = createCFrameTypeHandler(value);
		}

		protected void visit(CNumber value) {

			typeHandler = new CNumberTypeHandler(value);
		}

		protected void visit(CString value) {

			typeHandler = new CStringTypeHandler();
		}

		protected void visit(MFrame value) {

			typeHandler = new MFrameTypeHandler(value);
		}

		TypeHandlerCreator() {

			visit(slot.getValueType());
		}

		private TypeHandler createCFrameTypeHandler(CFrame valueType) {

			if (instanceRefValueType(valueType)) {

				return new InstanceRefTypeHandler(valueType);
			}

			if (fixedFrameValueType(valueType)) {

				return new FixedCFrameTypeHandler(valueType);
			}

			return new SelectableCFrameTypeHandler(valueType);
		}

		private boolean instanceRefValueType(CFrame valueType) {

			return !instantiator.queryInstance() && getController().instanceType(valueType);
		}

		private boolean fixedFrameValueType(CFrame valueType) {

			return valueType.getSubs(CVisibility.EXPOSED).isEmpty();
		}
	}

	DescriptorDisplay(AspectWindow aspectWindow, Descriptor descriptor) {

		this.aspectWindow = aspectWindow;
		this.descriptor = descriptor;

		rootWindow = aspectWindow.getRootWindow();
		instantiator = aspectWindow.getInstantiator();

		slot = descriptor.getSlot();
		currentValue = descriptor.getCurrentValue();

		new TypeHandlerCreator();
	}

	ISlot getSlot() {

		return slot;
	}

	boolean active() {

		return typeHandler.active();
	}

	IValue getCurrentValue() {

		return currentValue;
	}

	String getIdentityLabel() {

		String label = typeHandler.getIdentityLabel();

		if (descriptor.multiValueSlot()) {

			label += getIdentityLabelArraySuffix();
		}

		return label;
	}

	String getValueLabel() {

		return typeHandler.getValueLabel();
	}

	void performAction() {

		typeHandler.performAction();
	}

	abstract void onAspectActionPerformed(IFrame aspect, DescriptorsList descriptors);

	private String getIdentityLabelArraySuffix() {

		return " [" + (getValueIndex() + 1) + "]";
	}

	private int getValueIndex() {

		ISlotValues values = slot.getValues();

		if (currentValue == null) {

			return values.size();
		}

		return values.asList().indexOf(currentValue);
	}

	private boolean anyUserEditability() {

		return descriptor.anyUserEditability();
	}

	private boolean anyEffectiveValues() {

		return descriptor.anyEffectiveValues();
	}

	private boolean anyUserValues() {

		return descriptor.anyUserValues();
	}

	private boolean anyTerminalValues() {

		return descriptor.anyTerminalValues();
	}

	private boolean isCurrentValue() {

		return currentValue != null;
	}

	private boolean editableSlot() {

		return slot.getEditability().editable();
	}

	private boolean abstractEditableSlot() {

		return slot.getEditability().abstractEditable();
	}

	private Customiser getCustomiser() {

		return getController().getCustomiser();
	}

	private Controller getController() {

		return instantiator.getController();
	}
}

