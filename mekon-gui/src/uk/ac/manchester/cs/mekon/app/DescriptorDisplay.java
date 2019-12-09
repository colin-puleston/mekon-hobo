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
class DescriptorDisplay {

	private AspectWindow aspectWindow;

	private Window rootWindow;
	private Instantiator instantiator;

	private ISlot slot;
	private IValue currentValue;

	private TypeHandler typeHandler;

	private abstract class TypeHandler {

		private CValue<?> valueType;

		TypeHandler(CValue<?> valueType) {

			this.valueType = valueType;
		}

		String getIdentityLabel() {

			return getSlotLabel(slot);
		}

		String getValueLabel() {

			return anyEffectiveValues()
					? getCurrentValueLabel()
					: getNoEffectiveValueLabel();
		}

		String getIneffectiveValueLabel() {

			return getValueTypeLabel();
		}

		abstract void checkEdit();

		private boolean anyEffectiveValues() {

			return anyUserValues() || anyTerminalValues();
		}

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

		private AspectEditManager editManager = null;

		CFrameTypeHandler(CFrame valueType) {

			super(valueType);

			if (currentValue != null) {

				editManager = createEditManager((IFrame)currentValue);
			}
		}

		void checkEdit() {

			if (editManager == null || !editManager.checkInvokeEdit()) {

				IFrame aspect = getNewAspectOrNull();

				if (aspect != null) {

					addValue(aspect);

					editManager = createEditManager(aspect);
					editManager.checkInvokeEdit();

					return;
				}
			}

			editManager = null;
		}

		abstract IFrame getNewAspectOrNull();

		private AspectEditManager createEditManager(IFrame aspect) {

			return new AspectEditManager(aspectWindow, slot, aspect);
		}
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

			if (selector.display(false) == EditStatus.EDITED) {

				return instantiator.instantiate(selector.getSelection());
			}

			return null;
		}

