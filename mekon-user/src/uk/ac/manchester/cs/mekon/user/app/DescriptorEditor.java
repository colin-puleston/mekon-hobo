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

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

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

			return checkDisplayValueStructureDialog() || performValueEdit();
		}

		boolean checkDisplayValueStructureDialog() {

			return false;
		}

		abstract ValueObtainer getValueObtainer();

		boolean handleCancelledEdit() {

			return false;
		}

		private boolean performValueEdit() {

			ValueObtainer obtainer = getValueObtainer();

			switch (obtainer.getEditStatus()) {

				case INPUTTED:
					addValue(obtainer.getValue());
					break;

				case CLEARED:
					removeValue();
					break;

				case CANCELLED:
					return handleCancelledEdit();
			}

			return true;
		}
	}

	private class CustomTypeHandler extends TypeHandler {

		private ValueObtainerFactory customValueObtainerFactory;

		CustomTypeHandler(ValueObtainerFactory customValueObtainerFactory) {

			this.customValueObtainerFactory = customValueObtainerFactory;
		}

		ValueObtainer getValueObtainer() {

			return customValueObtainerFactory.createFor(parent, slot);
		}
	}

	private abstract class InputTypeHandler<I> extends TypeHandler {

		private class InputValueObtainer implements ValueObtainer {

			private Inputter<I> inputter;

			public EditStatus getEditStatus() {

				return inputter.display();
			}

			public IValue getValue() {

				return inputToValue(inputter.getInput());
			}

			InputValueObtainer() {

				inputter = createInputter(descriptor.hasValue());
			}
		}

		ValueObtainer getValueObtainer() {

			return new InputValueObtainer();
		}

		abstract Inputter<I> createInputter(boolean canClear);

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

		boolean checkDisplayValueStructureDialog() {

			return checkDisplayInstanceSubSectionDialog();
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

	private abstract class SelectableFrameTypeHandler extends InputTypeHandler<CFrame> {

		private CFrame rootCFrame;

		SelectableFrameTypeHandler(CFrame rootCFrame) {

			this.rootCFrame = rootCFrame;
		}

		Inputter<CFrame> createInputter(boolean canClear) {

			boolean query = instantiator.queryInstance();

			if (abstractEditableSlot()) {

				return new DisjunctionFrameSelector(parent, rootCFrame, query, canClear);
			}

			return new AtomicFrameSelector(parent, rootCFrame, query, canClear);
		}
	}

	private class SelectableCFrameTypeHandler extends SelectableFrameTypeHandler {

		SelectableCFrameTypeHandler(CFrame valueType) {

			super(valueType);
		}

		boolean checkDisplayValueStructureDialog() {

			return checkDisplayInstanceSubSectionDialog();
		}

		IFrame inputToValue(CFrame input) {

			return instantiator.instantiate(input);
		}
	}

	private class MFrameTypeHandler extends SelectableFrameTypeHandler {

		MFrameTypeHandler(MFrame valueType) {

			super(valueType.getRootCFrame());
		}

		CFrame inputToValue(CFrame input) {

			return input;
		}
	}

	private class InstanceGroupLinkCFrameTypeHandler extends InputTypeHandler<IFrame> {

		private CFrame valueType;
		private InstanceRefSelector refSelector = null;

		InstanceGroupLinkCFrameTypeHandler(CFrame valueType) {

			this.valueType = valueType;
		}

		Inputter<IFrame> createInputter(boolean canClear) {

			refSelector = new InstanceRefSelector(
									parent,
									instantiator,
									valueType,
									canClear,
									abstractEditableSlot());

			return refSelector;
		}

		IFrame inputToValue(IFrame input) {

			return input;
		}

		boolean handleCancelledEdit() {

			if (refSelector.alternativeEditSelected()) {

				if (abstractEditableSlot()) {

					return performAlternativeAbstractEdit();
				}

				return checkCreateAndAddRefInstance();
			}

			return false;
		}

		private boolean performAlternativeAbstractEdit() {

			if (fixedValueType(valueType)) {

				addValue(instantiator.instantiate(valueType));

				return true;
			}

			return new SelectableCFrameTypeHandler(valueType).performEditAction();
		}

		private boolean checkCreateAndAddRefInstance() {

			CIdentity refId = checkCreateRefInstance();

			if (refId != null) {

				addValue(instantiator.instantiateRef(valueType, refId));

				return true;
			}

			return false;
		}

		private CIdentity checkCreateRefInstance() {

			return createInstanceOps().checkCreate(valueType, instantiator.getStoreId());
		}

		private InstanceOps createInstanceOps() {

			return new InstanceOps(parent, getInstanceGroup(), IFrameFunction.ASSERTION);
		}

		private InstanceGroup getInstanceGroup() {

			return getController().getInstanceGroup(valueType);
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

		INumberInputter createInputter(boolean canClear) {

			if (abstractEditableSlot()) {

				return new IndefiniteINumberInputter(parent, valueType, canClear);
			}

			return new DefiniteINumberInputter(parent, valueType, canClear);
		}
	}

	private class CStringTypeHandler extends DataTypeHandler<IString> {

		private CString valueType;

		CStringTypeHandler(CString valueType) {

			this.valueType = valueType;
		}

		IStringInputter createInputter(boolean canClear) {

			return new IStringInputter(parent, valueType, canClear);
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

			typeHandler = new CStringTypeHandler(value);
		}

		protected void visit(MFrame value) {

			typeHandler = new MFrameTypeHandler(value);
		}

		StandardTypeHandlerCreator() {

			visit(slot.getValueType());
		}

		private TypeHandler createCFrameTypeHandler(CFrame valueType) {

			if (descriptor.instanceGroupLink()) {

				return new InstanceGroupLinkCFrameTypeHandler(valueType);
			}

			if (fixedValueType(valueType)) {

				return new FixedCFrameTypeHandler(valueType);
			}

			return new SelectableCFrameTypeHandler(valueType);
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

	private boolean checkDisplayInstanceSubSectionDialog() {

		if (descriptor.hasStructuredValue()) {

			return displayInstanceSubSectionDialog((IFrame)descriptor.getValue());
		}

		return false;
	}

	private boolean displayInstanceSubSectionDialog(IFrame value) {

		InstanceSubSectionDialog dialog = createInstanceSubSectionDialog(value);

		dialog.display(false);

		if (dialog.replaceSelected()) {

			return false;
		}

		if (dialog.clearSelected()) {

			removeValue();
		}

		return true;
	}

	private InstanceSubSectionDialog createInstanceSubSectionDialog(IFrame value) {

		return new InstanceSubSectionDialog(parent, instantiator, value);
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

	private boolean fixedValueType(CFrame valueType) {

		return valueType.getSubs(CVisibility.EXPOSED).isEmpty();
	}

	private Customiser getCustomiser() {

		return getController().getCustomiser();
	}

	private Controller getController() {

		return instantiator.getController();
	}
}
