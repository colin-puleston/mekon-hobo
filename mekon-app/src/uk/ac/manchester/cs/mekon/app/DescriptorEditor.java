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

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class DescriptorEditor {

	private JComponent parent;
	private Instantiator instantiator;

	private Descriptor descriptor;
	private ISlot slot;

	private TypeHandler typeHandler;

	private abstract class TypeHandler {

		boolean performEditAction() {

			ValueObtainer obtainer = getValueObtainer();

			switch (obtainer.getEditStatus()) {

				case INPUTTED:
					addValue(obtainer.getValue());
					break;

				case CLEARED:
					removeValue();
					break;

				case CANCELLED:
					return false;
			}

			return true;
		}

		abstract ValueObtainer getValueObtainer();
	}

	private class CustomTypeHandler extends TypeHandler {

		private ValueObtainerFactory customValueObtainerFactory;

		CustomTypeHandler(ValueObtainerFactory customValueObtainer) {

			this.customValueObtainerFactory = customValueObtainerFactory;
		}

		ValueObtainer getValueObtainer() {

			return customValueObtainerFactory.createFor(slot);
		}
	}

	private abstract class InputTypeHandler<I> extends TypeHandler {

		private class InputValueObtainer implements ValueObtainer {

			private InputDialog<I> inputterDialog;

			public EditStatus getEditStatus() {

				return inputterDialog.display();
			}

			public IValue getValue() {

				return inputToValue(inputterDialog.getInput());
			}

			InputValueObtainer() {

				inputterDialog = createInputDialog(descriptor.hasValue());
			}
		}

		ValueObtainer getValueObtainer() {

			return new InputValueObtainer();
		}

		abstract InputDialog<I> createInputDialog(boolean clearRequired);

		abstract IValue inputToValue(I input);
	}

	private class FixedCFrameTypeHandler extends TypeHandler {

		private CFrame valueType;

		private class FixedValueObtainer implements ValueObtainer {

			public EditStatus getEditStatus() {

				if (descriptor.hasValue()) {

					if (obtainRemoveValueConfirmation()) {

						return EditStatus.CLEARED;
					}

					return EditStatus.CANCELLED;
				}

				return EditStatus.INPUTTED;
			}

			public IValue getValue() {

				return instantiator.instantiate(valueType);
			}
		}

		FixedCFrameTypeHandler(CFrame valueType) {

			this.valueType = valueType;
		}

		ValueObtainer getValueObtainer() {

			return new FixedValueObtainer();
		}

		private boolean obtainRemoveValueConfirmation() {

			return obtainRemoveValueConfirmationOption() == JOptionPane.OK_OPTION;
		}

		private int obtainRemoveValueConfirmationOption() {

			return JOptionPane.showConfirmDialog(
						null,
						"Clear value?",
						"Clearing selected value",
						JOptionPane.OK_CANCEL_OPTION);
		}
	}

	private abstract class InputFrameTypeHandler extends InputTypeHandler<CFrame> {

		private CFrame rootCFrame;

		InputFrameTypeHandler(CFrame rootCFrame) {

			this.rootCFrame = rootCFrame;
		}

		InputDialog<CFrame> createInputDialog(boolean clearRequired) {

			boolean query = instantiator.queryInstance();

			if (abstractEditableSlot()) {

				return new DisjunctionFrameSelector(parent, rootCFrame, query, clearRequired);
			}

			return new AtomicFrameSelector(parent, rootCFrame, query, clearRequired);
		}
	}

	private class InputCFrameTypeHandler extends InputFrameTypeHandler {

		InputCFrameTypeHandler(CFrame valueType) {

			super(valueType);
		}

		IFrame inputToValue(CFrame input) {

			return instantiator.instantiate(input);
		}
	}

	private class MFrameTypeHandler extends InputFrameTypeHandler {

		MFrameTypeHandler(MFrame valueType) {

			super(valueType.getRootCFrame());
		}

		CFrame inputToValue(CFrame input) {

			return input;
		}
	}

	private class InstanceRefTypeHandler extends InputTypeHandler<IFrame> {

		private CFrame valueType;

		InstanceRefTypeHandler(CFrame valueType) {

			this.valueType = valueType;
		}

		InputDialog<IFrame> createInputDialog(boolean clearRequired) {

			return new AtomicInstanceRefSelector(
							parent,
							instantiator,
							valueType,
							clearRequired);
		}

		IFrame inputToValue(IFrame input) {

			return input;
		}
	}

	private abstract class DataTypeHandler<V extends IDataValue> extends InputTypeHandler<V> {

		V inputToValue(V input) {

			return input;
		}
	}

	private class CNumberTypeHandler extends DataTypeHandler<INumber> {

		private CNumber valueType;

		CNumberTypeHandler(CNumber valueType) {

			this.valueType = valueType;
		}

		INumberInputter createInputDialog(boolean clearRequired) {

			if (abstractEditableSlot()) {

				return new IndefiniteINumberInputter(parent, valueType, clearRequired);
			}

			return new DefiniteINumberInputter(parent, valueType, clearRequired);
		}
	}

	private class CStringTypeHandler extends DataTypeHandler<IString> {

		IStringInputter createInputDialog(boolean clearRequired) {

			return new IStringInputter(parent, clearRequired);
		}
	}

	private class StandardTypeHandlerCreator extends CValueVisitor {

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

		StandardTypeHandlerCreator() {

			visit(slot.getValueType());
		}

		private TypeHandler createCFrameTypeHandler(CFrame valueType) {

			if (descriptor.instanceRefType()) {

				return new InstanceRefTypeHandler(valueType);
			}

			if (fixedCFrameValueType(valueType)) {

				return new FixedCFrameTypeHandler(valueType);
			}

			return new InputCFrameTypeHandler(valueType);
		}

		private boolean fixedCFrameValueType(CFrame valueType) {

			return valueType.getSubs(CVisibility.EXPOSED).isEmpty();
		}
	}

	DescriptorEditor(JComponent parent, Instantiator instantiator, Descriptor descriptor) {

		this.parent = parent;
		this.instantiator = instantiator;
		this.descriptor = descriptor;

		slot = descriptor.getSlot();

		checkSetCustomTypeHandler();

		if (typeHandler == null) {

			new StandardTypeHandlerCreator();
		}
	}

	Descriptor getDescriptor() {

		return descriptor;
	}

	boolean performEditAction() {

		return typeHandler.performEditAction();
	}

	private void checkSetCustomTypeHandler() {

		ValueObtainerFactory valuesFactory = getCustomValueObtainerFactory();

		if (valuesFactory.handles(slot)) {

			typeHandler = new CustomTypeHandler(valuesFactory);
		}
	}

	private ValueObtainerFactory getCustomValueObtainerFactory() {

		return getCustomiser().getValueObtainerFactory();
	}

	private void addValue(IValue value) {

		if (descriptor.hasValue()) {

			removeValue();
		}

		getValuesEditor().add(value);
	}

	private void removeValue() {

		getValuesEditor().remove(descriptor.getValue());
	}

	private ISlotValuesEditor getValuesEditor() {

		return slot.getValuesEditor();
	}

	private boolean abstractEditableSlot() {

		return slot.getEditability().abstractEditable();
	}

	private Customiser getCustomiser() {

		return instantiator.getController().getCustomiser();
	}
}