		private FrameSelector createSelector() {

			return new FrameSelector(rootWindow, valueType, false);
		}
	}

	private abstract class SimpleTypeHandler<S> extends TypeHandler {

		SimpleTypeHandler(CValue<?> valueType) {

			super(valueType);
		}

		void checkEdit() {

			Selector<S> selector = createValueSelector();

			switch (selector.display(isCurrentValue())) {

				case EDITED:
					addSelectedValue(selector.getSelection());
					break;

				case CLEARED:
					removeCurrentValue();
					break;
			}
		}

		abstract Selector<S> createValueSelector();

		abstract IValue selectionToValue(S selection);

		private void addSelectedValue(S selection) {

			addValue(selectionToValue(selection));
		}
	}

	private class MFrameTypeHandler extends SimpleTypeHandler<CFrame> {

		private CFrame rootCFrame;

		MFrameTypeHandler(MFrame valueType) {

			super(valueType);

			rootCFrame = valueType.getRootCFrame();
		}

		FrameSelector createValueSelector() {

			return new FrameSelector(rootWindow, rootCFrame, abstractEdit());
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

		InstanceRefSelector createValueSelector() {

			return new InstanceRefSelector(aspectWindow, valueType, abstractEdit());
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

		INumberSelector createValueSelector() {

			if (abstractEdit()) {

				return new IndefiniteINumberSelector(rootWindow, valueType);
			}

			return new DefiniteINumberSelector(rootWindow, valueType);
		}
	}

	private class CStringTypeHandler extends DataTypeHandler<IString> {

		CStringTypeHandler() {

			super(CString.SINGLETON);
		}

		IStringSelector createValueSelector() {

			return new IStringSelector(rootWindow);
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

	private class IdentityCell extends ActiveTableCell {

		String getLabel() {

			String label = typeHandler.getIdentityLabel();

			if (multiValueSlot()) {

				label += getArraySuffix();
			}

			return label;
		}

		Color getForeground() {

			return IDENTITY_COLOUR;
		}

		int getFontStyle() {

			return IDENTITY_FONT_STYLE;
		}

		private String getArraySuffix() {

			return " [" + (getValueIndex() + 1) + "]";
		}

		private int getValueIndex() {

			ISlotValues values = slot.getValues();

			if (currentValue == null) {

				return values.size();
			}

			return values.asList().indexOf(currentValue);
		}
	}

	private class ValueCell extends ActiveTableCell {

		private DisplayColours colours = new DisplayColours();
		private DisplayFontStyles fontStyles = new DisplayFontStyles();

		private abstract class DisplayOptions<O> {

			O getOption() {

				if (anyUserEditability()) {

					return anyUserValues() ? getUserValuesOption() : getNoUserValuesOption();
				}

				return anyTerminalValues() ? getAutoValuesOption() : getNoAutoValuesOption();
			}

			abstract O getUserValuesOption();

			abstract O getAutoValuesOption();

			abstract O getNoUserValuesOption();

			abstract O getNoAutoValuesOption();
		}

		private class DisplayColours extends DisplayOptions<Color> {

			Color getUserValuesOption() {

				return USER_VALUE_COLOUR;
			}

			Color getAutoValuesOption() {

				return AUTO_VALUE_COLOUR;
			}

			Color getNoUserValuesOption() {

				return NO_USER_VALUE_COLOUR;
			}

			Color getNoAutoValuesOption() {

				return NO_AUTO_VALUE_COLOUR;
			}
		}

		private class DisplayFontStyles extends DisplayOptions<Integer> {

			Integer getUserValuesOption() {

				return USER_VALUE_FONT_STYLE;
			}

			Integer getAutoValuesOption() {

				return AUTO_VALUE_FONT_STYLE;
			}

			Integer getNoUserValuesOption() {

				return NO_USER_VALUE_FONT_STYLE;
			}

			Integer getNoAutoValuesOption() {

				return NO_AUTO_VALUE_FONT_STYLE;
			}
		}

		String getLabel() {

			return typeHandler.getValueLabel();
		}

		Color getForeground() {

			return colours.getOption();
		}

		Color getBackground() {

			return anyUserEditability()
					? DEFAULT_BACKGROUND_COLOUR
					: AUTO_EDIT_BACKGROUND_COLOUR;
		}

		int getFontStyle() {

			return fontStyles.getOption();
		}

		boolean userActionable() {

			return anyUserEditability();
		}

		void performCellAction() {

			aspectWindow.dispose();
			typeHandler.checkEdit();
			aspectWindow.displayCopy();
		}
	}

	DescriptorDisplay(AspectWindow aspectWindow, Descriptor descriptor) {

		this.aspectWindow = aspectWindow;

		rootWindow = aspectWindow.getRootWindow();
		instantiator = aspectWindow.getInstantiator();

		slot = descriptor.getSlot();
		currentValue = descriptor.getCurrentValue();

		new TypeHandlerCreator();
	}

	void checkEdit() {

		typeHandler.checkEdit();
	}

	ActiveTableCell createIdentityCell() {

		return new IdentityCell();
	}

	ActiveTableCell createValueCell() {

		return new ValueCell();
	}

	ISlot getSlot() {

		return slot;
	}

	IValue getCurrentValue() {

		return currentValue;
	}

	private void addValue(IValue value) {

		slot.getValuesEditor().add(value);
	}

	private void removeCurrentValue() {

		slot.getValuesEditor().remove(currentValue);
	}

	private boolean anyUserEditability() {

		if (editableSlot()) {

			return true;
		}

		return isCurrentValue() && ValuesTester.anyNestedUserEditability(currentValue);
	}

	private boolean anyUserValues() {

		if (!isCurrentValue()) {

			return false;
		}

		if (editableSlot()) {

			return true;
		}

		return ValuesTester.anyNestedUserValues(currentValue);
	}

	private boolean anyTerminalValues() {

		if (!isCurrentValue()) {

			return false;
		}

		if (ValuesTester.terminalValue(currentValue)) {

			return true;
		}

		return ValuesTester.anyNestedTerminalValues(currentValue);
	}

	private boolean editableSlot() {

		return slot.getEditability().editable();
	}

	private boolean abstractEdit() {

		return slot.getEditability().abstractEditable();
	}

	private boolean multiValueSlot() {

		return !slot.getType().getCardinality().singleValue();
	}

	private boolean isCurrentValue() {

		return currentValue != null;
	}

	private String getSlotLabel(ISlot slot) {

		return slot.getType().getIdentity().getLabel();
	}

	private Customiser getCustomiser() {

		return getController().getCustomiser();
	}

	private Controller getController() {

		return instantiator.getController();
	}

	private RuntimeException createException(String message) {

		return new RuntimeException(
					"Descriptor error: " + slot
					+ ": " + message);
	}
}
